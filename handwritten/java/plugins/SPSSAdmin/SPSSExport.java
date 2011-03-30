/* Date:        February 10, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * Despoina Antonakaki 
 */

package plugins.SPSSAdmin;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class SPSSExport<E extends Entity> extends PluginModel<E>
{

	private static final long serialVersionUID = -3120615290212884466L;

	public SPSSExport(String name, ScreenModel<E> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_SPSSAdmin_SPSSExport";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/SPSSAdmin/SPSSExport.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
		if ("SPSSExport".equals(request.getAction())) {
			System.out.println("SPSSExport begin....");
		     //ServletOutputStream out;
			
		     //this.exportToSpss(matrix, out);
			//http://localhost:8080/gcc/downloadspssfile?id=58342&download=all&stream=true
			//retrieve data and add in spss file  
			
		}	
			
			
			System.out.println("SPSSExport end....");
			
		}

	
//	private void exportToSpss(Object matrix, ServletOutputStream out) {
//
//		 try {
//			/** Create Spss file 
//			 *  Assign SPSS output to the file **/
//
//			   /** The columns of the matrix are the variables for the Spss file  */
//			   //get the columns from matrix  
//		       List<Column> columns = matrix.getColumns(); 
//		       
//		       for (int i=0; i<columns.size() ; i++) 
//		       System.out.println("SPss exporter "+ columns.get(i).getName());
//		       
//		       /** 1. Assign SPSS output to the file */
//		       SPSSWriter outSPSS = new SPSSWriter(out, "windows-1252");
//
//		       /** 2. Creating SPSS variable description table */
//		       outSPSS.setCalculateNumberOfCases(false);
//		       outSPSS.addDictionarySection(-1);
//
//		       /** 3. Describing variable names and types -- */
//		       //Let's assume these are the headers /column  name in the matrix TODO : see in output file and correct 
//		       List<String> colNames = getColNames(columns);
//		       
//		       
//		       //outSPSS.addStringVar("Column", 32, "this is the first column of the spss file");
//
//		       for (int i = 0; i < colNames.size(); i++) {
//			       outSPSS.addStringVar(colNames.get(i).toString(), colNames.size(), colNames.get(i));
//			       System.out.println("The :" + colNames.get(i).toString() + "was just added in the SPSS file");
//		       }
//		       
//		    
//
//		       /** 4. Create missing value -- TODO : Do we have  Missing values? */
//		       // MissingValue mv = new MissingValue();
//		       // mv.setOneDescreteMissingValue(1);
//		       // outSPSS.addNumericVar("count", 8, 2, "number of countries", mv);
//
//		       //Retrieve values from DB Matrix 
//		       Object[][] elements = matrix.getData();
//		       int numberOfRows = elements.length;
//
//		       /**5. Create value labels -   */
//		       // TODO : (Check if) Let's assume labels are the elements of the matrix (row headers of the excel)
//		       //for (int i = 0; i < elements.length; i++) {
//			   //    ValueLabels valueLabels = new ValueLabels();
//			   //    valueLabels.putLabel(44, "Forty four");
//			   //    outSPSS.addValueLabels(4 , valueLabels);
//			   //}  
//		   
//		       
//		       /** 6. Create SPSS variable value define table */
//		       outSPSS.addDataSection();
//
//		      
//		       int Insertedvalues = 0;
//		       
//		       /**
//		        * This is done because the data in the matrix is not strong type . We have to distinguish between different types
//		        */
//		       if (elements[0][0] instanceof Number) {  //instanceof NUMBER ???
//		           // TODO: format numbers?
//		           for (int i = 0; i < columns.size(); i++) {
//		               for (int j = 0; j < numberOfRows; j++) {		            	   
//		                   //Label l = new Label(i + 1, j + 1, elements[j][i].toString(), cellFormat);		                              
//		            	   outSPSS.addData( elements[j][i].toString());
//		               }
//		           }
//		       } else {
//		           for (int i = 0; i < columns.size(); i++) {
//		               for (int j = 0; j < numberOfRows; j++) {
//		                   if (elements[j][i] != null){
//		                	   Insertedvalues ++;
//			            	   outSPSS.addData(elements[j][i].toString());
////			            	   outSPSS.addData("another data field");
////			            	   outSPSS.addData("seriously where is this stored");
//		                   }
//		               }
//		           }
//		       }
//		       
//		       /** 8. Create SPSS ending section */
//		       outSPSS.addFinishSection();
//
//		       /** 9. Close output stream  */
//		       out.close();
//		     
		   
//	}

//	private Object getMatrix() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
