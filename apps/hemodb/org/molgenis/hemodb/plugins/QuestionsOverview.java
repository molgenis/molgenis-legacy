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

public class QuestionsOverview extends EasyPluginController<QuestionsModel>{

	public QuestionsOverview(String name, ScreenController<?> parent) {
		super(name, parent);
		//this.setModel(new QuestionsModel(this)); // the default model
		
		//create sub plugin
		//new MatrixManagerHemodb("QuestionsSub", this);
	}

	public ScreenView getView() {
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}

	@Override
	public void reload(Database db) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {
		
		if("back".equals(request.getAction()))
		{
			getModel().setState(QuestionState.QUESTION1);
		}
		
		
		
		return Show.SHOW_MAIN;
				
				
		
	}
	
}
