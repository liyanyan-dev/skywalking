/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.agent.core.remote;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.commands.CommandService;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.context.TracingContext;
import org.apache.skywalking.apm.agent.core.context.TracingContextListener;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegment;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.commons.datacarrier.DataCarrier;
import org.apache.skywalking.apm.commons.datacarrier.buffer.BufferStrategy;
import org.apache.skywalking.apm.commons.datacarrier.consumer.IConsumer;
import org.apache.skywalking.apm.network.common.Commands;
import org.apache.skywalking.apm.network.language.agent.UpstreamSegment;
import org.apache.skywalking.apm.network.language.agent.v2.TraceSegmentReportServiceGrpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.skywalking.apm.agent.core.conf.Config.Agent.LOG_PRINT_DURATION;
import static org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus.CONNECTED;

/**
 * @author wusheng
 */
@DefaultImplementor
public class TraceSegmentServiceClient implements BootService, IConsumer<TraceSegment>, TracingContextListener, GRPCChannelListener {
    private static final ILog logger = LogManager.getLogger(TraceSegmentServiceClient.class);

    private long lastLogTime;
    private long segmentUplinkedCounter;
    private long segmentAbandonedCounter;
    private long bufferFullPrintTime;
    private long bufferFullLostCounter;
    private int bufferFullLostLoop;
    private volatile DataCarrier<TraceSegment> carrier;
    private volatile TraceSegmentReportServiceGrpc.TraceSegmentReportServiceStub serviceStub;
    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;
    private static final long LOST_MAX_COUNTER = 10000L;

    @Override
    public void prepare() throws Throwable {
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() throws Throwable {
        lastLogTime = System.currentTimeMillis();
        bufferFullPrintTime = System.currentTimeMillis();
        segmentUplinkedCounter = 0;
        segmentAbandonedCounter = 0;
        bufferFullLostCounter = 0;
        bufferFullLostLoop = 0;
        carrier = new DataCarrier<TraceSegment>(Config.Buffer.CHANNEL_SIZE, Config.Buffer.BUFFER_SIZE);
        carrier.setBufferStrategy(BufferStrategy.IF_POSSIBLE);
        carrier.consume(this, Config.Agent.SEGMENT_CONSUMER_THREAD_POOL_SIZE);
    }

    @Override
    public void onComplete() throws Throwable {
        TracingContext.ListenerManager.add(this);
    }

    @Override
    public void shutdown() throws Throwable {
        TracingContext.ListenerManager.remove(this);
        carrier.shutdownConsumers();
    }

    @Override
    public void init() {

    }

    @Override
    public void consume(List<TraceSegment> data) {
        long callStart = System.currentTimeMillis();
        if (CONNECTED.equals(status)) {
            final GRPCStreamServiceStatus status = new GRPCStreamServiceStatus(false);
            StreamObserver<UpstreamSegment> upstreamSegmentStreamObserver = serviceStub.withDeadlineAfter(Config.Collector.GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS).collect(new StreamObserver<Commands>() {
                @Override
                public void onNext(Commands commands) {
                    ServiceManager.INSTANCE.findService(CommandService.class).receiveCommand(commands);
                }

                @Override
                public void onError(Throwable throwable) {
                    logger.error(throwable, "Send UpstreamSegment to collector fail with a grpc internal exception.");
                    status.finished();
                    ServiceManager.INSTANCE.findService(GRPCChannelManager.class).reportError(throwable);
                }

                @Override
                public void onCompleted() {
                    status.finished();
                }
            });

            try {
                for (TraceSegment segment : data) {
                    UpstreamSegment upstreamSegment = segment.transform();
                    upstreamSegmentStreamObserver.onNext(upstreamSegment);
                }
            } catch (Throwable t) {
                logger.error(t, "Transform and send UpstreamSegment to collector fail.");
            }

            upstreamSegmentStreamObserver.onCompleted();
            if (Config.Agent.MAX_WAIT_TIME > 0) {
                status.wait4Finish(Config.Agent.MAX_WAIT_TIME);
            } else {
                status.wait4Finish();
            }
            segmentUplinkedCounter += data.size();
        } else {
            segmentAbandonedCounter += data.size();
        }

        long callEnd = System.currentTimeMillis();
        printUplinkStatus(callStart, callEnd, data.size());
    }

    private void printUplinkStatus(long startTime, long endTime, long size) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastLogTime > LOG_PRINT_DURATION) {
            lastLogTime = currentTimeMillis;
            // Don't set DEBUG level,because of debug log much more.
            if (logger.isInfoEnable()) {
                logger.info("Duration:{},Send list:{},Total trace segments:{},Thread name:{}", endTime - startTime, size, segmentUplinkedCounter, Thread.currentThread().getName());
            }

            if (segmentAbandonedCounter > 0) {
                logger.error(segmentAbandonedCounter + " trace segments have been abandoned, cause by no available channel.");
            }
        }
        if (segmentAbandonedCounter >= Long.MAX_VALUE - 1) {
            logger.warn("segmentAbandonedCounter:{} >= {},reset to 0.", segmentAbandonedCounter, Long.MAX_VALUE - 1);
            segmentAbandonedCounter = 0;
        }

        if (segmentUplinkedCounter >= Long.MAX_VALUE - 1) {
            logger.warn("segmentUplinkedCounter:{} >= {},reset to 0.", segmentUplinkedCounter, Long.MAX_VALUE - 1);
            segmentUplinkedCounter = 0;
        }
    }

    @Override
    public void onError(List<TraceSegment> data, Throwable t) {
        logger.error(t, "Try to send {} trace segments to collector, with unexpected exception.", data.size());
    }

    @Override
    public void onExit() {

    }

    @Override
    public void afterFinished(TraceSegment traceSegment) {
        if (traceSegment.isIgnore()) {
            return;
        }

        if (!carrier.produce(traceSegment)) {
            bufferFullLostCounter++;
            long currentTimeMillis = System.currentTimeMillis();
            // Set info level for test.
            if ((bufferFullLostCounter >= LOST_MAX_COUNTER) || (currentTimeMillis - bufferFullPrintTime >= LOG_PRINT_DURATION)) {
                bufferFullPrintTime = currentTimeMillis;
                logger.error(String.format("The trace segment (%s:%s) has been abandoned, cause by buffer is full.Total lost segments:%d", traceSegment.getRelatedGlobalTraces().get(0).toString(), traceSegment.getTraceSegmentId().toString(), bufferFullLostLoop * LOST_MAX_COUNTER + bufferFullLostCounter));
                if (bufferFullLostCounter >= LOST_MAX_COUNTER) {
                    bufferFullLostLoop++;
                    bufferFullLostCounter %= LOST_MAX_COUNTER;
                }
            }
        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            serviceStub = TraceSegmentReportServiceGrpc.newStub(channel);
        }
        this.status = status;
    }
}
