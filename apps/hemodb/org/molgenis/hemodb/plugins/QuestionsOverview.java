package org.molgenis.hemodb.plugins;

import java.io.OutputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.hemodb.plugins.QuestionsModel.QuestionState;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class QuestionsOverview extends EasyPluginController<QuestionsModel> {

	public QuestionsOverview(String name, ScreenController<?> parent) {
		super(name, parent);
		this.setModel(new QuestionsModel(this)); // the default model
		getModel().setState(QuestionState.BEGINNING);
		// create sub plugin
		// new MatrixManagerHemodb("QuestionsSub", this);
		
		//initialiseer all paginas 1x
		new QuestionOne("questionOne", this);
	}

	public ScreenView getView() {
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}

	@Override
	public void reload(Database db) throws Exception {
		// TODO Auto-generated method stub
		
		for(ScreenController child: this.getChildren()) child.reload(db);

	}

	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {	
		
		try {
			
			if(QuestionState.QUESTION1.equals(getModel().getState()))
			{
				//delegate to the module of question 1
				this.get("questionOne").handleRequest(db, request, out);
			}
				
			getModel().setAction(request.getAction());

			if ("back".equals(request.getAction())) {
				getModel().setState(QuestionState.BEGINNING);
			}

			// Which question to show?
			String question = request.getString("questions");
			if ("questionOne".equals(question)) {
				getModel().setState(QuestionState.QUESTION1);
				
				//get the q1
				QuestionOne q1 = (QuestionOne) this.get("questionOne");
				
			} else if ("questionTwo".equals(question)) {
				getModel().setState(QuestionState.QUESTION2);
			} else {
				System.out.println("something bad happened");
				
				//TODO implement other questions
			}

			// Submit button
			if (getModel().getAction().equals("submitInformation")) {
				System.out
						.println("we handled the information on the site submitted via the submit button");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Show.SHOW_MAIN;

	}
}
