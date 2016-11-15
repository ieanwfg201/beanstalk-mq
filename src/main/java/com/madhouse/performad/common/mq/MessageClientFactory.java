package com.madhouse.performad.common.mq;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oneal on 15/9/7.
 */
@Component
public class MessageClientFactory {

    @Autowired
    private QueueConfiguration configuration;

    private static Map<String, BeanstalkPool> pools = new HashMap<String, BeanstalkPool>();

    protected BeanstalkClient getClient(String channel) throws BeanstalkException {
        if (pools.get(channel) == null) {
        	synchronized (MessageClientFactory.class) {
				if (pools.get(channel)==null) {
					BeanstalkPool pool = new BeanstalkPool(configuration.getHost(), configuration.getPort(),
		                    configuration.getPoolSize(), //poolsize
		                    channel //tube to use
		            );
		            pools.put(channel, pool);
				}
			}
        }
        return pools.get(channel).getClient();
    }
}
