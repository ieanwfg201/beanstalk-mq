package com.madhouse.performad.common.mq;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.madhouse.performad.common.mq.base.BaseTest;

public class ExampleHanlderTest extends BaseTest{
	
	@Autowired
	private MessageSender<Example> example;
	@Test
	public void testExampleHanlder() throws Exception{
		for (int i = 0; i < 10; i++) {
			example.sendMessage(new Example("NAME: "+i, i+10));
		}
		Thread.sleep(100000);
	}
}
