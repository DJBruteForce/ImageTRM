package com.sunyard.insurance.filenet.faf.cesupport.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLUtil {

	public static Document readXmlDocByUsrDir(String path)
			throws DocumentException {
		Document document = null;
		String dirPath = System.getProperty("user.dir");
		File file = new File(dirPath + path);
		if(FileUtil.isFile(file)){
			document = new SAXReader().read(file);
		}
		return document;
	}

	public static Document readXmlDocByClsPth(String path)
			throws DocumentException, IOException {
		Document document = null;
		InputStream in = null;
		try {
			in = XMLUtil.class.getClassLoader().getResourceAsStream(path);
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

	public static String getText(Element element) {
		if (element == null || element.getText() == null) {
			return null;
		}
		return element.getText().trim().replaceAll("\t|\r|\n", "");
	}
	
}
