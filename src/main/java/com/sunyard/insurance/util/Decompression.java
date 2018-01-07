package com.sunyard.insurance.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompression {
	private static final int BUFFER = 2048;
	
	
	
	/**
	 * 解压文件
	 * @param zipFilePath
	 * @param outputDirectory
	 * @return
	 * @throws IOException
	 */
	public static void unZip(String zipFilePath, String outputDirectory,String batchID)
			throws IOException,Exception{
		long startTime = System.currentTimeMillis();
		createDir(outputDirectory);   //创建输出目录
		FileInputStream fis = new FileInputStream(zipFilePath);
		CheckedInputStream cis = new CheckedInputStream(fis,new  CRC32());
		BufferedInputStream bis = new BufferedInputStream(cis,BUFFER);
		ZipInputStream zis = new ZipInputStream(bis);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipEntry zipEntry = null;
		try{
			while ((zipEntry = zis.getNextEntry()) != null) {
//				System.out.println("正在解压   " + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator+ name);
					f.mkdir();
				} else {
					File file = new File(outputDirectory + File.separator + zipEntry.getName());
					if(!file.exists()){
						fos = new FileOutputStream(file);
						bos = new BufferedOutputStream(fos,BUFFER);
						
						int count = -1;
						byte[] data = new byte[BUFFER];
						while ((count = zis.read(data)) != -1)
							bos.write(data,0,count);
						bos.flush();
						bos.close();
						bos = null;
						fos.flush();
						fos.close();
						fos = null;
					}
				}
			}
			System.out.println("批次："+batchID+"，解压完成，用时："+ (System.currentTimeMillis() - startTime) + "毫秒");
		}catch(Exception ex){
			throw new Exception("批次："+batchID+"解压批次失败");
		}finally{
			if(fis!=null){
				fis.close();
				fis=null;
			}
			if(cis!=null){
				cis.close();
				cis=null;
			}
			if(bis!=null){
				bis.close();
				bis=null;
			}
			if(zis!=null){
				zis.close();
				zis=null;
			}
			if(fos!=null){
				fos.flush();
				fos.close();
				fos=null;
			}
			if(bos!=null){
				bos.flush();
				bos.close();
				bos=null;
			}
		}
	}
	
	/**
	 * 解压文件
	 * @param zipFilePath
	 * @param outputDirectory
	 * @return
	 * @throws IOException
	 */
	public static void unZipByte(byte[] zipByte, String outputDirectory)
			throws IOException,Exception{
		long startTime = System.currentTimeMillis();
		createDir(outputDirectory);   //创建输出目录
		InputStream fis = new ByteArrayInputStream(zipByte);
		CheckedInputStream cis = new CheckedInputStream(fis,new  CRC32());
		BufferedInputStream bis = new BufferedInputStream(cis,BUFFER);
		ZipInputStream zis = new ZipInputStream(bis);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipEntry zipEntry = null;
		try{
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator+ name);
					f.mkdir();
				} else {
					File file = new File(outputDirectory + File.separator + zipEntry.getName());
					if(!file.exists()){
						fos = new FileOutputStream(file);
						bos = new BufferedOutputStream(fos,BUFFER);
						
						int count = -1;
						byte[] data = new byte[BUFFER];
						while ((count = zis.read(data)) != -1)
							bos.write(data,0,count);
						bos.flush();
						bos.close();
						bos = null;
						fos.flush();
						fos.close();
						fos = null;
					}
				}
			}
			System.out.println("解压文件到["+outputDirectory+"]完成，用时："+ (System.currentTimeMillis() - startTime) + "毫秒");
		}catch(Exception ex){
			throw new Exception("ZIP文件解压失败",ex);
		}finally{
			if(fis!=null){
				fis.close();
				fis=null;
			}
			if(cis!=null){
				cis.close();
				cis=null;
			}
			if(bis!=null){
				bis.close();
				bis=null;
			}
			if(zis!=null){
				zis.close();
				zis=null;
			}
			if(fos!=null){
				fos.flush();
				fos.close();
				fos=null;
			}
			if(bos!=null){
				bos.flush();
				bos.close();
				bos=null;
			}
			zipByte = null;
		}
	}
	
	
	
	private static void createDir(String path){
		File dir = new File(path);
		if(!dir.exists()){
			dir.mkdirs();
		}
	}
	
	public static void main(String[] args) {
		
	}
}