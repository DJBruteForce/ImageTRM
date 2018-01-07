package com.sunyard.insurance.socket.ServerImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.socket.bean.BatchCheckBean;
import com.sunyard.insurance.socket.bean.BatchCheckResBean;
import com.sunyard.insurance.socket.bean.BatchStartBean;
import com.sunyard.insurance.socket.bean.BatchStartResBean;
import com.sunyard.insurance.socket.bean.CheckUserBean;
import com.sunyard.insurance.socket.bean.CheckUserResBean;
import com.sunyard.insurance.socket.bean.FileCheckBean;
import com.sunyard.insurance.socket.bean.FileCheckResBean;
import com.sunyard.insurance.socket.bean.SocketXmlUtil;
import com.sunyard.insurance.socket.bean.TranceFileBean;
import com.sunyard.insurance.socket.bean.TranceFileResBean;
import com.sunyard.insurance.socket.bean.TranceServiceCode;
import com.sunyard.insurance.util.DateUtil;


public class SocketServerImpl implements Runnable, Serializable {
	
	private static final long serialVersionUID = -4272254313531070044L;
	private static final Logger logger = Logger.getLogger(SocketServerImpl.class);
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private DataInputStream dis = null;
	private String clientIp = "";
	private static final String BYE = "bye";
	private static final String HI = "hi";
	private static final String SUCCESS = "SUCCESS";
	
