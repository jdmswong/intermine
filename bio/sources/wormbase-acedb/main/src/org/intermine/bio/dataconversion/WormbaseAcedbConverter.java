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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.xpath.NodeSet;
import org.intermine.dataconversion.*;
import org.intermine.metadata.*;
import org.intermine.util.TypeUtil;
import org.intermine.xml.full.Item;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
    private Model model;
    private ClassDescriptor classCD; // CD of current data type being processed 
    
    // Items that have already been referenced and stored
    // Key: "className:id", Value: Item ID (ex: "4_1")
	private HashMap<String, Item> storedRefItems; 
	

	private String currentClass = "Gene"; // TODO set to gene for now
	private HashMap<String, String> keyMapping; // the primary key for each class TODO for debugging
	
    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public WormbaseAcedbConverter(ItemWriter writer, Model _model) {
        super(writer, _model, DATA_SOURCE_NAME, DATASET_TITLE);
        
        WMDebug.debug("Constructor called");
        keyMapping = new HashMap<String, String>();
        keyMapping.put("Organism", "name"); // TODO for debugging
        
        storedRefItems = new HashMap<String, Item>();
        model = _model;
        
        classCD = model.getClassDescriptorByName(currentClass);
        
//        // Begin debug code
//        ClassDescriptor geneCD = model.getClassDescriptorByName("Gene");
//        
//        
//        String label[] = {
//        		"Gene attrs",
//        		"Gene refs",
//        		"Gene colls",
//        		"Gene fields"
//        		};
//        String value[] = {
//        		geneCD.getAllAttributeDescriptors().toString(),
//        		geneCD.getAllReferenceDescriptors().toString(),
//        		geneCD.getAllCollectionDescriptors().toString(),
//        		geneCD.getAllFieldDescriptors().toString()
//        		};
//        for(int i=0; i<label.length; i++){
//        	System.out.printf("[%s]\t[%s]\n",label[i],value[i]);
//        }
//        // end debug code
        
    }

    /**
     * 
     * @param reader The java.io.BufferedReader that intermine passes in
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
    	WMDebug.debug("started WormbaseAcedbConverter.process()"); 
    	
    	if( dataMapping == null ){
    		throw new Exception("mapping.file property not defined for this"+
    				" source in the project.xml");
    	}
    	
    	FileParser fp = new FileParser(reader);
    	
    	// foreach XML string
    	String dataString;
    	while( (dataString = fp.getDataString()) != null ){
		
			// Load XML into org.w3c.dom.Document 
			Document doc = PackageUtils.loadXMLFrom(dataString);
			
		    // Get XPathFactory
	        XPathFactory xpf = XPathFactory.newInstance();
	        XPath xpath = xpf.newXPath();
	    	
	        // Get enumerator of InterMine datapaths to map (ex: primaryIdentifier)
	        Enumeration<Object> dataPathEnum = dataMapping.keys();
	        
	        // TODO: hard code Gene for now
	        Item item = createItem(currentClass);
	        
	        
	        String dataPath;
	        String ID = null;
	        while( dataPathEnum.hasMoreElements() ){ // foreach property mapping
	        	dataPath = (String) dataPathEnum.nextElement(); // ex: symbol
	        	WMDebug.debug("Processing property:["+dataPath+"]");
	        	String xpathQuery = dataMapping.getProperty(dataPath);
	        	
	        	// The XPath object compiles the XPath expression
		        XPathExpression expr = xpath.compile( xpathQuery );
		        
		        
		        // '.' indicates join, aka reference or collection
		        if( dataPath.contains(".") ){
		        	
		        	Matcher fNMatcher = Pattern.compile("(.*?)\\.").matcher(dataPath);
		        	if( fNMatcher.find() ){
			        	String fieldName = fNMatcher.group(1);
			        	
				        FieldDescriptor fd = classCD.getFieldDescriptorByName(fieldName);
				        if( fd == null ){
				        	throw new Exception("Type not found in model");
				        }
				        
			        	ReferenceDescriptor rd = (ReferenceDescriptor) fd; 
			        	String refClassName = 
			        			TypeUtil.unqualifiedName(rd.getReferencedClassName());
				        // Determine if field is reference or collection
				        if( fd.isReference() ){
				        	WMDebug.debug("This is a reference");
				        	
				        	String xPathValue = StringUtils.strip( expr.evaluate(doc) );
				        	String referencedItemID = getRefID(rd.getName(), xPathValue).getIdentifier();
				        	
				        	
				        	WMDebug.debug("Setting current "+currentClass+"."+fd.getName()+" to: ("+refClassName+")["+xPathValue+"]" );
				        	item.setReference(rd.getName(), referencedItemID);
				        	
				        }else if(fd.isCollection()){
				        	WMDebug.debug("This is a collection");
				        	
				        	// Get set of IDs
					        NodeList resultNodes = (NodeList) expr.evaluate(doc,  XPathConstants.NODESET);
					        String collectionIDs[] = new String[resultNodes.getLength()]; 
					        for(int i = 0; i < resultNodes.getLength(); i++) {
					            collectionIDs[i] = StringUtils.strip(resultNodes.item(i).getTextContent()); 
					        }
	
					        for(int i=0; i<collectionIDs.length; i++){
					        	System.out.println(collectionIDs[i]);  // TODO debug
					        }
					        
				        	// TODO fill this in
				        }else{
				        	throw new Exception(currentClass+".["+dataPath+
				        			"] either malformed, or is neither a "+
				        			"reference nor collection");
				        }
				        
		        	}else{
		        		throw new Exception("Matching error: ["+dataPath+"] expected to contain '.'");
		        	}
		        }else{
		        	WMDebug.debug("This is an attribute");
		        	
		        	String xPathValue = StringUtils.strip( expr.evaluate(doc) );
		        	
			        if(dataPath.equals("primaryIdentifier")){
			        	ID = xPathValue;
			        }
			        
		        	// DataPath describes attribute
					WMDebug.debug("Setting attribute ["+dataPath+"] to ["+xPathValue+"] if nonempty");
			        if (!StringUtils.isEmpty(xPathValue)) {
						item.setAttribute(dataPath, xPathValue);
					}
		        	
		        }
		        
		        
		        
	        }
	        
	        if( ID == null ){
	        	throw new Exception("InterMine ID not defined");
	        }
	        WMDebug.debug("Storing "+currentClass+" with ID:"+ID);
	        store(item);
	    	
    	}
    	
    	// Store all items in storedRefItems
    	Iterator<Entry<String, Item>> keySetIter = 
    			storedRefItems.entrySet().iterator();
    	while(keySetIter.hasNext()){
    		Entry<String, Item> keySet = keySetIter.next();
    		WMDebug.debug("Storing item:["+keySet.getKey()+"]");
    		store(keySet.getValue());
    	}
    	
    }
    
    /**
     * Gets ID of referenced object.
     * @param fieldName The reference or collection this object is referred to in 
     * @param pID Primary ID value of referenced object
     * @return InterMine item identifier for this object
     * @throws Exception 
     */
	public Item getRefID(String fieldName, String pID) throws Exception {
    	ReferenceDescriptor rd = classCD.getReferenceDescriptorByName(fieldName, true);
    	if( rd == null ){
    		throw new Exception(fieldName+" expected to be reference field for "+currentClass);
    	}
    	
    	String refClassName = 
    			TypeUtil.unqualifiedName(rd.getReferencedClassName());

		Item referencedItem; 
		if (storedRefItems.containsKey(fieldName + ":" + pID)) {
			referencedItem = storedRefItems.get(fieldName + ":"
					+ pID);
		} else {
			WMDebug.debug("new " + refClassName + " object:" + pID);
			referencedItem = createItem(refClassName);

			if (keyMapping.containsKey(refClassName)) {
				referencedItem.setAttribute(keyMapping.get(refClassName),
						pID);
			} else {
				throw new Exception(
						"keyMapping has no \"class key value\" for "
								+ refClassName);
			}

			WMDebug.debug("Storing " + refClassName + " with primary ID:"
					+ pID);
//			store(referencedItem);
			storedRefItems.put(fieldName + ":" + pID,
					referencedItem);
		}

		return referencedItem;
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
