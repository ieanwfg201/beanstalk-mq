package com.madhouse.performad.common.mq;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

/**
 * Created by oneal on 15/9/7.
 */
@Component
public class MessageQueueInitializer implements ApplicationContextAware {
	
	protected transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static Map<String, MessageLimits> channelTimeRecordMap = new ConcurrentHashMap<String, MessageLimits>();

    private ApplicationContext applicationContext;

    @Autowired
    private MessageClientFactory clientFactory;

    @PostConstruct
    public void afterPropertiesSet() {
        // get all message handler
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MessageSupport.class);
        if (beans==null||beans.size()==0) {
        	logger.warn("no configure bean implement MessageSupport, skip initializing beanstalk component...");
        	return ;
		}
        
        for (final Map.Entry<String, Object> en : beans.entrySet()) {

            Class<?> messageHandler = en.getValue().getClass();
            final String channel = getChannelNameFromClass(messageHandler);
            if (channel == null) {
				throw new IllegalAccessError("Failed to initialze beanstalk component, cannot get clazz name, class["+en.getKey()+":"+en.getValue()+"]");
			}
            final MessageSupport annotation = (MessageSupport) messageHandler.getAnnotation(MessageSupport.class);
            final int poolsize = annotation.threadPoolSize();
            // 限制的秒数内
            long limitSecondsWith = annotation.limitSecondsWithIn();
            // 限制的时间内
            long maxCountInLimitSeconds = annotation.maxCountsInLimitSeconds();
            channelTimeRecordMap.put(channel, new MessageLimits(limitSecondsWith, maxCountInLimitSeconds));
            
            logger.debug("register channel, channelName="+channel+", poolSize="+poolsize);
            ExecutorService es = Executors.newFixedThreadPool(poolsize);
            for (int i = 0; i < poolsize; i++) {
                es.execute(new Runnable() {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
                    public void run() {
                        BeanstalkClient client = null;
                        BeanstalkJob job = null;
                        try {
                            client = clientFactory.getClient(channel);
                            boolean isServerConnected = true;
                            while (true) {
                            	try {
                            		if (!channelTimeRecordMap.get(channel).checkIfLimit()) {
                            			job = client.reserve(null);
                            			if (!isServerConnected) {
                            				logger.info("Current thread["+Thread.currentThread().getName()+"] reconnect to beanstalk server, server already ok...");
                            				isServerConnected =true;
										}
                                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(job.getData()));
                                        Object message = ois.readObject();

                                        ((MessageHandler) en.getValue()).handleMessage(message);
                                        client.deleteJob(job);
                                        logger.debug("Success to execute job from beanstalk, job: "+message);
    								}
                            	} catch (Exception e) {
                            		logger.error("Error to execute beanstalk for job: "+job+". message = "+e.getMessage(),e);
									if (job!=null) {
										try{client.bury(job, 1);}catch(Exception e2){}
									}
									if (e instanceof BeanstalkException) {
                                		client.reconnect();
                                		if (isServerConnected) {
                                			logger.error("Current thread["+Thread.currentThread().getName()+"] Could not connect to beanstalk server, beanstalk server already down...");
                                        	isServerConnected = false;
										}
                                		Thread.sleep(500);
									}
								} 
                            }
                        }catch (Exception e) {
                        	logger.error("Error to get beanstalk client. message = "+e.getMessage(),e);
                        	client.close();
                        	throw new MessageQueueException(e);
                        }
                    }
                });
            }
            logger.debug("Success to register channel: "+channel);
        }
    }
    /**
     * 从class中获取到泛型的实际类型
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
	private String getChannelNameFromClass(Class clazz){
    	try {
    		ParameterizedType pt = (ParameterizedType) clazz.getGenericInterfaces()[0];
    		Class type = (Class)pt.getActualTypeArguments()[0];
    		return type.getName();
		} catch (Exception e) {
			return null;
		}
    	
    }
    public static Object bytes2Object(byte[] bytes){
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			return ois.readObject();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally{
			if (ois!=null) {
				try{ois.close();}catch(Exception e){}
			}
			if (bis!=null) {
				try{bis.close();}catch(Exception e){}
			}
		}
	}
    
    private static class MessageLimits{
    	private List<Long> timestamps = new ArrayList<Long>();
    	private long limitSecondsWithIn;
    	private long maxCountInLimitSeconds;
    	public MessageLimits(long limitSecondsWithIn, long maxCountInLimitSeconds){
    		this.limitSecondsWithIn = limitSecondsWithIn;
    		this.maxCountInLimitSeconds = maxCountInLimitSeconds;
    	}
    	
    	public synchronized boolean checkIfLimit(){
    		if (limitSecondsWithIn == 0|| maxCountInLimitSeconds == 0) {
				return false;
			}
    		long currentTime = new Date().getTime();
    		long checkTime = currentTime - limitSecondsWithIn*1000;
    		if (timestamps.size() != maxCountInLimitSeconds) {
    			timestamps.add(currentTime);
    			return false;
			}
    		if (timestamps.get(0)<=checkTime) {
				timestamps.remove(0);
				timestamps.add(currentTime);
				return false;
			}
    		return true;
    	}
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public ApplicationContext getContext(){
    	return this.applicationContext;
    }
}
