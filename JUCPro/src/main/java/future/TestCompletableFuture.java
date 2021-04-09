package future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestCompletableFuture {
	public static void main(String[] args) {
		// 同时从新浪和网易查询证券代码，只要任意一个返回结果，就进行下一步查询价格
		CompletableFuture<String> query1 = CompletableFuture.supplyAsync(()->{
			return queryCode("中国石油", "https://finance.sina.com.cn/code/");
		});
		
		CompletableFuture<String> query2 = CompletableFuture.supplyAsync(()->{
			return queryCode("中国石油", "https://money.163.com/code/");
		});
		
		CompletableFuture<Object> query = CompletableFuture.anyOf(query1,query2);
		
		// 查询价格也同时从新浪和网易查询，只要任意一个返回结果，就完成操作
		CompletableFuture<Double> fetchFromSina = query.thenApplyAsync((code)->{
			return fetchPrice((String)code, "https://finance.sina.com.cn/code/");
		});
		CompletableFuture<Double> fetchFrom163 = query.thenApplyAsync((code)->{
			return fetchPrice((String)code, "https://money.163.com/code/");
		});
		
		CompletableFuture<Object> fetch = CompletableFuture.anyOf(fetchFromSina,fetchFrom163);

		fetch.thenAccept((price)->{
			System.out.println("price: " + price);
		}).join();
	}
	
	static Double fetchPrice(String code, String url) {
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(Math.random() < 0.3) {
			throw new RuntimeException("fetch price failed!");
		}
		return 5 + Math.random()*20;
	}
	
	static String queryCode(String name, String url) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return "601857";
	}
}
