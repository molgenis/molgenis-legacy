package org.molgenis.matrix.component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DecimalInput;
import org.molgenis.framework.ui.html.EditableJQueryDataTable;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.LongInput;
import org.molgenis.framework.ui.html.MenuInput;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.Observation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class MatrixViewer extends HtmlWidget {
	private static final int BATCHSIZE = 100;

	ScreenController<?> callingScreenController;

	SliceableMatrix<?, ?, ?> matrix;
	Logger logger = Logger.getLogger(this.getClass());
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat(
			"dd-MM-yyyy", Locale.US);

	// Configuration parameters. TODO: solve in a nicer way; use a plugin
	// strategy?
	private boolean showLimitControls = true;
	private int selectMultiple = 0;
	private boolean showValueValidRange = false;
	private boolean showDownloadOptions = false;
	private boolean showNameFilter = true;
	private boolean isEditable;
	private String APPLICATION_STRING = "GENERIC";

	private String downloadLink = null;
	private Measurement d_selectedMeasurement = null;

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
	public String DOWNLOAD = getName() + "_download";
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
	// hack to pass database to toHtml() via toHtml(db)
	private Database db;

	public void setDatabase(Database db) {
		this.db = db;
	}

	/**
	 * Default constructor.
	 * 
	 */
	public MatrixViewer(ScreenController<?> callingScreenController,
			String name, SliceableMatrix<?, ?, ?> matrix,
			boolean showLimitControls, int selectMultiple,
			boolean showDownloadOptions, boolean showValueValidRange,
			List<MatrixQueryRule> filterRules) {
		super(name);
		super.setLabel("");
		this.callingScreenController = callingScreenController;
		this.matrix = matrix;
		this.showLimitControls = showLimitControls;
		this.selectMultiple = selectMultiple;
		this.showDownloadOptions = showDownloadOptions;
		this.showValueValidRange = showValueValidRange;
		if (filterRules != null) {
			this.matrix.getRules().addAll(filterRules);
		}
	}

	/**
	 * Constructor, same as default, except you get to set whether you want a
	 * filter option on "name".
	 * 
	 */
	public MatrixViewer(ScreenController<?> callingScreenController,
			String name, SliceableMatrix<?, ?, ?> matrix,
			boolean showLimitControls, int selectMultiple,
			boolean showDownloadOptions, boolean showValueValidRange,
			boolean showNameFilter, List<MatrixQueryRule> filterRules) {
		this(callingScreenController, name, matrix, showLimitControls,
				selectMultiple, showDownloadOptions, showValueValidRange,
				filterRules);
		this.showNameFilter = showNameFilter;
	}

	/**
	 * Constructor where you immediately restrict the column set by applying a
	 * colHeader filter rule.
	 * 
	 */
	public MatrixViewer(ScreenController<?> callingScreenController,
			String name, SliceablePhenoMatrix<?, ?> matrix,
			boolean showLimitControls, int selectMultiple,
			boolean showDownloadOptions, boolean showValueValidRange,
			List<MatrixQueryRule> filterRules, MatrixQueryRule columnRule)
			throws Exception {
		this(callingScreenController, name, matrix, showLimitControls,
				selectMultiple, showDownloadOptions, showValueValidRange,
				filterRules);
		if (columnRule != null
				&& columnRule.getFilterType().equals(
						MatrixQueryRule.Type.colHeader)) {
			this.matrix.getRules().add(columnRule);
		}
	}

	/**
	 * Constructor for editable version
	 */
	public MatrixViewer(ScreenController<?> callingScreenController,
			String name, SliceablePhenoMatrix<?, ?> matrix,
			boolean showLimitControls, int selectMultiple,
			boolean showDownloadOptions, boolean showValueValidRange,
			List<MatrixQueryRule> filterRules, MatrixQueryRule columnRule,
			boolean isEditable) throws Exception {
		this(callingScreenController, name, matrix, showLimitControls,
				selectMultiple, showDownloadOptions, showValueValidRange,
				filterRules);
		if (columnRule != null
				&& columnRule.getFilterType().equals(
						MatrixQueryRule.Type.colHeader)) {
			this.matrix.getRules().add(columnRule);
		}
		this.isEditable = isEditable;
	}

	/**
	 * Constructor where you immediately restrict the column set by applying a
	 * colHeader filter rule and you get to set whether you want a filter option
	 * on "name".
	 * 
	 */
	public MatrixViewer(ScreenController<?> callingScreenController,
			String name, SliceablePhenoMatrix<?, ?> matrix,
			boolean showLimitControls, int selectMultiple,
			boolean showDownloadOptions, boolean showValueValidRange,
			boolean showNameFilter, List<MatrixQueryRule> filterRules,
			MatrixQueryRule columnRule) throws Exception {
		this(callingScreenController, name, matrix, showLimitControls,
				selectMultiple, showDownloadOptions, showValueValidRange,
				filterRules, columnRule);
		this.showNameFilter = showNameFilter;
	}

	public void handleRequest(Database db, Tuple t)
			throws HandleRequestDelegationException {
		try {
			// refresh db
			((DatabaseMatrix) this.matrix).setDatabase(db);

			if (t.getAction().startsWith(REMOVEFILTER)) {
				removeFilter(t.getAction()); // in case of a remove filter
												// action, take the whole
												// action, as the filter number
												// is encoded therein
				return;
			} else if (t.getAction()
					.equals(this.getName()
							+ EditableJQueryDataTable.MATRIX_EDIT_ACTION)) {

				List<? extends Object>[][] values = this.getMatrix()
						.getValueLists();

				for (int rowIndex = 0; rowIndex < values.length; rowIndex++) {
					for (int colIndex = 0; colIndex < values[0].length; colIndex++) {
						if (values[rowIndex][colIndex].size() > 1) {
							// list has more than 1 element, too difficult to
							// edit
							// ignore for now...
						} else if (values[rowIndex][colIndex].size() == 0) {
							// empty, create new ObservedValue
							String newValue = t
									.getString(EditableJQueryDataTable.MATRIX_EDIT_VALUE
											+ "_" + colIndex + "_" + rowIndex);

							if (!(newValue == null || newValue.equals("NA") || newValue
									.trim().isEmpty())) {
								ObservedValue newOv = new ObservedValue();
								newOv.setFeature_Name(((ObservationElement) this
										.getMatrix().getColHeaders()
										.get(colIndex)).getName());
								newOv.setTarget_Name(((ObservationTarget) this
										.getMatrix().getRowHeaders()
										.get(rowIndex)).getName());
								newOv.setValue(newValue);
								db.add(newOv);
							}
						} else if (((ObservedValue) values[rowIndex][colIndex]
								.get(0)).getValue() == null) {
							// ignore:
							// this happens when we try to get values from the
							// request that are not in the form:
							// e.g. the hyperlinked inputs that we are now not
							// parsing!!!
						} else {
							String oldValue = ((ObservedValue) values[rowIndex][colIndex]
									.get(0)).getValue().toString();
							String newValue = t
									.getString(EditableJQueryDataTable.MATRIX_EDIT_VALUE
											+ "_" + colIndex + "_" + rowIndex);

							int id = ((ObservedValue) values[rowIndex][colIndex]
									.get(0)).getId();
							if (!oldValue.equals(newValue)) {
								if (!(newValue == null || newValue.equals("NA") || newValue
										.trim().isEmpty())) {
									// new valid value entered! update the
									// database...
									ObservedValue updateMe = db.find(
											ObservedValue.class,
											new QueryRule(ObservedValue.ID,
													Operator.EQUALS, id))
											.get(0);
									updateMe.setValue(newValue);
									db.update(updateMe);
								}
							}
						}
					}
				}
			}

			else {
				String action = t.getAction().substring(
						(getName() + "_").length());
				this.delegate(action, db, t);
			}

		} catch (Exception e) {
			throw new HandleRequestDelegationException(e);
		}

	}

	public String toHtml() {
		try {
			if (this.matrix instanceof DatabaseMatrix) {
				((DatabaseMatrix) this.matrix).setDatabase(db);
			}

			String result = "<script type=\"text/javascript\" src=\"res/scripts/tooltip.js\"></script>";
			result += "<div style=\"width:auto\">";
			if (downloadLink != null) {
				result += "<div><p><a href=\"tmpfile/" + downloadLink
						+ "\">Download your export (" + downloadLink
						+ ")</a></p></div>";
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
		} catch (Exception e) {
			// this.callingScreenController).setError(e.getMessage());
			e.printStackTrace();
			return new Paragraph("error", ExceptionUtils.getRootCauseMessage(e))
					.render();
		}
	}

	public String renderHeader() throws MatrixException {
		String divContents = "";
		// reload
		ActionInput reload = new ActionInput(RELOADMATRIX, "", "Reload");
		reload.setIcon("generated-res/img/update.gif");
		divContents += "<div style=\"float:left; vertical-align:middle\">"
				+ reload.render() + "</div>";
		// move vertical (row paging)
		ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
		moveUpEnd.setIcon("generated-res/img/first.png");
		divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
				+ moveUpEnd.render();
		ActionInput moveUp = new ActionInput(MOVEUP, "", "");
		moveUp.setIcon("generated-res/img/prev.png");
		divContents += moveUp.render();
		int rowOffset = this.matrix.getRowOffset();
		int rowLimit = this.matrix.getRowLimit();
		int rowCount = this.matrix.getRowCount();
		int rowMax = Math.min(rowOffset + rowLimit, rowCount);
		divContents += "&nbsp;Showing " + (rowOffset + 1) + " - " + rowMax
				+ " of " + rowCount + "&nbsp;";
		// collimit
		if (showLimitControls) {
			IntInput rowLimitInput = new IntInput(ROWLIMIT, rowLimit);
			rowLimitInput.setWidth(1);
			divContents += "|&nbsp;Page limit:";
			divContents += rowLimitInput.render();
			divContents += new ActionInput(CHANGEROWLIMIT, "", "Change")
					.render();
		}
		ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
		moveDown.setIcon("generated-res/img/next.png");
		divContents += moveDown.render();
		ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
		moveDownEnd.setIcon("generated-res/img/last.png");
		divContents += moveDownEnd.render() + "</div>";
		// download options
		if (showDownloadOptions) {
			MenuInput menu = new MenuInput(DOWNLOAD, "Download");
			ActionInput downloadAllCsv = new ActionInput(DOWNLOADALLCSV, "",
					"All to CSV");
			downloadAllCsv.setIcon("generated-res/img/download.png");
			downloadAllCsv.setWidth(180);
			menu.AddAction(downloadAllCsv);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadAllCsv.render() + "</div>";
			ActionInput downloadVisCsv = new ActionInput(DOWNLOADVISCSV, "",
					"Visible to CSV");
			downloadVisCsv.setIcon("generated-res/img/download.png");
			downloadVisCsv.setWidth(180);
			menu.AddAction(downloadVisCsv);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadVisCsv.render() + "</div>";
			ActionInput downloadAllExcel = new ActionInput(DOWNLOADALLEXCEL,
					"", "All to Excel");
			downloadAllExcel.setIcon("generated-res/img/download.png");
			downloadAllExcel.setWidth(180);
			menu.AddAction(downloadAllExcel);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadAllExcel.render() + "</div>";
			ActionInput downloadVisExcel = new ActionInput(DOWNLOADVISEXCEL,
					"", "Visible to Excel");
			downloadVisExcel.setIcon("generated-res/img/download.png");
			downloadVisExcel.setWidth(180);
			menu.AddAction(downloadVisExcel);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadVisExcel.render() + "</div>";
			ActionInput downloadAllSPSS = new ActionInput(DOWNLOADALLSPSS, "",
					"All to SPSS");
			downloadAllSPSS.setIcon("generated-res/img/download.png");
			downloadAllSPSS.setWidth(180);
			menu.AddAction(downloadAllSPSS);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadAllSPSS.render() + "</div>";
			ActionInput downloadVisSPSS = new ActionInput(DOWNLOADVISSPSS, "",
					"Visible to SPSS");
			downloadVisSPSS.setIcon("generated-res/img/download.png");
			downloadVisSPSS.setWidth(180);
			menu.AddAction(downloadVisSPSS);
			// divContents +=
			// "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
			// + downloadVisSPSS.render() + "</div>";

			divContents += "<div style=\"padding-left:10px; float:left; vertical-align:middle\">"
					+ menu.render() + "</div>";

		}

		return divContents;
	}

	/**
	 * Render a matrix, can be editable
	 * 
	 * @param editable
	 * @return
	 * @throws MatrixException
	 */
	public String renderTable() throws MatrixException {
		JQueryDataTable dataTable = null;

		if (this.isEditable) {
			dataTable = new EditableJQueryDataTable(getName() + "DataTable");
		} else {
			dataTable = new JQueryDataTable(getName() + "DataTable");
		}
		if (this.APPLICATION_STRING == "ANIMALDB") {
			dataTable.setbFilter(true);
			dataTable.setbSort(true);
		}

		Object[][] values = null;
		try {
			values = matrix.getValueLists();
		} catch (UnsupportedOperationException ue) {
			values = matrix.getValues();
		}

		List<?> rows = matrix.getRowHeaders();
		List<?> cols = matrix.getColHeaders();

		// print colHeaders

		if (selectMultiple > 0) {
			dataTable.addColumn("select"); // for checkbox / radio input
		}

		for (Object col : cols) {
			if (col instanceof ObservationElement) {
				ObservationElement colobs = (ObservationElement) col;
				dataTable.addColumn(colobs.getName());
			} else {
				dataTable.addColumn(col.toString());
			}

		}

		// print rowHeader + colValues
		int row;
		for (row = 0; row < values.length; row++) {
			// print rowHeader
			Object rowobj = rows.get(row);
			if (rowobj instanceof ObservationElement) {
				ObservationElement rowObs = (ObservationElement) rowobj;
				dataTable.addRow(rowObs.getName());
			} else {
				dataTable.addRow(rowobj.toString());
			}
			// print checkbox or radio input for this row (if selectMultiple >
			// 0)
			if (selectMultiple > 1) {
				List<String> options = new ArrayList<String>();
				options.add("" + row);
				List<String> optionLabels = new ArrayList<String>();
				optionLabels.add("");
				CheckboxInput rowCheckbox = new CheckboxInput(SELECTED + "_"
						+ row, options, optionLabels, "", null, true, false);
				rowCheckbox.setId(SELECTED + "_" + row);
				dataTable.setCell(0, row, rowCheckbox);
			}
			if (selectMultiple == 1) {
				// When the user may select only one, use a radio button group,
				// which is mutually exclusive
				String radioButtonCode = "<input type='radio' name='"
						+ SELECTED + "' id='" + (SELECTED + "_" + row)
						+ "' value='" + row + "'></input>";
				dataTable.setCell(0, row, radioButtonCode);
			}
			// get the data for this row
			if (values[row] != null && values[row].length > 0) {
				Object valueObject = values[row][0];
				if (valueObject instanceof List) {
					@SuppressWarnings("unchecked")
					List<Observation>[] rowValues = (List<Observation>[]) values[row];
					for (int col = 0; col < rowValues.length; col++) {
						int pos = col;
						if (selectMultiple > 0) {
							pos += 1; // Shift cells one to the right if we have
										// a select radio button / checkbox
						}

						if (rowValues[col] != null && rowValues[col].size() > 0) {
							boolean first = true;
							for (Observation val : rowValues[col]) {
								// Initially, value to show is literal value:
								String valueToShow = (String) val.get("value");
								// Remove time part from date:
								if (val instanceof ObservedValue) {
									Measurement measurement = (Measurement) cols
											.get(col);
									String dataType = measurement.getDataType();
									ColumnType columnType = Column
											.getColumnType(dataType);
									if (columnType == ColumnType.DATE) {
										valueToShow = StringUtils
												.substringBefore(valueToShow,
														" ");
									}
								}

								// If value is empty, use relation_Name:
								if (val instanceof ObservedValue
										&& valueToShow == null) {
									String relName = ((ObservedValue) val)
											.getRelation_Name();
									// Make a nice info box about the target in
									// the cell:
									String infoBoxContents = "<strong>"
											+ relName + "</strong><hr>";
									infoBoxContents += "<p>Values set on this target:</p>";
									infoBoxContents += "<ul>";
									try {
										List<ObservedValue> valList = db
												.query(ObservedValue.class)
												.eq(ObservedValue.TARGET_NAME,
														relName)
												.sortASC(
														ObservedValue.FEATURE_NAME)
												.find();
										for (ObservedValue infoVal : valList) {
											String infoValue = infoVal
													.getValue();
											if (infoValue == null) {
												infoValue = infoVal
														.getRelation_Name();
											}
											infoBoxContents += "<li>"
													+ (infoVal
															.getFeature_Name()
															+ ": " + infoValue + "</li>");
										}
										infoBoxContents += "</ul>";
										// If you want info about values in
										// which the target is referred to:
										// infoBoxContents +=
										// "<p>Values in which this target is referred to:</p>";
										// infoBoxContents += "<ul>";
										// valList =
										// db.query(ObservedValue.class).
										// eq(ObservedValue.RELATION_NAME,
										// relName).
										// sortASC(ObservedValue.FEATURE_NAME).
										// find();
										// for (ObservedValue infoVal : valList)
										// {
										// infoBoxContents +="<li>" +
										// (infoVal.getFeature_Name() + ": " +
										// infoVal.getTarget_Name() + "</li>");
										// }
										// infoBoxContents +="</ul>";
									} catch (DatabaseException e) {
										// List will remain empty
									}
									// valueToShow =
									// "<a href=\"\" onclick=\"alert('" +
									// infoBoxContents + "');\">" +
									// relName + "</a>" +
									valueToShow = relName
											+ "<a href=\"#\" onMouseOver=\"toolTip('"
											+ infoBoxContents
											+ "')\" onMouseOut=\"toolTip()\">...</a>";

								}

								// If timing should be shown:
								if (showValueValidRange) {
									if (val.get(ObservedValue.TIME) != null) {
										valueToShow += " (valid from "
												+ newDateOnlyFormat
														.format(val
																.get(ObservedValue.TIME));
									}
									if (val.get(ObservedValue.ENDTIME) != null) {
										valueToShow += " through "
												+ newDateOnlyFormat
														.format(val
																.get(ObservedValue.ENDTIME))
												+ ")";
									} else if (val.get(ObservedValue.TIME) != null) {
										valueToShow += ")";
									}
								}
								if (first) {
									first = false;
									dataTable.setCell(pos, row, valueToShow);
								} else {
									// Append to contents of cell, on new line
									dataTable.setCell(pos, row,
											dataTable.getCell(pos, row)
													+ "<br />" + valueToShow);
								}
							}
						} else {
							dataTable.setCell(pos, row, "NA"); // NA means Not
																// Available, so
																// there is no
																// ObservedValue
																// for this
																// target-feature
																// combination
						}
					}

				} else {
					Object[] rowValues = values[row];
					for (int col = 0; col < rowValues.length; col++) {
						Object val = rowValues[col];
						if (val != null) {
							dataTable.setCell(col + 1, row, val);
						} else {
							dataTable.setCell(col + 1, row, "NA");
						}
					}

				}
			}
		}

		if (selectMultiple > 1) {
			dataTable.addRow("");
			String selectAll = "<input type=\"button\" id=\"selectAllButton\" value=\"Select all visible\" onclick=\"";
			for (int i = 0; i < row; i++) { // putting for-loop inside
											// javascript failed so we do it in
											// this
											// ugly manner...
				selectAll += "document.getElementById('" + SELECTED + "_" + i
						+ "').checked=true; ";
			}
			selectAll += "\" /><script>$(\"#selectAllButton\").button();</script>";
			dataTable.setCell(0, row, selectAll);
		}

		return dataTable.toHtml();
	}

	@SuppressWarnings("unchecked")
	public String renderFilterPart() throws MatrixException, DatabaseException {
		String divContents = "<br /><img id='showHideSettingsButton' src=\"generated-res/img/plus.png\" "
				+ "onclick=\"if (document.getElementById('advancedSettings').style.display=='none') {document.getElementById('advancedSettings').style.display='block'; document.getElementById('showHideSettingsButton').src = 'generated-res/img/minus.png';} else {document.getElementById('advancedSettings').style.display='none'; document.getElementById('showHideSettingsButton').src = 'generated-res/img/plus.png';}\" "
				+ "/>";
		divContents += "<div id='advancedSettings' style='display:none'>";
		divContents += new Paragraph("filterRules", "Applied filters:"
				+ generateFilterRules()).render();
		// add column filter
		List<? extends Object> colHeaders = matrix.getColHeaders();
		divContents += "<div style=\"clear:both\">Add filter:";
		divContents += buildFilterChoices(colHeaders).render();
		divContents += buildFilterOperator(d_selectedMeasurement).render(); // TODO:
																			// chosen()
																			// screws
																			// up
																			// vertical
																			// alignment
																			// under
																			// FF+IE
		divContents += buildFilterInput(d_selectedMeasurement).render(); // TODO:
																			// chosen()
																			// screws
																			// up
																			// vertical
																			// alignment
																			// under
																			// FF+IE
		divContents += new ActionInput(FILTERCOL, "", "Apply").render()
				+ "</div>";
		// column header filter
		@SuppressWarnings("rawtypes")
		List selectedMeasurements = new ArrayList(colHeaders);
		MrefInput measurementChooser = new MrefInput(
				MEASUREMENTCHOOSER,
				"Add/remove columns:",
				selectedMeasurements,
				false,
				false,
				"Choose one or more columns (i.e. measurements) to be displayed in the matrix viewer",
				Measurement.class);
		// disable display of button for adding new measurements from here
		measurementChooser.setIncludeAddButton(false);
		divContents += new Newline().render();
		divContents += "<div style=\"clear: both; vertical-align:middle\">Add/remove columns:";
		divContents += measurementChooser.render();
		divContents += new ActionInput(UPDATECOLHEADERFILTER, "", "Update")
				.render();
		if (this.APPLICATION_STRING != "ANIMALDB") {
			divContents += new ActionInput(ADDALLCOLHEADERFILTER, "", "Add all")
					.render();
		}

		// divContents += new ActionInput(REMALLCOLHEADERFILTER, "",
		// "Remove all").render();
		divContents += "</div></div>";
		return divContents;
	}

	private HtmlInput<?> buildFilterInput(Measurement selectedMeasurement) {
		// At this moment, selectedMeasurement is always null. Temp. fix:
		if (selectedMeasurement == null) {
			return new StringInput(COLVALUE);
		}
		switch (MolgenisFieldTypes.getType(selectedMeasurement.getDataType())
				.getEnumType()) {
		// date/datetime
		case DATE:
			return new DateInput(COLVALUE);
		case DATE_TIME:
			return new DatetimeInput(COLVALUE);
		case DECIMAL:
			return new DecimalInput(COLVALUE);
		case INT:
			return new IntInput(COLVALUE);
		case LONG:
			return new LongInput(COLVALUE);
		case STRING:
			return new StringInput(COLVALUE);
		case TEXT:
			return new TextInput(COLVALUE);
			// unknown/none of the above gives regular text box (FIXME: Is
			// this desired behaviour?)
		default:
			return new StringInput(COLVALUE);
		}
	}

	private SelectInput buildFilterOperator(Measurement selectedMeasurement) {
		SelectInput operatorInput = new SelectInput(OPERATOR);
		// At this moment, selectedMeasurement is always null. Temp. fix:
		if (selectedMeasurement == null) {
			operatorInput.addOption(Operator.EQUALS.name(),
					Operator.EQUALS.name());
			operatorInput.addOption(Operator.LIKE.name(), Operator.LIKE.name());
			// operatorInput.addOption(Operator.ISNA, Operator.ISNA.name());
			// TODO: implement! Find a way to filter on ObservedValues that are
			// NOT present
			operatorInput.addOption(Operator.LESS.name(), Operator.LESS.name());
			operatorInput.addOption(Operator.LESS_EQUAL.name(),
					Operator.LESS_EQUAL.name());
			operatorInput.addOption(Operator.GREATER.name(),
					Operator.GREATER.name());
			operatorInput.addOption(Operator.GREATER_EQUAL.name(),
					Operator.GREATER_EQUAL.name());
			return operatorInput;
		}

		for (String operator : MolgenisFieldTypes.getType(
				selectedMeasurement.getDataType()).getAllowedOperators()) {
			operatorInput.addOption(operator, operator);
		}
		return operatorInput;
	}

	@SuppressWarnings("unchecked")
	private SelectInput buildFilterChoices(List<? extends Object> colHeaders) {
		SelectInput colId = new SelectInput(COLID);
		if (showNameFilter == true) {
			colId.addOption(-1, "name");
		}
		if (colHeaders != null && colHeaders.size() > 0
				&& colHeaders.get(0) instanceof Entity) {
			List<? extends Entity> headers = (List<? extends Entity>) colHeaders;
			if (!headers.isEmpty() && headers.get(0) instanceof Measurement
					&& matrix instanceof SliceablePhenoMatrixMV) {
				SliceablePhenoMatrixMV<ObservationTarget, Measurement, ObservedValue> m = (SliceablePhenoMatrixMV<ObservationTarget, Measurement, ObservedValue>) matrix;
				LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol = m
						.getMeasurementsByProtocol();
				for (Entry<Protocol, List<Measurement>> p : measurementsByProtocol
						.entrySet()) {
					Protocol protocol = p.getKey();
					for (Measurement measurement : p.getValue()) {
						colId.addOption(
								protocol.getId() + ";" + measurement.getId(),
								String.format("%s (%s)", measurement.getName(),
										protocol.getName()));
					}
				}
			} else {
				colId.addEntityOptions(headers);
			}
		} else {
			// TODO!!!!!
		}
		colId.setNillable(true);
		return colId;
	}

	private String generateFilterRules() throws MatrixException,
			DatabaseException {
		String outStr = "";
		int filterCnt = 0;
		for (MatrixQueryRule mqr : this.matrix.getRules()) {
			// Show only column value filters to user
			if (mqr.getFilterType().equals(
					MatrixQueryRule.Type.colValueProperty)
					|| (mqr.getFilterType().equals(
							MatrixQueryRule.Type.rowHeader) && mqr.getField()
							.equals("name"))) {
				outStr += generateFilterRule(filterCnt, mqr);
			}
			System.out.println("(mqr.getFilterType() " + mqr.getFilterType());
			++filterCnt;
		}

		// Show applied filter rules
		return outStr.equals("") ? " none" : outStr;
	}

	private String generateFilterRule(int filterCnt, MatrixQueryRule mqr)
			throws MatrixException, DatabaseException {
		String measurementName = findMeasurementName(mqr);
		Object value = mqr.getValue();
		try {
			Measurement meas = db.query(Measurement.class)
					.eq(Measurement.NAME, measurementName).find().get(0);
			if (value != null && meas.getUnit_Name().equals("TargetLink")) {
				int relationId = Integer.parseInt(value.toString());
				value = db.findById(ObservationTarget.class, relationId)
						.getName();
			}
		} catch (Exception e) {
			// No measurement or relation could be retrieved, stick with value
		}
		String field = ""; // Show fieldname only when it's something special
							// (not value or relation_Name but for instance
							// endtime)
		if (mqr.getField() != null && !mqr.getField().equals("value")
				&& !mqr.getField().contains("relation")
				&& !mqr.getField().equals("name")) {
			field = "." + mqr.getField();
		}
		String outStr = "<br />" + measurementName + field + " "
				+ mqr.getOperator().toString() + " "
				+ (value != null ? value.toString() : "NULL");
		ActionInput removeButton = new ActionInput(REMOVEFILTER + "_"
				+ filterCnt, "", "");
		removeButton.setIcon("generated-res/img/delete.png");
		outStr += removeButton.render();
		return outStr;
	}

	private String findMeasurementName(MatrixQueryRule mqr) {
		String measurementName = "";
		try {
			for (Object meas : matrix.getColHeaders()) {
				if (meas instanceof ObservationElement) {
					ObservationElement measr = (ObservationElement) meas;
					if (measr.getId() != null
							&& mqr.getDimIndex() != null
							&& measr.getId().intValue() == mqr.getDimIndex()
									.intValue()) {
						measurementName = measr.getName();
					}
				} else {
					measurementName = meas.toString();
				}
			}
		} catch (MatrixException e1) {
		}
		// If name not in column headers, retrieve via DB (if available)
		if (measurementName.equals("") && this.matrix instanceof DatabaseMatrix) {
			try {
				measurementName = db.findById(Measurement.class,
						mqr.getDimIndex()).getName();
			} catch (Exception e) {
			}
		}
		// If still unknown, assume "name"
		if (measurementName.equals("")) {
			measurementName = "name";
		}

		return measurementName;
	}

	public void removeFilter(String action) throws MatrixException {
		int filterNr = Integer.parseInt(action.substring(action
				.lastIndexOf("_") + 1));
		this.matrix.getRules().remove(filterNr);
		matrix.reload();
	}

	public void clearFilters(Database db, Tuple t) throws MatrixException {
		matrix.reset();
	}

	/**
	 * Remove only the colValueProperty type filters from the matrix.
	 * 
	 * @param db
	 * @param t
	 * @throws MatrixException
	 */
	public void clearValueFilters(Database db, Tuple t) throws MatrixException {
		List<MatrixQueryRule> removeList = new ArrayList<MatrixQueryRule>();
		for (MatrixQueryRule mqr : this.matrix.getRules()) {
			if (mqr.getFilterType().equals(
					MatrixQueryRule.Type.colValueProperty)) {
				removeList.add(mqr);
			}
		}
		this.matrix.getRules().removeAll(removeList);
		matrix.reload();
	}

	public String selectDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Date dat = new Date();
		return dateFormat.format(dat);
	}

	public File makeFile(String visAll, String extension) {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(tmpDir.getAbsolutePath() + File.separatorChar
				+ "Export" + visAll + "_" + selectDate().replace(":", "_")
				+ "." + extension);
		return file;
	}

	public void download(Database db, Tuple t) {
		// to prevent nullpointer error when clicking the Download menu button
	}

	@SuppressWarnings("unchecked")
	public void downloadAllCsv(Database db, Tuple t) throws MatrixException,
			IOException {
		File file = makeFile("_All", "csv");

		if (matrix instanceof SliceablePhenoMatrixMV) {
			// AbstractExporter<ObservationTarget, Measurement, ObservedValue>
			// exporter =
			// new CsvExporter<ObservationTarget, Measurement,
			// ObservedValue>((SliceablePhenoMatrixMV<ObservationTarget,
			// Measurement, ObservedValue>)matrix, new FileOutputStream(file));
			// exportAll(file, exporter);
		} else {
			// remember old limits and offset
			int rowOffset = matrix.getRowOffset();
			int rowLimit = matrix.getRowLimit();
			int colOffset = matrix.getColOffset();
			int colLimit = matrix.getColLimit();

			// max for batching
			int maxRow = matrix.getRowCount();

			CsvWriter writer = new CsvFileWriter(file);
			writer.setSeparator(",");

			// batch size = 100
			matrix.setRowLimit(BATCHSIZE);
			matrix.setColLimit(matrix.getColCount());

			// write headers
			List<String> headers = new ArrayList<String>();
			headers.add("Name");
			for (ObservationElement colHeader : (List<ObservationElement>) matrix
					.getColHeaders()) {
				headers.add(colHeader.getName());
			}
			writer.setHeaders(headers);
			writer.writeHeader();

			// iterate through all available rows
			for (int offset = 0; offset < maxRow; offset += BATCHSIZE) {
				// retrieve a batch
				matrix.setRowOffset(offset);
				// retrieve names of targets in batch
				List<ObservationElement> targets = (List<ObservationElement>) matrix
						.getRowHeaders();
				// write lines to file
				int rowCnt = 0;
				for (List<? extends ObservedValue>[] row : (List<? extends ObservedValue>[][]) matrix
						.getValueLists()) {
					writer.writeValue(targets.get(rowCnt).getName());
					for (int colId = 0; colId < row.length; colId++) {
						List<? extends ObservedValue> valueList = row[colId];
						writer.writeSeparator();
						writer.writeValue(this.valueListToString(valueList));
					}
					writer.writeEndOfLine();
					++rowCnt;
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
	}

	// private void exportAll(File file, Exporter<ObservationTarget,
	// Measurement, ObservedValue> exporter)
	// throws MatrixException {
	// try {
	// FileOutputStream os = new FileOutputStream(file);
	// exporter.exportAll();
	// os.flush();
	// os.close();
	// downloadLink = file.getName();
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// throw new MatrixException(e1);
	// }
	// }

	// private void exportVisible(String fileName, Exporter<ObservationTarget,
	// Measurement, ObservedValue> exporter)
	// throws MatrixException {
	// try {
	// exporter.exportVisible();
	// downloadLink = fileName;
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// throw new MatrixException(e1);
	// }
	// }

	@SuppressWarnings("unchecked")
	public void downloadVisibleCsv(Database db, Tuple t)
			throws MatrixException, IOException {
		File file = makeFile("_Visible", "csv");
		if (matrix instanceof SliceablePhenoMatrixMV) {
			FileOutputStream os = new FileOutputStream(file);
			// AbstractExporter<ObservationTarget, Measurement, ObservedValue>
			// exporter =
			// new CsvExporter<ObservationTarget, Measurement,
			// ObservedValue>((SliceablePhenoMatrixMV<ObservationTarget,
			// Measurement, ObservedValue>)matrix, os);
			// exportVisible(file.getName(), exporter);
		} else {
			List<?> rows = matrix.getRowHeaders();
			List<?> cols = matrix.getColHeaders();
			List<? extends ObservedValue>[][] values = null;

			try {
				values = (List<? extends ObservedValue>[][]) matrix
						.getValueLists();
			} catch (UnsupportedOperationException ue) {
				// values = matrix.getValues();
			}
			downloadCsv(rows, cols, values, file);
		}
	}

	@SuppressWarnings("unchecked")
	public void downloadCsv(List<?> rows, List<?> cols,
			List<? extends ObservedValue>[][] values, File file)
			throws IOException, MatrixException {
		CsvWriter writer = new CsvFileWriter(file);
		writer.setSeparator(",");

		// write headers
		List<String> headers = new ArrayList<String>();
		headers.add("Name");
		for (ObservationElement colHeader : (List<ObservationElement>) matrix
				.getColHeaders()) {
			headers.add(colHeader.getName());
		}
		writer.setHeaders(headers);
		writer.writeHeader();

		// print rowHeader + colValues
		for (int row = 0; row < values.length; row++) {
			// print name
			Object rowobj = rows.get(row);
			if (rowobj instanceof ObservationElement) {
				ObservationElement rowObs = (ObservationElement) rowobj;
				writer.writeValue(rowObs.getName());
			} else {
				writer.writeValue(rowobj.toString());
			}
			// get the data for this row
			List<? extends ObservedValue>[] value = values[row];

			for (List<? extends ObservedValue> valueList : value) {
				writer.writeSeparator();
				writer.writeValue(this.valueListToString(valueList));
			}
			writer.writeEndOfLine();
		}

		writer.close();
		downloadLink = file.getName();
	}

	@SuppressWarnings("unchecked")
	public void downloadAllExcel(Database db, Tuple t) throws MatrixException,
			IOException, RowsExceededException, WriteException {
		File excelFile = makeFile("_All", "xls");
		if (matrix instanceof SliceablePhenoMatrixMV) {
			// ExcelExporter<ObservationTarget, Measurement, ObservedValue>
			// exporter =
			// new ExcelExporter<ObservationTarget, Measurement, ObservedValue>
			// ((SliceablePhenoMatrixMV<ObservationTarget, Measurement,
			// ObservedValue>)matrix, new FileOutputStream(excelFile));
			// exportAll(excelFile, exporter);
		} else {
			// remember old limits and offset
			int rowOffset = matrix.getRowOffset();
			int rowLimit = matrix.getRowLimit();
			int colOffset = matrix.getColOffset();
			int colLimit = matrix.getColLimit();

			// max for batching
			int maxRow = matrix.getRowCount();
			String target = "name";

			File file = makeFile("_All", "xls");
			System.out.println(file);
			/* Create new Excel workbook and sheet */
			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(file, ws);
			WritableSheet s = workbook.createSheet("Sheet1", 0);

			/* Format the fonts */
			WritableCellFormat headerFormat = new WritableCellFormat(
					new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
			headerFormat.setWrap(false);
			WritableCellFormat cellFormat = new WritableCellFormat(
					new WritableFont(WritableFont.ARIAL, 10,
							WritableFont.NO_BOLD));
			cellFormat.setWrap(false);

			// batch size = 100
			matrix.setRowLimit(BATCHSIZE);
			matrix.setColLimit(matrix.getColCount());

			// write targetheader
			Label e = new Label(0, 0, target, headerFormat);
			s.addCell(e);

			// Write column headers
			int count = 1;
			for (ObservationElement colHeader : (List<ObservationElement>) matrix
					.getColHeaders()) {
				Label f = new Label(count, 0, colHeader.getName(), headerFormat);
				s.addCell(f);
				++count;
			}

			// iterate through all available rows
			for (int offset = 0; offset <= maxRow; offset += BATCHSIZE) {
				// retrieve a batch
				matrix.setRowOffset(offset);
				// retrieve names of targets in batch
				List<ObservationElement> targets = (List<ObservationElement>) matrix
						.getRowHeaders();
				// write lines to file
				int rowCnt = 0;
				for (List<? extends ObservedValue>[] row : (List<? extends ObservedValue>[][]) matrix
						.getValueLists()) {
					Label l = new Label(0, rowCnt + 1, targets.get(rowCnt)
							.getName(), cellFormat);
					s.addCell(l);

					for (int colId = 0; colId < row.length; colId++) {
						List<? extends ObservedValue> valueList = row[colId];

						Label m = new Label(colId + 1, rowCnt + 1,
								this.valueListToString(valueList), cellFormat);
						s.addCell(m);
					}

					rowCnt++;
				}
				db.getEntityManager().clear();
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
	}

	@SuppressWarnings("unchecked")
	public void downloadVisibleExcel(Database db, Tuple t) throws Exception {
		File excelFile = makeFile("_Visible", "xls");
		if (matrix instanceof SliceablePhenoMatrixMV) {
			// ExcelExporter<ObservationTarget, Measurement, ObservedValue>
			// exporter =
			// new ExcelExporter<ObservationTarget, Measurement, ObservedValue>
			// ((SliceablePhenoMatrixMV<ObservationTarget, Measurement,
			// ObservedValue>)matrix, new FileOutputStream(excelFile));
			// exportVisible(excelFile.getName(), exporter);
		} else {

			if (this.matrix instanceof DatabaseMatrix) {
				((DatabaseMatrix) this.matrix).setDatabase(db);
			}

			List<?> listCol = (List<Measurement>) this.matrix.getColHeaders();
			List<String> listC = new ArrayList<String>();
			List<?> listRow = (List<Individual>) this.matrix.getRowHeaders();
			List<String> listR = new ArrayList<String>();
			List<ObservedValue>[][] listVal = (List<ObservedValue>[][]) this.matrix
					.getValueLists();

			String target = "name";
			for (Object col : listCol) {
				if (col instanceof ObservationElement) {
					ObservationElement colobs = (ObservationElement) col;
					listC.add(colobs.getName());
				}
			}
			for (Object m : listRow) {
				if (m instanceof ObservationElement) {
					ObservationElement colobs = (ObservationElement) m;
					listR.add(colobs.getName());
				}
			}

			File file = makeFile("_Visible", "xls");
			/* Create new Excel workbook and sheet */
			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(file, ws);
			WritableSheet s = workbook.createSheet("Sheet1", 0);

			/* Format the fonts */
			WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD);
			WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
			headerFormat.setWrap(false);
			WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD);
			WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
			cellFormat.setWrap(false);

			//
			Label e = new Label(0, 0, target, headerFormat);
			s.addCell(e);

			// Write column headers
			for (int i = 0; i < listC.size(); i++) {

				Label l = new Label(i + 1, 0, listC.get(i), headerFormat);
				s.addCell(l);
			}

			// Write row headers
			for (int i = 0; i < listRow.size(); i++) {
				Object rowobj = listRow.get(i);
				if (rowobj instanceof ObservationElement) {
					ObservationElement rowObs = (ObservationElement) rowobj;
					Label j = new Label(0, i + 1, rowObs.getName(),
							headerFormat);
					s.addCell(j);

				} else {
					Label j = new Label(0, i + 1, rowobj.toString(),
							headerFormat);
					s.addCell(j);
				}
			}

			// Write elements
			for (int a = 0; a < listC.size(); a++) {
				for (int b = 0; b < listR.size(); b++) {
					if (listVal[b][a] != null) {
						Label l = new Label(a + 1, b + 1,
								listObsValToString(listVal[b][a]), cellFormat);
						s.addCell(l);
					} else {
						s.addCell(new Label(a + 1, b + 1, "", cellFormat));
					}

				}
			}

			workbook.write();
			workbook.close();

			downloadLink = file.getName();
		}
	}

	private String listObsValToString(List<ObservedValue> values)
			throws Exception {
		String result = "";
		int pass = 0;
		for (ObservedValue s : values) {
			if (pass > 0) {
				result += "|";
			}
			result += s.getValue();
			pass++;
		}
		return result;
	}

	public void reloadMatrix(Database db, Tuple t) throws MatrixException {
		matrix.reload();
	}

	public void filterCol(Database db, Tuple t) throws Exception {
		if (matrix instanceof SliceablePhenoMatrixMV) {
			String valuePropertyToUse = ObservedValue.VALUE;
			String protocolMeasurementIds = t.getString(COLID);
			String[] values = StringUtils.split(protocolMeasurementIds, ";");
			int protocolId = Integer.parseInt(values[0]);
			int measurementId = Integer.parseInt(values[1]);
			// First find out whether to filter on the value or the
			// relation_Name field
			Measurement filterMeasurement;
			try {
				filterMeasurement = db.findById(Measurement.class,
						measurementId);
			} catch (DatabaseException e) {
				throw new MatrixException(e);
			}
			if (filterMeasurement.getDataType().equals("xref")) {
				valuePropertyToUse = ObservedValue.RELATION_NAME;
			}
			// Find out operator to use
			Operator op = Operator.valueOf(t.getString(OPERATOR));
			// new Operator(t.getString(OPERATOR));
			// Then do the actual slicing
			matrix.sliceByColValueProperty(protocolId, measurementId,
					valuePropertyToUse, op, t.getObject(COLVALUE));

		} else {
			String valuePropertyToUse = ObservedValue.VALUE;
			int measurementId = t.getInt(COLID);
			if (measurementId == -1) { // Filter on name
				matrix.getRules().add(
						new MatrixQueryRule(MatrixQueryRule.Type.rowHeader,
								Individual.NAME, Operator.EQUALS, t
										.getObject(COLVALUE)));
			} else {
				// First find out whether to filter on the value or the
				// relation_Name field
				Measurement filterMeasurement;
				try {
					filterMeasurement = db.findById(Measurement.class,
							measurementId);
				} catch (DatabaseException e) {
					throw new MatrixException(e);
				}
				if (filterMeasurement.getDataType().equals("xref")) {
					valuePropertyToUse = ObservedValue.RELATION_NAME;
				}
				// Find out operator to use
				Operator op = Operator.valueOf(t.getString(OPERATOR));
				// new Operator(t.getString(OPERATOR));
				// Then do the actual slicing
				matrix.sliceByColValueProperty(measurementId,
						valuePropertyToUse, op, t.getObject(COLVALUE));
			}
		}
		matrix.reload(); // to make sure the paging info is correctly updated
	}

	@SuppressWarnings("unchecked")
	public void updateColHeaderFilter(Database db, Tuple t) throws Exception {
		if (matrix instanceof SliceablePhenoMatrixMV) {

			List<Integer> measurementIds = new ArrayList<Integer>();
			List<String> list = (List<String>) t.getList(MEASUREMENTCHOOSER);
			for (String s : list) {
				measurementIds.add(Integer.parseInt(s));
			}
			resetColumns(measurementIds);

		} else {
			List<?> chosenMeasurementIds;
			if (t.getList(MEASUREMENTCHOOSER) != null) {
				chosenMeasurementIds = t.getList(MEASUREMENTCHOOSER);
			} else {
				chosenMeasurementIds = new ArrayList<Object>();
			}
			List<String> chosenMeasurementNames = new ArrayList<String>();
			for (Object measurementId : chosenMeasurementIds) {
				int measId = Integer.parseInt((String) measurementId);
				chosenMeasurementNames.add(db.findById(Measurement.class,
						measId).getName());
			}
			setColHeaderFilter(chosenMeasurementNames);
		}

	}

	public void resetColumns(final List<Integer> columnIds)
			throws DatabaseException {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final LinkedHashMap<Protocol, List<Measurement>> pms = ((SliceablePhenoMatrixMV) matrix)
				.getMeasurementsByProtocol();
		pms.clear();

		// adding columns to pms in correct order
		final List<Measurement> measurements = db.query(Measurement.class)
				.in(Measurement.ID, columnIds).find();

		// find paId to remove (paId is automatical placed into list of column
		// by query backend)
		final Measurement paId = (Measurement) CollectionUtils.find(
				measurements, new Predicate() {
					@Override
					public boolean evaluate(Object arg0) {
						final Measurement m = (Measurement) arg0;
						return m.getName().equalsIgnoreCase("PA_ID");
					}
				});

		if (paId != null) {
			columnIds.remove(new Integer(paId.getId()));
		}

		final Protocol patient = db
				.getEntityManager()
				.createQuery(
						"SELECT p FROM Protocol p WHERE Lower(p.name) = :name",
						Protocol.class).setParameter("name", "patient")
				.getSingleResult();

		final Measurement paIDPk = db
				.getEntityManager()
				.createQuery(
						"SELECT m FROM Measurement m JOIN m.featuresProtocolCollection p WHERE Lower(m.name) = :measurementName AND p.id = :protocolID",
						Measurement.class)
				.setParameter("measurementName", "pa_id")
				.setParameter("protocolID", patient.getId()).getSingleResult();
		List<Measurement> patientMeasurment = new ArrayList();
		patientMeasurment.add(paIDPk);
		pms.put(patient, patientMeasurment);
		for (final Integer colId : columnIds) {
			Measurement m = (Measurement) CollectionUtils.find(measurements,
					new Predicate() {
						public boolean evaluate(Object o) {
							Measurement meas = (Measurement) o;
							return meas.getId().equals(colId);
						}
					});
			Protocol protocol = m.getFeaturesProtocolCollection().iterator()
					.next();
			if (pms.get(protocol) == null) {
				pms.put(protocol,
						new ArrayList<Measurement>(Collections.singleton(m)));
			} else {
				pms.get(protocol).add(m);
			}
		}

		System.out.println("----------");
		for (final Entry<Protocol, List<Measurement>> entry : pms.entrySet()) {
			for (Measurement m : entry.getValue()) {
				System.out.println(m.getName() + " " + m.getId() + " pID: "
						+ entry.getKey().getName());
			}
		}
		System.out.println("----------");
	}

	public void addAllColHeaderFilter(Database db, Tuple t) throws Exception {
		if (matrix instanceof SliceablePhenoMatrixMV) {
			List<Measurement> allMeasurements = db.find(Measurement.class);
			List<Integer> measruementIds = new ArrayList<Integer>();
			for (Measurement m : allMeasurements) {
				measruementIds.add(m.getId());
			}
			resetColumns(measruementIds);
		} else {
			List<Measurement> allMeasurements = db.find(Measurement.class);
			List<String> chosenMeasurementNames = new ArrayList<String>();
			for (Measurement measurement : allMeasurements) {
				chosenMeasurementNames.add(measurement.getName());
			}
			setColHeaderFilter(chosenMeasurementNames);
		}
	}

	public void setColHeaderFilter(List<String> chosenMeasurements)
			throws DatabaseException, MatrixException {
		if (matrix instanceof SliceablePhenoMatrixMV) {
			List<Integer> measurementIds = new ArrayList<Integer>(
					chosenMeasurements.size());
			for (String mId : chosenMeasurements) {
				measurementIds.add(Integer.parseInt(mId));
			}
			resetColumns(measurementIds);
		} else {
			// Find and update col header filter rule
			boolean hasRule = false;
			for (MatrixQueryRule mqr : matrix.getRules()) {
				if (mqr.getFilterType().equals(MatrixQueryRule.Type.colHeader)) {
					if (chosenMeasurements.size() > 0) {
						mqr.setValue(chosenMeasurements); // update
						hasRule = true;
					} else {
						matrix.getRules().remove(mqr);
					}
					break;
				}
			}
			if (!hasRule && chosenMeasurements.size() > 0) {
				matrix.getRules().add(
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader,
								Measurement.NAME, Operator.IN,
								chosenMeasurements));
			}
		}
		matrix.setColLimit(chosenMeasurements.size()); // grow with selected
														// measurements
		matrix.reload();
	}

	public void remAllColHeaderFilter(Database db, Tuple t) throws Exception {
		if (matrix instanceof SliceablePhenoMatrixMV) {

			((SliceablePhenoMatrixMV) matrix).getMeasurementsByProtocol()
					.clear();

		} else {
			List<MatrixQueryRule> removeList = new ArrayList<MatrixQueryRule>();
			for (MatrixQueryRule mqr : matrix.getRules()) {
				if (mqr.getFilterType().equals(MatrixQueryRule.Type.colHeader)) {
					removeList.add(mqr);
				}
			}
			matrix.getRules().removeAll(removeList);
			matrix.setColLimit(0);
			matrix.reload();
		}
	}

	public void rowHeaderEquals(Database db, Tuple t) throws Exception {
		matrix.sliceByRowProperty(ObservationElement.ID,
				QueryRule.Operator.EQUALS, t.getString(ROWHEADER));
	}

	public void changeRowLimit(Database db, Tuple t) {
		this.matrix.setRowLimit(t.getInt(ROWLIMIT));
	}

	public void moveLeftEnd(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setColOffset(0);
	}

	public void moveLeft(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setColOffset(matrix.getColOffset() - matrix.getColLimit() > 0 ? matrix
						.getColOffset() - matrix.getColLimit()
						: 0);
	}

	public void moveRight(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setColOffset(matrix.getColOffset() + matrix.getColLimit() < matrix
						.getColCount() ? matrix.getColOffset()
						+ matrix.getColLimit() : matrix.getColOffset());
	}

	public void moveRightEnd(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setColOffset((matrix.getColCount() % matrix.getColLimit() == 0 ? new Double(
						matrix.getColCount() / matrix.getColLimit()).intValue() - 1
						: new Double(matrix.getColCount()
								/ matrix.getColLimit()).intValue())
						* matrix.getColLimit());
	}

	public void moveUpEnd(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix.setRowOffset(0);
	}

	public void moveUp(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setRowOffset(matrix.getRowOffset() - matrix.getRowLimit() > 0 ? matrix
						.getRowOffset() - matrix.getRowLimit()
						: 0);
	}

	public void moveDown(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setRowOffset(matrix.getRowOffset() + matrix.getRowLimit() < matrix
						.getRowCount() ? matrix.getRowOffset()
						+ matrix.getRowLimit() : matrix.getRowOffset());
	}

	public void moveDownEnd(Database db, Tuple t) throws MatrixException {
		if (this.matrix instanceof DatabaseMatrix) {
			((DatabaseMatrix) this.matrix).setDatabase(db);
		}
		this.matrix
				.setRowOffset((matrix.getRowCount() % matrix.getRowLimit() == 0 ? new Double(
						matrix.getRowCount() / matrix.getRowLimit()).intValue() - 1
						: new Double(matrix.getRowCount()
								/ matrix.getRowLimit()).intValue())
						* matrix.getRowLimit());
	}

	public void delegate(String action, Database db, Tuple request)
			throws HandleRequestDelegationException {
		// try/catch for db.rollbackTx
		// try/catch for method calling
		try {
			System.out.println("trying to use reflection to call ######## "
					+ this.getClass().getName() + "." + action);
			Method m = this.getClass().getMethod(action, Database.class,
					Tuple.class);
			m.invoke(this, db, request);
			logger.debug("call of " + this.getClass().getName() + "(name="
					+ this.getName() + ")." + action + " completed");
		} catch (NoSuchMethodException e1) {
			this.callingScreenController.getModel().setMessages(
					new ScreenMessage("Unknown action: " + action, false));
			logger.error("call of " + this.getClass().getName() + "(name="
					+ this.getName() + ")." + action + "(db,tuple) failed: "
					+ e1.getMessage());
		} catch (Exception e) {
			logger.error("call of " + this.getClass().getName() + "(name="
					+ this.getName() + ")." + action + " failed: "
					+ e.getMessage());
			e.printStackTrace();
			this.callingScreenController.getModel().setMessages(
					new ScreenMessage(e.getCause().getMessage(), false));
		}
	}

	public List<?> getSelection(Database db) throws MatrixException {
		return matrix.getRowHeaders();
	}

	public SliceableMatrix<?, ?, ?> getMatrix() {
		return matrix;
	}

	public boolean getShowValueValidRange() {
		return showValueValidRange;
	}

	public void setShowValueValidRange(boolean showValueValidRange) {
		this.showValueValidRange = showValueValidRange;
	}

	private String valueListToString(List<? extends ObservedValue> valueList) {
		if (valueList == null)
			return "NA";

		String result = "";
		for (int i = 0; i < valueList.size(); i++) {
			if (i > 0)
				result += "|";
			String tmpValue;
			if (valueList.get(i) instanceof ObservedValue
					&& valueList.get(i).getValue() == null) {
				tmpValue = valueList.get(i).getRelation_Name();
			} else {
				tmpValue = valueList.get(i).getValue();
			}
			if (tmpValue == null) { // null values become NA, empty ones remain
									// empty
				tmpValue = "NA";
			}
			result += tmpValue;
		}
		return result;
	}

	public String getAPPLICATION_STRING() {
		return APPLICATION_STRING;
	}

	public void setAPPLICATION_STRING(String aPPLICATION_STRING) {
		APPLICATION_STRING = aPPLICATION_STRING;
	}

}
