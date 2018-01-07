package com.sunyard.insurance.socketServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.socket.ServerImpl.SocketServerImpl;

public class SocketListenService extends Thread {

	private static final Logger log = Logger
			.getLogger(SocketListenService.class);
	private ExecutorService pool = null;
	private ServerSocket serverListenSocket = null;
	private int port;

	public SocketListenService(int port) {
		this.port = port;
	}

	public void run() {
		this.pool = Executors.newFixedThreadPool(GlobalVar.socketMaxThreadPool);
		
		try {
			this.serverListenSocket = new ServerSocket(this.port);
			this.serverListenSocket.setReuseAddress(true);
			log.info("|传输平台|Socket服务|监听端口|" + port + "|开始监听");
			while (true) {
				Socket socket = this.serverListenSocket.accept();
				if(GlobalVar.isNewSocketTrance==0) {
					this.pool.execute(new SocketServerService(socket));
				} else {
					this.pool.execute(new SocketServerImpl(socket));
				}
				
			}
		} catch (IOException e) {
			log.info("Socket服务启动异常!\n",e);
			log.error("Socket服务启动异常!\n",e);
		} finally {
			cleanup();
		}

	}

	public void cleanup() {
		if (this.serverListenSocket != null) {
			try {
				this.serverListenSocket.close();
			} catch (IOException e) {
				log.error("Socket服务停止资源关闭异常!\n",e);
			}
		}
		this.pool.shutdown();
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket serverListenSocket = null;
			ExecutorService pool = null;
			serverListenSocket = new ServerSocket(8025);
			serverListenSocket.setReuseAddress(true);
			
			pool = Executors.newFixedThreadPool(5);
			while (true) {
				Socket socket = serverListenSocket.accept();
				pool.execute(new SocketServerImpl(socket));
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
