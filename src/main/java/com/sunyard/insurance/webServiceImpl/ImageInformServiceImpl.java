package com.sunyard.insurance.webServiceImpl;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.TRM_INFORM_RECORD;
import com.sunyard.insurance.webService.ImageInformService;

public class ImageInformServiceImpl implements ImageInformService {

	private static final Logger log = Logger
			.getLogger(ImageInformServiceImpl.class);
	private static JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
	private static ImageInformService service = null;

	public ImageInformService getImageInformService() throws Exception {
		if (null == service) {
			log.info("创建webService[ImageInformService]客户端service");
			String serviceURL = GlobalVar.USMURL+ "/webServices/ImageInformService";
			factory.setServiceClass(ImageInformService.class);
			factory.setAddress(serviceURL);
			service = (ImageInformService) factory.create();

			//设置超时时间
			Client proxy = ClientProxy.getClient(service); 
			HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
	        HTTPClientPolicy policy = new HTTPClientPolicy();
	        policy.setConnectionTimeout(180*1000);//连接超时时间
	        policy.setReceiveTimeout(180*1000);//影响超时时间
	        conduit.setClient(policy);
	        //设置超时时间
		}
		return service;
	}

	public boolean imageInformRecord(TRM_INFORM_RECORD inform_record) {
		try {
			ImageInformService service = this.getImageInformService();
			return service.imageInformRecord(inform_record);
		} catch (Exception e) {
			log.error("调用webService发起批次到达通知异常!BATCH_ID["
					+ inform_record.getBatch_id() + "]BATCH_VER["
					+ inform_record.getBatch_ver() + "]");
			return false;
		}
	}

}
