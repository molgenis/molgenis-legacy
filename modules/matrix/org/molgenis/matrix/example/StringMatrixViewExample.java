package org.molgenis.matrix.example;

import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.StringMemoryMatrix;
import org.molgenis.matrix.ui.StringMatrixView;
import org.molgenis.util.Tuple;

/**
 * StringMatrixViewExampleController takes care of all user requests and
 * application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>StringMatrixViewExampleModel holds application state and business
 * logic on top of domain model. Get it via this.getModel()/setModel(..) <li>
 * StringMatrixViewExampleView holds the template to show the layout. Get/set it
 * via this.getView()/setView(..).
 */
public class StringMatrixViewExample extends
		EasyPluginController<StringMatrixViewExampleModel>
{
	public StringMatrixViewExample(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new StringMatrixViewExampleModel(this)); // the default
		// model
		this.setView(new FreemarkerView("StringMatrixViewExampleView.ftl",
				getModel())); // <plugin flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception
	{

	}

	@Override
	public String render()
	{
		try
		{
			List<String> features = Arrays.asList(new String[]
			{ "f1", "f2", "f3", "f4", "f5" });
			List<String> targets = Arrays.asList(new String[]
			{ "t1", "t2", "t3" });
			StringMemoryMatrix m;

			m = new StringMemoryMatrix(targets, features);

			for (String t : targets)
			{
				for (String f : features)
				{
					m.setValue(t, f, t + f);
				}
			}

			StringMatrixView view = new StringMatrixView("test", m);

			return view.render();

		}
		catch (MatrixException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR " + e.getMessage();
		}

	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * 
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception
	{
		getModel().date = request.getDate("date");

		// //Easily create object from request and add to database
		// Investigation i = new Investigation(request);
		// db.add(i);
		// this.setMessage("Added new investigation");

		getModel().setSuccess("update succesfull");
	}
}