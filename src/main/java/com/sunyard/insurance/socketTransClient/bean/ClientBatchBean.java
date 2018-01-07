package com.sunyard.insurance.socketTransClient.bean;

import java.util.ArrayList;
import java.util.List;

public class ClientBatchBean
{
  private String batchId;
  private String batch_ver;
  private List<ClientBatchFileBean> BatchFileList = new ArrayList();
  private String reCode = "0";
  private String reMsg = "批次传输失败";
  
  public String getBatchId()
  {
    return this.batchId;
  }
  
  public void setBatchId(String batchId)
  {
    this.batchId = batchId;
  }
  
  public String getBatch_ver()
  {
    return this.batch_ver;
  }
  
  public void setBatch_ver(String batchVer)
  {
    this.batch_ver = batchVer;
  }
  
  public List<ClientBatchFileBean> getBatchFileList()
  {
    return this.BatchFileList;
  }
  
  public void setBatchFileList(List<ClientBatchFileBean> batchFileList)
  {
    this.BatchFileList = batchFileList;
  }
  
  public String getReCode()
  {
    return this.reCode;
  }
  
  public void setReCode(String reCode)
  {
    this.reCode = reCode;
  }
  
  public String getReMsg()
  {
    return this.reMsg;
  }
  
  public void setReMsg(String reMsg)
  {
    this.reMsg = reMsg;
  }
}
