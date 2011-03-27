package lifelines.matrix.Exporter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.pheno.Measurement;

import com.pmstation.spss.SPSSWriter;

import lifelines.matrix.Column;
import lifelines.matrix.ColumnUtils;
import lifelines.matrix.PagableMatrix;

public class SpssExporter implements MatrixExporter {

	@Override
	public void export(PagableMatrix matrix, OutputStream out)  throws IOException, Exception {
		 try {
				  /** Create Spss file 
				   *  Assign SPSS output to the file 
				   *  The columns of the matrix are the variables for the Spss file  */
			       
			 	   List<Column> columns = matrix.getColumns();
			       List<Measurement> measurements = new ArrayList<Measurement>();
			       
			       for(Column c : columns) {
			    	   measurements.add(ColumnUtils.getMeasurementByName(matrix.getInvestigation(), c.getName()));
			       }
			      
			       //Some debug print for(Measurement m : measurements) {
			       //	   System.out.println(m.getDataType());
			       //}
			       
			       
			       /** 1. Assign SPSS output to the file */
			       SPSSWriter outSPSS = new SPSSWriter(out, "windows-1252");

			       /** 2. Creating SPSS variable description table */
			       outSPSS.setCalculateNumberOfCases(false);
			       outSPSS.addDictionarySection(-1);

			       /** 3. Describing variable names and types -- */
			       List<String> colNames = ColumnUtils.getColNames(columns);
			       			       
			       for (int i = 0; i < colNames.size(); i++) {
				       
			    	   System.out.println("SPss exporter "+ columns.get(i).getName());

			    	   if (columns.get(i).getType().toString() == "Code" || 
			    		   columns.get(i).getType().toString() == "Integer") { 
					       outSPSS.addNumericVar(colNames.get(i).toString(), 0, 0, columns.get(i).getName().toString());
			    	   } else if (columns.get(i).getType().toString() == "String") {
			    		   outSPSS.addStringVar(colNames.get(i).toString(), 10, columns.get(i).toString());
			    	   } else if (columns.get(i).getType().toString() == "Decimal") {
			    		   System.out.println(columns.get(i).toString());
			    		   outSPSS.addNumericVar(columns.get(i).toString(), 10, 10, columns.get(i).toString());
			    	   }
		    	   
				       //outSPSS.addStringVar(colNames.get(i).toString(), colNames.size(), colNames.get(i));
				       System.out.println("The :" + colNames.get(i).toString() + "was just added in the SPSS file");
			       }
			       
			       	    

			       /** 4. Create missing value in case there are */
			       // MissingValue mv = new MissingValue();
			       // mv.setOneDescreteMissingValue(1);
			       // outSPSS.addNumericVar("count", 8, 2, "the missing value", mv);

			       //Retrieve values from DB Matrix 
			       Object[][] elements = matrix.getData();
			       int numberOfRows = elements.length;

			       /**5. Create value labels -   */
			       // TODO : (Check if) Let's assume labels are the elements of the matrix (row headers of the excel)
			       //for (int i = 0; i < elements.length; i++) {
				   //    ValueLabels valueLabels = new ValueLabels();
				   //    valueLabels.putLabel(44, "Forty four");
				   //    outSPSS.addValueLabels(4 , valueLabels);
				   //}  
			   
			       /** 6. Create SPSS variable value define table 
			        *  Add in case all the potential values the measurement getDataType takes 
			        *  and fill in the appropriate type in spss ;
			        *  */
			       outSPSS.addDataSection();
			       
			       for (int i=0; i<columns.size(); i++){
		               for (int j = 0; j < numberOfRows; j++) {		            	   
		            	 if (elements[j][i] != null) {  
				    	   Column.ColumnType columnType = columns.get(i).getType();
				    	   System.out.println("____the column Type is : " +  columnType.toString());
				    	   
			            	/** Although it's a Integer  it's added as double with zero decimal points. This is  declared as int by setting decimal point to 0 in  outSPSS.addNumericVar */
				    	   if (columns.get(i).getType().toString() == "Code" || columns.get(i).getType().toString() == "Integer") { 
			            	   	Integer value = Integer.parseInt(elements[j][i].toString());
				    	   		//Integer value = Integer.parseInt(columns.get(i).toString());
					            System.out.println(value);
					            outSPSS.addData(value.doubleValue()); 
				    	   	} else if (columns.get(i).getType().toString() == "Decimal") { 
				    	   		Double doubleValue = Double.parseDouble(elements[j][i].toString());
				    	   		System.out.println(doubleValue);
				            	if (doubleValue != null) outSPSS.addData(doubleValue); 			    	   	
   		
				    	   		//Long value = Long.parseLong(elements[j][i].toString());
			    	   			//if (value == null) value = Long.valueOf(elements[j][i].toString());
			    	   	
			    	   			//System.out.println(value);
				            	//if (value != null) outSPSS.addData(value.doubleValue()); 			    	   	
				    	   	} else if (columns.get(i).getType().toString() == "String") { 
			    	   			String value = elements[j][i].toString();
				            	System.out.println(value);
				            	outSPSS.addData(value);			    	   	
				    	   	} 
		            	 }
			    	}
			       }			       
			       
			      
//			       if (elements[0][0] instanceof Number) {  //instanceof NUMBER ???
//			           // TODO: format numbers?
//			           for (int i = 0; i < columns.size(); i++) {
//			               for (int j = 0; j < numberOfRows; j++) {		            	   
//			                   //Label l = new Label(i + 1, j + 1, elements[j][i].toString(), cellFormat);		                              
//			            	   Integer value = Integer.parseInt(elements[j][i].toString());
//			            	   System.out.println(value);
//			            	   outSPSS.addData(value.doubleValue());
//			            	   //outSPSS.addData( elements[j][i].toString());
//			               }
//			           }
//			       } else {
//			           for (int i = 0; i < columns.size(); i++) {
//			               for (int j = 0; j < numberOfRows; j++) {
//			                   if (elements[j][i] != null){
//			                	   Insertedvalues ++;
//				            	   Integer value = Integer.parseInt(elements[j][i].toString());
//				            	   System.out.println(value);
//				            	   outSPSS.addData(value.doubleValue());		                	   
//			                	   
//				            	   //outSPSS.addData(elements[j][i].toString());
////				            	   outSPSS.addData("another data field");
////				            	   outSPSS.addData("seriously where is this stored");
//			                   }
//			               }
//			           }
//			       }
			       
			       /** 8. Create SPSS ending section */
			       outSPSS.addFinishSection();

			       /** 9. Close output stream  */
			       out.close();
			     }
			     catch (FileNotFoundException exOb) {
			       System.out.println("FileNotFoundException (Demo.main): " +
			           exOb.getMessage());
			       exOb.printStackTrace(System.out);
			       return;
			     }
			     catch (IOException exOb) {
			       System.out.println("IOException (Demo.main): " + exOb.getMessage());
			       exOb.printStackTrace(System.out);
			       return;
			     }
	}

	@Override
	public String getContentType() {
		return "application/spss";
	}

	@Override
	public String getFileExtenstion() {
		return "sav";
	}
}