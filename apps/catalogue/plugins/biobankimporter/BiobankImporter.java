package plugins.biobankimporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.molgenis.auth.Institute;
import org.molgenis.auth.Person;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import app.FillMetadata;

public class BiobankImporter extends PluginModel<Entity>
{
	private String Status = "";

	private List<String> headers = null;

	private TableController table = null;

	private File file = null;

	private String investigationName = "";

	private boolean importingFinished = true;

	private static final long serialVersionUID = 6149846107377048848L;

	private List<String> spreadSheetHeanders = new ArrayList<String>();

	private List<String> chooseClassType = new ArrayList<String>();

	private List<String> chooseFieldName = new ArrayList<String>();

	private List<String> dataTypeOptions = new ArrayList<String>();

	private List<Integer> columnIndex = new ArrayList<Integer>();

	private HashMap<String, String> userInputToDataType = new HashMap<String, String>();
	private HashMap<Integer, String> columnIndexToClassType = new HashMap<Integer, String>();
	private HashMap<Integer, Integer> columnIndexToRelation = new HashMap<Integer, Integer>();
	private HashMap<Integer, String> columnIndexToFieldName = new HashMap<Integer, String>();
	private HashMap<Integer, String> columnIndexToMultipleValue = new HashMap<Integer, String>();

	private List<List<String>> mappingForMolgenisEntity = new ArrayList<List<String>>();

	private String excelDirection = "UploadFileByColumn";

	private String uploadFileName = "";

	private int StepsFlag = 0;

	private int columnCount = 0;

	private int previousAddingDataType = 0;

	private String filePath = null;

	private Integer startingRowIndex = 0;

	private Boolean multipleSheets = true;

	private Boolean multipleValue = false;

	private Boolean sheetImportProtocol = true;

	public BiobankImporter(String name, ScreenController<?> parent)
	{
		super(name, parent);

		setChooseClassType();
	}

	public List<List<String>> getMappingForMolgenisEntity()
	{
		return mappingForMolgenisEntity;
	}

	public String getFilePath()
	{

		if (filePath != null)
		{
			return filePath;
		}
		else
		{
			return "The file is not available";
		}
	}

	public String getInvestigationName()
	{
		return investigationName;
	}

	public boolean isImportingFinished()
	{
		return importingFinished;
	}

	public void setImportingFinished(boolean importingFinished)
	{
		this.importingFinished = importingFinished;
	}

	public List<String> getChooseClassType()
	{
		return chooseClassType;
	}

	public List<String> getSpreadSheetHeanders()
	{
		return spreadSheetHeanders;
	}

	public void setSpreadSheetHeanders(List<String> spreadSheetHeanders)
	{
		this.spreadSheetHeanders = spreadSheetHeanders;
	}

