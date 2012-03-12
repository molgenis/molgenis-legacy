/* Date:        November 9, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.animaldb.DeletedIndividual;
import org.molgenis.animaldb.DeletedObservationTarget;
import org.molgenis.animaldb.DeletedObservedValue;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ErrorCorrectionIndividualPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -366762636959036651L;
	private CommonService cs = CommonService.getInstance();
	MatrixViewer indMatrixViewer = null;
	static String INDMATRIX = "indmatrix";
	MatrixViewer delIndMatrixViewer = null;
	static String DELINDMATRIX = "delindmatrix";
	private boolean reload = true;
	private int userId = -1;
	private String indMatrixRendered;
	private String delIndMatrixRendered;
	
	public ErrorCorrectionIndividualPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n";
    }

	@Override
	public String getViewName()
	{
		return "plugins_system_ErrorCorrectionIndividualPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/ErrorCorrectionIndividualPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
		if (indMatrixViewer != null) {
			indMatrixViewer.setDatabase(db);
		}
		if (delIndMatrixViewer != null) {
			delIndMatrixViewer.setDatabase(db);
		}
		
		String action = request.getString("__action");
		
		try
		{	
			if (action != null && action.startsWith(indMatrixViewer.getName())) {
	    		indMatrixViewer.handleRequest(db, request);
	    		reload = false;
	    		return;
			}
			
			if (action != null && action.startsWith(delIndMatrixViewer.getName())) {
	    		delIndMatrixViewer.handleRequest(db, request);
	    		reload = false;
	    		return;
			}
			
			if (action.equals("deleteIndividuals")) {
				deleteIndividuals(db, request);
				reload = true;
			}
			
			if (action.equals("undeleteIndividuals")) {
				undeleteIndividuals(db, request);
				reload = true;
			}
			
		} catch(Exception e) {
			this.setError("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void undeleteIndividuals(Database db, Tuple request) throws DatabaseException, MatrixException {
		List<DeletedObservationTarget> removalList = new ArrayList<DeletedObservationTarget>();
		List<ObservationTarget> addList = new ArrayList<ObservationTarget>();
		List<DeletedObservedValue> valRemovalList = new ArrayList<DeletedObservedValue>();
		List<ObservedValue> valAddList = new ArrayList<ObservedValue>();
		List<Integer> delIndividualList = new ArrayList<Integer>();
		
		@SuppressWarnings("unchecked")
		List<ObservationElement> rows = (List<ObservationElement>) delIndMatrixViewer.getSelection(db);
		int rowCnt = 0;
		for (ObservationElement row : rows) {
			if (request.getBool(DELINDMATRIX + "_selected_" + rowCnt) != null) {
				delIndividualList.add(row.getId());
			}
			rowCnt++;
		}
		
		for (int delIndId : delIndividualList) {
			DeletedIndividual ind = db.findById(DeletedIndividual.class, delIndId);
			removalList.add(ind);
			
			Individual addInd = new Individual();
			addInd.setDescription(ind.getDescription());
			addInd.setInvestigation_Id(ind.getInvestigation_Id());
			addInd.setName(ind.getName());
			addInd.setOntologyReference_Id(ind.getOntologyReference_Id());
			addInd.setCanRead_Id(ind.getCanRead_Id());
			addInd.setCanWrite_Id(ind.getCanWrite_Id());
			addInd.setOwns_Id(ind.getOwns_Id());
			addList.add(addInd);
			
			Query<DeletedObservedValue> q = db.query(DeletedObservedValue.class);
			QueryRule qrTarget = new QueryRule(DeletedObservedValue.DELETEDTARGET, Operator.EQUALS, ind.getId());
			QueryRule qrRelation = new QueryRule(DeletedObservedValue.DELETEDRELATION, Operator.EQUALS, ind.getId());
			q.addRules(new QueryRule(qrTarget, new QueryRule(Operator.OR), qrRelation));
			q.addRules(new QueryRule(DeletedObservedValue.DELETIONTIME, Operator.EQUALS, ind.getDeletionTime()));
			List<DeletedObservedValue> valList = q.find();
			valRemovalList.addAll(valList);
			for (DeletedObservedValue val : valList) {
				ObservedValue addVal = new ObservedValue();
				addVal.setEndtime(val.getEndtime());
				addVal.setFeature_Id(val.getFeature_Id());
				addVal.setInvestigation_Id(val.getInvestigation_Id());
				addVal.setOntologyReference_Id(val.getOntologyReference_Id());
				addVal.setProtocolApplication_Id(val.getProtocolApplication_Id());
				addVal.setTime(val.getTime());
				addVal.setValue(val.getValue());
				if (val.getDeletedTarget_Id() != null && val.getDeletedTarget_Id().intValue() == ind.getId().intValue()) {
					// Attach to undeleted Target
					addVal.setTarget_Name(addInd.getName());
					addVal.setRelation_Id(val.getRelation_Id()); // Relation we can keep as-is because it wasn't (un)deleted
				} else {
					// Attach to undeleted Relation
					addVal.setRelation_Name(addInd.getName());
					addVal.setTarget_Id(val.getTarget_Id()); // Target we can keep as-is because it wasn't (un)deleted
				}
				valAddList.add(addVal);
			}
		}
		db.remove(valRemovalList);
		db.remove(removalList);
		db.add(addList);
		db.add(valAddList);
	}

	private void deleteIndividuals(Database db, Tuple request) throws MatrixException, DatabaseException {
		Date deletionTime = new Date();
		List<Individual> removalList = new ArrayList<Individual>();
		List<DeletedIndividual> addList = new ArrayList<DeletedIndividual>();
		List<ObservedValue> valRemovalList = new ArrayList<ObservedValue>();
		List<DeletedObservedValue> valAddList = new ArrayList<DeletedObservedValue>();
		List<Integer> individualList = new ArrayList<Integer>();
		
		@SuppressWarnings("unchecked")
		List<ObservationElement> rows = (List<ObservationElement>) indMatrixViewer.getSelection(db);
		int rowCnt = 0;
		for (ObservationElement row : rows) {
			if (request.getBool(INDMATRIX + "_selected_" + rowCnt) != null) {
				individualList.add(row.getId());
			}
			rowCnt++;
		}
		
		for (int indId : individualList) {
			Individual tgt = db.findById(Individual.class, indId);
			removalList.add(tgt);
			
			DeletedIndividual delInd = new DeletedIndividual();
			delInd.setDescription(tgt.getDescription());
			delInd.setInvestigation_Id(tgt.getInvestigation_Id());
			delInd.setName(tgt.getName());
			delInd.setOntologyReference_Id(tgt.getOntologyReference_Id());
			delInd.setCanRead_Id(tgt.getCanRead_Id());
			delInd.setCanWrite_Id(tgt.getCanWrite_Id());
			delInd.setOwns_Id(tgt.getOwns_Id());
			delInd.setDeletionTime(deletionTime);
			delInd.setDeletedBy_Id(this.getLogin().getUserId());
			addList.add(delInd);
			
			List<ObservedValue> valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, tgt.getId()).
					or().eq(ObservedValue.RELATION, tgt.getId()).find();
			valRemovalList.addAll(valList);
			for (ObservedValue val : valList) {
				DeletedObservedValue valDel = new DeletedObservedValue();
				valDel.setEndtime(val.getEndtime());
				valDel.setFeature_Id(val.getFeature_Id());
				valDel.setInvestigation_Id(val.getInvestigation_Id());
				valDel.setOntologyReference_Id(val.getOntologyReference_Id());
				valDel.setProtocolApplication_Id(val.getProtocolApplication_Id());
				valDel.setTime(val.getTime());
				valDel.setValue(val.getValue());
				if (val.getTarget_Id().intValue() == tgt.getId().intValue()) {
					// Attach to deleted Individual
					valDel.setDeletedTarget_Name(delInd.getName());
					valDel.setTarget_Id(1); // Hack TODO: solve.
					valDel.setRelation_Id(val.getRelation_Id()); // Relation we can keep as-is because this target wasn't deleted
				} else {
					// Attach to deleted Relation
					valDel.setDeletedRelation_Name(delInd.getName());
					valDel.setTarget_Id(val.getTarget_Id()); // Target we can keep as-is because this target wasn't deleted
				}
				valDel.setDeletionTime(deletionTime);
				valDel.setDeletedBy_Id(this.getLogin().getUserId());
				valAddList.add(valDel);
			}
		}
		db.remove(valRemovalList);
		db.remove(removalList);
		db.add(addList);
		db.add(valAddList);
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		
		// If a non-matrix related request was handled or if a new user has logged in, reload the matrix
		if (reload == true || userId != this.getLogin().getUserId().intValue()) {
			reload = false;
			userId = this.getLogin().getUserId().intValue();
			try {
				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
						Operator.IN, investigationNames));
				indMatrixViewer = new MatrixViewer(this, INDMATRIX, 
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
						true, 2, false, true, filterRules, null);
				delIndMatrixViewer = new MatrixViewer(this, DELINDMATRIX, 
						new SliceablePhenoMatrix<DeletedIndividual, Measurement>(DeletedIndividual.class, Measurement.class), 
						true, 2, false, true, filterRules, null);
			} catch(Exception e) {
				e.printStackTrace();
				this.setError("Something went wrong while loading individuals matrix: " + e.getMessage());
			}
		}
		indMatrixViewer.setDatabase(db);
		indMatrixRendered = indMatrixViewer.render();
		delIndMatrixViewer.setDatabase(db);
		delIndMatrixRendered = delIndMatrixViewer.render();
	}

	public String getIndividualMatrix() {
		return this.indMatrixRendered;
	}
	
	public String getDeletedIndividualMatrix() {
		return this.delIndMatrixRendered;
	}
	
}
