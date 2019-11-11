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

package org.apache.skywalking.oap.server.receiver.trace.provider.handler.agree;


import org.apache.skywalking.oap.server.core.CoreModule;
import org.apache.skywalking.oap.server.core.SegmentThreadConfig;
import org.apache.skywalking.oap.server.core.config.ConfigService;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NiYanchun
 **/
public class SegmentThreadPoolExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SegmentThreadPoolExecutor.class);
    private ThreadPoolExecutor pool = null;
    private int segmentThreadPoolSize = Runtime.getRuntime().availableProcessors() * 4;
    private int segmentQueueSize = -1;
    private SegmentThreadConfig config;

    public SegmentThreadPoolExecutor(ModuleManager moduleManager) {
        ConfigService service = moduleManager.find(CoreModule.NAME).provider().getService(ConfigService.class);
        config = service.getSegmentThreadConfig();
    }

    public void init() {

        if (config.getSegmentThreadPoolSize() > 0) {
            segmentThreadPoolSize = config.getSegmentThreadPoolSize();
        }

        if (config.getSegmentQueueSize() > 0) {
            segmentQueueSize = config.getSegmentQueueSize();
        }

        BlockingQueue<Runnable> workQueue;
        if (segmentQueueSize <= 0) {
            workQueue =  new LinkedBlockingDeque<Runnable>();
        } else {
            workQueue = new ArrayBlockingQueue<Runnable>(segmentQueueSize);
        }
        if (pool == null) {
            pool = new ThreadPoolExecutor(segmentThreadPoolSize,
                    segmentThreadPoolSize,
                    60,
                    TimeUnit.SECONDS,
                    workQueue,
                    new SegmentThreadFactory(),
                    new SegmentRejectedExecutionHandler());
            // create all threads
            pool.prestartAllCoreThreads();
        }

    }

    public void shutDown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    public ExecutorService getSegmentThreadPoolExecutor() {
        return this.pool;
    }

    private class SegmentThreadFactory implements ThreadFactory {
        private AtomicInteger count = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            String threadName = SegmentThreadPoolExecutor.class.getSimpleName() + "-" + count.addAndGet(1);
            Thread t = new Thread(r);
            t.setName(threadName);
            return t;
        }
    }

    private class SegmentRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error("The queue is full,executor:{}.",executor);
        }
    }



}


