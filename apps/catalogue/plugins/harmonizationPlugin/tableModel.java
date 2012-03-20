package plugins.harmonizationPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class tableModel {

	private HashMap<String, Integer> headerToIndex = new HashMap<String, Integer>();
	private HashMap<Integer, String> IndexToHeader = new HashMap<Integer, String>();
	private List<Cell[]> rowContents = new ArrayList<Cell[]>();
	private String spreadSheetName = null;

	public tableModel(String spreadSheetName){
		this.spreadSheetName = spreadSheetName;
		this.processingTable();
	}

	public void processingTable(){

		File file = new File(spreadSheetName);

		Workbook workbook = null;

		try {
			workbook = Workbook.getWorkbook(file);

			Sheet sheet = workbook.getSheet(0);

			int rows = sheet.getRows();

			int columns = sheet.getColumns();

			for(int i = 0; i < rows; i++){

				if(i == 0){
					for(int j = 0; j < columns; j++){
						String cellValue = sheet.getCell(j, i).getContents().toString();
						headerToIndex.put(cellValue, j);
						IndexToHeader.put(j, cellValue);
					}
				}else{
					rowContents.add(sheet.getRow(i));
				}

			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<String, HashMap<String, String>> getColumnsFromTable(String identifier, List<String> description){

		HashMap<String, HashMap<String, String>> annotationOnThreeLevels = new HashMap<String, HashMap<String,String>>();
		
		List<Integer> extractedColumns = new ArrayList<Integer>();
		
		for(String eachDescription : description){
			extractedColumns.add(headerToIndex.get(eachDescription));
		}
		
		Integer indentiferKey = headerToIndex.get(identifier);
		
		for(Cell[] eachRow : rowContents){
			
			String indentifier = eachRow[indentiferKey].getContents();
			
			HashMap<String, String> headerToContent = new HashMap<String, String>();
			
			for(Integer eachIndex : extractedColumns){
				
				headerToContent.put(IndexToHeader.get(eachIndex), eachRow[eachIndex].getContents());
			}
			annotationOnThreeLevels.put(indentifier, headerToContent);
		}
		
		return annotationOnThreeLevels;
	}

	public HashMap<String, String> getDescriptionForVariable(String identifier, String description){
		
		HashMap <String, String> stringToTokens = new HashMap<String, String>();

		Integer identifierIndex = headerToIndex.get(identifier);
		
		Integer descriptionIndex = headerToIndex.get(description);
		
		for(Cell[] eachRow : rowContents){
			stringToTokens.put(eachRow[identifierIndex].getContents(), eachRow[descriptionIndex].getContents());
		}
		return stringToTokens;
	}
}
