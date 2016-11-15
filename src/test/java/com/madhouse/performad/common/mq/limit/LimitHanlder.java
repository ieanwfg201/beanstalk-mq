package com.madhouse.performad.common.mq.limit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.madhouse.performad.common.mq.MessageHandler;
import com.madhouse.performad.common.mq.MessageSupport;

@Component
@MessageSupport(threadPoolSize=2,limitSecondsWithIn=10,maxCountsInLimitSeconds=15)
public class LimitHanlder implements MessageHandler<LimitObject>{

	@Override
	public void handleMessage(LimitObject msg) {
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S").format(new Date())+"--"+msg);
	}

}

class LimitObject implements Serializable{
	private String name;
	private Integer age;
	public LimitObject(String name, Integer age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	@Override
	public String toString(){
		return name+":"+age;
	}
}