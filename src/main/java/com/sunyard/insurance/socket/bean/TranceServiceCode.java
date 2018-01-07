package com.sunyard.insurance.socket.bean;

public enum TranceServiceCode {
	
	CHECK_USER,//用户校验
	BATCH_START,//批次开始传输
	TRANCE_FILE,//传输文件
	FILE_CHECK,//文件校验
	BATCH_CHECK//批次交易结束
}
