package org.molgenis.matrix.component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.MenuInput;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.Observation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.pmstation.spss.SPSSWriter;

public class MatrixViewer extends HtmlWidget
{
	ScreenController<?> callingScreenController;

	SliceableMatrix<?, ?, ?> matrix;
	Logger logger = Logger.getLogger(this.getClass());
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

	// Configuration booleans. TODO: solve in a nicer way; use a plugin
	// strategy?
	private boolean showLimitControls = true;
	private boolean columnsRestricted = false;
	private boolean selectMultiple = true;
	private boolean showValueValidRange = false;
	private boolean showDownloadOptions = false;

	private String downloadLink = null;

	public String ROWLIMIT = getName() + "_rowLimit";
	public String CHANGEROWLIMIT = getName() + "_changeRowLimit";
	// public String COLLIMIT = getName() + "_colLimit";
	// public String CHANGECOLLIMIT = getName() + "_changeColLimit";
	public String MOVELEFTEND = getName() + "_moveLeftEnd";
	public String MOVELEFT = getName() + "_moveLeft";
	public String MOVERIGHT = getName() + "_moveRight";
	public String MOVERIGHTEND = getName() + "_moveRightEnd";
	public String MOVEUPEND = getName() + "_moveUpEnd";
	public String MOVEUP = getName() + "_moveUp";
	public String MOVEDOWN = getName() + "_moveDown";
	public String MOVEDOWNEND = getName() + "_moveDownEnd";
	public String DOWNLOADALLCSV = getName() + "_downloadAllCsv";
	public String DOWNLOADALLEXCEL = getName() + "_downloadAllExcel";
	public String DOWNLOADALLSPSS = getName() + "_downloadAllSPSS";	
	public String DOWNLOADVISCSV = getName() + "_downloadVisibleCsv";
	public String DOWNLOADVISSPSS = getName() + "_downloadVisibleSPSS"; 
	public String DOWNLOADVISEXCEL = getName() + "_downloadVisibleExcel";
	public String COLID = getName() + "_colId";
	public String COLVALUE = getName() + "_colValue";
	public String FILTERCOL = getName() + "_filterCol";
	public String ROWHEADER = getName() + "_rowHeader";
	public String ROWHEADEREQUALS = getName() + "_rowHeaderEquals";
	public String CLEARFILTERS = getName() + "_clearValueFilters";
	public String REMOVEFILTER = getName() + "_removeFilter";
	public String RELOADMATRIX = getName() + "_reloadMatrix";
	public String SELECTED = getName() + "_selected";
	public String UPDATECOLHEADERFILTER = getName() + "_updateColHeaderFilter";
	public String ADDALLCOLHEADERFILTER = getName() + "_addAllColHeaderFilter";
	public String REMALLCOLHEADERFILTER = getName() + "_remAllColHeaderFilter";
	public String MEASUREMENTCHOOSER = getName() + "_measurementChooser";
	public String OPERATOR = getName() + "_operator";
	private String stat;
	private String extension;
	// hack to pass database to toHtml() via toHtml(db)
	private Database db;

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	/**
	 * Default constructor.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 */
	public MatrixViewer(ScreenController<?> callingScreenController, String name,
			SliceableMatrix<?, ?, ?> matrix,
			boolean showLimitControls, boolean selectMultiple, boolean showDownloadOptions,
			List<MatrixQueryRule> filterRules)
	{
		super(name);
		super.setLabel("");
		this.callingScreenController = callingScreenController;
		this.matrix = matrix;
		this.showLimitControls = showLimitControls;
		this.selectMultiple = selectMultiple;
		this.showDownloadOptions = showDownloadOptions;
		if (filterRules != null)
		{
			this.matrix.getRules().addAll(filterRules);
		}
	}

	/**
	 * Constructor where you immediately restrict the column set by applying a
	 * colHeader filter rule.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 * @param filterRules
	 * @throws Exception
	 */
	public MatrixViewer(ScreenController<?> callingScreenController, String name,
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix,
			boolean showLimitControls, boolean selectMultiple, boolean showDownloadOptions,
			List<MatrixQueryRule> filterRules, MatrixQueryRule columnRule) throws Exception
	{
		this(callingScreenController, name, matrix, showLimitControls, selectMultiple, showDownloadOptions, filterRules);
		if (columnRule != null && columnRule.getFilterType().equals(MatrixQueryRule.Type.colHeader))
		{
			columnsRestricted = true;
			this.matrix.getRules().add(columnRule);
		}
	}

	public void handleRequest(Database db, Tuple t) throws HandleRequestDelegationException
	{
		if (t.getAction().startsWith(REMOVEFILTER))
		{
			try
			{
				removeFilter(t.getAction());
			}
			catch (MatrixException e)
			{
				e.printStackTrace();
				throw new HandleRequestDelegationException();
			}
			return;
		}
		String action = t.getAction().substring((getName() + "_").length());
		((DatabaseMatrix) this.matrix).setDatabase(db);
		this.delegate(action, db, t);
	}

