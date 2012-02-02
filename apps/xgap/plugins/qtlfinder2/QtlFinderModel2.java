package plugins.qtlfinder2;

import java.util.HashMap;
import java.util.Map;

import org.molgenis.util.Entity;

import plugins.qtlfinder.QTLMultiPlotResult;
import plugins.reportbuilder.Report;


public class QtlFinderModel2{

	//the term you enter in the search box
	private String query;
	
	//listing of datatypes and the amount of records per type
	private Map<String, Integer> annotationTypeAndNr;
	private String selectedAnnotationTypeAndNr;

	//current hits to search term
	private Map<String, Entity> hits;
	
	//if the query string is too specific, we shorten it by 1 character at a time until there is at least 1 match
	//in that case, we want to inform the user that the query string has been shortened, and this is the one used
	String shortenedQuery;
	
	//names of things in the shoppingcart
	Map<String, Integer> shoppingCart = new HashMap<String, Integer>();
	
	//results for a multiplot request
	private QTLMultiPlotResult multiplot;
	
	//results for a single-entity report, overrules other results but does not remove them
	private Report report;
	
	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public Map<String, Integer> getAnnotationTypeAndNr()
	{
		return annotationTypeAndNr;
	}

	public void setAnnotationTypeAndNr(Map<String, Integer> annotationTypeAndNr)
	{
		this.annotationTypeAndNr = annotationTypeAndNr;
	}

	public String getSelectedAnnotationTypeAndNr()
	{
		return selectedAnnotationTypeAndNr;
	}

	public void setSelectedAnnotationTypeAndNr(String selectedAnnotationTypeAndNr)
	{
		this.selectedAnnotationTypeAndNr = selectedAnnotationTypeAndNr;
	}

	public Map<String, Entity> getHits()
	{
		return hits;
	}

	public void setHits(Map<String, Entity> hits)
	{
		this.hits = hits;
	}

	public String getShortenedQuery()
	{
		return shortenedQuery;
	}

	public void setShortenedQuery(String shortenedQuery)
	{
		this.shortenedQuery = shortenedQuery;
	}

	public Map<String, Integer> getShoppingCart()
	{
		return shoppingCart;
	}

	public void setShoppingCart(Map<String, Integer> shoppingCart)
	{
		this.shoppingCart = shoppingCart;
	}

	public QTLMultiPlotResult getMultiplot()
	{
		return multiplot;
	}

	public void setMultiplot(QTLMultiPlotResult multiplot)
	{
		this.multiplot = multiplot;
	}

	public Report getReport()
	{
		return report;
	}

	public void setReport(Report report)
	{
		this.report = report;
	}
	
	
	

}
