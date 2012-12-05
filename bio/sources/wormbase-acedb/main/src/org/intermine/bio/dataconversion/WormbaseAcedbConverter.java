package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.*;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;
import org.w3c.dom.Document;

import wormbase.model.parser.*;

/**
 * 
 * @author
 */
public class WormbaseAcedbConverter extends BioFileConverter
{
    
    private static final String DATASET_TITLE = "wormbaseAcedb"; //"Add DataSet.title here";
    private static final String DATA_SOURCE_NAME = "wormbaseAcedbFileconverter"; //"Add DataSource.name here";

    private DataMapper dataMapping = null;
	

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public WormbaseAcedbConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        
        WMDebug.debug("Constructor called"); // TODO
    }

    /**
     * 
     * @param reader The java.io.BufferedReader that intermine passes in
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
    	WMDebug.debug("started WormbaseAcedbConverter.process()"); // TODO
    	
    	if( dataMapping == null ){
    		throw new Exception("mapping.file property not defined for this"+
    				" source in the project.xml");
    	}
    	
    	FileParser fp = new FileParser(reader);
    	
    	// foreach XML string
    	String dataString = fp.getDataString();
		
		// Load XML into org.w3c.dom.Document 
		Document doc = PackageUtils.loadXMLFrom(dataString);
		
	    // Get XPathFactory
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
    	
        // Get enumerator of InterMine datapaths to map (ex: primaryIdentifier)
        Enumeration<Object> dataPathEnum = dataMapping.keys();
        
        // TODO: hard code Gene for now
        Item item = createItem("Gene");
        
        String dataPath;
        String ID = null;
        while( dataPathEnum.hasMoreElements() ){
        	dataPath = (String) dataPathEnum.nextElement();
        	String xpathQuery = dataMapping.getProperty(dataPath);
        	
        	// The XPath object compiles the XPath expression
	        XPathExpression expr = xpath.compile( xpathQuery );
	        // XPathExpression applies it to a document
	        String xPathValue = StringUtils.strip( expr.evaluate(doc) );
	        
	        if(dataPath.equals("primaryIdentifier")){
	        	ID = xPathValue;
	        }
	        
			WMDebug.debug("Setting ["+dataPath+"] to ["+xPathValue+"]");
	        if (!StringUtils.isEmpty(xPathValue)) {
				item.setAttribute(dataPath, xPathValue);
			}
	        
	        if( ID == null ){
	        	throw new Exception("InterMine ID not defined");
	        }
        }
        WMDebug.debug("Storing item "+ID);
        store(item);

    	
    	
    	// end foreach
    	
		// Get the XML org.w3c.dom.Document
//		while( (dataString = fp.getDataString()) != null ){ 
//			Document XML = PackageUtils.loadXMLFrom(dataString);
//			
////			Item item = createItem(type);
////			if (!StringUtils.isEmpty(ID)) {
////				item.setAttribute("primaryIdentifier", ID);
////			}
////			
////			
////			
////			WMDebug.debug("Storing item "+ID);
////			store(item);
//			
//			WMDebug.debug("STOPPED AFTER 1 GENE FOR TESTING PURPOSES");
//			return; // TODO DEBUG
//		}
    }
    
    public void setTestVal(String testVal){
    	System.out.println("JDJDJD:: WormbaseGff3CoreGff.setTestVal called");
    	if(testVal.length() > 1){ 
    		System.out.println("JDJDJD:: WormbaseGff3CoreGff.setTestVal = "+testVal);
    	}
    }

    public void setMappingFile(String mappingFile) throws Exception{
        dataMapping = new DataMapper();
    	try {
			dataMapping.load(new FileReader(mappingFile));
		} catch (FileNotFoundException e) {
			WMDebug.debug("ERROR: "+mappingFile+" not found");
			throw e;
		}
    	System.out.println("Processed mapping file: "+mappingFile);
    }
    
}
