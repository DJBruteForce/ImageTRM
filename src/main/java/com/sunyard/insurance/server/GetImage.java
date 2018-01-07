package com.sunyard.insurance.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import com.Ostermiller.util.Base64;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.ftp.FtpClientPool;
import com.sunyard.insurance.util.DateUtil;

public class GetImage extends HttpServlet {

	private static final long serialVersionUID = -3261966543308712586L;
	private static final Logger loger = Logger.getLogger(GetImage.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 请求参数
		String queryString = request.getQueryString();
		// 参数解码
		String queryDecode = Base64.decode(queryString);
		loger.debug("资源请求:" + queryDecode);
		String dateStr = "";
		String file_name = "";
		String file_encrypt = "";
		try {			
		    String[] code = queryDecode.split("&");
		    dateStr = code[0].split("=")[1];
		    file_name = code[1].split("=")[1];
		    file_encrypt = code.length > 2 ? code[2].split("=")[1] : "0";
		    
	        String safeFilePath = "";
	        if (("".equals(GlobalSafeVar.SAFE_FILE_PATH)) || (null == GlobalSafeVar.SAFE_FILE_PATH)) {
	        	GlobalSafeVar.loadConfigFile();
	        }
	        safeFilePath = GlobalSafeVar.SAFE_FILE_PATH;
	        if (!file_name.startsWith(safeFilePath)) {
	        	loger.info("无权限访问该路径下的文件，请检查！");
	        	return;
	        }
		} catch (Exception e1) {
		    loger.error("请求的参数：" + queryDecode + ",queryDecode.split==" + queryDecode.split("&").length + ",dateStr:" + dateStr + ",file_name:" + file_name + ",file_encrypt:" + file_encrypt, e1);
		}

		/* 太平个性化，外网TRM查询不能直接访问存储，需通过FTP，因此增设外网本地缓存目录 */
		// 1.获取批次的批次号，难点在于批次号的解析，因为传递进来的参数没有批次号。获取批次号可以取用字符串最后一个"/"和倒数第二个"/"之间的字符串，然后去掉下划线加版本号就是批次号了
		// 2.判断FTP开关是否开启
		// 3.FTP开关关闭，为内网TRM查询，直接查询USM界面配置的缓存目录即可。
		// 4.FTP开关开启，为外网TRM查询。
		// 	  4.1.首先去增设的缓存目录查找该批次，如该批次存在，则直接从增设的缓存目录取影像。
		//    4.2.如该批次不存在，首先去FTP下载该批次到增设的缓存目录，再从增设的缓存目录取影像。
		if(GlobalVar.isFTPOpen == 1) {
			System.out.println("原请求文件路径 = " + file_name);
			String ftpFilePath = file_name.replace(GlobalVar.RealRootPath, "");
			File ftpFile = new File(ftpFilePath);
			String ftpFolderPath = ftpFile.getParent();
			System.out.println("FTP下载文件目录路径 = " + ftpFolderPath);
//			file_name = file_name.replace(GlobalVar.svrInfoBean.getRootPath(), GlobalVar.extraCacheFolder);
			System.out.println("替换后请求文件路径 = " + file_name);
			File requestFile = new File(file_name);
			if(!requestFile.exists()) {// 若请求的文件不存在，则下载文件到增设的缓存目录
				FTPClient ftpClient = null;
				try {
					String localDir = requestFile.getParent();
					ftpClient = FtpClientPool.getFTPClient(GlobalVar.FTPHost, GlobalVar.FTPPort);
					FtpClientPool.loginFtp(ftpClient, GlobalVar.FTPUsername, GlobalVar.FTPPassword);
					FtpClientPool.downloadDir(ftpClient, localDir, ftpFolderPath);
				} catch (Exception e) {
					loger.error("下载文件过程中出现异常！", e);
				} finally {
					if(ftpClient != null) {
						try {
							FtpClientPool.logoutFtp(ftpClient);
							FtpClientPool.disconnectFtp(ftpClient);
						} catch (Exception e) {
							loger.error("注销FTP服务器异常或断开FTP服务器连接异常！", e);
						}
					}
				}
			}
		}
		
		File file = null;
		FileInputStream in = null;
		BufferedInputStream bin = null;
		RandomAccessFile raf = null;
		ServletOutputStream outStream = null;
		try {
			file = new File(file_name);
			if(file.exists()) {
				String miniType = file_name.substring(file_name.lastIndexOf(".")+ 1).toLowerCase();
				String mime = GlobalVar.mimeHasMap.get(miniType);
				//text/plain
				if(null == mime || "null".equals(mime) || "".equals(mime)) {
					mime = "text/plain";
				}
				
				if(!"image".equals(mime.split("/")[0])) {
					String fileNameStr = java.util.UUID.randomUUID().toString()+"."+miniType;
//					response.setHeader("Content-disposition", "attachment;filename="+fileNameStr);
					response.setHeader("Content-disposition", "filename="+fileNameStr);
				}
				
				response.setContentType(mime);
				response.setCharacterEncoding("GBK");
				outStream = response.getOutputStream();
				if (DateUtil.getDateStrCompact().equals(dateStr)) {
					if("1".equals(file_encrypt)){
						raf = new RandomAccessFile(file, "r");
						if ((raf != null) && (raf.length() >= 4L)) {
							byte[] absBytes = new byte[4];

				            raf.read(absBytes);

				            for (int i = 0; i < absBytes.length; i++) {				            	
				            	if (absBytes[i] == -128) {
				            		continue;
				            	}
				                absBytes[i] = (byte)(0 - absBytes[i]);
				            }
				            outStream.write(absBytes, 0, absBytes.length);

				            raf.seek(4L);
				            byte[] b = new byte[1024];
				            int len = 0;
				            while ((len = raf.read(b)) != -1){
				                outStream.write(b, 0, len);
				            }
						}
					}
					in = new FileInputStream(file);
					bin = new BufferedInputStream(in);
					
					if (bin != null) {
						byte[] b = new byte[1024];
						int len = 0;
						while ((len = bin.read(b)) != -1) {
							outStream.write(b, 0, len);
						}
					}
					/**
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte b[] = new byte[1024];
					while (true) {
						int bytes = bin.read(b);
						if (bytes == -1) {
							break;
						}
						baos.write(b, 0, bytes);
					}
					in.close();
					b = baos.toByteArray();
					
					outStream.write(b, 0, b.length);
					**/
					
				} else {
					String reStr = "file ConnectionTimeout!";
					loger.debug("资源请求超时，URL地址不可用!");
					outStream.write(reStr.getBytes());
				}	
			} else {
				loger.warn("资源请求文件不存在：["+file_name+"]");
			}
		} catch (Exception e) {
			loger.debug("资源请求异常!",e);
			String reStr = "FilePath[" + file.getAbsolutePath() + "]is null";
			outStream.write(reStr.getBytes());
		} finally {
			try {
				if (null != outStream) {
					outStream.flush();
					outStream.close();
				}
				if (null != in) {
					in.close();
				}
				if (null != bin) {
					bin.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
