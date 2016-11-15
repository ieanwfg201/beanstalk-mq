package com.madhouse.performad.common.mq;

/**
 * Created by oneal on 15/9/7.
 */
public interface MessageHandler<T> {
	/**
	 * 处理消息
	 * @param msg
	 */
    public void handleMessage(T msg);

}
