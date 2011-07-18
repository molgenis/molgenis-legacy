package org.molgenis.designgg;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

public class MainScreen extends PluginModel
{
	/**subsequent steps of the program*/
	static enum Steps {ask_parameters, calculate_design, show_results};
	private String sessionId;
	private String imagePath;
	private boolean bInvalidateSession = false;		// To signal MolgenisServlet 
	
	private AskParametersScreen screen1;
	private CalculateDesignScreen screen2;
	private ShowResultsScreen screen3;
	private int autoRefresh;

	public MainScreen(String string, ScreenController<?> parent)
	{
		super("Main", parent);
		
		//initialize the screens
		screen1 = new AskParametersScreen(Steps.ask_parameters.name(), this);
		screen2 = new CalculateDesignScreen(Steps.calculate_design.name(), this);
		screen3 = new ShowResultsScreen(Steps.show_results.name(), this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ScreenModel getSelected()
	{
		//returns the active screen. This depend on the status of the program
		//if(screen2.getIndPerCondition() != null && screen2.getIndPerSlide() != null)
		if( screen2.calculationDone() )
		{
			this.autoRefresh = 0;
			screen3.setIndPerCondition(	screen2.getIndPerCondition());
			screen3.setIndPerSlide(		screen2.getIndPerSlide());
			screen3.setImageLink(		screen2.getImageLink());
			screen3.setIndXCondLink(	screen2.getIndXCondLink());
			screen3.setIndXSlideLink(	screen2.getIndXSlideLink()); 
			screen3.setDesignParameters(screen2.getDesignParameters() );
			screen3.setOutputR(			screen2.getOutputR());
			return screen3;
		}
		if( screen1.isBReady2Go() ) 
				//screen1.getDesignParameters() != null)
		{
			this.autoRefresh = 60;
			screen2.setDesignParameters(screen1.getDesignParameters() );
			screen2.setImagePath(this.getImagePath());
			return screen2;
		}
		this.autoRefresh = 0;
		return screen1;

	}
	
	@Override
	public void handleRequest( Database db, Tuple request )
	{
		//super.handleRequest(db, request);
		
		if("back".equals(request.getString("__action")))
		{
			if(getSelected().equals(screen3))
			{
				//TODO This two maybe can be removed
				screen2.setIndPerCondition(null);//this is back to first
				screen2.setIndPerCondition(null);//this is back to first				
				
				screen2.setBCalculationDone(false);
				screen2.setBCalculationFail(false);
				screen2.setBCooking(false);				
				screen1.setDesignParameters(null);
				screen1.setBArgumentsOK(true);
				screen1.setBReady2Go(false);	
				setBInvalidateSession(true);
			}
			
			if(getSelected().equals(screen2))
			{				
				screen2.setBCalculationDone(false);
				screen2.setBCalculationFail(false);
				screen2.setBCooking(false);
				screen1.setDesignParameters(null);
				screen1.setBArgumentsOK(true);
				screen1.setBReady2Go(false);				
				setBInvalidateSession(true); 
			}
		}
	}

	public int getAutoRefresh()
	{
		return autoRefresh;
	}

	public void setAutoRefresh(int autoRefresh)
	{
		this.autoRefresh = autoRefresh;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) 
	{
		this.sessionId = sessionId;
		screen2.setSessionId(sessionId);
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		
		this.imagePath = imagePath;
		
		// We create the directory to store session files.
		File sessionDir = new File( imagePath + File.separator + getSessionId() );
		sessionDir.mkdir();		
	}
	
	@Override
	public String getLabel()
	{
		return "designing Genetical Genomics Experiments";
	}

	/**
	 * @return the bInvalidateSession
	 */
	public boolean isBInvalidateSession() {
		return bInvalidateSession;
	}

	/**
	 * @param invalidateSession the bInvalidateSession to set
	 */
	public void setBInvalidateSession(boolean invalidateSession) {
		bInvalidateSession = invalidateSession;
	}

	@Override
	public void reload(Database db)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getViewName()
	{
		// TODO Auto-generated method stub
		return "screens_MainScreen";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/designgg/MainScreen.ftl";
	}
	
	public String getCustomHtmlHeaders()
	{
		return "<link href=\"res/css/designgg.css\" rel=\"stylesheet\" type=\"text/css\"/>";
	}
}
