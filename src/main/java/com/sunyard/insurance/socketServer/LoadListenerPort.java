package com.sunyard.insurance.socketServer;

import com.sunyard.insurance.common.GlobalVar;

public class LoadListenerPort {
	
	private SocketListenService socketListenService = null;
	
	/**
	 * 加载监听端口类 return
	 */
	public void initListenerPort() {
		socketListenService = new SocketListenService(GlobalVar.svrInfoBean.getSocket_port());
		socketListenService.start();
	}
}
