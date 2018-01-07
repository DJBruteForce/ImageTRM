package com.sunyard.insurance.socketTransClient.util;

import com.sunyard.insurance.socketTransClient.bean.ClientBatchFileBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class TransClientUtil
{
  public static MessageDigest messageDigest = null;
  
  public static String getFileMD5(File file)
    throws Exception
  {
    if (messageDigest == null) {
      messageDigest = MessageDigest.getInstance("MD5");
    }
    FileInputStream in = new FileInputStream(file);
    byte[] buffer = new byte[1048576];
    int len = 0;
    while ((len = in.read(buffer)) > 0) {
      messageDigest.update(buffer, 0, len);
    }
    in.close();
    return toHexString(messageDigest.digest());
  }
  
  public static String getFileMD51(File file)
    throws Exception
  {
    if (messageDigest == null) {
      messageDigest = MessageDigest.getInstance("MD5");
    }
    FileInputStream in = new FileInputStream(file);
    FileChannel ch = in.getChannel();
    MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0L, file.length());
    messageDigest.update(byteBuffer);
    return toHexString(messageDigest.digest());
  }
  
  public static String toHexString(byte[] b)
  {
    StringBuilder sb = new StringBuilder(b.length * 2);
    for (int i = 0; i < b.length; i++)
    {
      sb.append(hexChar[((b[i] & 0xF0) >>> 4)]);
      sb.append(hexChar[(b[i] & 0xF)]);
    }
    return sb.toString();
  }
  
  public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', 
    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static long getFileSize(String filePath)
  {
    File file = new File(filePath);
    long length = file.length();
    return length;
  }
  
  public static List<ClientBatchFileBean> getFolderFile(String folderPth)
    throws Exception
  {
    List<ClientBatchFileBean> BatchFileList = new ArrayList();
    
    File f = new File(folderPth);
    if (f.exists())
    {
      String[] fileList = f.list();
      int size = fileList.length;
      for (int i = 0; i < size; i++)
      {
        File temFile = new File(fileList[i]);
        ClientBatchFileBean batchFileBean = new ClientBatchFileBean();
        batchFileBean.setFileName(temFile.getName());
        long beginTime = System.currentTimeMillis();
        batchFileBean.setFileSize(getFileSize(folderPth + File.separator + temFile.getName()));
        System.out.println("获取文件[" + temFile.getName() + "]大小耗时:" + (System.currentTimeMillis() - beginTime));
        File fm = new File(folderPth + File.separator + temFile.getName());
        beginTime = System.currentTimeMillis();
        batchFileBean.setMd5Code(getFileMD5(fm));
        System.out.println("获取文件[" + fm.getName() + "]MD5码[" + batchFileBean.getMd5Code() + "]耗时:" + (System.currentTimeMillis() - beginTime));
        batchFileBean.setFilePath(folderPth + File.separator + temFile.getName());
        batchFileBean.setSubmitSuccess(false);
        BatchFileList.add(batchFileBean);
      }
    }
    return BatchFileList;
  }
  
  public static void main(String[] args)
  {
    File file = new File("E:/00033-DD9C6227-7AA3-4eca-988B-9EA9C65B0BD3.JPG");
    try
    {
      long beginTime = System.currentTimeMillis();
      String md5Str = getFileMD5(file);
      System.out.println("1获取文件[" + file.getName() + "]MD5码" + md5Str + "耗时:" + (System.currentTimeMillis() - beginTime));
      beginTime = System.currentTimeMillis();
      md5Str = getFileMD51(file);
      System.out.println("2获取文件[" + file.getName() + "]MD5码" + md5Str + "耗时:" + (System.currentTimeMillis() - beginTime));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
