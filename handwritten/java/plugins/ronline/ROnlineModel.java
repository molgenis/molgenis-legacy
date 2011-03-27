package plugins.ronline;

import java.util.List;

public class ROnlineModel {


	String test;
	String loc;
	
	RProcess rp;
	
	List<String> results;


	
	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

	public RProcess getRp() {
		return rp;
	}

	public void setRp(RProcess rp) {
		this.rp = rp;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	
	
	

}
