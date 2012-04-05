package plugins.harmonizationPlugin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LinkedMap {
	
	private List<LinkedInformation> links = new ArrayList<LinkedInformation>();
	
	private HashMap<String, LinkedInformation> uniqueElements = new HashMap<String, LinkedInformation>();
	
	public List<LinkedInformation> getSortedInformation(){
		sorting();
		return links;
	}
	
	public void add(String expandedQuery, String matchedItem, Double similarity) throws Exception{
		
		if(expandedQuery == null || matchedItem == null || similarity == null)
			throw new Exception ("Parameters have to be not null!");
			
		if(expandedQuery.equals("") || matchedItem.equals(""))
			throw new Exception ("Parameters have to be not empty");
		
		LinkedInformation inf = new LinkedInformation(expandedQuery, matchedItem, similarity);
		
		if(uniqueElements.containsKey(inf.expandedQuery)){
			
			if(similarity > uniqueElements.get(inf.expandedQuery).similarity){
				uniqueElements.get(inf.expandedQuery).similarity = similarity;
			}
		}else{
			links.add(inf);
			uniqueElements.put(inf.expandedQuery, inf);
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
    		links.clear();
    		for(i = 0; i < columns.length;i++){
    			links.add(columns[i]);
    		}
    	}
	}
}
