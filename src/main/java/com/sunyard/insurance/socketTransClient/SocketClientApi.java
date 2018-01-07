package com.sunyard.insurance.socketTransClient;

import com.sunyard.insurance.socketTransClient.bean.ClientBatchBean;

public class SocketClientApi
{
  private String ip;
  private int socketPort;
  private SocketTransClient socketTransClient = null;
  
  public SocketClientApi(String ip, int port)
  {
    this.ip = ip;
    this.socketPort = port;
  }
  
  public SocketClientApi(String ip, int port, String batchEndTransCode)
  {
    this.ip = ip;
    this.socketPort = port;
    com.sunyard.insurance.socketTransClient.bean.TransConfigBean.batchEndTransCode = batchEndTransCode;
  }
  
  public ClientBatchBean submitBatch(String batchId, String batch_ver, String batchForder)
    throws Exception
  {
    this.socketTransClient = new SocketTransClient(this.ip, this.socketPort);
    return this.socketTransClient.submitBatch(batchId, batch_ver, batchForder);
  }
  
  public ClientBatchBean submitBatch(ClientBatchBean batchBean)
    throws Exception
  {
    this.socketTransClient = new SocketTransClient(this.ip, this.socketPort);
    return this.socketTransClient.submitBatch(batchBean);
  }
}
