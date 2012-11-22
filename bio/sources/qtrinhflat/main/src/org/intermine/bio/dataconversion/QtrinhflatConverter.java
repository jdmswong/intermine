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

import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;


/**
 * 
 * @author
 */
public class QtrinhflatConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Add DataSet.title here";
    private static final String DATA_SOURCE_NAME = "Add DataSource.name here";

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public QtrinhflatConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

    	/*dd
        Iterator<?> lineIter = FormattedTextParser.parseTabDelimitedReader(reader);

        // data is in format:
        // primaryIdentifier | identifier | symbol
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 3 || line[0].startsWith("#") || line[0].startsWith(HEADER_LINE)) {
                continue;
            }
            String primaryidentifier = line[0];
            String symbol = line[1];
            String identifier = line[2];
            Item gene = createItem("Gene");
            if (!StringUtils.isEmpty(primaryidentifier)) {
                gene.setAttribute("primaryIdentifier", primaryidentifier);
            }
            if (!StringUtils.isEmpty(symbol)) {
                gene.setAttribute("symbol", symbol);
                // per Rachel.  We can't seem to get the gene names out of wormmart.
                gene.setAttribute("name", symbol);
            }
            if (!StringUtils.isEmpty(identifier)) {
                gene.setAttribute("secondaryIdentifier", identifier);
            }
            gene.setReference("organism", getOrganism("6239"));
            store(gene);
        }
    }*/


		 System.out.println ("QTQTQT::QtrinhflatCoverter.process() aaaaaaa");  // FLAG statement
		 Iterator<?> lineIter = FormattedTextParser.parseTabDelimitedReader(reader);
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line[0].startsWith("#")) {
                continue;
            }
				System.out.println ("QTQTQT::line\t" + line.toString());
				String sampleID = line[0];
				String sampleDescription = line[1];
				System.out.println(sampleID + " " + sampleDescription);
            Item sss = createItem("QTSample");  // create your item
            if (!StringUtils.isEmpty(sampleID)) {
                //gene.setAttribute("primaryIdentifier", sampleID);
                sss.setAttribute("sidentifier", sampleID);
            }
            if (!StringUtils.isEmpty(sampleDescription)) {
                sss.setAttribute("description", sampleDescription);
            }
            store(sss);  // put sss in database
            //sss.setReference("organism", getOrganism("6239"));
				
			}
    }
}