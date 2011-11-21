package com.parser;

import java.io.*;
import java.util.Iterator;

import org.dom4j.*;
import org.dom4j.io.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParserDom4j {
	public void readXML() {
		try {

			SAXReader reader = new SAXReader();  
			EntityResolver resolver = new EntityResolver() {  
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {  
					InputStream in //= this.getClass().getResourceAsStream("tvschedule.dtd");  
						= this.getClass().getClassLoader().getResourceAsStream("tvschedule.dtd");
					InputSource is = new InputSource(in);
					is.setPublicId(publicId);
					is.setSystemId(systemId);
					return is;
				}
			};  
			reader.setEntityResolver(resolver);  
			reader.setValidation(true);
			File file = new File("satvexample.xml");
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			for (Iterator it = root.elementIterator(); it.hasNext();) {
				Element element = (Element) it.next();
				System.out.println(element.getName() + ":"  + element.getTextTrim());
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
