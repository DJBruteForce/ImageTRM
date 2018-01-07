package com.sunyard.insurance.cmapi.util;

import com.ibm.mm.sdk.common.DKDatastorePool;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.sunyard.insurance.common.CMConstant;

public class Test {
	
	private static DKDatastorePool datastorePool;
	private static int n = 0;
	
	/**
	 * 
	 *@Description CM连接池初始化
	 *@param uid
	 *@param pwd
	 *@throws Exception
	 */
	public static void initConnectionPool(String uid, String pwd)
			throws Exception {
		String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";
		try {
			datastorePool = new DKDatastorePool(fullClassName);
			//
			datastorePool.setDatastoreName(CMConstant.RMN);
			// 最少有10个连接,最多有100个连接
			datastorePool.setMinAndMaxPoolSize(CMConstant.minPool, CMConstant.maxPool);
			// 初始化连接个数
			datastorePool.initConnections(uid, pwd, CMConstant.initPool);
			datastorePool.setValidate(true);
			// 连接空闲超过2分钟，则被destroy
			datastorePool.setTimeOut(5);
			
			//datastorePool.setReclaimUnusedConnection(true);
			
		} catch (DKException e) {
			throw new DKException("创建CM连接池失败 !"+e);
		} catch (Exception e) {
			throw new Exception("创建CM连接池失败 !"+e);
		}
	}
	
	public static synchronized int getNum() {
		n++;
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return n;
	}
	
	/**
	 * 
	 *@Description
	 * 
	 *@param uid
	 *@param pwd
	 *@return 返回连接池中的一个可用连接
	 */
	public static synchronized DKDatastoreICM getConnection(String uid, String pwd) throws Exception {
		DKDatastoreICM dsICM = null;
		try {
			if (datastorePool == null) {
				initConnectionPool(uid, pwd);
			}
			dsICM = (DKDatastoreICM) datastorePool.getConnection(uid, pwd);
		} catch (DKException e) {
			throw new Exception("CM连接池获取失败Error:",e);
		} catch (Exception e) {
			throw new Exception("CM连接池获取失败Error:",e);
		}
		return dsICM;
	}
	
	/**
	 * 
	 *@Description 将连接放回连接池
	 *@param dsICM
	 *@throws DKException
	 *@throws Exception
	 */
	public static void returnConnection(DKDatastoreICM dsICM)
			throws DKException, Exception {
		datastorePool.returnConnection(dsICM);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CMConstant.RMN = "ICMNLSDB";
		CMConstant.maxPool = 100;
		CMConstant.minPool = 10;
		CMConstant.initPool = 10;
		
		try {
			System.out.println("初始化连接池");
			long cmConTime1 = System.currentTimeMillis();
			Test.initConnectionPool("icmadmin", "password");
			System.out.println("初始化连接池耗时："+(System.currentTimeMillis()-cmConTime1));
		} catch (Exception e) {
			System.out.println("初始化连接池异常!"+e);
		}
		
	}

}
