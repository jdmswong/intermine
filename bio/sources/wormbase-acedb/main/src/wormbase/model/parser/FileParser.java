package wormbase.model.parser;
/*
 * 
 */
import java.io.*;
import java.util.*;

/**
 *  This class handles the parsing of the flat files into data structures
 *  for individual processing.
 *  
 *  Sample use:
 *
 *  FileParser fp = new FileParser(jaceFile);
 *	
 *	String[] dataObj;
 *	// foreach ""-separated paragraph in jacefile
 *	while( (dataObj = fp.getDataObj()) != null ){ 
 *		// do something
 *	}
 *
 */
public class FileParser {

	private MyBufferedReader inputStream;
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public FileParser(String jaceFile) throws IOException {
 		inputStream = new MyBufferedReader(new FileReader(jaceFile));
	}
	
	public FileParser(Reader reader) throws IOException {
 		inputStream = new MyBufferedReader(reader);
	}
	
	
	
	/**
	 * Returns multiline chunks of text separated by newlines.
	 * @return Array of strings comprising the Ace data object
	 * @throws IOException  
	 */
	public String[] getDataObj() throws IOException{
		if( inputStream.isStreamClosed() ){
			return null;
		}
		
		
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = null;
		boolean startedObj = false; // Switched if non-whitespace passed in 
        try {
			while ((line = (String) inputStream.readLine()) != null) {
			    
			    //System.out.print("*"); // DEBUG
			    
			    if(line.equals("")){
			    	if(startedObj){
			    		break;
			    	}else{
			    		continue;
			    	}
			    }else if(startedObj != true){
			    	startedObj = true;
			    }
			    lines.add(line);
			}
		} finally {
            if (line == null) {
            	inputStream.close();
            }
            //System.out.println(""); // DEBUG
		}

		return lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * Applies command to inputStream global variable
	 * @param command only "close" accepted
	 * @throws IOException 
	 */
	public int streamCmd(String command) throws IOException{
		if(command.equals("close")){
			inputStream.close();
			return 1;
		}else{
			return -1;
		}
	}
}
