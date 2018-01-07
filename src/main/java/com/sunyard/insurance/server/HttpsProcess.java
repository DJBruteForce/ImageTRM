package com.sunyard.insurance.server;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;

import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.socketServer.TransService;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.util.NumberUtil;
import com.sunyard.insurance.util.ZipUtil;


public class HttpsProcess extends HttpServlet {

	private static final long serialVersionUID = -3427757631899450598L;
	private static final Logger loger = Logger.getLogger(HttpsProcess.class);
	private static String USER = "sunyard";
	private static String PASSWORD = "admin123";
	private static String SUCCESS = "0000";
	private static String UPLOAD_ERR = "0001";
	private static String LOGON_USER_PASSWORD_ERR = "0002";
	private static String EXCEPTION_ERR = "0003"; 

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4*1024) ;         
        ServletFileUpload upload = new ServletFileUpload(factory);         
        upload.setSizeMax(100*1024*1024);          
        String batchTemPath = "";
        try {
	        List<FileItem> list = upload.parseRequest(request) ;
	        Iterator<FileItem> iter = list.iterator();
	        File zipTempfile = null;
	        String name = "";
	        String[] arrString = (String[])null;
	        
	        //缓存根路径
	        batchTemPath = GlobalVar.tempDir + File.separator + "scan"+ File.separator + DateUtil.getDateStrCompact()
	        		+ File.separator+NumberUtil.getRandomNum(10)
	        		+ File.separator+NumberUtil.getRandomNum(10);
	        File tempFile = new File(batchTemPath);
	        if (!tempFile.exists()) {
	        	tempFile.mkdirs();
	        }
	        long startTime = System.currentTimeMillis();
	        while(iter.hasNext()){ 
	        	FileItem item = iter.next();
	        	if(item.isFormField()) {
	        		String fieldValue = new String(item.getString().getBytes("ISO8859_1"), "utf-8");
	        	
	                Hex hex = new Hex();
	                byte[] hFieldValue = (byte[])hex.decode(fieldValue);
	                fieldValue = new String(hFieldValue);
	                System.out.println("HTTP上传:"+fieldValue);
	                Base64 base64 = new Base64();
	                byte[] dFieldValue = (byte[]) base64.decode(hFieldValue);
	                fieldValue = new String(dFieldValue);
	                
	                arrString = fieldValue.split(",");
	                String user = "";
	                String password = "";
	                if (arrString.length > 0) {
	                  if ((arrString[3] != null) || (arrString[4] != null)) {
	                	user = arrString[3];
	                    password = arrString[4];
	                  } else {
	                    throw new Exception("filed value is err ,pls check it then try again");
	                  }
	                }
	                if (!user.equalsIgnoreCase(USER) || !password.equalsIgnoreCase(PASSWORD)) {
	                	response.setHeader("retcode", LOGON_USER_PASSWORD_ERR);
					}
	        	} else {
	        		//接收zip文件 
		        	name = item.getName();
		        	zipTempfile = new File(batchTemPath+File.separator+name);
					item.write(zipTempfile);
	        	}
	        }
	        //zip文件的解压的路径
	        File batchTemFile  =new File(batchTemPath+File.separator+UUID.randomUUID().toString());
			if (!batchTemFile.exists()) {
				batchTemFile.mkdirs();
			}
			//解压zip文件
	        boolean b = ZipUtil.unZip(zipTempfile.getAbsolutePath(), batchTemFile.getAbsolutePath());
	        if (b) {
	        	//所有文件数量
	        	int count = batchTemFile.list().length;
	        	//上传批次
	        	String result = TransService.endBatchFile(name.substring(0, name.indexOf("."))+","+count,batchTemFile.getAbsolutePath(), "SunTXM003");
	        	if (result.equals("2")) {
	        		response.setHeader("retcode", SUCCESS);
	        		zipTempfile.delete();
	        	}else {
	        		response.setHeader("retcode", UPLOAD_ERR);
	        	}
			}
	        loger.warn("HTTP传输["+batchTemPath+"]耗时:"+(System.currentTimeMillis()- startTime));
        } catch (Exception e) {
        	loger.error("HTTP同步上传["+batchTemPath+"]过程异常",e);
			response.setHeader("retcode", EXCEPTION_ERR);
		}
        
	}
}
