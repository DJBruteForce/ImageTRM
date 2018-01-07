package com.sunyard.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			try {
				Socket socket = new Socket("127.0.0.1",2222);
				System.out.println(i+"===");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
