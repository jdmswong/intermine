package org.intermine.bio.dataconversion;

import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.intermine.objectstore.ObjectStoreException;

/**
 * 
 * @author
 */
public class QtsnpConverter extends BioFileConverter {
	private static String method = "QTQTQT::QTSNPConvert.process() ";
	//
	private static final String DATASET_TITLE = "Add QTSNPDataSet.title here";
	private static final String DATA_SOURCE_NAME = "Add QTSNPDataSource.name here";

	protected HashMap<String, Item> sampleMap = new HashMap<String, Item>();
	private Item sample = null;

	/**
	 * Constructor
	 * 
	 * @param writer
	 *            the ItemWriter used to handle the resultant items
	 * @param model
	 *            the Model
	 */
	public QtsnpConverter(ItemWriter writer, Model model) {
		super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
	}

	/**
	 * Create a new pathway Item or fetch from a map if it has been seen before
	 * Trying to get obj ref from DB
	 * 
	 * @param sampleID
	 *            the id of a KEGG pathway to look up
	 * @return an Item representing the pathway
	 */
	private Item getSample(String sampleID) throws ObjectStoreException {
		System.out.println(method + " BBBBBBBBB " + sampleID);
		Item sss = sampleMap.get(sampleID);
		if (sss == null) {
			System.out.println(method + " creating Sample with id '" + sampleID
					+ "'");
			sss = createItem("QTSample");
			sss.setAttribute("sidentifier", sampleID);
			sampleMap.put(sampleID, sss);
			store(sss);
		} else {
			System.out.println(method + " found Sample with id '" + sampleID
					+ "'");
		}
		return sss;
	}

	/**
	 * 
	 * 
	 * {@inheritDoc}
	 */
	public void process(Reader reader) throws Exception {
		Iterator<?> lineIter = FormattedTextParser
				.parseTabDelimitedReader(reader);

		// data is in format:
		// primaryIdentifier | identifier | symbol
		System.out.println(method);
		while (lineIter.hasNext()) {
			String[] line = (String[]) lineIter.next();
			System.out.println(method + " loading line: " + line);

			if (line[0].startsWith("#")) { // ||
											// line[0].startsWith(HEADER_LINE))
											// {
				continue;
			}

			String ID = line[0];
			String chr = line[1];
			String start = line[2];
			String end = line[3];
			String change = line[4];
			String sampleID = line[5];

			Item sss = createItem("QTSNP");

			if (!StringUtils.isEmpty(ID)) {
				sss.setAttribute("qtsnpidentifier", ID);
			}
			if (!StringUtils.isEmpty(chr)) {
				sss.setAttribute("chr", chr);
			}
			if (!StringUtils.isEmpty(start)) {
				sss.setAttribute("start", start);
			}
			if (!StringUtils.isEmpty(end)) {
				sss.setAttribute("end", end);
			}
			if (!StringUtils.isEmpty(change)) {
				sss.setAttribute("change", change);
			}

			System.out.println(method + " looking for reference '" + sampleID
					+ "'");
			try {
				Item qt = getSample(sampleID);
				sss.setReference("sample", getSample(sampleID));
			} catch (ObjectStoreException e) {
				System.out.println(method + " ERROR trying to get sample '"
						+ sampleID + "'!!!!");
			}

			System.out.println(method + " loading line: done");

			store(sss);
		}
	}
}