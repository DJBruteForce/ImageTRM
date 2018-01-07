package com.test.cmapi;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomSkepFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RandomAccessFile raf = null;
		RandomAccessFile raf2 = null;
		
		try {
			raf = new RandomAccessFile("E:/防火墙策略申请表-访问生产应用-文档集中作业 .xls", "r");
			raf2 = new RandomAccessFile("E:/防火墙策略申请表-访问生产应用-文档集中作业2 .xls", "rw");
			
			raf.seek(16192);
			raf2.seek(16192);
			
			long fileSize = raf.length();
			
			int bufSize = 8096;
			boolean flag = true;
			while(flag) {
				System.out.println("==raf=="+raf.getFilePointer());
				System.out.println("==raf2=="+raf2.getFilePointer());
				
//				if(raf2.getFilePointer()>16096) {
//					System.out.println("===="+raf2.getFilePointer());
//					throw new Exception("异常中断");
//				}
				
    			if((fileSize - raf.getFilePointer())<=bufSize) {
    				bufSize = (int) (fileSize - raf.getFilePointer());
    				flag = false;
    			}
    			
    			int read = 0;
    			byte[] buf = new byte[bufSize];
    			read = raf.read(buf);	
    			if (-1 == read) {
                    break;
                }
    			raf2.write(buf, 0, read);
    		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				raf2.close();
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		

	}

}
