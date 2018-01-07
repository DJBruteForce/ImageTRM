package com.sunyard.insurance.cmapi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.sunyard.insurance.common.CMConstant;

/**
 * 
  * @Title Outsourcer.java
  * @Package com.sunyard.insurance.cmapi
  * @Description 
  * 多线程从CM获取文件
  * @time 2012-8-8 下午03:04:39  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class Outsourcer {

	private static final Logger log = Logger.getLogger(Outsourcer.class);

	public Outsourcer() {
		super();
	}
	
	public List<Object> execute(List<Callable<Object>> tasks)
			throws InterruptedException, ExecutionException, Exception {
		List<Object> results = new ArrayList<Object>();
		ExecutorService eService = null;
		try {
			
			if(null == tasks || tasks.size()==0) {
				return results;
			} else {
				if(tasks.size()<CMConstant.imageThreadNum) {
					eService = Executors.newFixedThreadPool(tasks.size());
				} else {
					eService = Executors.newFixedThreadPool(CMConstant.imageThreadNum);
				}
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
			log.error("多线程从CM获取文件失败：", e);
			throw e;
		} catch (ExecutionException e) {
			log.error("多线程从CM获取文件失败：", e);
			throw e;
		}  finally {
			eService.shutdown();
			eService = null;
		}
	    return results;
	}
}
