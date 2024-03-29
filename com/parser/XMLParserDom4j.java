package com.parser;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLParserDom4j {
	final static String dtdFile = "input_file/tvschedule.dtd";
	final static String xmlFile = "input_file/satvexample.xml";
	final static String jdbcURL = "jdbc:mysql://localhost:3306/test";   
	final static String jdbcDriver = "com.mysql.jdbc.Driver";   
	final static String uid = "root";
	final static String pwd = "123456";
	
	public boolean verify() {
		try {

			SAXReader reader = new SAXReader();  
			EntityResolver resolver = new EntityResolver() {  
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {  
					InputStream in //= this.getClass().getResourceAsStream("tvschedule.dtd");  
						= this.getClass().getClassLoader().getResourceAsStream(dtdFile);
					InputSource is = new InputSource(in);
					is.setPublicId(publicId);
					is.setSystemId(systemId);
					return is;
				}
			};
			reader.setEntityResolver(resolver);  
			reader.setValidation(true);
			File file = new File(xmlFile);
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			System.out.println("Use " + dtdFile + ", the " + xmlFile + " is correct. The root element is:" + root.getName());
		}catch (DocumentException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> toSQL() {
		List<String> sqlList = new ArrayList<String>();
		String dropTable = "drop table IF EXISTS `test`.`tv`;";
		String createTable = "CREATE  TABLE `test`.`tv` ( "
			 + " `idtv` INT NOT NULL AUTO_INCREMENT ,"
			 + " `CHAN` VARCHAR(45) NULL ,"
			 + " `BANNER` VARCHAR(1024) NULL ,"
			 + " `DATE` VARCHAR(45) NULL ,"
			 + " `TIME` VARCHAR(45) NULL ,"
			 + " `TITLE` VARCHAR(1024) NULL ,"
			 + " `TITLE_RATING` VARCHAR(45) NULL ,"
			 + " `TITLE_LANGUAGE` VARCHAR(100) NULL ,"
			 + " `DESCRIPTION` VARCHAR(2048) NULL ,"
			 + " PRIMARY KEY (`idtv`) );";
		sqlList.add(dropTable);
		sqlList.add(createTable);
		try {
			SAXReader reader = new SAXReader();  
			EntityResolver resolver = new EntityResolver() {  
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {  
					InputStream in //= this.getClass().getResourceAsStream("tvschedule.dtd");  
						= this.getClass().getClassLoader().getResourceAsStream(dtdFile);
					InputSource is = new InputSource(in);
					is.setPublicId(publicId);
					is.setSystemId(systemId);
					return is;
				}
			};  
			reader.setEntityResolver(resolver);  
			reader.setValidation(true);
			File file = new File(xmlFile);
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			String chan = null;
			String banner = null;
			String date = null;
			String time = null;
			String title = null;
			String titleRating = null;
			String titleLang = null;
			String describ = null;
			
			//for (Iterator it = root.elementIterator(); it.hasNext();) {
			List<Element> rootList = root.elements();
			for (Element chanElement : rootList) {
				//Element chanElement = (Element) it.next();
				Attribute attribute = chanElement.attribute("CHAN");
				chan = attribute.getValue();
				List<Element> dayElementList = chanElement.elements();
				for (Element dayElement : dayElementList) {
					if (dayElement.getName().trim().equals("BANNER")) {
						banner = dayElement.getTextTrim();
					} else {
						List<Element> dateElementList = dayElement.elements();
						for (Element dateElement : dateElementList) {
							if (dateElement.getName().trim().equals("DATE")) {
								date = dateElement.getTextTrim();
							} else if (dateElement.getName().trim().equals("PROGRAMSLOT")) {
								List<Element> timeElementList = dateElement.elements();
								for (Element timeElement : timeElementList) {
									if (timeElement.getName().trim().equals("TIME")) {
										time = timeElement.getTextTrim();
									} else if (timeElement.getName().trim().equals("TITLE")) {
										title = timeElement.getTextTrim();
										titleRating = timeElement.attributeValue("RATING");
										titleLang = timeElement.attributeValue("LANGUAGE");
										//System.out.println(titleRating + " " + titleLang);
									} else if (timeElement.getName().trim().equals("DESCRIPTION")) {
										describ = timeElement.getTextTrim();
									} 
								}
								if (title != null) {
									title = title.replace("\'", "\\'");
								} 
								if (describ != null) {
									describ = describ.replace("\'", "\\'");
								}
								String sql = "insert into test.tv values (null, " 
									+ "\'" + chan + "\',"
									+ "\'" + banner + "\',"
									+ "\'" + date + "\',"
									+ "\'" + time + "\',"
									+ "\'" + title + "\',"
									+ "\'" + titleRating + "\',"
									+ "\'" + titleLang + "\',"
									+ "\'" + describ + "\');";
								sqlList.add(sql.replace("\'null\'", "null"));
							}
						}
					}	
				}
			}
		}catch (DocumentException e) {
			System.out.println(e.getMessage());
		}
		
		return sqlList;
	}
	
	public void insert2RDB(){
		System.out.println("XML to MySQL...");
		if (DbUtils.loadDriver(jdbcDriver)) {
			try {
				Connection conn = (Connection) DriverManager.getConnection(jdbcURL, uid, pwd);
				List<String> sqlList = toSQL();
				for (String sql : sqlList) {
					QueryRunner runner = new QueryRunner();
					if (runner.update(conn, sql) >= 0) {
						//System.out.println("Succeed:");
						//System.out.println(sql);
					} else {
						System.out.println("Insert failed.");
						System.out.println(sql);
					}
				}
				System.out.println("Succeed.");
				DbUtils.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
