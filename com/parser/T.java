package com.parser;

public class T {
	
	public static void main(String[] args) {
//		XMLParser parser = new XMLParser();
//		parser.doParser();
		
		XMLParserDom4j xml = new XMLParserDom4j();
		//xml.readXML();
		for (String string : xml.toMySQL()) {
			System.out.println(string);
		}
	}
}
