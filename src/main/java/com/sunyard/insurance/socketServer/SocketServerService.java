package com.sunyard.insurance.socketServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.log4j.Logger;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.util.DateUtil;

public class SocketServerService implements Runnable, Serializable {

	private static final long serialVersionUID = -2809455986003376547L;
	private static final Logger log = Logger.getLogger(SocketServerService.class);
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private DataInputStream dis = null;
	private int transBufferSize = GlobalVar.transBuffersize;

	public SocketServerService(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		String clientIp = "";
		try {
			this.dis = new DataInputStream(this.socket.getInputStream());
//			this.dis = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
			this.in = new BufferedReader(new InputStreamReader(this.socket
					.getInputStream()));
			this.out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(this.socket.getOutputStream())),
					true);
			this.socket.setSoTimeout(GlobalVar.socketSoTimeout);
			this.socket.setReceiveBufferSize(GlobalVar.recvsocketbuffersize);
			this.socket.setSendBufferSize(GlobalVar.sendsocketbuffersize);
			
			InetAddress addr = socket.getInetAddress();
			clientIp = addr.getHostAddress();

			boolean isAlive = true;
			String batchId = "";
			String batchTemPath = GlobalVar.tempDir + File.separator + "scan"
					+ File.separator + DateUtil.getDateStrCompact();

			// 开始时间
			long startTime = System.currentTimeMillis();
			while (isAlive) {

				// try {
				// Thread.currentThread();
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// log.error("|传输平台|线程休眠异常|",e);
				// isAlive = false;
				// return;
				// }
				
				String str = this.in.readLine();
				System.out.println("客户端IP["+clientIp+"]输入:" + str);
				log.info("客户端IP["+clientIp+"]输入:" + str);
				long beginTime = System.currentTimeMillis();
				if (null == str || "".equals(str) || "null".equals(str)) {
					isAlive = false;
					continue;
				}

				// 分解消息报文
				String[] content = str.split(":");
				String result = "99";

				if (content[0].equals("0001")) {
					result = TransService.checkUser(content[1], content[2]);
					if (!result.equals("0")) {
						isAlive = false;
					}
				} else if (content[0].equals("0002")) {
					if ("SunTXM001".equals(content[1])) {
						batchId = content[2];// 记录下BATCH_ID
						
						batchTemPath = batchTemPath + File.separator
								+ content[2].substring(0, 1) + File.separator
								+ content[2].substring(1, 2) + File.separator
								+ content[2];
						
						//带有版本号
						if(content.length>3) {
							batchTemPath += ("_" + content[3]);
						}
						
						result = TransService.startBatchFile(content[2],
								batchTemPath);
						if (!result.equals("0003")) {
							isAlive = false;
						}
					} else if ("SunTXM002".equals(content[1])) {
						// 单文件校验MD5
						result = TransService.checkBatchFile(content[2],
								batchTemPath);
						if (!result.equals("2")) {
							// MD5校验失败
							isAlive = false;
						}
					} else if ("SunTXM003".equals(content[1])) {
						// 批次传输结束
						long tranceEndTime = System.currentTimeMillis();
						log.info("批次[" + batchId + "]文件交易花费时间["+ (tranceEndTime - startTime) + "]毫秒");
						result = TransService.endBatchFile(content[2],
								batchTemPath, "SunTXM003");
						if (!result.equals("2")) {
							// 上传失败
							isAlive = false;
						}
						//增加大地永诚个性化,没有收到SYD文件的情况下返回客户端成功标识
//						if("-7".equals(result)) {
//							result = "2";
//						}
						
					} else if ("SunTRM003".equals(content[1])) {
						// 批次传输结束
						// 开始时间
						long tranceEndTime = System.currentTimeMillis();
						log.info("批次[" + batchId + "]文件交易花费时间["+ (tranceEndTime - startTime) + "]毫秒");
						result = TransService.endBatchFile(content[2],
								batchTemPath, "SunTRM003");
						if (!result.equals("2")) {
							// 上传失败
							isAlive = false;
						}
					}
				} else if (content[0].equals("0009")) {
					isAlive = false;
					result = "0";// 断开连接请求，返回成功
				}
				
				System.out.println("输出:" + result);
				log.info(batchId+"向客户端IP["+clientIp+"]输出:" + result);
				this.out.println(result);

				System.out.println("交易类型:" + content[0]+"耗时:"+(System.currentTimeMillis()-beginTime));
				
				if (!content[0].equals("0003")) {
					continue;
				} else {
					if (recieveFile(batchTemPath, str)) {
						continue;
					} else {
						isAlive = false;
						continue;
					}
				}

			}
			// 开始时间
			long endTime = System.currentTimeMillis();
			log.info("批次[" + batchId + "]传输交易花费时间[" + (endTime - startTime)
					+ "]毫秒");
		} catch (IOException e) {
			log.error("|client["+clientIp+"]|传输平台|Socket|IOException异常!",e);
		} finally {
			System.out.println("-------------Socket传输线程销毁-------------");
			try {
				this.dis.close();
				this.out.close();
				this.in.close();
				this.socket.close();
			} catch (IOException e) {
				log.error("|传输平台|Socket|资源关闭失败了!",e);
			}
		}
	}

	private boolean recieveFile(String rootPath, String str) {
		File recieveFile = null;
		FileOutputStream fileOutPutStream = null;
//		BufferedOutputStream bos = null;
		long startTime = System.currentTimeMillis();
		try {
			String[] content = str.split(":");
			String fileName = content[1];
			long fileSize = Long.parseLong(content[2]);
			this.transBufferSize = Integer.parseInt(content[3]);
			boolean transFlag = true;
			File recieveFolder = new File(rootPath);
			if (!recieveFolder.exists()) {
				recieveFolder.mkdirs();
			}
			
			recieveFile = new File(rootPath + File.separator + fileName);
			if (recieveFile.exists()) {
				recieveFile.delete();
			}
			
			fileOutPutStream = new FileOutputStream(recieveFile);
			while (transFlag) {
				int bufSize = this.transBufferSize;
				if (fileSize <= this.transBufferSize) {
					bufSize = Integer.parseInt(Long.toString(fileSize));
					transFlag = false;
				}
				byte[] buf = new byte[bufSize];
				this.dis.readFully(buf);
				fileOutPutStream.write(buf);
				fileSize -= this.transBufferSize;
			}
			long endTime = System.currentTimeMillis();
			log.info("|传输平台|接收文件|" + recieveFile.getAbsolutePath() + "成功!耗时:"+(endTime-startTime));
			return true;
		} catch (IOException e) {
			// 传输出现异常删除异常的文件
			log.error("|传输平台|接收文件|" + recieveFile.getAbsolutePath() + "失败了!",e);
			return false;
		} finally {
			try {
				fileOutPutStream.flush();
				fileOutPutStream.close();
			} catch (IOException e) {
				log.error("|传输平台|接收文件|流fileOutPutStream关闭异常!",e);
			}
		}
	}

}