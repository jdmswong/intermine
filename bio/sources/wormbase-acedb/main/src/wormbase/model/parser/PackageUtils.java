/**
 * 
 */
package wormbase.model.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
/**
 * @author jwong
 *
 */
public class PackageUtils {
	
//	public static Document loadXMLFrom(String xml)
//	    throws SAXException, IOException {
//	    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
//	    
////	    StringBuilder sb = new StringBuilder();
////	    int i;
////	    while( ( i = is.read() ) != -1 ){
////	    	sb.append( (char) i );
////	    }
////	    
////	    
////	    String str = sb.toString();
////	    
////	    return null;
//	    
//	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	    factory.setNamespaceAware(true);
//	    
//	    DocumentBuilder builder = null;
//	    try {
//	        builder = factory.newDocumentBuilder();
//	    }
//	    catch (ParserConfigurationException ex) {
//	    }  
//	    Document doc = builder.parse(is);
//	    is.close();
//	    return doc;
//
//	}

	public static Document loadXMLFrom(String xml)
		    throws SAXException, java.io.IOException {
		    return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
		}
	
	public static Document loadXMLFrom(InputStream is) 
	    throws SAXException, IOException {
	    DocumentBuilderFactory factory =
	        DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = null;
	    try {
	        builder = factory.newDocumentBuilder();
	    }
	    catch (ParserConfigurationException ex) {
	    }  
	    Document doc = builder.parse(is);
	    is.close();
	    return doc;
	}
	
	/**
	 * Removes numbers from the beginnings of XML tags in the string.
	 * Ex: <2_point> -> <two_point>
	 * @param xml
	 * @return
	 */
	public static String sanitizeXMLTags(String xml){
		String repairedXML = new String();
		
		HashMap<Integer, String> numberMap = new HashMap<Integer, String>();
		numberMap.put(1, "one");
		numberMap.put(2, "two");
		numberMap.put(3, "three");
		numberMap.put(4, "four");
		numberMap.put(5, "five");
		numberMap.put(6, "six");
		numberMap.put(7, "seven");
		numberMap.put(8, "eight");
		numberMap.put(9, "nine");
		numberMap.put(0, "zero");

		String patternStr = "(</?)(\\d)";
		
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(xml);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String digit = matcher.group(2);
			String processedText = numberMap.get(Integer.parseInt(digit));
			matcher.appendReplacement(sb, matcher.group(1)+processedText);
		}
		matcher.appendTail(sb);

		
		return sb.toString();
	}
}
