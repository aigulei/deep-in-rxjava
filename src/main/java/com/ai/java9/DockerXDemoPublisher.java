package com.ai.java9;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class DockerXDemoPublisher<T> implements Publisher<T>,AutoCloseable {
	private final ExecutorService executor;
	private CopyOnWriteArrayList<DockerXDemoSubscription> list = new CopyOnWriteArrayList();
	
	
	public void submit(T item) {
		System.out.println("************开始发布元素item:"+item+"*********"); 
		list.forEach(e -> { 
			e.future=executor.submit(() ->{e.subscriber.onNext(item);}); 
		});
		 
	}
	
	public DockerXDemoPublisher(ExecutorService executor) {
		this.executor = executor;
	}
	
	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		subscriber.onSubscribe(new DockerXDemoSubscription(subscriber, executor));
		list.add(new DockerXDemoSubscription(subscriber, executor));
	}
	
	
	@Override
	public void close() {
		list.forEach(e ->{
			e.future = executor.submit(() ->{e.subscriber.onComplete();});
		});
	}
	
	
	
	static class DockerXDemoSubscription<T> implements Subscription{
		private final Subscriber<? super T> subscriber;
		private final ExecutorService executor;
		private Future<?> future;
		private T item;
		private boolean completed;
		
		public DockerXDemoSubscription(Subscriber<? super T> subscriber,ExecutorService executor) {
			this.subscriber = subscriber;
			this.executor = executor;
		}
		
		@Override
		public void request(long n) {
			if(n!=0&&!completed) {
				if(n<0) {
					IllegalArgumentException ex = new IllegalArgumentException();
					executor.execute(() -> subscriber.onError(ex));
				}else {
					future = executor.submit(()->{
						subscriber.onNext(item);
					});
				}
			}else {
				subscriber.onComplete();
			}
			
		}

		@Override
		public void cancel() {
			completed = true;
			if(future!=null && !future.isCancelled()) {
				this.future.cancel(true);
			}
		}
		
	}
	
	

}
