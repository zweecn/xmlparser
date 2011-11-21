package com.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class T {
	public static void main(String[] args) {
		try {
			FileOutputStream out = null;

			// Verify the XML, get the tree format
			XMLParser parser = new XMLParser();
			String tree = parser.doParser();
			System.out.println(tree);
			System.out.println();
			out = new FileOutputStream(new File("output_file/tree.txt"));
			out.write(tree.getBytes());
			out.close();
			
			// Verify the XML, use dom4j
			XMLParserDom4j xml = new XMLParserDom4j();
			xml.verify();

			// Print the SQL
			List<String> sqlList =  xml.toSQL();
			String sqlbuffer = "";
			System.out.println("\nThe SQLs are:");
			for (String string : sqlList) {
				System.out.println(string);
				sqlbuffer += string + "\n";
			}
			System.out.println();
			out = new FileOutputStream(new File("output_file/sql.txt"));
			out.write(sqlbuffer.trim().getBytes());
			out.close();
			
			// Insert to MySQL
			xml.insert2RDB();
			
			System.out.println("\nThe program output is in output_file folder.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
