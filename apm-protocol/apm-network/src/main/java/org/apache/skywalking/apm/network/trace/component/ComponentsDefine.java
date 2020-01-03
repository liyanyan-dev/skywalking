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


package org.apache.skywalking.apm.network.trace.component;

/**
 * The supported list of skywalking java sniffer.
 *
 * @author wusheng
 */
public class ComponentsDefine {

    public static final OfficialComponent TOMCAT = new OfficialComponent(1, "Tomcat");

    public static final OfficialComponent HTTPCLIENT = new OfficialComponent(2, "HttpClient");

    public static final OfficialComponent DUBBO = new OfficialComponent(3, "Dubbo");

    public static final OfficialComponent MOTAN = new OfficialComponent(8, "Motan");

    public static final OfficialComponent RESIN = new OfficialComponent(10, "Resin");

    public static final OfficialComponent FEIGN = new OfficialComponent(11, "Feign");

    public static final OfficialComponent OKHTTP = new OfficialComponent(12, "OKHttp");

    public static final OfficialComponent SPRING_REST_TEMPLATE = new OfficialComponent(13, "SpringRestTemplate");

    public static final OfficialComponent SPRING_MVC_ANNOTATION = new OfficialComponent(14, "SpringMVC");

    public static final OfficialComponent STRUTS2 = new OfficialComponent(15, "Struts2");

    public static final OfficialComponent NUTZ_MVC_ANNOTATION = new OfficialComponent(16, "NutzMVC");

    public static final OfficialComponent NUTZ_HTTP = new OfficialComponent(17, "NutzHttp");

    public static final OfficialComponent JETTY_CLIENT = new OfficialComponent(18, "JettyClient");

    public static final OfficialComponent JETTY_SERVER = new OfficialComponent(19, "JettyServer");

    public static final OfficialComponent SHARDING_JDBC = new OfficialComponent(21, "ShardingJDBC");

    public static final OfficialComponent GRPC = new OfficialComponent(23, "GRPC");

    public static final OfficialComponent ELASTIC_JOB = new OfficialComponent(24, "ElasticJob");

    public static final OfficialComponent HTTP_ASYNC_CLIENT = new OfficialComponent(26, "httpasyncclient");

    public static final OfficialComponent SERVICECOMB = new OfficialComponent(28, "ServiceComb");

    public static final OfficialComponent HYSTRIX =  new OfficialComponent(29, "Hystrix");

    public static final OfficialComponent JEDIS =  new OfficialComponent(30, "Jedis");

    public static final OfficialComponent H2_JDBC_DRIVER =  new OfficialComponent(32, "jdbc-jdbc-driver");

    public static final OfficialComponent MYSQL_JDBC_DRIVER = new OfficialComponent(33, "mysql-connector-java");

    public static final OfficialComponent OJDBC = new OfficialComponent(34, "ojdbc");

    public static final OfficialComponent SPYMEMCACHED = new OfficialComponent(35, "Spymemcached");

    public static final OfficialComponent XMEMCACHED = new OfficialComponent(36, "Xmemcached");

    public static final OfficialComponent POSTGRESQL_DRIVER = new OfficialComponent(37, "postgresql-jdbc-driver");

    public static final OfficialComponent ROCKET_MQ_PRODUCER = new OfficialComponent(38, "rocketMQ-producer");

    public static final OfficialComponent ROCKET_MQ_CONSUMER = new OfficialComponent(39, "rocketMQ-consumer");

    public static final OfficialComponent KAFKA_PRODUCER = new OfficialComponent(40, "kafka-producer");

    public static final OfficialComponent KAFKA_CONSUMER = new OfficialComponent(41, "kafka-consumer");

    public static final OfficialComponent MONGO_DRIVER = new OfficialComponent(42, "mongodb-driver");

    public static final OfficialComponent SOFARPC =  new OfficialComponent(43, "SOFARPC");

