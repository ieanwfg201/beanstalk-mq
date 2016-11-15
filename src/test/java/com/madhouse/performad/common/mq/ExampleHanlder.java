package com.madhouse.performad.common.mq;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
@MessageSupport(threadPoolSize=10) // set pool size
public class ExampleHanlder implements MessageHandler<Example>{
	@Override
	public void handleMessage(Example msg) {
		System.out.println("Received data: "+msg);
	}
	
}
// must implement interface 'Serializable'
class Example implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	public Example(String name, int age){
		this.name = name;
		this.age =age;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String toString(){
		return name +": "+age;
	}
}


