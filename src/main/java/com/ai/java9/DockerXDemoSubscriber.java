package com.ai.java9;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class DockerXDemoSubscriber<T> implements Subscriber<T> {
	private String name;
	private Subscription subscription;
	final long bufferSize;
	long count;
	public String getName() {
		return name;
	}
	public Subscription getSubscription() {
		return subscription;
	}
	public DockerXDemoSubscriber(long bufferSize,String name) {
		this.bufferSize = bufferSize;
		this.name = name;
	}
	@Override
	public void onSubscribe(Subscription subscription) {
		(this.subscription = subscription).request(bufferSize);
		System.out.println("开始onSubscribe订阅");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onNext(T item) {
		System.out.println("#######"+
			Thread.currentThread().getName()+
			" name:"+name+" item:"+item+"######");
		System.out.println(name+"received:"+item);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onError(Throwable throwable) {
		throwable.printStackTrace();
	}
	
	@Override
	public void onComplete() {
		System.out.println("Completed");
	}

}
