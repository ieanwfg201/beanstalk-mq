package com.madhouse.performad.common.mq;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages="com.madhouse.performad.common.mq")
@PropertySource(value = {"classpath:message-queue.properties","file:${external_conf}/message-queue.properties"}, ignoreResourceNotFound = true)
public class MessageQueueConfig {

}
