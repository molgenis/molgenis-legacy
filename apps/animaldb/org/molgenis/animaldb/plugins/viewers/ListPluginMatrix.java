/* Date:        February 10, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.viewers;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.EditableJQueryDataTable;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

public class ListPluginMatrix extends EasyPluginController {
	private static final long serialVersionUID = 8804579908239186037L;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	static String EDIT_BUTTON_ACTION = "switch_to_edit_mode";
	private Container container = null;
	private DivPanel div = null;
	private String action = "init";
	private CommonService cs = CommonService.getInstance();
	private boolean reload = true;
	private int userId = -1;
	private Boolean inEditMode;

	public ListPluginMatrix(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) {
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
			targetMatrixViewer.setAPPLICATION_STRING("ANIMALDB");
		}

		reload = true;
		action = request.getAction();

		try {
			if (action != null
					&& action.startsWith(targetMatrixViewer.getName())) {
				targetMatrixViewer.handleRequest(db, request);
				reload = false;
			}

			if (action.equals(EDIT_BUTTON_ACTION)
					|| action.equals(targetMatrixViewer.getName()
							+ EditableJQueryDataTable.MATRIX_EDIT_ACTION)) {
				if (inEditMode) {
					inEditMode = false;
				} else {
					inEditMode = true;
				}
				reload = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().add(
					new ScreenMessage(
							"Something went wrong while handling request: "
									+ e.getMessage(), false));
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void reload(Database db) {
		if (inEditMode == null) {
			inEditMode = false;
		}

		cs.setDatabase(db);

		// If a non-matrix related request was handled or if a new user has
		// logged in, reload the matrix
		if (reload == true || userId != db.getLogin().getUserId().intValue()) {
			userId = db.getLogin().getUserId().intValue();
			container = new Container();
			div = new DivPanel();
			try {
				List<String> investigationNames = cs
						.getAllUserInvestigationNames(db.getLogin()
								.getUserName());
				List<String> measurementsToShow = new ArrayList<String>();
				// Some measurements that we think AnimalDB users like to see
				// most:
				measurementsToShow.add("Active");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Species");
				measurementsToShow.add("Line");
				// measurementsToShow.add("OldUliDbId");
				measurementsToShow.add("OldUliDbTiernummer");
				measurementsToShow.add("OldRhutDbAnimalId");
				measurementsToShow.add("Location");
				measurementsToShow.add("Background");
				// measurementsToShow.add("Remark");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(
						MatrixQueryRule.Type.rowHeader,
						Individual.INVESTIGATION_NAME, Operator.IN,
						investigationNames));
				filterRules.add(new MatrixQueryRule(
						MatrixQueryRule.Type.colValueProperty, cs
								.getMeasurementId("Active"),
						ObservedValue.VALUE, Operator.EQUALS, "Alive"));

				if (inEditMode) {
					// restore filters and measurements to show
					filterRules = targetMatrixViewer.getMatrix().getRules();
					int oldOffset = targetMatrixViewer.getMatrix()
							.getRowOffset();
					int oldLimit = targetMatrixViewer.getMatrix().getRowLimit();

					targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX,
							new SliceablePhenoMatrix<Individual, Measurement>(
									Individual.class, Measurement.class), true,
							0, true, false, filterRules, null, true);
					// enable animalDB specific traits (sorting filtering etc)
					targetMatrixViewer.setAPPLICATION_STRING("ANIMALDB");
					// restore paging
					targetMatrixViewer.getMatrix().setRowOffset(oldOffset);
					targetMatrixViewer.getMatrix().setRowLimit(oldLimit);

					String nameIdSave = targetMatrixViewer.getName()
							+ EditableJQueryDataTable.MATRIX_EDIT_ACTION;
					ActionInput saveButton = new ActionInput(nameIdSave, "",
							"Save");
					saveButton.setId(nameIdSave);
					div.add(saveButton);

					ActionInput editButton = new ActionInput(
							EDIT_BUTTON_ACTION, "", "Cancel");
					editButton.setId(EDIT_BUTTON_ACTION);
					div.add(editButton);

				} else {
					int oldOffset = 0;
					int oldLimit = 10;
					MatrixQueryRule measurements = new MatrixQueryRule(
							MatrixQueryRule.Type.colHeader, Measurement.NAME,
							Operator.IN, measurementsToShow);

					if (targetMatrixViewer != null) {
						// restore filters and measurements to show
						filterRules = targetMatrixViewer.getMatrix().getRules();
						oldOffset = targetMatrixViewer.getMatrix()
								.getRowOffset();
						oldLimit = targetMatrixViewer.getMatrix().getRowLimit();
						measurements = null;
					}

					targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX,
							new SliceablePhenoMatrix<Individual, Measurement>(
									Individual.class, Measurement.class), true,
							0, true, false, filterRules, measurements, false);

					// enable animalDB specific traits (sorting filtering etc)
					targetMatrixViewer.setAPPLICATION_STRING("ANIMALDB");
					// restore paging
					targetMatrixViewer.getMatrix().setRowOffset(oldOffset);
					targetMatrixViewer.getMatrix().setRowLimit(oldLimit);

					// Temporarily make this function admin only, because it is
					// to dangerous for normal users in its current form
					String userName = db.getLogin().getUserName();
					if (userName.equals("admin")) {
						ActionInput editButton = new ActionInput(
								EDIT_BUTTON_ACTION, "", "Edit");
						editButton.setId(EDIT_BUTTON_ACTION);
						div.add(editButton);
					}
				}

				targetMatrixViewer.setDatabase(db);
				div.add(targetMatrixViewer);
				container.add(div);
			} catch (Exception e) {
				e.printStackTrace();
				this.getMessages().add(
						new ScreenMessage(
								"Something went wrong while loading matrix: "
										+ e.getMessage(), false));
			}
		} else {
			targetMatrixViewer.setDatabase(db);
		}
	}

	public ScreenView getView() {
		MolgenisForm view = new MolgenisForm(this);
		view.add(container);
		return view;
	}

}
