package com.sunyard.insurance.filenet.ce.depend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import com.sunyard.insurance.filenet.faf.cesupport.CEConstant;

/**
 * 
  * @Title ExecPool.java
  * @Package com.sunyard.insurance.filenet.ce.depend
  * @Description 多线程下载CE影像
  * @author wuzelin
  * @time 2013-7-18 下午03:27:35  
  * @version 1.0
 */
public class ExecPool {
	
	private static final Logger log = Logger.getLogger(ExecPool.class);
	
	private static ExecutorService eService = null;
	
	public ExecPool() {
		super();
	}
	
	
	public List<Object> execute(List<Callable<Object>> tasks)
			throws InterruptedException, ExecutionException {
		List<Object> results = new ArrayList<Object>();
		
		try {
			
			if(tasks.size()<CEConstant.imageThreadNum) {
				eService = Executors.newFixedThreadPool(tasks.size());
			} else {
				eService = Executors.newFixedThreadPool(CEConstant.imageThreadNum);
			}
			
			
			List<Future<Object>> futures = eService.invokeAll(tasks);
			for (Future<Object> future : futures) {
				Object obj = future.get();
				if (obj == null) {
					obj = Boolean.TRUE;
				}
				results.add(obj);
			}
		} catch (InterruptedException e) {
			log.error("多线程从CE中下载影像异常",e);
			throw e;
		} catch (ExecutionException e) {
			log.error("多线程从CE中下载影像异常",e);
			throw e;
		} finally {
			eService.shutdown();
			eService = null;
		}
		return results;
	}
	
}
