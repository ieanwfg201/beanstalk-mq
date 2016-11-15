package com.madhouse.performad.common.mq.sender;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.madhouse.performad.common.mq.MessageSender;
import com.madhouse.performad.common.mq.base.BaseTest;

public class SenderTest extends BaseTest {
	@Autowired
	private MessageSender<String> sender;
	
	@Test
	public void testSender() throws Exception{
		sender.sendMessage("Message: 1");
	}
}
