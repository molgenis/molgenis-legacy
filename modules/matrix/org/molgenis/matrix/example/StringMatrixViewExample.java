package org.molgenis.matrix.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Label;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.StringMemoryMatrix;
import org.molgenis.matrix.ui.StringMatrixView;
import org.molgenis.util.Tuple;

/**
 * Can be removed?
 */
public class StringMatrixViewExample extends
		EasyPluginController<StringMatrixViewExampleModel>
{
	// these should be handled by the matrixview!
	List<String> selectedRows = new ArrayList<String>();
	List<String> selectedCols = new ArrayList<String>();
	StringMemoryMatrix m = null;

	public StringMatrixViewExample(String name, ScreenController<?> parent)
	{
		super(name, parent);
		try
		{

			this.setModel(new StringMatrixViewExampleModel(this)); // the
			// default
			// model

			List<String> features = Arrays.asList(new String[]
			{ "f1", "f2", "f3", "f4", "f5" });
			List<String> targets = Arrays.asList(new String[]
			{ "t1", "t2", "t3" });
			StringMemoryMatrix m;

			this.m = new StringMemoryMatrix(targets, features);

			for (String t : targets)
			{
				for (String f : features)
				{
					this.m.setValue(t, f, t + f);
				}
			}

		}
		catch (MatrixException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.getModel().setMessages(
					new ScreenMessage("ERROR " + e.getMessage(), false));
		}
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("StringMatrixViewExampleView.ftl",
				getModel());
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
		if(this.selectedCols == null || this.selectedCols.size() == 0)
		{
			this.selectedCols = m.getColNamesByOffset(0,1);
		}
	}
	
	/**
	 * HandleRequest action 'doClearAll'
	 * @param db
	 */
	public void doReset(Database db, Tuple t)
	{
		this.selectedCols = null;
	}
	
	/**
	 * HandleRequest action 'doCelectCol'
	 * @param db
	 * @param t
	 */
	public void doSelectCol(Database db, Tuple t)
	{
		if(!this.selectedCols.contains(t.getString("col")))
			this.selectedCols.add(t.getString("col"));
	}

	@Override
	public String render()
	{

		try
		{
			MolgenisForm form = new MolgenisForm(this.getModel());
			
			form.add(new Label("l2","Selected matrix:"));
			
			SelectInput selectCol = new SelectInput("col");
			selectCol.setOptions(m.getColNames(), m.getColNames());
			selectCol.setOnchange("");
			form.add(selectCol);

			form.add(new ActionInput("doSelectCol"));
			form.add(new ActionInput("doReset"));

			//render all rows and only selected cols; default column 1
			Matrix<String,String,String> visible = m.getSubMatrixByName(m.getRowNames(), this.selectedCols);
			form.add(new StringMatrixView("test", visible));
			
			
			form.add(new Label("l1","Whole matrix:"));
			form.add(new StringMatrixView("all", m));

			return form.render();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR "+e.getMessage();
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