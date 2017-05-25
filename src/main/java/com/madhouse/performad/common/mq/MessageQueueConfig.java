package com.madhouse.performad.common.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages="com.madhouse.performad.common.mq")
@PropertySource(value = {"classpath:message-queue.properties","file:${external_conf}/message-queue.properties"}, ignoreResourceNotFound = true)
public class MessageQueueConfig {
    @Resource
    private Environment env;

    @Bean
    public QueueConfiguration queueConfiguration(){
        QueueConfiguration configuration = new QueueConfiguration();
        // checking host if valid
        if (!env.containsProperty(QueueConfiguration.PROPERTIES_KEY_HOST)) throw new RuntimeException("No configuration found for beanstalkd host");
        configuration.setHost(env.getProperty(QueueConfiguration.PROPERTIES_KEY_HOST));

        // PORT
        int port = 11300;
        if (!env.containsProperty(QueueConfiguration.PROPERTIES_KEY_PORT))
            port = praseInt(env.getProperty(QueueConfiguration.PROPERTIES_KEY_PORT), port);
        configuration.setPort(port);

        // POOL SIZE
        int poolSize = 30;
        if (!env.containsProperty(QueueConfiguration.PROPERTIES_KEY_POOLSIZE))
            poolSize = praseInt(env.getProperty(QueueConfiguration.PROPERTIES_KEY_POOLSIZE), poolSize);
        configuration.setPoolSize(poolSize);

        // ttr
        int ttr = 30;
        if (!env.containsProperty(QueueConfiguration.PROPERTIES_KEY_TTR))
            ttr = praseInt(env.getProperty(QueueConfiguration.PROPERTIES_KEY_TTR), ttr);
        configuration.setTtr(ttr);
        return configuration;
    }

    private int praseInt(String str, int defaultValue){
        if (str==null||str.trim().equals("")) return defaultValue;
        try {
            return Integer.valueOf(str.trim());
        }catch (Exception e){}
        return defaultValue;
    }
}
