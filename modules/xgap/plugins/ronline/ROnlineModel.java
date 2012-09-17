package plugins.ronline;

import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class ROnlineModel {

	private String test;
	private String loc;
	private RProcess rp;
	private List<String> results;
	private Long timeOut;
	
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

	public Long getTimeOut()
	{
		return timeOut;
	}

	public void setTimeOut(Long timeOut)
	{
		this.timeOut = timeOut;
	}

}
