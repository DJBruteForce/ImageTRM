package com.sunyard.cm.test;
/*
 * LoadRunner Java script. (Build: 3020)
 * 
 * Script Description: 
 *                     
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import com.sunyard.insurance.socketTransClient.SocketClientApi;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchBean;

public class Actions
{

	public int init() throws Throwable {
	    System.out.println("已初始化!");
		return 0;
	}//end of init


	public int action() throws Throwable {
	    System.out.println("===开始准备数据===");

	    String batchId = java.util.UUID.randomUUID().toString().replace("-", "");
	    String busiNo = batchId;
	    
	    // 每笔业务3个版本影像文件
	    for (int k = 0; k < 3; k++) {
		    int ver = k + 1;
		    File srcDir = new File("E:/srcFile1/4700fe6c222144eaa351161ca0031045_1" + ver);
		    File destDir = new File("E:/temp/" + batchId + "_1");
		    try {
			    FileUtils.copyDirectory(srcDir, destDir);
		    } catch (IOException e) {
			    System.out.println(e);
		    }
		    File sydFile = new File(destDir.getAbsolutePath()+ File.separator + "4700fe6c222144eaa351161ca0031045_"+ ver + ".syd");
		    File sydFile2 = new File(destDir.getAbsolutePath()+ File.separator + batchId + "_" + ver + ".syd");
		    // 修改文件名
		    sydFile.renameTo(sydFile2);
		    // 修改内容
		    try {
			    SAXReader saxReader = new SAXReader();
			    Document document = saxReader.read(sydFile2);

			    Element batchNode = (Element) document
					    .selectSingleNode("//DocInfo/BATCH_ID");
			    batchNode.setText(batchId);

			    Element busiNoNode = (Element) document
					    .selectSingleNode("//DocInfo/BUSI_NO");
			    busiNoNode.setText(busiNo);

			    /** 将document中的内容写入文件中 */
			    XMLWriter writer = new XMLWriter(new FileOutputStream(
					    sydFile2));
			    writer.write(document);
			    writer.flush();
			    writer.close();

			    // 传输批次文件
			    SocketClientApi sc = new SocketClientApi("127.0.0.1", 8026);
			    ClientBatchBean cBean = sc.submitBatch(batchId, "" + ver, destDir.getAbsolutePath());
			    // 删除文件
			   // FileUtils.deleteDirectory(destDir);
			    if(!"1".equals(cBean.getReCode())) {
				    //上传失败
				System.out.println("上传影像失败");
				    break;
			    }else {
				System.out.println("上传影像成功");
			    }
		    } catch (Exception e) {
			    System.out.println(e);
		    }

	    }



	    return 0;
	}//end of action


	public int end() throws Throwable {
	    System.out.println("完成!");
		return 0;
	}//end of end
}
