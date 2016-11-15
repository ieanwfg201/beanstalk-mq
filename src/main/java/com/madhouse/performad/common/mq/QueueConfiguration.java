package com.madhouse.performad.common.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Madhouse on 2015/9/7.
 */
@Component
public class QueueConfiguration {

    @Value("${message.queue.host}")
    private String host;

    @Value("${message.queue.port}")
    private int port;

    @Value("${message.queue.connection.pool.size}")
    private int poolSize;

    @Value("${message.queue.connection.message.ttr}")
    private int ttr;

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getTtr() {
        return ttr;
    }

    public void setTtr(int ttr) {
        this.ttr = ttr;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
