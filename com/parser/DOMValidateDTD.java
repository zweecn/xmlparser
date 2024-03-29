package com.parser;

import java.io.*;
import java.util.Map;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.swing.text.TabableView;
import javax.xml.parsers.*;
/*import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;*/

public class DOMValidateDTD {
	static int tab;
	private static void printTab() {
		for (int i = 0; i < tab; i++) {
			System.out.print(" | ");
		}
	}
	private static void stepThrough (Node start)   
	{
		for(Node child = start.getFirstChild();child != null;child = child.getNextSibling())   
		{  
			if(child instanceof Element)//去除多余的空白
			{
				Element element = (Element) child;
//				NodeList nodeList = element.getChildNodes();
				printTab();
				System.out.print(element.getTagName());
				tab++;
//				for (int i = 0; i < nodeList.getLength(); i++) {
//					if (nodeList.item(i).getNodeType() == Node.TEXT_NODE 
//							&& element.getFirstChild().getNodeValue().trim() != null) {
////						System.out.print(element.getNodeName() + " has a Text node: " 
////								+ element.getFirstChild().getNodeValue().trim());
//						System.out.print(element.getTextContent());
//						break;
//					}
//				}
				//System.out.print("\tValue: "+ element.getNodeValue());
				if (child.hasAttributes()) {
					//System.out.print("\t" + child.getNodeName() + " has attributes: ");
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
					System.out.print(" " + value);
				}
				System.out.println();
			}
			if(child != null) {
				stepThrough(child);
			}
			tab--;
		}
	}   
	public static void main(String args[]) {
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
			//System.out.println("Encoding: " + xmlDocument.getXmlEncoding());
			xmlDocument.normalize();
			Element root = xmlDocument.getDocumentElement();
			System.out.println("The root element is:"   +   root.getNodeName());
			//			NodeList children =  root.getChildNodes();
			tab = 0;
			stepThrough(root);

			/*			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result = new StreamResult(System.out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "Employee.dtd");
			transformer.transform(source, result);*/
		} catch (IOException e) {
			e.printStackTrace();
			/*		} catch (TransformerException e) {
			e.printStackTrace();*/
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("XML grammar error:\n" + e.getMessage());
			e.printStackTrace();
		} 
	}
}