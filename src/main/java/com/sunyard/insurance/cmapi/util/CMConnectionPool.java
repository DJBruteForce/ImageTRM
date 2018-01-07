package com.sunyard.insurance.cmapi.util;

import org.apache.log4j.Logger;

import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDatastorePool;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.sunyard.insurance.common.CMConstant;

/**
 * 
 * @Title CMConnectionPool.java
 * @Package com.sunyard.insurance.cmapi
 * @Description CM连接池
 * @time 2012-8-6 下午03:44:28 @author xxw @version 1.0
 * -------------------------------------------------------
 */
public class CMConnectionPool implements DKConstantICM {

	private static DKDatastorePool datastorePool;
	private static boolean isDestroy = false;
	private static final Logger log = Logger.getLogger(CMConnectionPool.class);

	/**
	 * 
	 *@Description CM连接池初始化
	 *@param uid
	 *@param pwd
	 *@throws Exception
	 */
	public static void initConnectionPool(String uid, String pwd)
			throws Exception {
		log.info("CM initConnectionPool userID：" + uid + " pwd：" + pwd);
		String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";
		try {
			datastorePool = new DKDatastorePool(fullClassName);
			//
			datastorePool.setDatastoreName(CMConstant.RMN);
			//Set timeout in minutes, 0 is no timeout
			datastorePool.setTimeOut(10);
			// 最少有10个连接,最多有100个连接
			datastorePool.setMinAndMaxPoolSize(CMConstant.minPool, CMConstant.maxPool);
			// 初始化连接个数
			datastorePool.initConnections(uid, pwd, CMConstant.initPool);
			datastorePool.setValidate(true);
			//设置回收，如果连接池已满,尝试从其他用户手中回收未使用的连接
			//datastorePool.setReclaimUnusedConnection(true);
		} catch (DKException e) {
			log.error("创建CM连接池失败 !", e);
			throw new DKException("创建CM连接池失败 !"+e);
		} catch (Exception e) {
			log.error("创建CM连接池失败 !", e);
			throw new Exception("创建CM连接池失败 !"+e);
		}
	}

	/**
	 * 释放连接
	 * 
	 * @Description
	 * 
	 */
	public static void destroyConnectionPool() {
		if (datastorePool != null) {
			try {
				datastorePool.clearConnections();
				datastorePool.destroy();
			} catch (DKException e) {
				log.error("释放CM连接失败 Error:", e);
			} catch (Exception e) {
				log.error("释放CM连接失败 Error:", e);
			}
		}

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
			long startTime = System.currentTimeMillis();
			dsICM = (DKDatastoreICM) datastorePool.getConnection(uid, pwd);
			long endTime = System.currentTimeMillis();
			log.warn("IBM CM当前连接总数："+datastorePool.getUsedConnections()
					+"连接池中空闲连接总数："+datastorePool.getFreeConnections()
					+"本次获取到连接的sessionId："+dsICM.getSessionId()
					+"本次获取连接真实耗时："+(endTime-startTime)
					);
		} catch (DKException e) {
			log.error("CM连接池获取失败 Error:", e);
			throw new Exception("CM连接池获取失败Error:",e);
		} catch (Exception e) {
			log.error("CM连接池获取失败 Error:", e);
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
		log.warn("IBM CM当前连接总数："+datastorePool.getUsedConnections()
				+"连接池中空闲连接总数："+datastorePool.getFreeConnections()
				+"本次归还连接的sessionId："+dsICM.getSessionId()
				);
		long startTime = System.currentTimeMillis();
		datastorePool.returnConnection(dsICM);
		long endTime = System.currentTimeMillis();
		log.warn("IBM CM当前连接总数："+datastorePool.getUsedConnections()
				+"连接池中空闲连接总数："+datastorePool.getFreeConnections()
				+"本次归还连接的sessionId："+dsICM.getSessionId()
				+"本次归还连接真实耗时："+(endTime-startTime)
				);
	}

	/**
	 * 
	 *@Description
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static void reInitConnectionPool() {
		try {
			if (datastorePool != null) {
				// 释放连接池
				if (!isDestroy) {
					isDestroy = true;
					datastorePool.destroy();
				}

				// 重新初始化连接池
				log.debug("CM reInitConnectionPool userID：" + CMConstant.CMUser+ " pwd：" + CMConstant.CMPwd);
				String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";

				datastorePool = new DKDatastorePool(fullClassName);
				//ICMNLSDB
				datastorePool.setDatastoreName(CMConstant.RMN);
				// 最多有100个连接
				datastorePool.setMaxPoolSize(CMConstant.maxPool);
				// 最少有10个连接
				datastorePool.setMinPoolSize(CMConstant.minPool);
				// 连接空闲超过1分钟，则被destroy
				datastorePool.setTimeOut(1);
				// 初始化连接个数
				datastorePool.initConnections(CMConstant.CMUser,CMConstant.CMPwd, CMConstant.initPool);
				isDestroy = false;
			}
		} catch (DKException e) {
			log.error("重新初始化CM连接池失败 Error:", e);
		} catch (Exception e) {
			log.error("重新初始化CM连接池失败 Error:", e);
		}
	}

	/**
	 * 
	 *@Description 
	 *测试连接池是否成功
	 *@param args
	 */
	public static void main(String[] args) {
		try {
			CMConstant.RMN = "icmnlsdb";
			CMConstant.maxPool = 10;
			CMConstant.minPool = 1;
			CMConstant.initPool = 1;									
			CMConnectionPool.initConnectionPool("icmadmin", "cic1234");
			long cmConTime = System.currentTimeMillis();
			DKDatastoreICM dsICM = CMConnectionPool.getConnection("icmadmin","cic1234");
			dsICM.getSessionId();
			System.out.println("获取CM连接用时："+(System.currentTimeMillis()-cmConTime));
			CMConnectionPool.returnConnection(dsICM);
			System.out.println("Test connection pool isConnected[true-成功；false-失败]:"+dsICM.isConnected());
		} catch (DKException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}