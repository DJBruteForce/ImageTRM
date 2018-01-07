package com.sunyard.insurance.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpClientPool
{
  private static final Logger loger = Logger.getLogger(FtpClientPool.class);

  public static FTPClient getFTPClient(String hostname, int port)
    throws Exception
  {
    FTPClient ftpClient = new FTPClient();
    try {
      ftpClient.connect(hostname, port);
      ftpClient.enterLocalPassiveMode();
      int reply = ftpClient.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftpClient.disconnect();
        loger.error("FTP server [" + hostname + "] refused connection.");
      }
    } catch (Exception e) {
      loger.error("连接FTP服务器[" + hostname + "]，端口[" + port + "]异常!", e);
      if (ftpClient.isConnected()) {
        ftpClient.disconnect();
      }
      throw new Exception("连接FTP服务器[" + hostname + "]，端口[" + port + 
        "]异常!", e);
    }

    return ftpClient;
  }

  public static boolean loginFtp(FTPClient ftpClient, String username, String password)
    throws Exception
  {
    boolean flag = false;
    try {
      if (ftpClient.isConnected()) {
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode();
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
          ftpClient.disconnect();
          loger.error("FTP服务器拒绝登录,请检查登录用户名和密码是否正确.");
        } else {
          flag = true;
        }
      } else {
        loger.error("登录FTP服务器失败，未连接到FTP服务器，请先建立到FTP服务器的连接后再登录");
      }
    } catch (Exception e) {
      loger.error("用户[" + username + "]登录FTP服务器异常!", e);
      throw new Exception("用户[" + username + "]登录FTP服务器异常!", e);
    }
    return flag;
  }

  public static boolean logoutFtp(FTPClient ftpClient)
    throws Exception
  {
    boolean flag = false;
    try {
      flag = ftpClient.logout();
    } catch (Exception e) {
      loger.error("注销FTP登录异常!", e);
      throw new Exception("注销FTP登录异常!", e);
    }

    return flag;
  }

  public static void disconnectFtp(FTPClient ftpClient)
    throws Exception
  {
    try
    {
      if (ftpClient.isConnected())
        ftpClient.disconnect();
    }
    catch (Exception e) {
      loger.error("FTPClient断开连接异常!", e);
      throw new Exception("FTPClient断开连接异常!", e);
    }
  }

  public static String currentDirectory(FTPClient ftpClient)
    throws Exception
  {
    try
    {
      return ftpClient.printWorkingDirectory();
    } catch (Exception e) {
      loger.error("FTPClient获取当前工作目录路径异常!", e);
      throw new Exception("FTPClient获取当前工作目录路径异常!", e);
    }
  }

  public static boolean changeDirectory(FTPClient ftpClient, String newPath)
    throws Exception
  {
    boolean flag = false;
    try {
      flag = ftpClient.changeWorkingDirectory(newPath);
    } catch (Exception e) {
      loger.error("切换FTP工作目录异常!", e);
      throw new Exception("切换FTP工作目录异常!", e);
    }

    return flag;
  }

  public static boolean deleteFile(FTPClient ftpClient, String fileName)
    throws Exception
  {
    boolean flag = false;
    try {
      flag = ftpClient.deleteFile(fileName);
      if (flag)
        loger.info("删除FTP服务器文件[" + fileName + "]成功");
      else
        loger.info("删除FTP服务器文件[" + fileName + "]失败，文件不存在或无权限删除文件");
    }
    catch (Exception e) {
      loger.error("删除FTP服务器上文件[" + fileName + "]异常!", e);
      throw new Exception("删除FTP服务器上文件[" + fileName + "]异常!", e);
    }

    return flag;
  }

  public static boolean deleteDirectory(FTPClient ftpClient, String folderName)
    throws Exception
  {
    boolean flag = false;
    try {
      flag = ftpClient.removeDirectory(folderName);
      if (flag)
        loger.info("删除FTP服务器目录[" + folderName + "]成功");
      else
        loger.info("删除FTP服务器目录[" + folderName + "]失败，目录不存在或无权限删除目录");
    }
    catch (Exception e) {
      loger.error("删除FTP服务器上目录[" + folderName + "]异常!", e);
      throw new Exception("删除FTP服务器上目录[" + folderName + "]异常!", e);
    }

    return flag;
  }

  public static void createDirectory(FTPClient ftpClient, String newfolder)
    throws Exception
  {
    try
    {
      String[] paths = newfolder.split("/");
      boolean flag = false;
      for (String path : paths)
      {
        if ((path != null) && (!"".equals(path))) {
          flag = changeDirectory(ftpClient, path);
          if (!flag) {
            ftpClient.makeDirectory(path);

            changeDirectory(ftpClient, path);
          }
        }
      }
      flag = changeDirectory(ftpClient, "/");
    }
    catch (Exception e) {
      loger.error("在FTP服务器上创建目录[" + newfolder + "]异常!", e);
      throw new Exception("在FTP服务器上创建目录[" + newfolder + "]异常!", e);
    }
  }

  public static List<FTPFile> getFileList(FTPClient ftpClient)
    throws Exception
  {
    List list = new ArrayList();
    FTPFile[] ftpFiles = (FTPFile[])null;
    try {
      ftpFiles = ftpClient.listFiles();
      if ((ftpFiles != null) && (ftpFiles.length > 0)) {
        for (int i = 0; i < ftpFiles.length; i++) {
          if (ftpFiles[i].isFile()) {
            list.add(ftpFiles[i]);
          }
        }
      }
      return list;
    } catch (Exception e) {
      loger.error("获取FTP服务器上当前工作目录下的所有文件异常!", e);
      throw new Exception("获取FTP服务器上当前工作目录下的所有文件异常!", e);
    }
  }

  public static List<FTPFile> getFileList(FTPClient ftpClient, String dir)
    throws Exception
  {
    List list = new ArrayList();
    try {
      ftpClient.changeWorkingDirectory(dir);
      list = getFileList(ftpClient);
      return list;
    } catch (Exception e) {
      loger.error("获取FTP服务器目录[" + dir + "]下的所有文件异常!", e);
      throw new Exception("获取FTP服务器目录[" + dir + "]下的所有文件异常!", e);
    }
  }

  public static boolean uploadFile(FTPClient ftpClient, String filePath, String ftpPath)
    throws Exception
  {
    boolean flag = false;
    FileInputStream fis = null;
    try {
      if ((!"".equals(filePath)) && (filePath != null) && (!"".equals(ftpPath)) && 
        (ftpPath != null)) {
        File file = new File(filePath);
        if (!"Thumbs.db".equals(file.getName())) {
          fis = new FileInputStream(filePath);
          ftpClient.setBufferSize(1024);
          ftpClient.setControlEncoding("GBK");

          ftpClient.setFileType(2);
          flag = ftpClient.storeFile(file.getName(), fis);
          if (flag)
            loger.info("上传文件[" + filePath + "]到FTP服务器成功!");
          else
            loger.error("上传文件[" + filePath + "]到FTP服务器成功!");
        }
        else {
          flag = true;
        }
      } else {
        if ((filePath == null) || ("".equals(filePath))) {
          loger.info("上传文件的本地路径为空[" + filePath + "]");
        }
        if ((ftpPath == null) || ("".equals(ftpPath)))
          loger.info("上传到FTP服务器的目录路径为空[" + ftpPath + "]");
      }
    }
    catch (Exception e) {
      loger.error("文件[" + filePath + "]上传到FTP服务器异常！", e);
      try
      {
        if (fis != null) {
          fis.close();
        }
      }catch (IOException e1) {
        loger.error("关闭文件流异常！", e1);
      }
    }
    finally
    {
      try
      {
        if (fis != null)
          fis.close();
      }
      catch (IOException e) {
        loger.error("关闭文件流异常！", e);
      }
    }

    return flag;
  }

  public static boolean uploadDir(FTPClient ftpClient, String dir, String ftpPath)
  {
    boolean flag = false;
    try {
      if ((!"".equals(dir)) && (dir != null) && (!"".equals(ftpPath)) && 
        (ftpPath != null)) {
        File folder = new File(dir);
        String folderName = folder.getName();
        File[] files = folder.listFiles();
        if ((files != null) && (files.length > 0)) {
          int i = 0;
          for (i = 0; i < files.length; i++) {
            boolean result = uploadFile(ftpClient, 
              files[i].getAbsolutePath(), ftpPath + "/" + 
              folderName);
            if (!result) {
              System.out.println("上传文件[" + 
                files[i].getAbsolutePath() + 
                "]到FTP服务器失败，停止后续文件的上传");
              break;
            }
            if (!"Thumbs.db".equals(files[i].getName())) {
              loger.info("文件[" + files[i].getAbsolutePath() + 
                "]上传到FTP服务器目录[" + ftpPath + 
                File.separator + folderName + "]成功");
            }
          }

          if (i == files.length) {
            flag = true;
            loger.info("本地目录[" + dir + "]下所有文件上传到FTP服务器目录[" + 
              ftpPath + File.separator + folderName + "]成功");
          }
        } else {
          loger.info("本地上传目录中不存在上传到FTP服务器的文件");
        }
      } else {
        if ((dir == null) || ("".equals(dir))) {
          loger.info("上传到FTP的本地目录路径为空");
        }
        if ((ftpPath == null) || ("".equals(ftpPath)))
          loger.info("上传到FTP服务器的服务器目录路径为空");
      }
    }
    catch (Exception e) {
      flag = false;
      loger.error("上传本地目录[" + dir + "]中的文件到FTP服务器异常！", e);
    }
    return flag;
  }

  public static boolean downloadFile(FTPClient ftpClient, String localFile, String ftpFile)
  {
    boolean flag = false;
    FileOutputStream fos = null;
    try {
      if ((!"".equals(localFile)) && (localFile != null) && 
        (!"".equals(ftpFile)) && (ftpFile != null)) {
        File file = new File(localFile);
        if (!file.exists()) {
          File parentFile = file.getParentFile();
          if (!parentFile.exists()) {
            parentFile.mkdirs();
          }
          fos = new FileOutputStream(localFile);
          ftpClient.setFileType(2);
          flag = ftpClient.retrieveFile(ftpFile, fos);
        } else {
          flag = true;
        }
      } else {
        loger.error("下载FTP服务的路径为空，或本地存放路径为空，请检查！");
      }
    } catch (Exception e) {
      loger.error(
        "下载FTP上的文件[" + ftpFile + "]到本地文件[" + localFile + "]异常！", e);
      try
      {
        if (fos != null)
          fos.close();
      } catch (IOException e1) {
        loger.error("关闭文件流异常！", e1);
      }
    }
    finally
    {
      try
      {
        if (fos != null)
          fos.close();
      }
      catch (IOException e) {
        loger.error("关闭文件流异常！", e);
      }
    }
    return flag;
  }

  public static boolean downloadDir(FTPClient ftpClient, String localDir, String ftpPath)
  {
    boolean flag = false;
    try {
      if ((!"".equals(localDir)) && (localDir != null) && (!"".equals(ftpPath)) && 
        (ftpPath != null)) {
        File dirFile = new File(localDir);
        if (!dirFile.exists()) {
          dirFile.mkdirs();
        }
        ftpClient.changeWorkingDirectory(ftpPath);
        String workingDir = ftpClient.printWorkingDirectory();
        String folderName = workingDir.substring(workingDir
          .lastIndexOf("/") + 1);

        List ftpFileList = getFileList(ftpClient);
        if ((ftpFileList != null) && (ftpFileList.size() > 0)) {
          int i = 0;
          for (i = 0; i < ftpFileList.size(); i++) {
            FTPFile tempFtpFile = (FTPFile)ftpFileList.get(i);
            String ftpFileName = tempFtpFile.getName();
            if (!"Thumbs.db".equals(ftpFileName)) {
              String localFile = localDir + File.separator + 
                ftpFileName;
              boolean result = downloadFile(ftpClient, localFile, 
                ftpFileName);
              if (!result) {
                loger.info("FTP文件[" + ftpPath + File.separator + 
                  ftpFileName + "]下载到本地目录[" + localDir + 
                  "]失败");
                break;
              }
              loger.info("FTP文件[" + ftpPath + File.separator + 
                ftpFileName + "]下载到本地目录[" + localDir + 
                "]成功");
            }
          }

          if (i == ftpFileList.size()) {
            flag = true;
            loger.info("下载FTP服务器目录[" + ftpPath + "]下所有文件到本地目录[" + 
              localDir + "]成功");
          }
        } else {
          loger.info("FTP服务器目录[" + ftpPath + "]中不存在任何文件");
        }
      } else {
        loger.error("下载FTP服务器的目录路径为空，或本地存放目录路径为空，请检查！");
      }
    } catch (Exception e) {
      loger.error("下载FTP服务器的目录[" + ftpPath + "]下所有文件异常！");
    }
    return flag;
  }

  public static void main(String[] args)
  {
    try {
      FTPClient ftpClient = getFTPClient("172.29.2.61", 21);
      boolean loginFlag = loginFtp(ftpClient, "admin", "admin");
      System.out.println("登录状态：" + loginFlag);

      System.out.println("创建目录：/images/TRMTP1/GIS_C_08/2013/11/23/40/");
      createDirectory(ftpClient, "/images/TRMTP1/GIS_C_08/2013/11/23/40/");

      boolean logoutFlag = logoutFtp(ftpClient);
      System.out.println("注销状态：" + logoutFlag);

      disconnectFtp(ftpClient);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
