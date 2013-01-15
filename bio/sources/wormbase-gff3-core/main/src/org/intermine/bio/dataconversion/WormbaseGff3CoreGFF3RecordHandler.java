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

import org.intermine.bio.io.gff3.GFF3Record;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

import wormbase.model.parser.WMDebug;

/**
 * A converter/retriever for the WormbaseGff3Core dataset via GFF files.
 */

public class WormbaseGff3CoreGFF3RecordHandler extends GFF3RecordHandler
{

    /**
     * Create a new WormbaseGff3CoreGFF3RecordHandler for the given data model.
     * @param model the model for which items will be created
     */
    public WormbaseGff3CoreGFF3RecordHandler (Model model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GFF3Record record) {
        // This method is called for every line of GFF3 file(s) being read.  Features and their
        // locations are already created but not stored so you can make changes here.  Attributes
        // are from the last column of the file are available in a map with the attribute name as
        // the key.   For example:
        //
        //     Item feature = getFeature();
        //     String symbol = record.getAttributes().get("symbol");
        //     feature.setAttrinte("symbol", symbol);
        //
        // Any new Items created can be stored by calling addItem().  For example:
        // 
        //     String geneIdentifier = record.getAttributes().get("gene");
        //     gene = createItem("Gene");
        //     gene.setAttribute("primaryIdentifier", geneIdentifier);
        //     addItem(gene);
        //
        // You should make sure that new Items you create are unique, i.e. by storing in a map by
        // some identifier. 

    	Item feature = getFeature();
    	String ID = record.getId();
    	
    	String sequenceName = "";
    	try{
    	sequenceName = ID.split(":")[1]; // Kludgy way of removing prefix
    	}catch( Exception e ){
    		WMDebug.debug("RECORD INVALID FORMAT:"+record.toString());
    		return;
    	}
    	
    	// Convert ID if available
    	if( IDMap != null && IDMap.containsKey(sequenceName)){
		    	String matchedID = IDMap.get(sequenceName);
		    	feature.setAttribute("primaryIdentifier", matchedID);
	    	}else{
		    	feature.setAttribute("primaryIdentifier", sequenceName);
	    	}
    	
    	// Convert record type is available
    	String recordType = record.getType();
    	if( typeMap != null && typeMap.containsKey(recordType) ){
    		feature.setClassName(typeMap.get(recordType));
    		WMDebug.debug("Setting "+recordType+" to "+typeMap.get(recordType));
    	}
    	
//    	WMDebug.debug("WormbaseGff3CoreGFF3RecordHandler.process() called"); // TODO DEBUG
//    	System.out.println("JDJDJD:: WormbaseGff3CoreGFF3RecordHandler.process() :\t"+record.toString());
    }
    
    public void processTranscript(){
    	
    }


}
