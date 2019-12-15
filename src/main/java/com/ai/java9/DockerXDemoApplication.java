package com.ai.java9;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class DockerXDemoApplication {
	public static void main(String[] args) {
		flowCustomSubmissionPublisher();
	}
	private static void demoSubscribe(DockerXDemoPublisher<Integer> publisher,String subscriberName) {
		DockerXDemoSubscriber<Integer> subscriber = new DockerXDemoSubscriber<>(4L, subscriberName);
		
		publisher.subscribe(subscriber);
	}
	private static void flowCustomSubmissionPublisher() {
		ExecutorService executorService = ForkJoinPool.commonPool();
		try(DockerXDemoPublisher<Integer> publisher = new DockerXDemoPublisher<>(executorService)){
			demoSubscribe(publisher, "One");
			demoSubscribe(publisher, "Two");
			demoSubscribe(publisher, "Three");
			IntStream.range(1, 5).forEach(publisher::submit);
		}finally {
			try {
				executorService.shutdown();
				int shutdownDelaySec = 1;
				System.out.println("------等待 "+shutdownDelaySec+"秒后结束任务");
				executorService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
			} catch (Exception e) {
				System.out.println("executorService.awaitTermination()方法异常:"+e.getClass().getName());
			}finally {
				System.out.println("调用executorService.shutdown()结束服务");
				List<Runnable> list = executorService.shutdownNow();
				System.out.println("还剩 "+list.size()+"个任务等待被执行，服务已关闭");
			}
		}
	}
}
