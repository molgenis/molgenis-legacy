/* Date:        February 27, 2012
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.hemodb.plugins;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;

/**
 * EditIndividualModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class QuestionsModel extends EasyPluginModel
{

	String action = null;
	ArrayList <String> names = new ArrayList <String>();

	public enum QuestionState {
		EMPTYGROUPLIST,
		QUESTION1,
		QUESTION1_RESULT,
		QUESTION2,
		QUESTION2_RESULT,
		BEGINNING
	}
	
	private QuestionState state = QuestionState.EMPTYGROUPLIST;
	
	
	public QuestionsModel(QuestionOne controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
	
	public QuestionsModel(QuestionsOverview controller) {
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
}
