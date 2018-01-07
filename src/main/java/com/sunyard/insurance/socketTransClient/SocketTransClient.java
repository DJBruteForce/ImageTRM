package com.sunyard.insurance.socketTransClient;

import com.sunyard.insurance.socketTransClient.bean.ClientBatchBean;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchFileBean;
import com.sunyard.insurance.socketTransClient.bean.TransConfigBean;
import com.sunyard.insurance.socketTransClient.util.TransClientUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketTransClient
{
  private Socket socket;
  private PrintWriter printWriter = null;
  private BufferedReader inStream = null;
  private DataOutputStream outputStream = null;
  private int transBufferSize = TransConfigBean.transBufferSize;
  private static final Log logger = LogFactory.getLog(SocketTransClient.class);
  
  public SocketTransClient(String ip, int port)
    throws Exception
  {
    try
    {
      this.socket = new Socket(ip, port);
      
      this.socket.setTrafficClass(20);
      
      logger.info("连接服务器" + ip + "端口" + port + "设置超时毫秒数：" + TransConfigBean.soTimeout);
      this.socket.setSoTimeout(TransConfigBean.soTimeout);
    }
    catch (Exception e)
    {
      logger.error("连接服务器" + ip + "端口" + port + "失败!", e);
      throw new Exception("连接服务器" + ip + "端口" + port + "失败!", e);
    }
  }
  
  public ClientBatchBean submitBatch(String batchId, String batch_ver, String batchForder)
    throws Exception
  {
    ClientBatchBean batchBean = new ClientBatchBean();
    long beginTime = System.currentTimeMillis();
    batchBean.setBatchId(batchId);
    batchBean.setBatch_ver(batch_ver);
    batchBean.setReCode("0");
    batchBean.setReMsg("批次传输失败");
    batchBean.setBatchFileList(TransClientUtil.getFolderFile(batchForder));
    logger.info("批次号[" + batchId + "]将文件夹对象[" + batchForder + "]转换为BatchBean对象耗时:" + (System.currentTimeMillis() - beginTime) + "毫秒!");
    
    return submitBatch(batchBean);
  }
  
  public ClientBatchBean submitBatch(ClientBatchBean batchBean)
  {
    int len = batchBean.getBatchFileList().size();
    if (len == 0)
    {
      batchBean.setReCode("-1");
      logger.info("批次号[" + batchBean.getBatchId() + "]没有任何文件需要提交！");
      return batchBean;
    }
    boolean hasSysFile = false;
    for (int i = 0; i < batchBean.getBatchFileList().size(); i++)
    {
      String fileName = ((ClientBatchFileBean)batchBean.getBatchFileList().get(i)).getFileName();
      String prefix = fileName.substring(fileName.lastIndexOf(".") + 1)
        .toLowerCase();
      if (prefix.equals("syd"))
      {
        hasSysFile = true;
        break;
      }
    }
    if (!hasSysFile)
    {
      batchBean.setReCode("-7");
      logger.info("批次号[" + batchBean.getBatchId() + "]不存在SYD文件！");
      return batchBean;
    }
    String resultStr = "";
    try
    {
      this.printWriter = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(this.socket.getOutputStream(), "GBK")), 
        true);
      
      this.outputStream = new DataOutputStream(this.socket.getOutputStream());
      this.inStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      
      logger.info("批次号[" + batchBean.getBatchId() + "]开始传输交易" + new Date());
      this.printWriter.println("0001:" + TransConfigBean.userName + ":" + 
        TransConfigBean.password);
      resultStr = this.inStream.readLine();
      if (!resultStr.equals("0"))
      {
        logger.info("批次号[" + batchBean.getBatchId() + "]校验用户失败！");
        this.printWriter.println("0009:true");
        batchBean.setReCode("-2");
        batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]用户校验失败");
        return batchBean;
      }
      logger.info("批次号[" + batchBean.getBatchId() + "]校验用户完成" + new Date());
      this.printWriter.println("0002:SunTXM001:" + batchBean.getBatchId() + 
        ":" + batchBean.getBatch_ver());
      
      resultStr = this.inStream.readLine();
      if (!resultStr.equals("0003"))
      {
        logger.info("批次号[" + batchBean.getBatchId() + "]开始提交，没有收到服务端响应！");
        this.printWriter.println("0009:true");
        batchBean.setReCode("-3");
        batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]开始传输,服务端创建临时文件夹失败");
        return batchBean;
      }
      logger.info("批次号[" + batchBean.getBatchId() + "]传递交易批次信息完成" + new Date());
      logger.info("批次号[" + batchBean.getBatchId() + "]开始传输文件" + new Date());
      long beginTime = System.currentTimeMillis();
      int i;
      long beginTime;
      for (int i = 0; i < len; i++)
      {
        ClientBatchFileBean fileBean = 
          (ClientBatchFileBean)batchBean.getBatchFileList().get(i);
        
        this.printWriter.println("0003:" + fileBean.getFileName() + ":" + 
          fileBean.getFileSize() + ":" + this.transBufferSize + ":");
        resultStr = this.inStream.readLine();
        if (!resultStr.equals("99"))
        {
          logger.info("批次号[" + batchBean.getBatchId() + "]没有收到服务端响应");
          this.printWriter.println("0009:true");
          batchBean.setReCode("-4");
          batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]文件[" + fileBean.getFileName() + 
            "]开始传输,没有收到服务端响应");
          return batchBean;
        }
        ClientBatchFileBean fileBean;
        int i;
        long beginTime;
        boolean b = sendFile(fileBean);
        if (!b)
        {
          logger.info("批次号[" + batchBean.getBatchId() + "]文件[" + fileBean.getFilePath() + "]传输失败");
          this.printWriter.println("0009:true");
          batchBean.setReCode("-5");
          batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]文件[" + fileBean.getFileName() + "]传输失败");
          fileBean.setSubmitSuccess(false);
          return batchBean;
        }
        boolean b;
        ClientBatchFileBean fileBean;
        int i;
        long beginTime;
        this.printWriter.println("0002:SunTXM002:" + batchBean.getBatchId() + 
          "," + fileBean.getFileName() + "," + 
          fileBean.getMd5Code());
        resultStr = this.inStream.readLine();
        if (!resultStr.equals("2"))
        {
          logger.info("批次号[" + batchBean.getBatchId() + "]文件[" + fileBean.getFilePath() + "]MD5校验失败!");
          this.printWriter.println("0009:true");
          batchBean.setReCode("-6");
          batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]文件[" + fileBean.getFilePath() + "]MD5校验失败!");
          fileBean.setSubmitSuccess(false);
          
          return batchBean;
        }
        boolean b;
        ClientBatchFileBean fileBean;
        fileBean.setSubmitSuccess(true);
      }
      logger.info("批次号[" + batchBean.getBatchId() + "]传输所有文件完成" + new Date());
      logger.info("批次号[" + batchBean.getBatchId() + "]传输所有文件总耗时:" + (System.currentTimeMillis() - beginTime));
      

      this.printWriter.println("0002:" + TransConfigBean.batchEndTransCode + 
        ":" + batchBean.getBatchId() + "," + 
        batchBean.getBatchFileList().size());
      resultStr = this.inStream.readLine();
      if (!resultStr.equals("2"))
      {
        logger.info("批次号[" + batchBean.getBatchId() + "]发送结束,但是对方存储失败了,版本号[" + batchBean.getBatch_ver() + "]");
        this.printWriter.println("0009:true");
        batchBean.setReCode("0");
        batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]发送结束,但是对方存储失败了,版本号[" + batchBean.getBatch_ver() + "]");
        return batchBean;
      }
      long beginTime;
      logger.info("批次号[" + batchBean.getBatchId() + "]上传成功,版本号[" + batchBean.getBatch_ver() + "]");
      batchBean.setReCode("1");
      batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]传输成功");
      return batchBean;
    }
    catch (UnknownHostException e)
    {
      logger.info("UnknownHostException异常", e);
      batchBean.setReCode("0");
      batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]传输失败");
      return batchBean;
    }
    catch (IOException e)
    {
      ClientBatchBean localClientBatchBean;
      logger.info("IOException异常", e);
      batchBean.setReCode("0");
      batchBean.setReMsg("批次号[" + batchBean.getBatchId() + "]传输失败");
      return batchBean;
    }
    finally
    {
      try
      {
        if (this.printWriter != null)
        {
          this.printWriter.println("0009:true");
          this.printWriter.close();
        }
        if (this.inStream != null) {
          this.inStream.close();
        }
        if (this.outputStream != null)
        {
          this.outputStream.flush();
          this.outputStream.close();
        }
        if (this.socket != null) {
          this.socket.close();
        }
      }
      catch (IOException e)
      {
        logger.error("socket客户端资源关闭异常", e);
      }
    }
  }
  
  public boolean sendFile(ClientBatchFileBean fileBean)
  {
    long beginTime = System.currentTimeMillis();
    FileInputStream fileInputStream = null;
    try
    {
      fileInputStream = new FileInputStream(fileBean.getFilePath());
      boolean transFlag = true;
      long fileSize = fileBean.getFileSize();
      while (transFlag)
      {
        int bufSize = this.transBufferSize;
        if (fileSize < this.transBufferSize)
        {
          bufSize = Integer.parseInt(Long.toString(fileSize));
          transFlag = false;
        }
        byte[] buf = new byte[bufSize];
        fileInputStream.read(buf);
        this.outputStream.write(buf);
        fileSize -= this.transBufferSize;
      }
      logger.info("传输文件[" + fileBean.getFileName() + "]耗时:" + (System.currentTimeMillis() - beginTime));
      return true;
    }
    catch (Exception e)
    {
      logger.error("传输文件异常", e);
      return false;
    }
    finally
    {
      try
      {
        if (fileInputStream != null) {
          fileInputStream.close();
        }
      }
      catch (IOException e)
      {
        logger.error("sockeClient关闭资源异常", e);
      }
    }
  }
}
