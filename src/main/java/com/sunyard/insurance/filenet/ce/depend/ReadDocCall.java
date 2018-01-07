package com.sunyard.insurance.filenet.ce.depend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import com.sunyard.insurance.util.ZipUtil;

public class ReadDocCall implements Callable<Object> {
	
	private InputStream in = null;
	private File file = null;
	private static final Logger log = Logger.getLogger(ReadDocCall.class);
	
	public ReadDocCall (InputStream in, File file) {
		this.in = in;
		this.file = file;
	}
	
	public Object call() throws Exception {
		OutputStream os = null;
		try {
			//文件已存在就直接删除
//			if (null != file && file.exists() && file.isFile()) {
//				FileUtils.forceDelete(file);
//				file.createNewFile();
//			}
			if(!file.exists()) {
				if(null != in) { 
					os = new FileOutputStream(file);
					byte[] data = new byte[4*1024];
					int len = 0;
					while((len = in.read(data))!=-1) {
						os.write(data, 0, len);
					}
				}
			}
			
			//下载后的文件是ZIP压缩包则，解压其中的文件
			if(file.getName().toUpperCase().endsWith(".ZIP") && (file.getName().toUpperCase().indexOf("PART") > -1)) {
				ZipUtil.unZip(file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
				//解压zip文件
				log.info("下载CM部件["+file.getAbsolutePath()+"]为ZIP类型文件,解压完成!");
			}
			
			return null;
		} catch (Exception e) {
			log.error("从CE中下载影像文件到["+file.getAbsolutePath()+"]失败!");
			throw e;
		} finally {
			if(null != os) {
				os.flush();
				os.close();
				os = null;
			}
			if(null != in) {
				in.close();
				in = null;
			}
		}
	}

}
