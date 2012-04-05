package plugins.harmonizationPlugin;

public class LinkedInformation implements Comparable<LinkedInformation>{
	
	public String name = "";
	
	public String expandedQuery = "";
	
	public String matchedItem = "";
	
	public Double similarity = 0.0;
	
	public LinkedInformation(String expandedQuery, String matchedItem, Double similarity) throws Exception{
		
		if(expandedQuery == null || matchedItem == null || similarity == null)
			throw new Exception ("Parameters have to be not null!");
			
		if(expandedQuery.equals("") || matchedItem.equals(""))
			throw new Exception ("Parameters have to be not empty");
		
		this.expandedQuery = expandedQuery;
		this.matchedItem = matchedItem;
		this.similarity = similarity;
		this.name = expandedQuery + matchedItem;
	}

	@Override
	public int compareTo(LinkedInformation o) {
		// TODO Auto-generated method stub
		return Double.compare(this.similarity, o.similarity);
	}
	
}
