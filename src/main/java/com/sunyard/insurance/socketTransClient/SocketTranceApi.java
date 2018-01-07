package com.sunyard.insurance.socketTransClient;

import com.sunyard.insurance.socket.bean.BatchCheckBean;
import com.sunyard.insurance.socket.bean.BatchCheckResBean;
import com.sunyard.insurance.socket.bean.BatchStartBean;
import com.sunyard.insurance.socket.bean.BatchStartResBean;
import com.sunyard.insurance.socket.bean.CheckUserBean;
import com.sunyard.insurance.socket.bean.CheckUserResBean;
import com.sunyard.insurance.socket.bean.FileBean;
import com.sunyard.insurance.socket.bean.FileCheckBean;
import com.sunyard.insurance.socket.bean.FileCheckResBean;
import com.sunyard.insurance.socket.bean.SocketXmlUtil;
import com.sunyard.insurance.socket.bean.TranceFileBean;
import com.sunyard.insurance.socket.bean.TranceFileResBean;
import com.sunyard.insurance.socket.bean.TranceReqObj;
import com.sunyard.insurance.socket.bean.TranceServiceCode;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchBean;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchFileBean;
import com.sunyard.insurance.socketTransClient.bean.ResultBean;
import com.sunyard.insurance.socketTransClient.util.TransClientUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class SocketTranceApi
{
  private static final Logger logger = Logger.getLogger(SocketTranceApi.class);
  private static final String BYE = "bye";
  
  public static CheckUserResBean checkUser(PrintWriter printWriter, BufferedReader inStream, String ip)
    throws Exception
  {
    String xmlStr = "";
    String resultStr = "";
    
    CheckUserBean checkUserBean = new CheckUserBean(TranceServiceCode.CHECK_USER.toString(), TranceReqObj.SunTRM.toString(), "admin", "admin");
    xmlStr = SocketXmlUtil.getCheckUserBeanXML(checkUserBean);
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]交易类型:[" + TranceServiceCode.CHECK_USER.toString() + "]输出:[" + xmlStr + "]");
    printWriter.println(xmlStr);
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]交易类型:[" + TranceServiceCode.CHECK_USER.toString() + "]服务端返回:[" + resultStr + "]");
    CheckUserResBean checkUserResBean = SocketXmlUtil.getCheckUserResBean(resultStr);
    return checkUserResBean;
  }
  
  public static BatchStartResBean batchStart(PrintWriter printWriter, BufferedReader inStream, ClientBatchBean clientBatchBean, String ip)
    throws Exception
  {
    String xmlStr = "";
    String resultStr = "";
    
    BatchStartBean batchStartBean = new BatchStartBean(TranceServiceCode.BATCH_START.toString(), TranceReqObj.SunTRM.toString(), clientBatchBean.getBatchId(), clientBatchBean.getBatch_ver(), null);
    xmlStr = SocketXmlUtil.getBatchStartBeanXML(batchStartBean);
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_START.toString() + "]输出:[" + xmlStr + "]");
    printWriter.println(xmlStr);
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_START.toString() + "]服务端返回:[" + resultStr + "]");
    BatchStartResBean batchStartResBean = SocketXmlUtil.getBatchStartResBean(resultStr);
    return batchStartResBean;
  }
  
  public static TranceFileResBean tranceFile(PrintWriter printWriter, BufferedReader inStream, ClientBatchBean clientBatchBean, ClientBatchFileBean clientBatchFileBean, String ip)
    throws Exception
  {
    String xmlStr = "";
    String resultStr = "";
    
    TranceFileBean tranceFileBean = new TranceFileBean(TranceServiceCode.TRANCE_FILE.toString(), TranceReqObj.SunTRM.toString(), 
      clientBatchBean.getBatchId(), clientBatchBean.getBatch_ver(), clientBatchFileBean.getFileName(), 
      clientBatchFileBean.getFileSize(), clientBatchFileBean.getMd5Code());
    xmlStr = SocketXmlUtil.getTranceFileBeanXML(tranceFileBean);
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.TRANCE_FILE.toString() + "]输出:[" + xmlStr + "]");
    printWriter.println(xmlStr);
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.TRANCE_FILE.toString() + "]服务端返回:[" + resultStr + "]");
    TranceFileResBean tranceFileResBean = SocketXmlUtil.getTranceFileResBean(resultStr);
    
    return tranceFileResBean;
  }
  
  public static FileCheckResBean fileCheck(PrintWriter printWriter, BufferedReader inStream, ClientBatchBean clientBatchBean, ClientBatchFileBean clientBatchFileBean, String ip)
    throws Exception
  {
    String xmlStr = "";
    String resultStr = "";
    
    FileCheckBean fileCheckBean = new FileCheckBean(TranceServiceCode.FILE_CHECK.toString(), TranceReqObj.SunTRM.toString(), 
      clientBatchBean.getBatchId(), clientBatchBean.getBatch_ver(), clientBatchFileBean.getFileName(), clientBatchFileBean.getMd5Code());
    xmlStr = SocketXmlUtil.getFileCheckBeanXML(fileCheckBean);
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.FILE_CHECK.toString() + "]输出:[" + xmlStr + "]");
    printWriter.println(xmlStr);
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.FILE_CHECK.toString() + "]服务端返回:[" + resultStr + "]");
    FileCheckResBean fileCheckResBean = SocketXmlUtil.getFileCheckResBean(resultStr);
    return fileCheckResBean;
  }
  
  public static BatchCheckResBean batchCheck(PrintWriter printWriter, BufferedReader inStream, ClientBatchBean clientBatchBean, List<FileBean> fileList, TranceReqObj tranceReqObj, String ip)
    throws Exception
  {
    String xmlStr = "";
    String resultStr = "";
    
    BatchCheckBean batchCheckBean = new BatchCheckBean(TranceServiceCode.BATCH_CHECK.toString(), tranceReqObj.toString(), 
      clientBatchBean.getBatchId(), clientBatchBean.getBatch_ver(), fileList);
    xmlStr = SocketXmlUtil.getBatchCheckBeanXML(batchCheckBean);
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_CHECK.toString() + "]输出:[" + xmlStr + "]");
    printWriter.println(xmlStr);
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_CHECK.toString() + "]服务端返回:[" + resultStr + "]");
    BatchCheckResBean batchCheckResBean = SocketXmlUtil.getBatchCheckResBean(resultStr);
    return batchCheckResBean;
  }
  
  public static String bye(PrintWriter printWriter, BufferedReader inStream, String ip)
    throws Exception
  {
    String resultStr = "";
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]交易类型:[结束交易]输出:[" + "bye" + "]");
    printWriter.println("bye");
    resultStr = inStream.readLine();
    System.out.println("|传输JAR包|Socket|服务端IP[" + ip + "]交易类型:[结束交易]服务端返回:[" + resultStr + "]");
    return resultStr;
  }
  
  public static ResultBean submitBatch(ClientBatchBean clientBatchBean, TranceReqObj tranceReqObj, String ip, int port)
    throws Exception
  {
    ResultBean resultBean = new ResultBean();
    Socket socket = null;
    PrintWriter printWriter = null;
    BufferedReader inStream = null;
    DataOutputStream outputStream = null;
    try
    {
      socket = new Socket(ip, port);
      socket.setTrafficClass(20);
      printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK")), true);
      outputStream = new DataOutputStream(socket.getOutputStream());
      inStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
      
      CheckUserResBean checkUserResBean = checkUser(printWriter, inStream, ip);
      if ("SUCCESS".equals(checkUserResBean.getRESCODE()))
      {
        BatchStartResBean batchStartResBean = batchStart(printWriter, inStream, clientBatchBean, ip);
        if ("SUCCESS".equals(batchStartResBean.getRESCODE()))
        {
          List<FileBean> fileList = new ArrayList();
          for (int i = 0; i < clientBatchBean.getBatchFileList().size(); i++)
          {
            ClientBatchFileBean clientBatchFileBean = (ClientBatchFileBean)clientBatchBean.getBatchFileList().get(i);
            FileBean fileBean = new FileBean(clientBatchFileBean.getFileName(), "");
            fileList.add(fileBean);
            
            TranceFileResBean tranceFileResBean = tranceFile(printWriter, inStream, clientBatchBean, clientBatchFileBean, ip);
            if ("SUCCESS".equals(tranceFileResBean.getRESCODE()))
            {
              if (!tranceFileResBean.getMD5STRNOW().equalsIgnoreCase(clientBatchFileBean.getMd5Code()))
              {
                sendFile(clientBatchFileBean, tranceFileResBean, outputStream);
                FileCheckResBean fileCheckResBean = fileCheck(printWriter, inStream, clientBatchBean, clientBatchFileBean, ip);
                if (!"SUCCESS".equals(fileCheckResBean.getRESCODE()))
                {
                  resultBean.setResultCode(tranceFileResBean.getRESCODE());
                  resultBean.setResultMessage("文件校验失败，服务端返回错误信息为:[" + tranceFileResBean.getRESMSG() + "]");
                  logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.FILE_CHECK.toString() + "]服务端返回:[" + tranceFileResBean.getRESCODE() + "]");
                  break;
                }
              }
            }
            else
            {
              resultBean.setResultCode(tranceFileResBean.getRESCODE());
              resultBean.setResultMessage("文件交易失败，服务端返回错误信息为:[" + tranceFileResBean.getRESMSG() + "]");
              logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.TRANCE_FILE.toString() + "]服务端返回:[" + tranceFileResBean.getRESCODE() + "]");
              break;
            }
          }
          if (fileList.size() == clientBatchBean.getBatchFileList().size())
          {
            BatchCheckResBean batchCheckResBean = batchCheck(printWriter, inStream, clientBatchBean, fileList, tranceReqObj, ip);
            if ("SUCCESS".equals(batchCheckResBean.getRESCODE()))
            {
              resultBean.setResultCode(batchCheckResBean.getRESCODE());
              resultBean.setResultMessage("批次上传成功!服务端返回信息为:[" + batchCheckResBean.getRESMSG() + "]");
              logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_CHECK.toString() + "]服务端返回:[" + batchCheckResBean.getRESCODE() + "]");
            }
            else
            {
              resultBean.setResultCode(batchCheckResBean.getRESCODE());
              resultBean.setResultMessage("批次上传失败了!，服务端返回错误信息为:[" + batchCheckResBean.getRESMSG() + "]");
              logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_CHECK.toString() + "]服务端返回:[" + batchCheckResBean.getRESCODE() + "]");
            }
          }
        }
        else
        {
          resultBean.setResultCode(batchStartResBean.getRESCODE());
          resultBean.setResultMessage("批次开始交易失败，服务端返回错误信息为:[" + batchStartResBean.getRESMSG() + "]");
          logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.BATCH_START.toString() + "]服务端返回:[" + batchStartResBean.getRESCODE() + "]");
        }
      }
      else
      {
        resultBean.setResultCode(checkUserResBean.getRESCODE());
        resultBean.setResultMessage("用户校验失败，服务端返回错误信息为:[" + checkUserResBean.getRESMSG() + "]");
        logger.info("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]交易类型:[" + TranceServiceCode.CHECK_USER.toString() + "]服务端返回:[" + checkUserResBean.getRESCODE() + "]");
      }
    }
    catch (Exception e)
    {
      logger.error("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]传输交易异常!", e);
      throw new Exception("|传输JAR包|Socket|服务端IP[" + ip + "]批次[" + clientBatchBean.getBatchId() + "]版本[" + clientBatchBean.getBatch_ver() + "]传输交易异常!", e);
    }
    finally
    {
      try
      {
        if (printWriter != null) {
          printWriter.close();
        }
        if (inStream != null) {
          inStream.close();
        }
        if (outputStream != null)
        {
          outputStream.flush();
          outputStream.close();
        }
        if (socket != null) {
          socket.close();
        }
      }
      catch (IOException e)
      {
        logger.error("|传输JAR包|Socket|服务端IP[" + ip + "]传输交易关闭资源异常!", e);
      }
    }
    return resultBean;
  }
  
  public static ResultBean submitBatch(String batchId, String batch_ver, String batchForder, TranceReqObj tranceReqObj, String ip, int port)
    throws Exception
  {
    ClientBatchBean clientBatchBean = new ClientBatchBean();
    clientBatchBean.setBatchId(batchId);
    clientBatchBean.setBatch_ver(batch_ver);
    clientBatchBean.setBatchFileList(TransClientUtil.getFolderFile(batchForder));
    return submitBatch(clientBatchBean, tranceReqObj, ip, port);
  }
  
  public static void sendFile_bak(ClientBatchFileBean fileBean, TranceFileResBean tranceFileResBean, DataOutputStream outputStream)
    throws Exception
  {
    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(fileBean.getFilePath());
      long fileSize = fileBean.getFileSize();
      
      int bufSize = Integer.parseInt(tranceFileResBean.getCATCHSIZE());
      
      boolean flag = true;
      while (flag)
      {
        if (fileSize <= bufSize)
        {
          bufSize = Integer.parseInt(Long.toString(fileSize));
          flag = false;
        }
        int read = 0;
        byte[] buf = new byte[bufSize];
        read = fis.read(buf);
        if (-1 == read) {
          break;
        }
        outputStream.write(buf, 0, read);
        fileSize -= read;
      }
    }
    catch (Exception e)
    {
      logger.error("|传输JAR包|Socket|传输文件[" + fileBean.getFilePath() + "]流异常!", e);
      throw new Exception("|传输JAR包|Socket|传输文件[" + fileBean.getFilePath() + "]流异常!", e);
    }
    finally
    {
      try
      {
        if (fis != null)
        {
          outputStream.flush();
          fis.close();
          fis = null;
        }
      }
      catch (IOException e)
      {
        logger.error("|传输JAR包|Socket|传输文件流关闭资源异常!", e);
      }
    }
  }
  
  public static void sendFile(ClientBatchFileBean fileBean, TranceFileResBean tranceFileResBean, DataOutputStream outputStream)
    throws Exception
  {
    RandomAccessFile raf = null;
    try
    {
      raf = new RandomAccessFile(fileBean.getFilePath(), "r");
      long fileSize = raf.length();
      
      raf.seek(Long.parseLong(tranceFileResBean.getFILESIZENOW()));
      int bufSize = Integer.parseInt(tranceFileResBean.getCATCHSIZE());
      
      boolean flag = true;
      while (flag)
      {
        if (fileSize - raf.getFilePointer() <= bufSize) {
          bufSize = Integer.parseInt(Long.toString(fileSize - raf.getFilePointer()));
        }
        int read = 0;
        byte[] buf = new byte[bufSize];
        read = raf.read(buf);
        if (-1 == read) {
          break;
        }
        outputStream.write(buf, 0, read);
        if (fileSize - raf.getFilePointer() <= 0L) {
          flag = false;
        }
      }
    }
    catch (Exception e)
    {
      logger.error("|传输JAR包|Socket|传输文件[" + fileBean.getFilePath() + "]流异常!", e);
      throw new Exception("|传输JAR包|Socket|传输文件[" + fileBean.getFilePath() + "]流异常!", e);
    }
    finally
    {
      try
      {
        if (raf != null)
        {
          outputStream.flush();
          raf.close();
          raf = null;
        }
      }
      catch (IOException e)
      {
        logger.error("|传输JAR包|Socket|传输文件流关闭资源异常!", e);
      }
    }
  }
  
  public static void main(String[] args)
  {
    ClientBatchBean clientBatchBean = new ClientBatchBean();
    clientBatchBean.setBatchId("f508fa834e6739c9a3bfd9a8dc701a57");
    clientBatchBean.setBatch_ver("1");
    List<ClientBatchFileBean> BatchFileList = new ArrayList();
    
    ClientBatchFileBean clientBatchFileBean1 = new ClientBatchFileBean();
    clientBatchFileBean1.setFileName("00001-672C01D6-1CCE-4958-B610-E48CFA2184B5.jpg.jpg");
    clientBatchFileBean1.setFilePath("E:/SunWebScan/UpdateFile/f508fa834e6739c9a3bfd9a8dc701a57_1/00001-672C01D6-1CCE-4958-B610-E48CFA2184B5.jpg.jpg");
    clientBatchFileBean1.setFileSize(new File(clientBatchFileBean1.getFilePath()).length());
    try
    {
      clientBatchFileBean1.setMd5Code(TransClientUtil.getFileMD5(new File(clientBatchFileBean1.getFilePath())));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    BatchFileList.add(clientBatchFileBean1);
    

    ClientBatchFileBean clientBatchFileBean2 = new ClientBatchFileBean();
    clientBatchFileBean2.setFileName("00002-91007A21-918D-4636-9DDF-5E1ED6097612.jpg.jpg");
    clientBatchFileBean2.setFilePath("E:/SunWebScan/UpdateFile/f508fa834e6739c9a3bfd9a8dc701a57_1/00002-91007A21-918D-4636-9DDF-5E1ED6097612.jpg.jpg");
    clientBatchFileBean2.setFileSize(new File(clientBatchFileBean2.getFilePath()).length());
    try
    {
      clientBatchFileBean2.setMd5Code(TransClientUtil.getFileMD5(new File(clientBatchFileBean2.getFilePath())));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    BatchFileList.add(clientBatchFileBean2);
    

    ClientBatchFileBean clientBatchFileBean3 = new ClientBatchFileBean();
    clientBatchFileBean3.setFileName("a3b0b9ba-2ab8-4443-9108-89be912f71b0.jpg");
    clientBatchFileBean3.setFilePath("E:/SunWebScan/UpdateFile/f508fa834e6739c9a3bfd9a8dc701a57_1/a3b0b9ba-2ab8-4443-9108-89be912f71b0.jpg");
    clientBatchFileBean3.setFileSize(new File(clientBatchFileBean3.getFilePath()).length());
    try
    {
      clientBatchFileBean3.setMd5Code(TransClientUtil.getFileMD5(new File(clientBatchFileBean3.getFilePath())));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    BatchFileList.add(clientBatchFileBean3);
    

    ClientBatchFileBean clientBatchFileBean4 = new ClientBatchFileBean();
    clientBatchFileBean4.setFileName("c3902e3e-f5df-4e47-a872-a9d854da9fd0.jpg");
    clientBatchFileBean4.setFilePath("E:/SunWebScan/UpdateFile/f508fa834e6739c9a3bfd9a8dc701a57_1/c3902e3e-f5df-4e47-a872-a9d854da9fd0.jpg");
    clientBatchFileBean4.setFileSize(new File(clientBatchFileBean4.getFilePath()).length());
    try
    {
      clientBatchFileBean4.setMd5Code(TransClientUtil.getFileMD5(new File(clientBatchFileBean4.getFilePath())));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    BatchFileList.add(clientBatchFileBean4);
    
    ClientBatchFileBean clientBatchFileBean5 = new ClientBatchFileBean();
    clientBatchFileBean5.setFileName("f508fa834e6739c9a3bfd9a8dc701a57_1.syd");
    clientBatchFileBean5.setFilePath("E:/SunWebScan/UpdateFile/f508fa834e6739c9a3bfd9a8dc701a57_1/f508fa834e6739c9a3bfd9a8dc701a57_1.syd");
    clientBatchFileBean5.setFileSize(new File(clientBatchFileBean5.getFilePath()).length());
    try
    {
      clientBatchFileBean5.setMd5Code(TransClientUtil.getFileMD5(new File(clientBatchFileBean5.getFilePath())));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    BatchFileList.add(clientBatchFileBean5);
    


    clientBatchBean.setBatchFileList(BatchFileList);
    try
    {
      ResultBean resultBean = submitBatch(clientBatchBean, TranceReqObj.other, "127.0.0.1", 8026);
      System.out.println("==传输结果是==" + resultBean.getResultCode() + " MSG:" + resultBean.getResultMessage());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
