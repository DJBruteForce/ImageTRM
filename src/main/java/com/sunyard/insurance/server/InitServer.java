package com.sunyard.insurance.server;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.scheduler.JobsFactory;
import com.sunyard.insurance.socketServer.LoadListenerPort;

public class InitServer extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 3114611233146758123L;
	private static final Logger log = Logger.getLogger(InitServer.class);

	public InitServer() {
		
		if (GlobalVar.initConfig()) {
			LoadListenerPort loadListenerPort = new LoadListenerPort();
			loadListenerPort.initListenerPort();
		} else {
			log.error("服务启动初始化参数失败！");
		}

	}
	
	@Override
	public void destroy() {
		//销毁排程
		JobsFactory.shutdownScheduler();
		//销毁实时推送任务
		JobsFactory.autoPushThreadPool.shutdown();
		log.info("服务停止:销毁所有排程!");
		log.info("服务停止:销毁实时推送任务!");
	}

}