	public void setChooseClassType()
	{

		chooseClassType.add(Measurement.class.getSimpleName());
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.DESCRIPTION);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.DATATYPE);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.LABEL);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.UNIT_NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.TEMPORAL);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.INVESTIGATION_NAME);
		chooseFieldName.add(Measurement.class.getSimpleName() + ":" + Measurement.CATEGORIES_NAME);
		chooseClassType.add(Protocol.class.getSimpleName());
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.FEATURES_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.INVESTIGATION_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.SUBPROTOCOLS_NAME);
		chooseFieldName.add(Protocol.class.getSimpleName() + ":" + Protocol.DESCRIPTION);
		// chooseClassType.add(ComputeProtocol.class.getSimpleName());
		// chooseFieldName.add(ComputeProtocol.class.getSimpleName() + ":" +
		// ComputeProtocol.NAME);
		// chooseFieldName.add(ComputeProtocol.class.getSimpleName() + ":" +
		// ComputeProtocol.FEATURES_NAME);
		// chooseFieldName.add(ComputeProtocol.class.getSimpleName() + ":" +
		// ComputeProtocol.SCRIPTTEMPLATE);
		chooseClassType.add(Category.class.getSimpleName());
		chooseClassType.add(Category.class.getSimpleName() + ":" + Category.ISMISSING);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.NAME);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.CODE_STRING);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.LABEL);
		chooseFieldName.add(Category.class.getSimpleName() + ":" + Category.DESCRIPTION);
		chooseFieldName.add(Person.class.getSimpleName() + ":" + Person.NAME);
		chooseFieldName.add(Person.class.getSimpleName() + ":" + Person.LASTNAME);
		chooseFieldName.add(Person.class.getSimpleName() + ":" + Person.FIRSTNAME);
		chooseFieldName.add(Institute.class.getSimpleName() + ":" + Institute.NAME);
		chooseFieldName.add(ObservedValue.class.getSimpleName());
		chooseFieldName.add(ObservationTarget.class.getSimpleName() + ":" + ObservationTarget.NAME);
		chooseFieldName.add(Panel.class.getSimpleName() + ":" + Panel.NAME);
		chooseFieldName.add(Panel.class.getSimpleName() + ":" + Panel.INDIVIDUALS_NAME);
		chooseFieldName.add(OntologyTerm.class.getSimpleName() + ":" + OntologyTerm.NAME);
		chooseFieldName.add("NULL");
		chooseClassType.add(ObservedValue.class.getSimpleName());
		chooseClassType.add(ObservationTarget.class.getSimpleName());
		chooseClassType.add(Person.class.getSimpleName());
		chooseClassType.add(Institute.class.getSimpleName());
		chooseClassType.add(Panel.class.getSimpleName());
		chooseClassType.add(OntologyTerm.class.getSimpleName());
		chooseClassType.add("NULL");

		dataTypeOptions.add("string");
		dataTypeOptions.add("int");
		dataTypeOptions.add("datetime");
		dataTypeOptions.add("categorical");
		dataTypeOptions.add("decimal");

	}

	public List<String> getChooseFieldName()
	{
		return chooseFieldName;
	}

	public void setChooseFieldName(List<String> chooseFieldName)
	{
		this.chooseFieldName = chooseFieldName;
	}

	public List<String> getDataTypeOptions()
	{
		return dataTypeOptions;
	}

	public void setDataTypeOptions(List<String> dataTypeOptions)
	{
		this.dataTypeOptions = dataTypeOptions;
	}

	@Override
	public String getViewName()
	{
		return "BiobankImporter";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/biobankimporter/BiobankImporter.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{

		mappingForMolgenisEntity.clear();

		investigationName = "";

		if ("UploadFileByColumn".equals(request.getAction()))
		{
			excelDirection = "UploadFileByColumn";
			System.out.println(request);
			uploadFileName = request.getString("uploadFile");
			filePath = request.getString("uploadFileOriginalFileName");
			if (uploadFileName != null && !uploadFileName.equals(""))
			{
				readHeaders(request);
			}
			else
			{
				this.setStatus("Please select a file to import!");
			}
			this.setStepsFlag(1);

		}
		else if ("UploadFileByRow".equals(request.getAction()))
		{

			excelDirection = "UploadFileByRow";
			System.out.println(request);
			uploadFileName = request.getString("uploadFile");

			if (uploadFileName != null && !uploadFileName.equals(""))
			{
				readHeaders(request);
			}
			else
			{
				this.setStatus("Please select a file to import!");
			}

			this.setStepsFlag(1);

		}
		else if ("backToPreviousStep".equals(request.getAction()))
		{

			importingFinished = true;

			multipleValue = false;

			file = null;

			filePath = null;

		}
		else if ("uploadMapping".equals(request.getAction()))
		{// Upload the
			// mapping
			// file to
			// avoid
			// repeated
			// work!

			String mappingFileName = request.getString("uploadMapping");

			if (mappingFileName != null)
			{

				File mappingFile = new File(mappingFileName);

				Workbook workbook = Workbook.getWorkbook(mappingFile);

				Sheet sheet = workbook.getSheet(0);

				int columns = sheet.getColumns();

				int rows = sheet.getRows();

				int startingRow = 0;

				if (sheet.getCell(0, startingRow).getContents().toString().equals("InvestigationName"))
				{

					investigationName = sheet.getCell(1, startingRow).getContents().toString();

					startingRow++;

				}
				else
				{
					investigationName = "";
				}

				for (int j = 0; j < columns; j++)
				{

					List<String> mappingForEachColumn = new ArrayList<String>();

					for (int i = startingRow; i < rows; i++)
					{

						mappingForEachColumn.add(sheet.getCell(j, i).getContents().toString());

					}

					mappingForMolgenisEntity.add(mappingForEachColumn);
				}
			}

		}
		else if ("saveMapping".equals(request.getAction()))
		{

			List<List<String>> twoDimensionalTable = new ArrayList<List<String>>();

			WorkbookSettings ws = new WorkbookSettings();

			ws.setLocale(new Locale("en", "EN"));

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File mappingResult = new File(tmpDir + File.separator + "mappingResult.xls");

			WritableWorkbook workbook = Workbook.createWorkbook(mappingResult, ws);

			WritableSheet outputExcel = workbook.createSheet("Sheet1", 0);

			String investigationName = null;

			int startingRow = 0;

			if (request.getString("investigation") != null)
			{
				investigationName = request.getString("investigation");
				outputExcel.addCell(new Label(0, startingRow, "InvestigationName"));
				outputExcel.addCell(new Label(1, startingRow, investigationName));
				startingRow++;
			}

			if (headers != null)
			{
				for (int columnCount = 0; columnCount < headers.size(); columnCount++)
				{
					// adding headers to the mapping file
					outputExcel.addCell(new Label(columnCount, startingRow, headers.get(columnCount)));

					if (request.getList(headers.get(columnCount)) != null)
					{
						twoDimensionalTable.add((List<String>) request.getList(headers.get(columnCount)));
					}
				}
			}

			if (twoDimensionalTable.size() > 0)
			{

				for (int i = 0; i < twoDimensionalTable.size(); i++)
				{

					if (twoDimensionalTable.get(i).size() < 4)
					{
						twoDimensionalTable.get(i).add("false");
					}

					for (int j = 0; j < twoDimensionalTable.get(i).size(); j++)
					{

						outputExcel.addCell(new Label(i, j + 1 + startingRow, twoDimensionalTable.get(i).get(j)));
					}

					if (twoDimensionalTable.get(i).get(1)
							.equals(Measurement.class.getSimpleName() + ":" + Measurement.DATATYPE))
					{

						String member = headers.get(i);

						// int addedNumberOfDataType = previousAddingDataType;

						previousAddingDataType = 0;

						for (int index = 0; index < request.getInt("__dataTypeCount"); index++)
						{

							if (request.getString(member + "_options_" + index) != null)
							{
								String eachMember = request.getString(member + "_options_" + index);
								System.out.println(eachMember.toString() + " Molgenis option!");
								String MolgenisDataTypeOption = eachMember.toString();

								if (request.getString(member + "_input_" + index) != null)
								{
									String userInputDatType = request.getString(member + "_input_" + index);
									String dataType = MolgenisDataTypeOption + ";" + userInputDatType;
									outputExcel.addCell(new Label(i, twoDimensionalTable.get(i).size()
											+ previousAddingDataType + 1 + startingRow, dataType));
								}
								previousAddingDataType++;
							}
						}
					}
				}
			}

			System.out.println("I am coming to the saveMapping mode!++++++++++++++!");

			workbook.write();
			workbook.close();

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest = rt.getRequest();
			HttpServletResponse httpResponse = rt.getResponse();
			// System.out.println(">>> " + this.getParent().getName()+
			// "or >>>  "+ this.getSelected().getLabel());
			// String redirectURL = httpRequest.getRequestURL() + "?__target=" +
			// this.getParent().getName() + "&select=MeasurementsDownloadForm";
			String redirectURL = "tmpfile/mappingResult.xls";

			httpResponse.sendRedirect(redirectURL);

		}
		else if ("ImportLifelineToPheno".equals(request.getAction()))
		{

			int count = 0;

			String MolgenisDataTypeOption = null;

			String userInputDatType = null;

			int columnIndex = 0;

			if (headers != null)
			{
				for (String member : headers)
				{
					if (request.getList(member) != null)
					{
						int index = 0;

						for (Object eachMember : request.getList(member))
						{
							System.out.println(eachMember.toString());
							if (index == 0)
							{
								columnIndexToClassType.put(columnIndex, eachMember.toString());
								index++;

							}
							else if (index == 1)
							{

								columnIndexToFieldName.put(columnIndex, eachMember.toString());
								index++;

							}
							else if (index == 2)
							{
								System.out.println(columnIndex + "-------------------------->" + eachMember.toString());
								columnIndexToRelation.put(columnIndex, Integer.parseInt(eachMember.toString()));
								index++;
							}
							else if (index == 3)
							{
								columnIndexToMultipleValue.put(columnIndex, "true");

							}
						}
					}

					if (request.getBool(columnIndex) != null)
					{
						System.out.println();
					}
					else
					{

					}

					columnIndex++;

					while (request.getString(member + "_options_" + count) != null)
					{
						String eachMember = request.getString(member + "_options_" + count);
						System.out.println(eachMember.toString() + " Molgenis option!");
						MolgenisDataTypeOption = eachMember.toString();

						if (request.getString(member + "_input_" + count) != null)
						{
							userInputDatType = request.getString(member + "_input_" + count);

							userInputToDataType.put(MolgenisDataTypeOption, userInputDatType);
						}
						count++;
					}

				}

				if (request.getString("investigation") != null)
				{
					investigationName = request.getString("investigation");

				}

				loadDataFromExcel(db, request, null);

			}
			else
			{
				setStatus("Please do the step one first!");
			}

		}
		else if ("fillinDatabase".equals(request.getAction()))
		{

			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			this.setStatus("The database is empty now");
		}

	}

	public void readHeaders(Tuple request) throws BiffException, IOException
	{

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		filePath = tmpDir.getAbsolutePath() + "/" + filePath;

		file = new File(uploadFileName);

		importingFinished = false;

		if (file.exists())
		{

			setStatus("");

			Workbook workbook = Workbook.getWorkbook(file);

			Sheet sheet = workbook.getSheet(0);

			int columns = sheet.getColumns();

			int rows = sheet.getRows();

			headers = new ArrayList<String>();

			columnIndex.add(0);

			startingRowIndex = request.getInt("startingRowIndex");

			startingRowIndex--;

			if (request.getBool("multipleSheets") != null)
			{
				multipleSheets = request.getBool("multipleSheets");
			}

			if (request.getBool("sheetImportProtocol") != null)
			{
				sheetImportProtocol = request.getBool("sheetImportProtocol");
			}

			if (request.getAction().equals("UploadFileByColumn"))
			{
				setColumnCount(columns);

				for (int i = 0; i < columns; i++)
				{
					columnIndex.add(i + 1);
					headers.add(sheet.getCell(i, startingRowIndex).getContents().toString().replaceAll(" ", "_"));
					System.out.println(sheet.getCell(i, startingRowIndex).getContents().toString());

				}

				setSpreadSheetHeanders(headers);
			}

			if (request.getAction().equals("UploadFileByRow"))
			{
				setColumnCount(rows);

				for (int i = 0; i < rows; i++)
				{
					columnIndex.add(i);
					headers.add(sheet.getCell(0, i).getContents().toString().replaceAll(" ", "_"));
					System.out.println(sheet.getCell(0, i).getContents().toString());
				}

				setSpreadSheetHeanders(headers);
			}

		}
		else
		{

			this.setStatus("Please upload a file first!");
		}
	}

	public List<Integer> getColumnIndex()
	{
		return columnIndex;
	}

	public void setColumnIndex(List<Integer> columnIndex)
	{
		this.columnIndex = columnIndex;
	}

	@SuppressWarnings("unchecked")
	public void loadDataFromExcel(Database db, Tuple request, Investigation inv) throws BiffException, IOException,
			DatabaseException
	{

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		// File file = new File(tmpDir+ "/DataShaperExcel.xls");

		if (file.exists())
		{

			System.out.println("The excel file is being imported, please be patient");

			this.setStatus("The excel file is being imported, please be patient");

			Workbook workbook = Workbook.getWorkbook(file);

			Sheet dictionaryCategory = workbook.getSheet(0);

			table = new TableController(dictionaryCategory.getColumns(), db);

			{
				List<String> referenceClass = new ArrayList<String>();
				referenceClass.add(Measurement.CATEGORIES_NAME);
				referenceClass.add(Protocol.SUBPROTOCOLS_NAME);
				referenceClass.add(Protocol.FEATURES_NAME);

				for (Integer columnIndex : columnIndexToClassType.keySet())
				{
					// columnIndex--;

					String classType = columnIndexToClassType.get(columnIndex);

					String fieldName = columnIndexToFieldName.get(columnIndex);

					String multipleValues = "false";

					if (columnIndexToMultipleValue.containsKey(columnIndex))
					{
						multipleValues = columnIndexToMultipleValue.get(columnIndex);
					}

					String splitByColon[] = fieldName.toString().split(":");

					fieldName = fieldName.toString().split(":")[splitByColon.length - 1];

					Integer dependedColumn = columnIndexToRelation.get(columnIndex);

					dependedColumn--;

					table.setDirection(excelDirection);

					if (classType.equals(ObservedValue.class.getSimpleName()))
					{
						int coHeaders[] =
						{ columnIndex.intValue() };
						System.out.println(columnIndex);
						table.addField(classType, ObservedValue.VALUE, multipleValues, coHeaders,
								dependedColumn.intValue(), TableField.COLHEADER);

					}
					else if (classType.equals(Category.class.getSimpleName() + ":" + Category.ISMISSING))
					{

						Tuple defaults = new SimpleTuple();
						defaults.set(Category.ISMISSING, true);
						table.addField(Category.class.getSimpleName(), "name", multipleValues, columnIndex.intValue(),
								TableField.COLVALUE, defaults);
						table.addField(classType, fieldName, multipleValues, TableField.COLVALUE,
								dependedColumn.intValue(), columnIndex.intValue());

					}
					else
					{

						if (dependedColumn.intValue() == -1)
						{
							table.addField(classType, fieldName, multipleValues, columnIndex.intValue(),
									TableField.COLVALUE);

						}
						else
						{

							if (referenceClass.contains(fieldName))
							{
								table.addField(classType, "name", multipleValues, columnIndex.intValue(),
										TableField.COLVALUE);
							}

							table.addField(classType, fieldName, multipleValues, TableField.COLVALUE,
									dependedColumn.intValue(), columnIndex.intValue());

							if (classType.equals(Measurement.class.getSimpleName())
									&& fieldName.equals(Measurement.DATATYPE))
							{

								for (String molgenisOption : userInputToDataType.keySet())
								{
									table.setDataType(userInputToDataType.get(molgenisOption), molgenisOption);
								}
							}
						}
					}
				}

				table.setInvestigationName(investigationName);

				table.convertIntoPheno(workbook.getSheets(), startingRowIndex, multipleSheets, sheetImportProtocol);

			}

			this.setStatus("finished!");

			importingFinished = true;

		}
		else
		{

			this.setStatus("The file should be in " + file);

		}
	}

	@Override
	public void reload(Database db)
	{
	}

	public void setStatus(String status)
	{
		Status = status;
	}

	public String getStatus()
	{
		return Status;
	}

	public void setStepsFlag(int stepsFlag)
	{
		StepsFlag = stepsFlag;
	}

	public int getStepsFlag()
	{
		return StepsFlag;
	}

	public void setColumnCount(int columnCount)
	{
		this.columnCount = columnCount;
	}

	public boolean getColumnCount()
	{
		if (this.columnCount > 5) return true;
		else
			return false;
	}

	public String getMultipleValue()
	{
		return multipleValue.toString();
	}
}