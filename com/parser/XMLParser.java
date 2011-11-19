package com.parser;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLParser {
	int tab;
	public  void printTab() {
		for (int i = 0; i < tab; i++) {
			System.out.print("   ");
		}
		System.out.print("|__");
	}
	
	public void stepThrough (Node start)   
	{
		for(Node child = start.getFirstChild();child != null;child = child.getNextSibling())   
		{  
			if(child instanceof Element)//去除多余的空白
			{
				Element element = (Element) child;
				printTab();
				System.out.print(element.getTagName());
				if (child.hasAttributes()) {
					NamedNodeMap attributes = child.getAttributes();
					for (int i = 0; i<attributes.getLength(); i++)
					{
						Node attribute = attributes.item(i);
						String name = attribute.getNodeName();//获得属性名
						String value = attribute.getNodeValue();//获得属性值
						System.out.print(" " + name + "=" + value);
					}
				}
				String value = element.getFirstChild().getNodeValue().trim();
				if (value != "" && value != null) {
					System.out.print(" " + value.replace('\n', ' '));
				}
				System.out.println();
			}
			if(child != null) {
				tab++;
				stepThrough(child);
				tab--;
			}
		}
	}
	
	public void doParser() {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
				//Ignore the fatal errors
				public void fatalError(SAXParseException exception)throws SAXException { }
				//Validation errors 
				public void error(SAXParseException e)throws SAXParseException {
					System.out.println("Error at line " +e.getLineNumber() + ":\n" + e.getMessage());
					System.exit(0);
				}
				//Show warnings
				public void warning(SAXParseException err)throws SAXParseException{
					System.out.println(err.getMessage());
					System.exit(0);
				}
			});

			Document xmlDocument = builder.parse(new FileInputStream("satvexample.xml"));
			System.out.println("The xml version is: " + xmlDocument.getXmlVersion());
			xmlDocument.normalize();
			Element root = xmlDocument.getDocumentElement();
			System.out.println("The root element is:"   +   root.getNodeName());
			tab = 0;
			stepThrough(root);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("XML grammar error:\n" + e.getMessage());
			e.printStackTrace();
		} 
	}
}
