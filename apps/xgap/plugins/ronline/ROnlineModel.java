package plugins.ronline;

import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class ROnlineModel extends SimpleScreenModel {


	public ROnlineModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	

}