    public static final  OfficialComponent ACTIVEMQ_PRODUCER = new OfficialComponent(45,"activemq-producer");

    public static final  OfficialComponent ACTIVEMQ_CONSUMER = new OfficialComponent(46,"activemq-consumer");

    public static final OfficialComponent TRANSPORT_CLIENT =  new OfficialComponent(48, "transport-client");

    public static final OfficialComponent UNDERTOW =  new OfficialComponent(49, "Undertow");

    public static final OfficialComponent RABBITMQ_PRODUCER = new OfficialComponent(52,"rabbitmq-producer");

    public static final OfficialComponent RABBITMQ_CONSUMER = new OfficialComponent(53,"rabbitmq-consumer");

    public static final OfficialComponent CANAL = new OfficialComponent(54,"Canal");
  
    public static final OfficialComponent GSON = new OfficialComponent(55,"Gson");
  
    public static final OfficialComponent REDISSON =  new OfficialComponent(56, "Redisson");

    public static final OfficialComponent LETTUCE =  new OfficialComponent(57, "Lettuce");

    public static final OfficialComponent ZOOKEEPER =  new OfficialComponent(58, "Zookeeper");

    public static final OfficialComponent VERTX =  new OfficialComponent(59, "Vert.x");

    public static final OfficialComponent SHARDING_SPHERE = new OfficialComponent(60, "ShardingSphere");

    public static final OfficialComponent SPRING_CLOUD_GATEWAY =  new OfficialComponent(61, "spring-cloud-gateway");

    public static final OfficialComponent RESTEASY =  new OfficialComponent(62, "RESTEasy");

    public static final OfficialComponent SOLRJ =  new OfficialComponent(63, "solrj");

    public static final OfficialComponent SPRING_ASYNC = new OfficialComponent(65, "SpringAsync");

    public static final OfficialComponent JDK_HTTP = new OfficialComponent(66, "JdkHttp");

    public static final OfficialComponent SPRING_WEBFLUX = new OfficialComponent(67, "spring-webflux");

    public static final OfficialComponent PLAY = new OfficialComponent(68, "Play");

    public static final OfficialComponent ASDK_HTTP = new OfficialComponent(69, "ASDK-HTTP");

    public static final OfficialComponent AFA_ABUS = new OfficialComponent(70, "AFA-ABUS");

    public static final OfficialComponent AFA_HTTP = new OfficialComponent(71, "AFA-HTTP");

    public static final OfficialComponent AFA_RPC = new OfficialComponent(72, "AFA-RPC");

    public static final OfficialComponent AFA_ACTIVEMQ = new OfficialComponent(73, "AFA-ACTIVEMQ");

    public static final OfficialComponent AFA_AFT = new OfficialComponent(74, "AFA-AFT");

    public static final OfficialComponent AFA_AIMLSR = new OfficialComponent(75, "AFA-AIMLSR");

    public static final OfficialComponent AFA_ROCKETMQ = new OfficialComponent(76, "AFA-ROCKETMQ");

    public static final OfficialComponent AFA_CMC = new OfficialComponent(77, "AFA-CMC");

    public static final OfficialComponent AFA_WS = new OfficialComponent(78, "AFA-WS");

    public static final OfficialComponent AFA_KAFKA = new OfficialComponent(79, "AFA-KAFKA");

    public static final OfficialComponent AFA_SOCKET = new OfficialComponent(80, "AFA-SOCKET");

    public static final OfficialComponent AFA_JMQ = new OfficialComponent(81, "AFA-JMQ");

    public static final OfficialComponent AFA_RABBITMQ = new OfficialComponent(82, "AFA-RABBITMQ");

    public static final OfficialComponent AFA_REPLY = new OfficialComponent(83, "AFA-REPLY");

    public static final OfficialComponent AFA_WEBSOCKET = new OfficialComponent(84, "AFA-WEBSOCKET");

    public static final OfficialComponent AFA_SCHEDULE = new OfficialComponent(85, "AFA-SCHEDULE");

}
