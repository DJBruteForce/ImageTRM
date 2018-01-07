package com.sunyard.insurance.cmapi;

import java.io.File;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKImageICM;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.util.Decompression;
/**
 * 
  * @Title ReadImgCall.java
  * @Package com.sunyard.insurance.cmapi
  * @Description 
  * 
  * @time 2012-8-8 下午03:52:11  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class ReadImgCall implements Callable<Object> {
	private static final Logger log = Logger.getLogger(ReadImgCall.class);
	private DKImageICM image = null;
	private String queryStr = "";
	private String batchCachePath = "";
	private int innerVerCache = 0;
	
	ReadImgCall(DKImageICM image, String queryStr,String batchCachePath, int innerVerCache) {
		this.image = image;
		this.queryStr = queryStr;
		this.batchCachePath = batchCachePath;
		this.innerVerCache = innerVerCache;
	}
	
	public Object call() throws Exception {
		String sourceID = "";
		try{
			StopWatch imgWatch = new Log4JStopWatch();
			long imgTime = System.currentTimeMillis();
			sourceID = (String)image.getData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docPartSourceID));
			String sInnerVer = (String)image.getData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer));
			if(sInnerVer==null || sInnerVer.equals("")){
				sInnerVer="1";
			}
			
			if(Integer.parseInt(sInnerVer)>innerVerCache) {
				log.info("下载部件文件:"+sourceID+"到目录:"+batchCachePath);
				image.getContentToClientFile(batchCachePath+File.separator+sourceID, 1);//文件存在就覆盖
				log.info("获取部件"+queryStr+"，用时："+(System.currentTimeMillis()-imgTime));
				imgWatch.stop("获取部件*");
				
				File file = new File(batchCachePath+File.separator+sourceID);
				//下载后的文件是ZIP压缩包则，解压其中的文件
				if(file.getName().toUpperCase().endsWith(".ZIP") && (file.getName().toUpperCase().indexOf("PART") > -1)) {
//					ZipUtil.unZip(file.getAbsolutePath(), batchCachePath);
					//解压zip文件
					Decompression.unZipByte(image.getContent(), batchCachePath);
					log.info("下载CM部件["+file.getAbsolutePath()+"]为ZIP类型文件,解压完成!");
				}
				
			}
			//释放对象
			image.setNull();
			image=null;
		}catch(Exception ex){
			log.error("从CM获取文件："+sourceID+"，失败：",ex);
			throw ex;
		}
		return null;
	}

	public static void main(String[] args){
		long hqTime = System.currentTimeMillis();
		File file = new File("F:\\cm\\query\\cache\\3d584c6ef7ea456fa040320cad13ae18");
		
		File[] fs = file.listFiles();
		int[] batchVers = new int[1];
		for(int m=0;m<fs.length;m++){
			if(fs[m].getName().endsWith("syd")){
				System.out.println(fs[m].getName());
				String fileName = fs[m].getName();
		    	int start = fileName.indexOf("_");
		    	int end = fileName.indexOf(".");
		    	int batchVer = Integer.parseInt(fileName.substring(start+1,end));
		    	batchVers[0] = batchVer;
			}
		}
		
	    System.out.println(System.currentTimeMillis()-hqTime);
	}
}
