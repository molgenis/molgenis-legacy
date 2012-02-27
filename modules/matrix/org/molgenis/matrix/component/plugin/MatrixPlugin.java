/* Date:        February 10, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.matrix.component.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;

public class MatrixPlugin extends GenericPlugin
{
	private static final long serialVersionUID = 8804579908239186037L;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	private Container container = null;
	private DivPanel div = null;
	private String action = "init";
	
	public MatrixPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
		}
		
		action = request.getAction();
		
		try {
			if (action.startsWith(targetMatrixViewer.getName())) {
	    		targetMatrixViewer.handleRequest(db, request);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage("Something went wrong while handling request: " + e.getMessage(), false));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void reload(Database db)
	{
		if(db instanceof JpaDatabase) {
			if (container == null) {
				container = new Container();
				div = new DivPanel();
				try {
					List<String> measurementsToShow = new ArrayList<String>();
					
					// FIXME : magic numbers
					Investigation investigation = db.find(Investigation.class).get(0);
					Protocol patient = Protocol.findByNameInvestigation(db, "PATIENT", investigation.getId());
					
					LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
					measurementByProtocol.put(patient, (List<Measurement>)(List)patient.getFeatures());
					SliceableMatrix<ObservationTarget, Measurement, ObservedValue> matrix = new SliceablePhenoMatrixMV<ObservationTarget, Measurement, ObservedValue>(db, ObservationTarget.class, Measurement.class, investigation, measurementByProtocol);
					targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX, 
							matrix, 
							true, 0, true, true, Arrays.asList(new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow)));
					targetMatrixViewer.setDatabase(db);
					div.add(targetMatrixViewer);
					container.add(div);
				} catch(MatrixException e) { 
					e.printStackTrace();
					this.getMessages().add(new ScreenMessage("Something went wrong while loading matrix: " + e.getMessage(), false));			
				} catch(Exception e) {
					e.printStackTrace();
					this.getMessages().add(new ScreenMessage("Something went wrong while loading matrix: " + e.getMessage(), false));
				} 
			} else {
				targetMatrixViewer.setDatabase(db);
			}
		} else {
			if (container == null) {
				container = new Container();
				div = new DivPanel();
				try {
					List<String> measurementsToShow = new ArrayList<String>();
					List<Measurement> allMeasurements = db.query(Measurement.class).sortASC(Measurement.NAME).find();
					int nr = 0;
					for (Measurement measurement : allMeasurements) {
						measurementsToShow.add(measurement.getName());
						if (nr == 9) {
							break;
						}
						nr++;
					}
					List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
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
    }
	
	public String render()
    {
    	return container.toHtml();
    }
	
}
