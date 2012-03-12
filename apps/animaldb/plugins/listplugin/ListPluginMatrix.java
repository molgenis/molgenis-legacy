/* Date:        February 10, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ListPluginMatrix extends GenericPlugin
{
	private static final long serialVersionUID = 8804579908239186037L;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	private Container container = null;
	private DivPanel div = null;
	private String action = "init";
	private CommonService cs = CommonService.getInstance();
	private boolean reload = true;
	private int userId = -1;
	
	public ListPluginMatrix(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
		}
		
		reload = true;
		action = request.getAction();
		
		try {
			if (action != null && action.startsWith(targetMatrixViewer.getName())) {
	    		targetMatrixViewer.handleRequest(db, request);
	    		reload = false;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage("Something went wrong while handling request: " + e.getMessage(), false));
		}
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		
		// If a non-matrix related request was handled or if a new user has logged in, reload the matrix
		if (reload == true || userId != this.getLogin().getUserId().intValue()) {
			userId = this.getLogin().getUserId().intValue();
			container = new Container();
			div = new DivPanel();
			try {
				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
				List<String> measurementsToShow = new ArrayList<String>();
				// Some measurements that we think AnimalDB users like to see most:
				measurementsToShow.add("Active");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Line");
				measurementsToShow.add("OldUliDbId");
				measurementsToShow.add("OldUliDbTiernummer");
				measurementsToShow.add("OldRhutDbAnimalId");
				measurementsToShow.add("Remark");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
						cs.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS,
						"Alive"));
				targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 0, true, false, filterRules, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				targetMatrixViewer.setDatabase(db);
				div.add(targetMatrixViewer);
				container.add(div);
			} catch(Exception e) {
				e.printStackTrace();
				this.getMessages().add(new ScreenMessage("Something went wrong while loading matrix: " + e.getMessage(), false));
			}
		} else {
			targetMatrixViewer.setDatabase(db);
		}
    }
	
	public String render()
    {
    	return container.toHtml();
    }
	
}
