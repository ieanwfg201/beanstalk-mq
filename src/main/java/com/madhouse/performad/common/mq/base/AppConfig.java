package com.madhouse.performad.common.mq.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by oneal on 15/9/7.
 */
@Configuration
@ComponentScan(value = "com.madhouse.performad.common.mq")
@PropertySource(value = "classpath:/message-queue.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
