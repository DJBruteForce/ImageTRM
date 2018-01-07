package com.sunyard.insurance.filenet.ce.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlUtil {

	public static Document readXmlDocByUsrDir(String path)
			throws DocumentException {
		String dirPath = System.getProperty("user.dir");
		File file = new File(dirPath + path);
		return readXmlDocByFile(file);
	}
	
	public static Document readXmlDocByFile(File file) throws DocumentException {
		Document document = null;
		if (FileUtil.isFile(file)) {
			document = new SAXReader().read(file);
		}
		return document;
	}

	public static Document readXmlDocByClsPth(String path)
			throws DocumentException, IOException {
		InputStream in = XmlUtil.class.getClassLoader().getResourceAsStream(path);
		return readXmlDocByInStream(in);
	}
	
	public static Document readXmlDocByInStream(InputStream in)
			throws DocumentException, IOException {
		Document document = null;
		try {
			document = new SAXReader().read(in);
		} finally {
			if (in != null)
				in.close();
		}
		return document;
	}
	
	public static void writeXmlDocPretty(Document document, String path,
			String encoding) throws IOException {
		XMLWriter writer = null;
		OutputFormat format = null;
		try {
			format = OutputFormat.createPrettyPrint();
			format.setEncoding(encoding);
			format.setNewLineAfterDeclaration(false);
			writer = new XMLWriter(new FileWriter(path), format);
			writer.write(document);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public static String getAttrValue(Element element, String name) {
		return (element != null) ? element.attributeValue(name) : null;
	}

	public static String getTrimText(Node node) {
		return (node != null) ? node.getText().replaceAll("\t|\r|\n", "")
				.trim() : null;
	}
	
	public static Node getNode(Node node, String xpath) {
		return (node != null) ? node.selectSingleNode(xpath) : null;
	}
	
	public static List<?> getNodes(Node node, String xpath) {
		return (node != null) ? node.selectNodes(xpath) : null;
	}
	
	public static String getNodeText(Node node, String xpath) {
		return (node != null) ? node.selectSingleNode(xpath).getText() : null;
	}
	
}
