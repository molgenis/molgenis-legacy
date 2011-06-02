
package org.molgenis.ngs.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

/**
 * ImportWorksheetController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>ImportWorksheetModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>ImportWorksheetView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class ImportWorksheet extends EasyPluginController<ImportWorksheetModel>
{
	public ImportWorksheet(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new ImportWorksheetModel(this)); //the default model
		this.setView(new FreemarkerView("ImportWorksheetView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
//		//example: update model with data from the database
//		Query q = db.query(Investigation.class);
//		q.like("name", "molgenis");
//		getModel().investigations = q.find();
		
	}
	
	public void uploadaction(Database db, Tuple request) throws Exception
	{
		File file = request.getFile("upload");
//		File file = new File(request.getString("upload"));
		/*if (file == null) {
			throw new Exception("No file selected.");
		} else if (!file.getName().endsWith(".csv")) {
			throw new Exception("File does not end with '.csv', other formats are not supported.");
		}*/

		System.out.println(">> Start reading csv");
		
		CsvReader reader = new CsvFileReader(new File("/Users/mdijkstra/Desktop/lane-barcodes.csv"));
//		System.out.println(">>" + reader.)
		reader.setSeparator(',');
		System.out.println(">> " + reader.colnames());
		reader.parse(new CsvReaderListener()
		{
			@Override
			public void handleLine(int line_number, Tuple tuple) throws Exception
			{
				System.out.println(">>" + line_number);
				System.out.println(tuple);
//				tuple.getString("status")
			}
		});
		
		System.out.println(">> Stop");
		getModel().setSuccess("UPLOAD " + request.getString("uploadOriginalFileName"));
	}
	
	
	/**
	 * When action="updateDate": update model and/or view accordingly.
	 *
	 * Exceptions will be logged and shown to the user automatically.
	 * All db actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception
	{
		getModel().date = request.getDate("date");
	
//		//Easily create object from request and add to database
//		Investigation i = new Investigation(request);
//		db.add(i);
//		this.setMessage("Added new investigation");

		getModel().setSuccess("update succesfull");
	}
}