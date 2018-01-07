package com.sunyard.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketListen {
	
	private static ExecutorService pool = null;
	private static ServerSocket serverListenSocket = null;
	private static int num = 0;
	
	public void listenSocket() {
		pool = Executors.newFixedThreadPool(5);
		try {
			serverListenSocket = new ServerSocket(2222);
			serverListenSocket.setReuseAddress(true);
			while (true) {
				Socket socket = serverListenSocket.accept();
				num++;
				pool.execute(new SocketListenImpl(num,socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverListenSocket != null) {
				try {
					serverListenSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pool.shutdown();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SocketListen().listenSocket();
	}

}
