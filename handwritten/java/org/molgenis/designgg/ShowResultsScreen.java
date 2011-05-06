package org.molgenis.designgg;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Tuple;

public class ShowResultsScreen extends PluginModel
{
	private static final long serialVersionUID = 7479201455882238849L;
	/**IN parameters*/
	private DesignParameters designParameters;
	private String outputR;
	private List<Tuple> indPerSlide;
	private List<Tuple> indPerCondition;
	private String imageLink;
	private String indXCondLink;			// Individuals per Condition
	private String indXSlideLink;			// Individuals per Slide
	
	public ShowResultsScreen(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reload(Database db)
	{
		// TODO Auto-generated method stub
		
	}

//GETTERS AND SETTERS
	public List<Tuple> getIndPerSlide()
	{
		return indPerSlide;
	}

	public void setIndPerSlide(List<Tuple> indPerSlide)
	{
		this.indPerSlide = indPerSlide;
	}

	public List<Tuple> getIndPerCondition()
	{
		return indPerCondition;
	}

	public void setIndPerCondition(List<Tuple> indPerCondition)
	{
		this.indPerCondition = indPerCondition;
	}
	
	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	/**
	 * @return the designParameters
	 */
	public DesignParameters getDesignParameters() {
		return designParameters;
	}

	/**
	 * @param designParameters the designParameters to set
	 */
	public void setDesignParameters(DesignParameters designParameters) {
		this.designParameters = designParameters;
	}

	/**
	 * @return the outputR
	 */
	public String getOutputR() {
		return outputR;
	}

	/**
	 * @param outputR the outputR to set
	 */
	public void setOutputR(String outputR) {
		this.outputR = outputR;
	}

	/**
	 * @return the indXCondLink
	 */
	public String getIndXCondLink() {
		return indXCondLink;
	}

	/**
	 * @param indXCondLink the indXCondLink to set
	 */
	public void setIndXCondLink(String indXCondLink) {
		this.indXCondLink = indXCondLink;
	}

	/**
	 * @return the indXSlideLink
	 */
	public String getIndXSlideLink() {
		return indXSlideLink;
	}

	/**
	 * @param indXSlideLink the indXSlideLink to set
	 */
	public void setIndXSlideLink(String indXSlideLink) {
		this.indXSlideLink = indXSlideLink;
	}

	@Override
	public String getViewName()
	{
		// TODO Auto-generated method stub
		return "screens_ShowResultsScreen";
	}

	@Override
	public String getViewTemplate()
	{
		// TODO Auto-generated method stub
		return "org/molgenis/designgg/ShowResultsScreen.ftl";
	}


}