	public String toHtml()
	{
		try
		{
			if (this.matrix instanceof DatabaseMatrix)
			{
				((DatabaseMatrix) this.matrix).setDatabase(db);
			}

			String result = "<div style=\"width:auto\">";
			if (downloadLink != null)
			{
				result += "<div><p><a href=\"tmpfile/" + downloadLink + "\">Download your export ("+downloadLink+")</a></p></div>";
			}
			result += "<table style=\"width:auto\"><tr><td>";
			result += renderHeader();
			result += "</td></tr><tr><td>";
			result += renderTable();
			result += "</td></tr><tr><td>";
			result += renderFilterPart();
			result += "</td></tr></table>";
			result += "</div>";

			return result;
		}
		catch (Exception e)
		{
			((PluginModel) this.callingScreenController).setError(e.getMessage());
			e.printStackTrace();
			return new Paragraph("error", e.getMessage()).render();
		}
	}

	public String renderHeader() throws MatrixException
	{
		String divContents = "";
		// reload
		ActionInput reload = new ActionInput(RELOADMATRIX, "", "Reload");
		reload.setIcon("generated-res/img/update.gif");
		divContents += "<div style=\"float:left; vertical-align:middle\">" + reload.render() + "</div>";
		// move vertical (row paging)
		ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
		moveUpEnd.setIcon("generated-res/img/first.png");
		divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">" + moveUpEnd.render();
		ActionInput moveUp = new ActionInput(MOVEUP, "", "");
		moveUp.setIcon("generated-res/img/prev.png");
		divContents += moveUp.render();
		int rowOffset = this.matrix.getRowOffset();
		int rowLimit = this.matrix.getRowLimit();
		int rowCount = this.matrix.getRowCount();
		int rowMax = Math.min(rowOffset + rowLimit, rowCount);
		divContents += "&nbsp;Showing " + (rowOffset + 1) + " - " + rowMax + " of " + rowCount + "&nbsp;";
		// collimit
		if (showLimitControls)
		{
			IntInput rowLimitInput = new IntInput(ROWLIMIT, rowLimit);
			divContents += "|&nbsp;Page limit:";
			divContents += rowLimitInput.render();
			divContents += new ActionInput(CHANGEROWLIMIT, "", "Change").render();
		}
		ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
		moveDown.setIcon("generated-res/img/next.png");
		divContents += moveDown.render();
		ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
		moveDownEnd.setIcon("generated-res/img/last.png");
		divContents += moveDownEnd.render() + "</div>";
		// download options
		if (showDownloadOptions)
		{
			MenuInput menu = new MenuInput("Download", "Download");
			ActionInput downloadAllCsv = new ActionInput(DOWNLOADALLCSV, "", "All to CSV");
			downloadAllCsv.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadAllCsv);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadAllCsv.render() + "</div>";
			ActionInput downloadVisCsv = new ActionInput(DOWNLOADVISCSV, "", "Visible to CSV");
			downloadVisCsv.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadVisCsv);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadVisCsv.render() + "</div>";
			ActionInput downloadAllExcel = new ActionInput(DOWNLOADALLEXCEL, "", "All to Excel");
			downloadAllExcel.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadAllExcel);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadAllExcel.render() + "</div>";
			ActionInput downloadVisExcel = new ActionInput(DOWNLOADVISEXCEL, "", "Visible to Excel");
			downloadVisExcel.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadVisExcel);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadVisExcel.render() + "</div>";
			ActionInput downloadAllSPSS = new ActionInput(DOWNLOADALLSPSS, "", "All to SPSS");
			downloadAllSPSS.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadAllSPSS);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadAllSPSS.render() + "</div>";
			ActionInput downloadVisSPSS = new ActionInput(DOWNLOADVISSPSS, "", "Visible to SPSS");
			downloadVisSPSS.setIcon("generated-res/img/download.png");
			menu.AddAction(downloadVisSPSS);
			//divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			//		+ downloadVisSPSS.render() + "</div>";
			
			divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
				+ menu.render() + "</div>";

		}

		return divContents;
	}

	// public String renderVerticalMovers(Database db) throws MatrixException {
	// String divContents = "";
	// // move vertical
	// ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
	// moveUpEnd.setIcon("generated-res/img/rowStart.png");
	// divContents += moveUpEnd.render();
	// divContents += new Newline().render();
	// ActionInput moveUp = new ActionInput(MOVEUP, "", "");
	// moveUp.setIcon("generated-res/img/up.png");
	// divContents += moveUp.render();
	// divContents += new Newline().render();
	// int rowOffset = this.matrix.getRowOffset();
	// int rowLimit = this.matrix.getRowLimit();
	// int rowCount = this.matrix.getRowCount(db);
	// int rowMax = Math.min(rowOffset + rowLimit, rowCount);
	// divContents += "Showing " + (rowOffset + 1) + " - " + rowMax + " of " +
	// rowCount;
	// divContents += new Newline().render();
	// // rowLimit
	// if (showLimitControls) {
	// IntInput rowLimitInput = new IntInput(ROWLIMIT, rowLimit);
	// divContents += "Row limit:";
	// divContents += new Newline().render();
	// divContents += rowLimitInput.render();
	// divContents += new Newline().render();
	// divContents += new ActionInput(CHANGEROWLIMIT, "", "Change").render();
	// divContents += new Newline().render();
	// }
	// ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
	// moveDown.setIcon("generated-res/img/down.png");
	// divContents += moveDown.render();
	// divContents += new Newline().render();
	// ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
	// moveDownEnd.setIcon("generated-res/img/rowStop.png");
	// divContents += moveDownEnd.render();
	//
	// return divContents;
	// }

	public String renderTable() throws MatrixException
	{
		JQueryDataTable dataTable = new JQueryDataTable(getName() + "DataTable");

		Object[][] values = null;
		try
		{
			values = matrix.getValueLists();
		}
		catch (UnsupportedOperationException ue)
		{
			values = matrix.getValues();
		}

		List<?> rows = matrix.getRowHeaders();
		List<?> cols = matrix.getColHeaders();

		// print colHeaders
		dataTable.addColumn("Select"); // for checkbox / radio input

		for (Object col : cols)
		{
			if (col instanceof ObservationElement)
			{
				ObservationElement colobs = (ObservationElement) col;
				dataTable.addColumn(colobs.getName());
			}
			else
			{
				dataTable.addColumn(col.toString());
			}

		}

		// print rowHeader + colValues
		for (int row = 0; row < values.length; row++)
		{
			// print rowHeader
			Object rowobj = rows.get(row);
			if (rowobj instanceof ObservationElement)
			{
				ObservationElement rowObs = (ObservationElement) rowobj;
				dataTable.addRow(rowObs.getName());
			}
			else
			{
				dataTable.addRow(rowobj.toString());
			}
			// print checkbox or radio input for this row
			if (selectMultiple)
			{
				List<String> options = new ArrayList<String>();
				options.add("" + row);
				List<String> optionLabels = new ArrayList<String>();
				optionLabels.add("");
				CheckboxInput rowCheckbox = new CheckboxInput(SELECTED + "_" + row, options, optionLabels, "", null,
						true, false);
				rowCheckbox.setId(SELECTED + "_" + row);
				dataTable.setCell(0, row, rowCheckbox);
			}
			else
			{
				// When the user may select only one, use a radio button group,
				// which is mutually exclusive
				String radioButtonCode = "<input type='radio' name='" + SELECTED + "' id='" + (SELECTED + "_" + row)
						+ "' value='" + row + "'></input>";
				dataTable.setCell(0, row, radioButtonCode);
			}
			// get the data for this row
			if (values[row] != null && values[row].length > 0)
			{
				Object valueObject = values[row][0];
				if (valueObject instanceof List)
				{
					List<Observation>[] rowValues = (List<Observation>[]) values[row];
					for (int col = 0; col < rowValues.length; col++)
					{
						if (rowValues[col] != null && rowValues[col].size() > 0)
						{
							boolean first = true;
							for (Observation val : rowValues[col])
							{
								String valueToShow = (String) val.get("value");

								if (val instanceof ObservedValue && valueToShow == null)
								{
									valueToShow = ((ObservedValue) val).getRelation_Name();
								}
								// if timing should be shown:
								if (showValueValidRange)
								{
									if (val.get(ObservedValue.TIME) != null)
									{
										valueToShow += " (valid from "
												+ newDateOnlyFormat.format(val.get(ObservedValue.TIME));
									}
									if (val.get(ObservedValue.ENDTIME) != null)
									{
										valueToShow += " through "
												+ newDateOnlyFormat.format(val.get(ObservedValue.ENDTIME)) + ")";
									}
									else if (val.get(ObservedValue.TIME) != null)
									{
										valueToShow += ")";
									}
								}

								if (first)
								{
									first = false;
									dataTable.setCell(col + 1, row, valueToShow);
								}
								else
								{
									// Append to contents of cell, on new line
									dataTable.setCell(col + 1, row, dataTable.getCell(col + 1, row) + "<br />"
											+ valueToShow);
								}
							}
						}
						else
						{
							dataTable.setCell(col + 1, row, "NA");
						}
					}

				}
				else
				{
					Object[] rowValues = values[row];
					for (int col = 0; col < rowValues.length; col++)
					{
						Object val = rowValues[col];
						if (val != null)
						{
							dataTable.setCell(col + 1, row, val);
						}
						else
						{
							dataTable.setCell(col + 1, row, "NA");
						}
					}

				}
			}
		}

		return dataTable.toHtml();
	}

	public String renderFilterPart() throws MatrixException, DatabaseException
	{
		String divContents = "";

		// Show applied filter rules
		String filterRules = "";
		if (this.matrix.getRules().size() > 0)
		{
			int filterCnt = 0;
			for (MatrixQueryRule mqr : this.matrix.getRules())
			{
				// Show only column value filters to user
				if (mqr.getFilterType().equals(MatrixQueryRule.Type.colValueProperty))
				{
					// Try to retrieve measurement name
					String measurementName = "";
					for (Object meas : matrix.getColHeaders())
					{
						if (meas instanceof ObservationElement)
						{
							ObservationElement measr = (ObservationElement) meas;
							if (measr.getId().intValue() == mqr.getDimIndex().intValue())
							{
								measurementName = measr.getName();

							}
						}
						else
						{
							measurementName = meas.toString();
						}
					}

					// Name not in column headers, so retrieve via DB (if
					// available)
					if (measurementName.equals("") && this.matrix instanceof DatabaseMatrix)
					{
						measurementName = db.findById(Measurement.class, mqr.getDimIndex()).getName();
					}

					filterRules += "<br />" + measurementName + " " + mqr.getOperator().toString() + " "
							+ mqr.getValue();
					ActionInput removeButton = new ActionInput(REMOVEFILTER + "_" + filterCnt, "", "");
					removeButton.setIcon("generated-res/img/delete.png");
					filterRules += removeButton.render();
				}
				System.out.println("(mqr.getFilterType() " + mqr.getFilterType());
				filterCnt++;
			}

		}

		if (filterRules.equals(""))
		{
			filterRules = " none";
		}
		divContents += new Paragraph("filterRules", "Applied filters:" + filterRules).render();
		// button to clear all value filter rules
		// divContents += new ActionInput(CLEARFILTERS, "",
		// "Clear all filters").render();
		// add column filter
		SelectInput colId = new SelectInput(COLID);
		divContents += "<div style=\"vertical-align:middle\">Add filter:";
		List<? extends Object> colHeaders = matrix.getColHeaders();
		if (colHeaders != null && colHeaders.size() > 0 && colHeaders.get(0) instanceof Entity)
		{
			List<? extends Entity> headers = (List<? extends Entity>) colHeaders;
			colId.setEntityOptions(headers);
		}
		else
		{
			// TODO!!!!!
		}
		colId.setNillable(true);
		divContents += colId.render();
		SelectInput operator = new SelectInput(OPERATOR);
		operator.addOption("Like", "Like");
		operator.addOption("Equals", "Equals");
		divContents += operator.render();
		StringInput colValue = new StringInput(COLVALUE);
		divContents += colValue.render();
		divContents += new ActionInput(FILTERCOL, "", "Apply").render();
		divContents += "</div>";
		// column header filter
		if (columnsRestricted && colHeaders != null)
		{
			List selectedMeasurements = new ArrayList();
			selectedMeasurements.addAll(colHeaders);
			MrefInput measurementChooser = new MrefInput(MEASUREMENTCHOOSER, "Add/remove columns:",
					selectedMeasurements, false, false,
					"Choose one or more columns (i.e. measurements) to be displayed in the matrix viewer",
					Measurement.class);
			// disable display of button for adding new measurements from here
			measurementChooser.setIncludeAddButton(false);
			divContents += new Newline().render();
			divContents += new Newline().render();
			divContents += "<div style=\"vertical-align:middle\">Add/remove columns:";
			divContents += measurementChooser.render();
			divContents += new ActionInput(UPDATECOLHEADERFILTER, "", "Update").render();
			divContents += new ActionInput(ADDALLCOLHEADERFILTER, "", "Add all").render();
			divContents += new ActionInput(REMALLCOLHEADERFILTER, "", "Remove all").render();
			divContents += "</div>";
		}
		return divContents;
	}

	public void removeFilter(String action) throws MatrixException
	{
		int filterNr = Integer.parseInt(action.substring(action.lastIndexOf("_") + 1));
		this.matrix.getRules().remove(filterNr);
		matrix.reload();
	}

	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}

	/**
	 * Remove only the colValueProperty type filters from the matrix.
	 * 
	 * @param db
	 * @param t
	 * @throws MatrixException
	 */
	public void clearValueFilters(Database db, Tuple t) throws MatrixException
	{
		List<MatrixQueryRule> removeList = new ArrayList<MatrixQueryRule>();
		for (MatrixQueryRule mqr : this.matrix.getRules())
		{
			if (mqr.getFilterType().equals(MatrixQueryRule.Type.colValueProperty))
			{
				removeList.add(mqr);
			}
		}
		this.matrix.getRules().removeAll(removeList);
		matrix.reload();
	}

	public String selectDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Date dat = new Date();
		return dateFormat.format(dat);
	}
	
	public File file(String visAll,String extension){
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(tmpDir.getAbsolutePath() + File.separatorChar + "Export"+visAll+"_"+selectDate()+"."+extension);
		return file; 
	}
	
	
	public void downloadAllCsv(Database db, Tuple t) throws MatrixException, IOException
	{
		// remember old limits and offset
		int rowOffset = matrix.getRowOffset();
		int rowLimit = matrix.getRowLimit();
		int colOffset = matrix.getColOffset();
		int colLimit = matrix.getColLimit();
		
		// max for batching
		int maxRow = matrix.getRowCount();

		stat = "_All";
		extension = "csv";

		File file = file(stat,extension);
		CsvWriter writer = new CsvFileWriter(file);
		writer.setSeparator(",");

		// batch size = 100
		matrix.setRowLimit(100);
		matrix.setColLimit(matrix.getColCount());

		// write headers
		List<String> headers = new ArrayList<String>();
		headers.add("Name");
		for (ObservationElement colHeader : (List<ObservationElement>)matrix.getColHeaders())
		{
			headers.add(colHeader.getName());
		}
		writer.setHeaders(headers);
		writer.writeHeader();

		// iterate through all available rows
		for (int offset = 0; offset < maxRow; offset += 100)
		{
			// retrieve a batch
			matrix.setRowOffset(offset);
			// retrieve names of targets in batch
			List<ObservationElement> targets = (List<ObservationElement>)matrix.getRowHeaders();
			// write lines to file
			int rowCnt = 0;
			for (List<? extends ObservedValue>[] row : (List<? extends ObservedValue>[][])matrix.getValueLists())
			{
				writer.writeValue(targets.get(rowCnt).getName());
				for (int colId = 0; colId < row.length; colId++)
				{
					List<? extends ObservedValue> valueList = row[colId];
					writer.writeSeparator();
					writer.writeValue(this.valueListToString(valueList));
				}
				writer.writeEndOfLine();
				rowCnt++;
			}
		}
		writer.close();

		// restore limit and offset
		matrix.setRowOffset(rowOffset);
		matrix.setRowLimit(rowLimit);
		matrix.setColOffset(colOffset);
		matrix.setColLimit(colLimit);

		downloadLink = file.getName();
	}

	
	
	public void downloadVisibleCsv(Database db, Tuple t) throws MatrixException, IOException
	{
		List<?> rows = matrix.getRowHeaders();
		List<?> cols = matrix.getColHeaders();
		List<? extends ObservedValue>[][] values = null;
		stat = "_Visible";
		extension = "xls";

		File file = file(stat,extension);
		try
		{
			values = (List<? extends ObservedValue>[][]) matrix.getValueLists();
		}
		catch (UnsupportedOperationException ue)
		{
			//values = matrix.getValues();
		}
		downloadCsv(rows, cols, values,file);
	}

	public void downloadCsv(List<?> rows, List<?> cols, List<? extends ObservedValue>[][] values,File file) throws IOException, MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}


		CsvWriter writer = new CsvFileWriter(file);
		writer.setSeparator(",");

		// write headers
		List<String> headers = new ArrayList<String>();
		headers.add("Name");
		for (ObservationElement colHeader : (List<ObservationElement>)matrix.getColHeaders())
		{
			headers.add(colHeader.getName());
		}
		writer.setHeaders(headers);
		writer.writeHeader();

		// print rowHeader + colValues
		for (int row = 0; row < values.length; row++)
		{
			// print name
			Object rowobj = rows.get(row);
			if (rowobj instanceof ObservationElement)
			{
				ObservationElement rowObs = (ObservationElement) rowobj;
				writer.writeValue(rowObs.getName());
			}
			else
			{
				writer.writeValue(rowobj.toString());
			}
			// get the data for this row
			List<? extends ObservedValue>[] value = values[row];

			for(List<? extends ObservedValue> valueList: value)
			{
				writer.writeSeparator();
				writer.writeValue(this.valueListToString(valueList));
			}
//			else
//			{
//				Object[] rowValues = values[row];
//				for (int col = 0; col < rowValues.length; col++)
//				{
//					Object val = rowValues[col];
//					if (val != null)
//					{
//						output.write("," + val);
//					}
//					else
//					{
//						output.write(",NA");
//					}
//				}
//			}
			writer.writeEndOfLine();
		}

		writer.close();
		downloadLink = file.getName();
	}

	public void downloadAllExcel(Database db, Tuple t) throws MatrixException, IOException, RowsExceededException, WriteException
	{
		// remember old limits and offset
		int rowOffset = matrix.getRowOffset();
		int rowLimit = matrix.getRowLimit();
		int colOffset = matrix.getColOffset();
		int colLimit = matrix.getColLimit();
		
		// max for batching
		int maxRow = matrix.getRowCount();
		String target = "name";
		
		/* Create tmp file */
		stat = "_All";
		extension = "xls";

		File file = file(stat,extension);
		System.out.println(file);
		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(file, ws);
		WritableSheet s = workbook.createSheet("Sheet1", 0);

		/* Format the fonts */
		WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
		headerFormat.setWrap(false);
		WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setWrap(false);

		// batch size = 100
		matrix.setRowLimit(100);
		matrix.setColLimit(matrix.getColCount());
		
		// write targetheader
		Label e = new Label(0, 0, target, headerFormat);
		s.addCell(e);

		// Write column headers
		int teller =1;
		for (ObservationElement colHeader : (List<ObservationElement>)matrix.getColHeaders())
		{
			Label f = new Label(teller, 0, colHeader.getName(), headerFormat);
			s.addCell(f);
			teller++;
		}

		// iterate through all available rows
		for (int offset = 0; offset <= maxRow; offset += 100)
		{
			// retrieve a batch
			matrix.setRowOffset(offset);
			// retrieve names of targets in batch
			List<ObservationElement> targets = (List<ObservationElement>)matrix.getRowHeaders();
			// write lines to file
			int rowCnt = 0;
			for (List<? extends ObservedValue>[] row : (List<? extends ObservedValue>[][])matrix.getValueLists())
			{
				Label l = new Label(0, rowCnt+1, targets.get(rowCnt).getName(), cellFormat);
				s.addCell(l);

				for (int colId = 0; colId < row.length; colId++)
				{
					List<? extends ObservedValue> valueList = row[colId];
					
					Label m = new Label(colId+1, rowCnt+1, this.valueListToString(valueList), cellFormat);
					s.addCell(m);
				}

				rowCnt++;
			}
		}
		workbook.write();
		workbook.close();

		// restore limit and offset
		matrix.setRowOffset(rowOffset);
		matrix.setRowLimit(rowLimit);
		matrix.setColOffset(colOffset);
		matrix.setColLimit(colLimit);

		downloadLink = file.getName();
	}
	
	public void downloadVisibleExcel(Database db, Tuple t) throws Exception
	{

		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}

		List<?> listCol = (List<Measurement>) this.matrix.getColHeaders();
		List<String> listC = new ArrayList<String>();
		List<?> listRow = (List<Individual>) this.matrix.getRowHeaders();
		List<String> listR = new ArrayList<String>();
		List<ObservedValue>[][] listVal = (List<ObservedValue>[][]) this.matrix.getValueLists();

		String target = "name";
		for (Object col : listCol)
		{
			if (col instanceof ObservationElement)
			{
				ObservationElement colobs = (ObservationElement) col;
				listC.add(colobs.getName());
			}
		}
		for (Object m : listRow)
		{
			if (m instanceof ObservationElement)
			{
				ObservationElement colobs = (ObservationElement) m;
				listR.add(colobs.getName());
			}
		}
		/* Create tmp file */
		stat = "_Visible";
		extension = "xls";

		File file = file(stat,extension);
		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(file, ws);
		WritableSheet s = workbook.createSheet("Sheet1", 0);

		/* Format the fonts */
		WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
		headerFormat.setWrap(false);
		WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setWrap(false);

		//
		Label e = new Label(0, 0, target, headerFormat);
		s.addCell(e);

		// Write column headers
		for (int i = 0; i < listC.size(); i++)
		{

			Label l = new Label(i + 1, 0, listC.get(i), headerFormat);
			s.addCell(l);
		}

		// Write row headers
		for (int i = 0; i < listRow.size(); i++)
		{
			Object rowobj = listRow.get(i);
			if (rowobj instanceof ObservationElement)
			{
				ObservationElement rowObs = (ObservationElement) rowobj;
				Label j = new Label(0, i + 1, rowObs.getName(), headerFormat);
				s.addCell(j);

			}
			else
			{
				Label j = new Label(0, i + 1, rowobj.toString(), headerFormat);
				s.addCell(j);
			}
		}

		// Write elements
		for (int a = 0; a < listC.size(); a++)
		{
			for (int b = 0; b < listR.size(); b++)
			{
				if (listVal[b][a] != null)
				{
					Label l = new Label(a + 1, b + 1, listObsValToString(listVal[b][a]), cellFormat);
					s.addCell(l);
				}
				else
				{
					s.addCell(new Label(a + 1, b + 1, "", cellFormat));
				}

			}
		}

		workbook.write();
		workbook.close();

		downloadLink = file.getName();
	}

	public void downloadVisibleSPSS(Database db, Tuple t) throws Exception
	{
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		stat = "_Visible";
		extension = "sav";

		File file = file(stat,extension);
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		SPSSWriter spssWriter = new SPSSWriter(out, "windows-1252");
		spssWriter.setCalculateNumberOfCases(false);
		spssWriter.addDictionarySection(-1);
		
		List<?> listCol = (List<Measurement>) this.matrix.getColHeaders();
		List<String> listC = new ArrayList<String>();
		List<?> listRow = (List<Individual>) this.matrix.getRowHeaders();
		List<String> listR = new ArrayList<String>();	
		List<ObservedValue>[][] elements = (List<ObservedValue>[][]) this.matrix.getValueLists();
		
		for(Object col: listCol){
			if(col instanceof ObservationElement) {
				ObservationElement colobs = (ObservationElement) col;
				listC.add(colobs.getName());
			}
		}
		for(Object m : listRow){
			if(m instanceof ObservationElement) {
				ObservationElement colobs = (ObservationElement) m;
				listR.add(colobs.getName());
			}
		}
		spssWriter.addStringVar("name", 10, "name");
		for(String colName : listC){
			spssWriter.addStringVar(colName, 10, colName);
		}
		
		spssWriter.addDataSection();

		for(int rowIndex = 0; rowIndex < listR.size(); rowIndex++)
		{
			Object rowobj = listRow.get(rowIndex);
			ObservationElement rowObs = (ObservationElement) rowobj;
			spssWriter.addData(rowObs.getName());
			for(int colIndex = 0; colIndex < listC.size(); colIndex++)
			{
				Object val = listObsValToString(elements[rowIndex][colIndex]);
				if(val == null)
				{
					spssWriter.addData(""); //FIXME: correct?
				}
				else
				{
					
					spssWriter.addData(val.toString());	
				}
			}
			
		}
		
		spssWriter.addFinishSection();
		out.close();
		downloadLink = file.getName();
	}
		
	public void downloadAllSPSS(Database db, Tuple t) throws MatrixException, IOException
	{
		// remember old limits and offset
		int rowOffset = matrix.getRowOffset();
		int rowLimit = matrix.getRowLimit();
		int colOffset = matrix.getColOffset();
		int colLimit = matrix.getColLimit();
		
		// max for batching
		int maxRow = matrix.getRowCount();
//
		stat = "_All";
		extension = "sav";

		File file = file(stat,extension);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		SPSSWriter spssWriter = new SPSSWriter(out, "windows-1252");
		spssWriter.setCalculateNumberOfCases(false);
		spssWriter.addDictionarySection(-1);
//
		// batch size = 100
		matrix.setRowLimit(100);
		matrix.setColLimit(matrix.getColCount());

		// write headers
		List<String> headers = new ArrayList<String>();
		headers.add("Name");
		for (ObservationElement colHeader : (List<ObservationElement>)matrix.getColHeaders())
		{
			headers.add(colHeader.getName());
		}
		for(String colName : headers){
			spssWriter.addStringVar(colName, 10, colName);
		}
		
		spssWriter.addDataSection();
		
		// iterate through all available rows
		for (int offset = 0; offset < maxRow; offset += 100)
		{
			// retrieve a batch
			matrix.setRowOffset(offset);
			// retrieve names of targets in batch
			List<ObservationElement> targets = (List<ObservationElement>)matrix.getRowHeaders();
			// write lines to file
			int rowCnt = 0;
			for (List<? extends ObservedValue>[] row : (List<? extends ObservedValue>[][])matrix.getValueLists())
			{
				spssWriter.addData(targets.get(rowCnt).getName());
				for (int colId = 0; colId < row.length; colId++)
				{
					List<? extends ObservedValue> valueList = row[colId];
					
					spssWriter.addData(this.valueListToString(valueList));
				}
				
				rowCnt++;
			}
		}
		spssWriter.addFinishSection();
		out.close();
		downloadLink = file.getName();

		// restore limit and offset
		matrix.setRowOffset(rowOffset);
		matrix.setRowLimit(rowLimit);
		matrix.setColOffset(colOffset);
		matrix.setColLimit(colLimit);

		
	} 
	 
	private String listObsValToString(List<ObservedValue> values) throws Exception
	{
		String result = "";
		int pass = 0;
		for (ObservedValue s : values)
		{
			if (pass > 0) {
				result += "|";
			}
			result += s.getValue();
			pass++;
		}
		return result;
	}
	
	public void reloadMatrix(Database db, Tuple t) throws MatrixException
	{
		matrix.reload();
	}

	public void filterCol(Database db, Tuple t) throws Exception
	{
		// First find out whether to filter on the value or the relation_Name
		// field
		String valuePropertyToUse = ObservedValue.VALUE;
		int measurementId = t.getInt(COLID);
		Measurement filterMeasurement = db.findById(Measurement.class, measurementId);
		if (filterMeasurement.getDataType().equals("xref"))
		{
			valuePropertyToUse = ObservedValue.RELATION_NAME;
		}
		// Find out operator to use
		QueryRule.Operator op;
		if (t.getString(OPERATOR).equals("Equals"))
		{
			op = QueryRule.Operator.EQUALS;
		}
		else
		{
			op = QueryRule.Operator.LIKE;
		}
		// Then do the actual slicing
		matrix.sliceByColValueProperty(measurementId, valuePropertyToUse, op, t.getObject(COLVALUE));
	}

	public void updateColHeaderFilter(Database db, Tuple t) throws Exception
	{
		List<?> chosenMeasurementIds;
		if (t.getList(MEASUREMENTCHOOSER) != null)
		{
			chosenMeasurementIds = t.getList(MEASUREMENTCHOOSER);
		}
		else
		{
			chosenMeasurementIds = new ArrayList<Object>();
		}
		List<String> chosenMeasurementNames = new ArrayList<String>();
		for (Object measurementId : chosenMeasurementIds)
		{
			int measId = Integer.parseInt((String) measurementId);
			chosenMeasurementNames.add(db.findById(Measurement.class, measId).getName());
		}
		setColHeaderFilter(chosenMeasurementNames);
	}

	public void addAllColHeaderFilter(Database db, Tuple t) throws Exception
	{
		List<Measurement> allMeasurements = db.find(Measurement.class);
		List<String> chosenMeasurementNames = new ArrayList<String>();
		for (Measurement measurement : allMeasurements)
		{
			chosenMeasurementNames.add(measurement.getName());
		}
		setColHeaderFilter(chosenMeasurementNames);
	}

	public void setColHeaderFilter(List<String> chosenMeasurements) throws DatabaseException, MatrixException
	{
		// Find and update col header filter rule
		boolean hasRule = false;
		for (MatrixQueryRule mqr : matrix.getRules())
		{
			if (mqr.getFilterType().equals(MatrixQueryRule.Type.colHeader))
			{
				if (chosenMeasurements.size() > 0)
				{
					mqr.setValue(chosenMeasurements); // update
					hasRule = true;
				}
				else
				{
					matrix.getRules().remove(mqr);
				}
				break;
			}
		}
		if (hasRule == false && chosenMeasurements.size() > 0)
		{
			matrix.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN,
							chosenMeasurements));
		}
		matrix.setColLimit(chosenMeasurements.size()); // grow with selected
														// measurements
		matrix.reload();
	}

	public void remAllColHeaderFilter(Database db, Tuple t) throws Exception
	{
		List<MatrixQueryRule> removeList = new ArrayList<MatrixQueryRule>();
		for (MatrixQueryRule mqr : matrix.getRules())
		{
			if (mqr.getFilterType().equals(MatrixQueryRule.Type.colHeader))
			{
				removeList.add(mqr);
			}
		}
		matrix.getRules().removeAll(removeList);
		matrix.setColLimit(0);
		matrix.reload();
	}

	public void rowHeaderEquals(Database db, Tuple t) throws Exception
	{
		matrix.sliceByRowProperty(ObservationElement.ID, QueryRule.Operator.EQUALS, t.getString(ROWHEADER));
	}

	public void changeRowLimit(Database db, Tuple t)
	{
		this.matrix.setRowLimit(t.getInt(ROWLIMIT));
	}

	// public void changeColLimit(Database db, Tuple t)
	// {
	// this.matrix.setColLimit(t.getInt(COLLIMIT));
	// }

	public void moveLeftEnd(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setColOffset(0);
	}

	public void moveLeft(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setColOffset(matrix.getColOffset() - matrix.getColLimit() > 0 ? matrix.getColOffset()
				- matrix.getColLimit() : 0);
	}

	public void moveRight(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setColOffset(matrix.getColOffset() + matrix.getColLimit() < matrix.getColCount() ? matrix
				.getColOffset() + matrix.getColLimit() : matrix.getColOffset());
	}

	public void moveRightEnd(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setColOffset((matrix.getColCount() % matrix.getColLimit() == 0 ? new Double(matrix.getColCount()
				/ matrix.getColLimit()).intValue() - 1 : new Double(matrix.getColCount() / matrix.getColLimit())
				.intValue()) * matrix.getColLimit());
	}

	public void moveUpEnd(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setRowOffset(0);
	}

	public void moveUp(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setRowOffset(matrix.getRowOffset() - matrix.getRowLimit() > 0 ? matrix.getRowOffset()
				- matrix.getRowLimit() : 0);
	}

	public void moveDown(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setRowOffset(matrix.getRowOffset() + matrix.getRowLimit() < matrix.getRowCount() ? matrix
				.getRowOffset() + matrix.getRowLimit() : matrix.getRowOffset());
	}

	public void moveDownEnd(Database db, Tuple t) throws MatrixException
	{
		if (this.matrix instanceof DatabaseMatrix)
		{
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setRowOffset((matrix.getRowCount() % matrix.getRowLimit() == 0 ? new Double(matrix.getRowCount()
				/ matrix.getRowLimit()).intValue() - 1 : new Double(matrix.getRowCount() / matrix.getRowLimit())
				.intValue()) * matrix.getRowLimit());
	}

	public void handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		// automatically calls functions with same name as action (ommiting
		// widget specific prefix)
		delegate(request.getAction(), db, request);
	}

	public void delegate(String action, Database db, Tuple request) throws HandleRequestDelegationException
	{
		// try/catch for db.rollbackTx
		try
		{
			// try/catch for method calling
			try
			{
				// db.beginTx();
				System.out.println("trying to use reflection to call ######## " + this.getClass().getName() + "."
						+ action);
				Method m = this.getClass().getMethod(action, Database.class, Tuple.class);
				m.invoke(this, db, request);
				logger.debug("call of " + this.getClass().getName() + "(name=" + this.getName() + ")." + action
						+ " completed");
				// if(db.inTx())
				// db.commitTx();
			}
			catch (NoSuchMethodException e1)
			{
				this.callingScreenController.getModel().setMessages(
						new ScreenMessage("Unknown action: " + action, false));
				logger.error("call of " + this.getClass().getName() + "(name=" + this.getName() + ")." + action
						+ "(db,tuple) failed: " + e1.getMessage());
				// db.rollbackTx();
			}
			catch (Exception e)
			{
				logger.error("call of " + this.getClass().getName() + "(name=" + this.getName() + ")." + action
						+ " failed: " + e.getMessage());
				e.printStackTrace();
				this.callingScreenController.getModel()
						.setMessages(new ScreenMessage(e.getCause().getMessage(), false));
				// db.rollbackTx();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<?> getSelection(Database db) throws MatrixException
	{
		return matrix.getRowHeaders();
	}

	public SliceableMatrix getMatrix()
	{
		return matrix;
	}

	public boolean getShowValueValidRange()
	{
		return showValueValidRange;
	}

	public void setShowValueValidRange(boolean showValueValidRange)
	{
		this.showValueValidRange = showValueValidRange;
	}

	private String valueListToString(List<? extends ObservedValue> valueList)
	{
		if(valueList == null) return "NA";
		
		String result = "";
		for (int i = 0; i < valueList.size(); i++)
		{
			if (i > 0) result += "|";
			if (valueList.get(i) instanceof ObservedValue && valueList.get(i).getValue() == null)
			{
				result += valueList.get(i).getRelation_Name();
			}
			else
			{
				result += valueList.get(i).getValue();
			}
		}
		return result;
	}

}
