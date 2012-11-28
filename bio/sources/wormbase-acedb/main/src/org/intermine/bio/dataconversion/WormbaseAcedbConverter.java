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

import java.io.IOException;
import java.io.Reader;
import java.util.regex.*;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.*;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

import wormbase.model.parser.*;

/**
 * 
 * @author
 */
public class WormbaseAcedbConverter extends BioFileConverter
{
    
    private static final String DATASET_TITLE = "wormbaseAcedb"; //"Add DataSet.title here";
    private static final String DATA_SOURCE_NAME = "wormbaseAcedbFileconverter"; //"Add DataSource.name here";

	private FileParser fp;

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public WormbaseAcedbConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     * @param reader The java.io.BufferedReader that intermine passes in
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
    	WMDebug.debug("started WormbaseAcedbConverter.process()");
    	fp = new FileParser(reader);
    	
    	String[] dataChunk;
		
		// Get store each WormBase gene ID
		while( (dataChunk = fp.getDataObj()) != null ){ 
			//WMDebug.debug(dataChunk[0]);  // DEBUG
			
			String firstLine = dataChunk[0];
			String[] spaceTokens = firstLine.split("\\s+");
			
			String[] qtokens = spaceTokens[0].split("\\?",0);
			
			String type = qtokens[1];
			String ID = qtokens[2];

			Item item = createItem(type);
			if (!StringUtils.isEmpty(ID)) {
				item.setAttribute("primaryIdentifier", ID);
			}
			
			WMDebug.debug("Storing item "+ID);
			store(item);
			
//			WMDebug.debug("STOPPED AFTER 1 GENE FOR TESTING PURPOSES");
//			return; // TODO DEBUG
		}
    }
    
    public void setTestVal(String testVal){
    	if(testVal.length() > 1){
    		System.out.println("JDJDJD:: WormbaseGff3CoreGff.setTestVal = "+testVal);
    	}
    }

}
