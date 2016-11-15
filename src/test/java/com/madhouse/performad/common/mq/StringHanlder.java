package com.madhouse.performad.common.mq;

import org.springframework.stereotype.Component;

import com.madhouse.performad.common.mq.MessageHandler;
import com.madhouse.performad.common.mq.MessageSupport;
@Component
@MessageSupport(threadPoolSize=10)
public class StringHanlder implements MessageHandler<String>{

	@Override
	public void handleMessage(String msg) {
		System.out.println("Receive message...:"+msg);
	}

}
