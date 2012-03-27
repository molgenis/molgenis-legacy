package plugins.harmonizationPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.binding.corba.wsdl.Array;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class tableModel {

	private HashMap<String, Integer> headerToIndex = new HashMap<String, Integer>();
	private HashMap<Integer, String> IndexToHeader = new HashMap<Integer, String>();
	private HashMap<String, List<Cell[]>> sheetNameToContents = new HashMap<String, List<Cell[]>>();
//	private List<Cell[]> rowContents = new ArrayList<Cell[]>();
	private String spreadSheetName = null;
	private int startingRow = 0;
	private boolean allSheets = false;


	public tableModel(String spreadSheetName, boolean allSheets){
		this.spreadSheetName = spreadSheetName;
		this.allSheets = allSheets;
	}

	public void setStartingRow (int startingRow){
		this.startingRow = startingRow - 1;
	}

	public void processingTable(){

		File file = new File(spreadSheetName);

		Workbook workbook = null;

		try {
			workbook = Workbook.getWorkbook(file);

			Sheet[] sheetNames = workbook.getSheets();

			for(int index = 0; index < sheetNames.length; index++){
				processingEachSheet(workbook, index);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void processingEachSheet(Workbook workbook, int sheetIndex){

		Sheet sheet = workbook.getSheet(sheetIndex);

		int rows = sheet.getRows();

		int columns = sheet.getColumns();

		List<Cell[]> rowContents = new ArrayList<Cell[]>();

		for(int i = startingRow; i < rows; i++){

			if(i == this.startingRow){
				for(int j = 0; j < columns; j++){
					String cellValue = sheet.getCell(j, i).getContents().toString();
					if(!cellValue.equals("")){
						headerToIndex.put(cellValue, j);
						IndexToHeader.put(j, cellValue);
					}
				}
			}else{
				rowContents.add(sheet.getRow(i));
			}

		}

		sheetNameToContents.put(sheet.getName(), rowContents);
	}

//	public HashMap<String, HashMap<String, String>> getColumnsFromTable(String identifier, List<String> description){
//
//		HashMap<String, HashMap<String, String>> annotationOnThreeLevels = new HashMap<String, HashMap<String,String>>();
//
//		List<Integer> extractedColumns = new ArrayList<Integer>();
//
//		for(String eachDescription : description){
//			extractedColumns.add(headerToIndex.get(eachDescription));
//		}
//
//		Integer indentiferKey = headerToIndex.get(identifier);
//
//		for(Cell[] eachRow : rowContents){
//
//			String indentifier = eachRow[indentiferKey].getContents();
//
//			HashMap<String, String> headerToContent = new HashMap<String, String>();
//
//			for(Integer eachIndex : extractedColumns){
//
//				headerToContent.put(IndexToHeader.get(eachIndex), eachRow[eachIndex].getContents());
//			}
//			annotationOnThreeLevels.put(indentifier, headerToContent);
//		}
//
//		return annotationOnThreeLevels;
//	}

	public HashMap<String, String> getDescriptionForVariable(String identifier, String description){

		HashMap <String, String> stringToTokens = new HashMap<String, String>();

		Integer identifierIndex = headerToIndex.get(identifier);

		Integer descriptionIndex = headerToIndex.get(description);

		if(identifierIndex != null && descriptionIndex != null){

			for(String eachSheetName : sheetNameToContents.keySet()){
				for(Cell[] eachRow : sheetNameToContents.get(eachSheetName)){
					if(eachRow.length > identifierIndex && eachRow.length > descriptionIndex)
						stringToTokens.put(eachRow[identifierIndex].getContents(), eachRow[descriptionIndex].getContents());
				}
			}
		}
		System.out.println(stringToTokens.keySet().size());
		return stringToTokens;
	}
}
