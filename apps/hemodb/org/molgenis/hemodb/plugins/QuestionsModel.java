/* 
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 */

package org.molgenis.hemodb.plugins;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;

/**
 * EditIndividualModel takes care of all state and it can have helper methods to
 * query the database. It should not contain layout or application logic which
 * are solved in View and Controller.
 * 
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */

/**
 * @author JvD
 * 
 */
@SuppressWarnings("serial")
public class QuestionsModel extends EasyPluginModel
{

	String action = null;
	ArrayList<String> names = new ArrayList<String>();
	List<String> results = new ArrayList<String>();

	public enum QuestionState
	{
		EMPTYGROUPLIST, QUESTION1, QUESTION1_RESULT, QUESTION2, QUESTION2_RESULT, QUESTION3, QUESTION3_RESULT, BEGINNING
	}

	private QuestionState state = QuestionState.EMPTYGROUPLIST;

	public QuestionsModel(QuestionsOverview controller)
	{
		// TODO Auto-generated constructor stub
		super(controller);
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public ArrayList<String> getNames()
	{
		return names;
	}

	public void setNames(ArrayList<String> names)
	{
		this.names = names;
	}

	public QuestionState getState()
	{
		return state;
	}

	public void setState(QuestionState state)
	{
		this.state = state;
	}

	public List<String> getResults()
	{
		return results;
	}

	public void setResults(List<String> results)
	{
		this.results = results;
	}

}
