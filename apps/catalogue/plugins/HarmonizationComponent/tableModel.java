package plugins.HarmonizationComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class tableModel {

	private List<String> header = new ArrayList<String>();
	private HashMap<String, Integer> headerToIndex = new HashMap<String, Integer>();
	private HashMap<Integer, String> IndexToHeader = new HashMap<Integer, String>();
	private HashMap<String, List<Cell[]>> sheetNameToContents = new HashMap<String, List<Cell[]>>();
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
						header.add(cellValue);
					}
				}
			}else{
				rowContents.add(sheet.getRow(i));
			}

		}

		sheetNameToContents.put(sheet.getName(), rowContents);
	}

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
	
	public List<String> getColumn(String identifier){

		List<String> columns = new ArrayList<String>();

		Integer identifierIndex = headerToIndex.get(identifier);

		if(identifierIndex != null){

			for(String eachSheetName : sheetNameToContents.keySet()){
				for(Cell[] eachRow : sheetNameToContents.get(eachSheetName)){
					columns.add(eachRow[identifierIndex].getContents());
				}
			}
		}
		return columns;
	}
	
	public List<String> getHeaders(){
		return this.header;
	}
}
