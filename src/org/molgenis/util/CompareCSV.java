package org.molgenis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase.Files;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class CompareCSV {
	
	static class StringArrayComperator implements Comparator<String[]> {
		@Override
		public int compare(final String[] o1, final String[] o2) {
        	if(o1.length > o2.length) {
        		return 1;
        	} else if(o1.length < o2.length) {
        		return -1;
        	}
        	
        	for(int i = 0; i < o1.length; ++i) {
        		int res = o1[i].compareTo(o2[i]);
        		if(res != 0)
        			return res;
            }		        	
        	return 0;
        }		
	}
	
	public static boolean compareCSVFilesByContent(File file0, File file1, String errorMessage) {
		File[] files = new File[]{file0, file1};
		
		final List<String[]> file0Content = new ArrayList<String[]>();
		final List<String[]> file1Content = new ArrayList<String[]>();
		
		final List<List<String[]>> contents = new ArrayList<List<String[]>>();
		contents.add(file0Content);
		contents.add(file1Content);
		
		try {
			for(int i = 0; i < contents.size(); ++i) {
				final List<String[]> content = contents.get(i);
				new CsvFileReader(files[i]).parse(new CsvReaderListener() {
				  	public void handleLine( int line_number, Tuple tuple ) {
				  		String[] values = new String[tuple.getNrColumns()]; 
				  		for(int i = 0; i < tuple.getNrColumns(); ++i) {
				  			values[i] = tuple.getString(i);
				  		}
				  		content.add(values);
				  	}
				});
				Collections.sort(content, new CompareCSV.StringArrayComperator());
			}
			
			if(contents.get(0).size() != contents.get(1).size()) {
				errorMessage = "files content is not equal: differnt number of rows";
				return false;
			}
			
			for(int i = 0; i < contents.get(0).size(); ++i) {
				String[] array0 = contents.get(0).get(i);
				String[] array1 = contents.get(1).get(i);
				if(!Arrays.equals(array0, array1)) {
					errorMessage = String.format("files content is not equal: \n row: %s \n row: %s",Arrays.toString(array0), Arrays.toString(array1));
					return false;	
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean compareCSVFilesByContent(String file0, String file1, String errorMessage) {
		return compareCSVFilesByContent(new File(file0), new File(file1), errorMessage);
	}
}