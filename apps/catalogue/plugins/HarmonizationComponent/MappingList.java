package plugins.HarmonizationComponent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.molgenis.pheno.Measurement;

public class MappingList {
	
	private List<LinkedInformation> links = new ArrayList<LinkedInformation>();
	
	private HashMap<String, LinkedInformation> uniqueElements = new HashMap<String, LinkedInformation>();
	
	public List<LinkedInformation> getSortedInformation(){
		sorting();
		return links;
	}
	
	public void add(String expandedQuery, String matchedItem, Double similarity, String measurementName) throws Exception{
		
		if(expandedQuery == null || matchedItem == null || similarity == null || measurementName == null)
			throw new Exception ("Parameters have to be not null!");
			
		if(expandedQuery.equals("") || matchedItem.equals(""))
			throw new Exception ("Parameters have to be not empty");
		
		LinkedInformation inf = new LinkedInformation(expandedQuery, matchedItem, similarity, measurementName);
		
		if(uniqueElements.containsKey(inf.expandedQuery + measurementName)){
			
			if(similarity > uniqueElements.get(inf.expandedQuery + measurementName).similarity){
				uniqueElements.get(inf.expandedQuery + measurementName).similarity = similarity;
			}
		}else{
			links.add(inf);
			uniqueElements.put(inf.expandedQuery + measurementName, inf);
		}
	}
	
	public void remove(String expandedQuery){
		if(uniqueElements.containsKey(expandedQuery)){
			uniqueElements.remove(expandedQuery);
			links.remove(uniqueElements.get(expandedQuery));
		}
	}
	
	public void sorting(){
		
		LinkedInformation[] columns = new LinkedInformation[links.size()];
    	
    	if(links != null){
    		
    		int i = 0;
    		
    		for(LinkedInformation eachElement : links){
    			columns[i] = eachElement;
    			i++;
    		}
    		Arrays.sort(columns);
    		links = Arrays.asList(columns);
    		
    		for(LinkedInformation inf : links){
    			System.out.println(inf.expandedQuery + " ------------ similarity: " + inf.similarity);
    		}
    	}
	}
}
