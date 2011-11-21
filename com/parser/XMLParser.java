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
	final static String dtdFile = "tvschedule.dtd";
	final static String xmlFile = "satvexample.xml";
	
	int tab;
	String treeString;
	
	public String printTab() {
		String tabString = "";
		for (int i = 0; i < tab; i++) {
			tabString += "  ";
		}
		tabString += "|__";
		//System.out.println(tabString);
		return tabString;
	}
	
	public void stepThrough (Node start)   
	{
		for(Node child = start.getFirstChild();child != null;child = child.getNextSibling())   
		{
			if(child instanceof Element)
			{
				Element element = (Element) child;
				treeString += printTab() + element.getTagName();
				//System.out.print(element.getTagName());
				if (child.hasAttributes()) {
					NamedNodeMap attributes = child.getAttributes();
					for (int i = 0; i<attributes.getLength(); i++)
					{
						Node attribute = attributes.item(i);
						String name = attribute.getNodeName();
						String value = attribute.getNodeValue();
						//System.out.print(" " + name + "=" + value);
						treeString += " " + name + "=" + value;
					}
				}
				String value = null;
				if (element.getFirstChild() != null) {
					value = element.getFirstChild().getNodeValue();
				}
				if (value != "" && value != null) {
					//System.out.print(" " + value.replace('\n', ' '));
					treeString += " " + value.replace('\n', ' ');
				}
				//System.out.println();
				treeString += "\n";
			}
			if(child != null) {
				tab++;
				stepThrough(child);
				tab--;
			}
		}
	}
	
	public String doParser() {
		treeString = "";
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

			Document xmlDocument = builder.parse(new FileInputStream(xmlFile));
			System.out.println("Use " + dtdFile + ", the " + xmlFile + " is correct.\n");
			//System.out.println("The xml version is: " + xmlDocument.getXmlVersion());
			xmlDocument.normalize();
			Element root = xmlDocument.getDocumentElement();
			//System.out.println(root.getNodeName());
			treeString += root.getNodeName() + "\n";
			tab = 1;
			stepThrough(root);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("XML grammar error:\n" + e.getMessage());
			e.printStackTrace();
		}
		
		return treeString;
	}
	
}
