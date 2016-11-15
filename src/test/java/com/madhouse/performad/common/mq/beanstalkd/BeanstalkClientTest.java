package com.madhouse.performad.common.mq.beanstalkd;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkJob;

public class BeanstalkClientTest {
	private static String HOST = "172.16.30.47";
	
	public static void main(String[] args) throws Exception{
		BeanstalkClient client = new BeanstalkClient(HOST, 11300, "example");
		BeanstalkJob key = client.peekReady();
		System.out.println("dd");
		System.out.println(key);
		System.out.println(client.tubeReadyCount("example"));
	}
}
