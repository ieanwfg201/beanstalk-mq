package com.madhouse.performad.common.mq;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.madhouse.performad.common.mq.base.BaseTest;

public class StringHanlderTest extends BaseTest{

	@Autowired
	private MessageSender<String> sender;
	
	@Test
	public void testStringHanlder() throws Exception{
		
		for (int i = 0; i < 10; i++) {
			sender.sendMessage("message: "+i);
		}
		Thread.sleep(100000);
	}
}
