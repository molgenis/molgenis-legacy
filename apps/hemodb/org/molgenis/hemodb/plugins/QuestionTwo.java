package org.molgenis.hemodb.plugins;

import java.io.OutputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class QuestionTwo extends EasyPluginController<QuestionsModel>{

	public QuestionTwo(String name, ScreenController<?> parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ScreenView getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload(Database db) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {
				return null;
	
	}
}
