package com.madhouse.performad.common.mq.limit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.madhouse.performad.common.mq.MessageSender;
import com.madhouse.performad.common.mq.base.BaseTest;

public class LimitHanlderTest extends BaseTest{
	
	@Autowired
	private MessageSender<LimitObject> sender;
	
	@Test
	public void testSendLimit() throws Exception{
		for (int i = 0; i < 100; i++) {
			sender.sendMessage(new LimitObject("Name:"+i, i));
		}
		Thread.sleep(100000);
	}
	
}
