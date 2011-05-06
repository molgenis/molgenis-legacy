package plugins.cluster.demo.dependencymanager;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class DependencyManagerModel extends SimpleScreenModel
{
	public DependencyManagerModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}
	private Boolean rqtl;
	private Boolean bitops;
	private Boolean rcurl;
	private Boolean clusterjobs;
	
	
	public Boolean getRqtl()
	{
		return rqtl;
	}
	public void setRqtl(Boolean rqtl)
	{
		this.rqtl = rqtl;
	}
	public Boolean getBitops()
	{
		return bitops;
	}
	public void setBitops(Boolean bitops)
	{
		this.bitops = bitops;
	}
	public Boolean getRcurl()
	{
		return rcurl;
	}
	public void setRcurl(Boolean rcurl)
	{
		this.rcurl = rcurl;
	}
	public Boolean getClusterjobs()
	{
		return clusterjobs;
	}
	public void setClusterjobs(Boolean clusterjobs)
	{
		this.clusterjobs = clusterjobs;
	}
	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
