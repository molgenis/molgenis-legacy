package org.molgenis.lifelines;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.util.SimpleTuple;

import au.com.bytecode.opencsv.CSVReader;

public class CSVFileSpliter {
	
	private final int maxNumRows;
	private int splitKeyIndex;
	private final String[] headers;
	
	private List<String[]> leftOver = new ArrayList<String[]>();
	private boolean hasMore = true;
	
	private final String fileName;
	
	private final List<String[]> lines;
	private int lineIdx;
	
	public CSVFileSpliter(String fileName, int maxNumRows, int splitKeyIndex) throws Exception {
		this.maxNumRows = maxNumRows;
		this.splitKeyIndex = splitKeyIndex;
		this.fileName = fileName;
		this.lines = new CSVReader(new FileReader(fileName)).readAll();
	    this.headers = lines.get(lineIdx++);
	}

	public CSVFileSpliter(String fileName, int maxNumRows, String splitFieldName) throws Exception {
		this(fileName, maxNumRows,-1);
		for(int i = 0; i < headers.length; ++i) {
			if(headers[i].toLowerCase().equals(splitFieldName.toLowerCase())) {
				splitKeyIndex = i;
				break;
			}
		}
		if(splitKeyIndex == -1) {
			throw new IllegalArgumentException(String.format("Couldn't find fieldName '%s' in file '%s'", splitFieldName, fileName));
		}
	}	
	
	public synchronized List<String[]> getLines() throws Exception {
		List<String[]> result = new ArrayList<String[]>(maxNumRows);
		if(!leftOver.isEmpty())
			result.addAll(leftOver);
		
		while(lineIdx < lines.size()) {		    
	        result.add(lines.get(lineIdx));

			++lineIdx;
			if(result.size() > maxNumRows) {
				break;
			}
		}
		if(lineIdx >= lines.size()) {
			hasMore = false;
		}		
		
		if(hasMore) {
			String lastSplitKey = result.get(result.size() - 1)[splitKeyIndex];
			int i = result.size();
			for(; i > 0; --i) {
				if(!result.get(i-1)[splitKeyIndex].equals(lastSplitKey))
				{
					leftOver = result.subList(i, result.size());
					result = result.subList(0, i);
					
					break;
				}
			}	
			if(i <= 0) {
				throw new RuntimeException(String.format(
						"File '%s' has to many similar splitKey [index =%d]",
						fileName, splitKeyIndex
				)); 
			}
		}
		return result;
	}
	
	public synchronized List<SimpleTuple> getTuples() throws Exception {
		List<String[]> lines = getLines();
		List<SimpleTuple> simpleTuples = new ArrayList<SimpleTuple>(lines.size());
		for(String[] line : lines) {
			Map<String, Object> valueMap = new HashMap<String, Object>(headers.length);
			for(int i = 0; i < line.length; i++) {
				try {
					valueMap.put(headers[i], line[i]);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}			
			simpleTuples.add(new SimpleTuple(valueMap));			
		}
		return simpleTuples;
	}
	
	public synchronized boolean hasMore() {
		return hasMore;
	}	
	
	public String[] getHeaders() {
		return headers;
	}
}
