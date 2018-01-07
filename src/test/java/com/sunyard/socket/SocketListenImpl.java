package com.sunyard.socket;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class SocketListenImpl implements Runnable, Serializable {

	private static final long serialVersionUID = -3306776462671469199L;
	private Socket socket = null;
	private int num;

	public SocketListenImpl(int num,Socket socket) {
		this.socket = socket;
		this.num = num;
	}
	
	public void run() {
		try {
			System.out.println(this.num+"[线程启动]"+System.currentTimeMillis());
			Thread.sleep(1000*20);
			System.out.println(this.num+"[线程结束]"+System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != socket) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
