/**
 * 
 */
package wormbase.model.parser;

import java.io.*;
import java.util.ArrayList;

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
}
