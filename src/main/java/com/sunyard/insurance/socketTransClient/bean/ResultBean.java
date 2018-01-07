package com.sunyard.insurance.socketTransClient.bean;

import java.io.Serializable;

public class ResultBean
  implements Serializable
{
  private static final long serialVersionUID = 2986194892807670019L;
  private String resultCode;
  private String resultMessage;
  
  public ResultBean() {}
  
  public ResultBean(String resultCode, String resultMessage)
  {
    this.resultCode = resultCode;
    this.resultMessage = resultMessage;
  }
  
  public String getResultCode()
  {
    return this.resultCode;
  }
  
  public void setResultCode(String resultCode)
  {
    this.resultCode = resultCode;
  }
  
  public String getResultMessage()
  {
    return this.resultMessage;
  }
  
  public void setResultMessage(String resultMessage)
  {
    this.resultMessage = resultMessage;
  }
}