	public SocketServerImpl(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			this.dis = new DataInputStream(this.socket.getInputStream());
//		this.dis = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
			this.in = new BufferedReader(new InputStreamReader(this.socket
					.getInputStream(),"GBK"));
			this.out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(this.socket.getOutputStream(), "GBK")),
					true);
			this.socket.setSoTimeout(GlobalVar.socketSoTimeout);
			this.socket.setReceiveBufferSize(GlobalVar.recvsocketbuffersize);
			this.socket.setSendBufferSize(GlobalVar.sendsocketbuffersize);
			InetAddress addr = socket.getInetAddress();
			clientIp = addr.getHostAddress();
			String batchTemPath = GlobalVar.tempDir + File.separator + "scan"
					+ File.separator + DateUtil.getDateStrCompact();
			TranceFileBean tranceFileBean = null;
			TranceFileResBean tranceFileResBean = null;
			
			boolean isAlive = true;
			while (isAlive) {
				String xmlStr = this.in.readLine();
				System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]输入:["+xmlStr+"]");
				if(StringUtils.isEmpty(xmlStr)) {
					isAlive = false;
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]交易内容为空,跳出socket交易!");
					continue;
				}
				
				if(BYE.equalsIgnoreCase(xmlStr)) {
					isAlive = false;
					this.out.println("bye");
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求终止传输交易,跳出socket交易!");
					continue;
				}
				
				if(HI.equalsIgnoreCase(xmlStr)) {
					this.out.println("im here!");
					continue;
				}
				
				String serviceCode = SocketXmlUtil.getServiceCode(xmlStr);
				if(TranceServiceCode.CHECK_USER.toString().equals(serviceCode)) {
					CheckUserBean checkUserBean = SocketXmlUtil.getCheckUserBean(xmlStr);
					CheckUserResBean checkUserResBean = SocketTranceUtil.checkUser(checkUserBean);
					String resultStr = SocketXmlUtil.getCheckUserResBeanXML(checkUserResBean);
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端返回客户端字符串:["+resultStr+"]");
					this.out.println(resultStr);
					if(!SUCCESS.equals(checkUserResBean.getRESCODE())) {
						//交易出现问题及时关闭交易连接
						isAlive = false;
					}
				} else if(TranceServiceCode.BATCH_START.toString().equals(serviceCode)) {
					BatchStartBean batchStartBean = SocketXmlUtil.getBatchStartBean(xmlStr);
					BatchStartResBean batchStartResBean = SocketTranceUtil.batchStart(batchStartBean);
					String resultStr = SocketXmlUtil.getBatchStartResBeanXML(batchStartResBean);
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端返回客户端字符串:["+resultStr+"]");
					//指定临时存储目录
					batchTemPath = batchTemPath + File.separator
							+ batchStartBean.getBATCHID().substring(0, 1) + File.separator
							+ batchStartBean.getBATCHID().substring(1, 2) + File.separator
							+ batchStartBean.getBATCHID()+"_"+batchStartBean.getBATCHVER();
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端临时存储目录为:["+batchTemPath+"]");
					this.out.println(resultStr);
					if(!SUCCESS.equals(batchStartResBean.getRESCODE())) {
						//交易出现问题及时关闭交易连接
						isAlive = false;
					}
				} else if(TranceServiceCode.TRANCE_FILE.toString().equals(serviceCode)) {
					tranceFileBean = SocketXmlUtil.getTranceFileBean(xmlStr);
					tranceFileResBean = SocketTranceUtil.tranceFile(batchTemPath, tranceFileBean);
					String resultStr = SocketXmlUtil.getTranceFileResBeanXML(tranceFileResBean);
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端返回客户端字符串:["+resultStr+"]");
					this.out.println(resultStr);
					//只有MD5不一致才需要接收文件流
					if(!(tranceFileResBean.getMD5STRNOW().toUpperCase()).equals(tranceFileBean.getMD5STR().toUpperCase())) {
						//进入接收文件流的方法
						recieveFile(batchTemPath,tranceFileBean,tranceFileResBean);
					}
				} else if(TranceServiceCode.FILE_CHECK.toString().equals(serviceCode)) {
					FileCheckBean fileCheckBean = SocketXmlUtil.getFileCheckBean(xmlStr);
					FileCheckResBean fileCheckResBean = SocketTranceUtil.fileCheck(batchTemPath, fileCheckBean);
					String resultStr = SocketXmlUtil.getFileCheckResBeanXML(fileCheckResBean);
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端返回客户端字符串:["+resultStr+"]");
					this.out.println(resultStr);
					if(!SUCCESS.equals(fileCheckResBean.getRESCODE())) {
						//交易出现问题及时关闭交易连接
						isAlive = false;
					}
				} else if(TranceServiceCode.BATCH_CHECK.toString().equals(serviceCode)) {
					BatchCheckBean batchCheckBean = SocketXmlUtil.getBatchCheckBean(xmlStr);
					BatchCheckResBean batchCheckResBean = SocketTranceUtil.BatchCheck(batchTemPath, batchCheckBean);
					String resultStr = SocketXmlUtil.getBatchCheckResBeanXML(batchCheckResBean);
					System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]请求交易类型:["+serviceCode+"]服务端返回客户端字符串:["+resultStr+"]");
					this.out.println(resultStr);
					isAlive = false;
				}
			}
			
		} catch (Exception e) {
			logger.error("|传输平台|Socket|传输发生异常!",e);
		} finally {
			try {
				if(null != this.dis) {
					this.dis.close();
				}
				if(null != this.out) {
					this.out.close();
				}
				if(null != this.in) {
					this.in.close();
				}
				if(null != this.socket) {
					this.socket.close();
				}
			} catch (IOException e) {
				logger.error("|传输平台|Socket|客户端IP["+clientIp+"]资源关闭失败了!",e);
			}
			System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]传输交易结束!");
		}
	}
	
	public void recieveFile_bak(String rootPath,TranceFileBean tranceFileBean, TranceFileResBean tranceFileResBean) {
		long startTime = System.currentTimeMillis();//RandomAccessFile
		FileOutputStream fos = null;
		long fileSize = tranceFileBean.getFILESIZE();
		File recieveFile = new File(rootPath+File.separator+tranceFileResBean.getFILENAME());
		try {
			//检查根目录文件夹是否存在
			File recieveFolder = new File(rootPath);
			if (!recieveFolder.exists()) {
				recieveFolder.mkdirs();
			}
			
			fos = new FileOutputStream(recieveFile);
			
			int bufSize = Integer.parseInt(tranceFileResBean.getCATCHSIZE());
			
			boolean flag = true;
			while (flag) {
				if (fileSize <= bufSize) {
					bufSize = Integer.parseInt(Long.toString(fileSize));
					flag = false;
				}
				
				byte[] buf = new byte[bufSize];
                int read = 0;
                read = dis.read(buf);
                if (-1 == read) {														
                    break;
                }
                fos.write(buf, 0, read);
                
                fileSize -= read;
            }
			System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]接收文件["+recieveFile.getAbsolutePath()+"]成功!耗时："+(System.currentTimeMillis()-startTime));
		} catch (IOException e) {
			logger.error("|传输平台|Socket|客户端IP["+clientIp+"]|接收文件[" + recieveFile.getAbsolutePath() + "]失败了!",e);
		} finally {
			try {
				if(null != fos) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				logger.error("|传输平台|Socket|客户端IP["+clientIp+"]fileOutPutStream关闭异常!",e);
			}
		}
	}
	
	public void recieveFile(String rootPath,TranceFileBean tranceFileBean, TranceFileResBean tranceFileResBean) {
		long startTime = System.currentTimeMillis();//RandomAccessFile
		RandomAccessFile raf = null;
		long fileSize = tranceFileBean.getFILESIZE();
		File recieveFile = new File(rootPath+File.separator+tranceFileResBean.getFILENAME());
		try {
			//检查根目录文件夹是否存在
			File recieveFolder = new File(rootPath);
			if (!recieveFolder.exists()) {
				recieveFolder.mkdirs();
			}
			
			raf = new RandomAccessFile(recieveFile.getAbsolutePath(), "rw");
			
			raf.seek(Long.parseLong(tranceFileResBean.getFILESIZENOW()));
			int bufSize = Integer.parseInt(tranceFileResBean.getCATCHSIZE());//buffer缓冲区大小
			
			boolean flag = true;
			while (flag) {
				
				//缩减缓冲区
				if ((fileSize - raf.getFilePointer())<=bufSize) {
					bufSize = Integer.parseInt(Long.toString(fileSize - raf.getFilePointer()));
				}
				
				byte[] buf = new byte[bufSize];
                int read = 0;
                read = dis.read(buf);
                if (-1 == read) {														
                    break;
                }
                raf.write(buf, 0, read);
                
                //表示全部接受完成
                if((fileSize-raf.getFilePointer())<=0) {
                	flag = false;
                }
                
            }
			System.out.println("|传输平台|Socket|客户端IP["+clientIp+"]接收文件["+recieveFile.getAbsolutePath()+"]成功!耗时："+(System.currentTimeMillis()-startTime));
		} catch (IOException e) {
			logger.error("|传输平台|Socket|客户端IP["+clientIp+"]|接收文件[" + recieveFile.getAbsolutePath() + "]失败了!",e);
		} finally {
			try {
				if(null != raf) {
					raf.close();
					raf = null;
				}
			} catch (IOException e) {
				logger.error("|传输平台|Socket|客户端IP["+clientIp+"]fileOutPutStream关闭异常!",e);
			}
		}
	}
	
	
	public static void main(String[] args) {
		ServerSocket serverListenSocket = null;
		try {
			
			ExecutorService pool = null;
			serverListenSocket = new ServerSocket(8025);
			serverListenSocket.setReuseAddress(true);
			
			pool = Executors.newFixedThreadPool(5);
			while (true) {
				Socket socket = serverListenSocket.accept();
				pool.execute(new SocketServerImpl(socket));
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverListenSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
