package org.molgenis.xgap.other.xqtlworkbench_lifelines.ThreadReaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.james.mime4j.field.FieldName;
import org.molgenis.util.SimpleTuple;

public class CSVFileSpliter {
	
	private final BufferedReader br;
	private final int maxNumRows;
	private int splitKeyIndex;
	private final String[] headers;
	
	private List<String[]> leftOver = new ArrayList<String[]>();
	private boolean hasMore = true;
	
	public CSVFileSpliter(String fileName, int maxNumRows, int splitKeyIndex) throws Exception {
		this.br = new BufferedReader(new FileReader(fileName));
		this.maxNumRows = maxNumRows;
		this.splitKeyIndex = splitKeyIndex;
		
		this.headers  = br.readLine().replace("\"", "").split("\\,");
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
		
		while(br.ready()) {
			String line = br.readLine();
			String[] parts = line.split("\\,");

			result.add(parts);
			
			if(result.size() > maxNumRows) {
				break;
			}
		}
		if(!br.ready()) {
			hasMore = false;
		}		
		
		if(hasMore) {
			String lastSplitKey = result.get(result.size()-1)[splitKeyIndex];
			for(int i = result.size(); i >= 0; --i) {
				if(!result.get(i-1)[splitKeyIndex].equals(lastSplitKey))
				{
					leftOver = result.subList(i, result.size());
					result = result.subList(0, i);
					
					break;
				}
			}	
		}
		return result;
	}
	
	public synchronized List<SimpleTuple> getTuples() throws Exception {
		List<String[]> lines = getLines();
		List<SimpleTuple> simpleTuples = new ArrayList<SimpleTuple>(lines.size());
		for(String[] line : lines) {
			Map<String, Object> valueMap = new HashMap<String, Object>(headers.length);
			for(int i = 0; i < line.length; ++i) {
				valueMap.put(headers[i], line[i]);
			}			
			simpleTuples.add(new SimpleTuple(valueMap));			
		}
		return simpleTuples;
	}
	
	public synchronized boolean hasMore() {
		return hasMore;
	}	
	
	public void close() throws Exception {
		br.close();
	}	
	
	public String[] getHeaders() {
		return headers;
	}
}
