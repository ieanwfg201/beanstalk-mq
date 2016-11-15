package com.madhouse.performad.common.mq;

import com.trendrr.beanstalk.BeanstalkClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by oneal on 15/9/7.
 */
@Component
public class MessageSender<T> {
    protected Logger logger = LoggerFactory.getLogger(MessageSender.class);
    
    @Autowired
    private QueueConfiguration configuration;

    @Autowired
    private MessageClientFactory clientFactory;

    public void sendMessage(T msg) {
        this.sendMessage(msg, configuration.getTtr());
    }
    
    public void sendMessage(T msg, int ttr) {
    	try {
        	if (msg == null) {
				return;
			}
        	if(!(msg instanceof Serializable)){
        		throw new IllegalArgumentException("Serializable failed, the object must implement interface 'Serializable'");
        	}
            BeanstalkClient client = clientFactory.getClient(msg.getClass().getName());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(msg);
            client.put(0, 0, ttr, b.toByteArray());
            client.close();
           
        } catch (Exception e) {
            throw new MessageQueueException("Failed to send message.", e);
        }
    }
}
