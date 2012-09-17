package plugins.batch;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.batch.service.BatchService;
import org.molgenis.batch.ui.form.BatchEntitySelectForm;
import org.molgenis.batch.ui.form.BatchSelectForm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;


public class BatchPlugin extends EasyPluginController {

    private static final long serialVersionUID = 6468497779526846505L;
    private Container container;
    private BatchService service;
    private String action = "init";
    private int batchId;

    public BatchPlugin(String name, ScreenController<?> parent)
    {
    	super(name, parent);
    	this.service   = new BatchService();
    }

    @Override
    public void reload(Database db)
    {
    	if ("init".equals(this.action))
    	{
    		try
    		{
    			this.populateBatchSelectForm(db);
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }

    @Override
    public Show handleRequest(Database db, Tuple request, OutputStream out)
    {
    	try
    	{
    		this.action = request.getString("__action");

    		if ( action.equals("Select") )
    		{
    			this.handleSelectRequest(db, request);
    		}
    		else if (action.equals("Add"))
    		{
    			this.handleAddRequest(db, request);
    		}
    		else if (action.equals("Remove"))
    		{
    			this.handleRemoveRequest(db, request);
    		}
    		else if (action.equals("Clear"))
    		{
    			this.action = "init";
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return Show.SHOW_MAIN;
    }

	/**
     * Add an ObservationTarget to the Batch
     * @param request
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws DatabaseException 
     */
    private void handleAddRequest(Database db, Tuple request) throws DatabaseException, ParseException, IOException
    {
    	List<Integer> ids = new ArrayList<Integer>();
    	
    	for (Object o : request.getList("addId"))
    		ids.add(Integer.parseInt(o.toString()));
		
		this.service.addToBatch(db, this.batchId, ids);
		
		this.populateBatchEntitySelectTable(db);
    }

    /**
     * Remove an ObservationElement from the Batch
     * @param request
     * @throws IOException 
     * @throws DatabaseException 
     * @throws ParseException 
     */
	private void handleRemoveRequest(Database db, Tuple request) throws DatabaseException, IOException, ParseException
	{
		List<Integer> ids = new ArrayList<Integer>();
    	
    	for (Object o : request.getList("removeId"))
    		ids.add(Integer.parseInt(o.toString()));
		
		this.service.removeFromBatch(db, this.batchId, ids);
		
		this.populateBatchEntitySelectTable(db);
	}

	/**
     * Render the html
     */
    public ScreenView getView()
    {
    	return this.container;
    }

    private void handleSelectRequest(Database db, Tuple request) throws DatabaseException, ParseException
    {
    	this.batchId = request.getInt("batches");
    	
    	this.populateBatchEntitySelectTable(db);
    }

    private void populateBatchSelectForm(Database db) throws DatabaseException, ParseException
    {
    	BatchSelectForm batchSelectForm = new BatchSelectForm();
    	
    	List<MolgenisBatch> batches = service.getBatches(db, db.getLogin().getUserId());
    	((SelectInput) ((DivPanel) batchSelectForm.get("batchPanel")).get("batches")).setOptions(batches, "id", "name"); 
    	
    	this.container = batchSelectForm;
    }

    private void populateBatchEntitySelectTable(Database db) throws DatabaseException, ParseException
    {
    	BatchEntitySelectForm batchEntitySelectForm = new BatchEntitySelectForm();

    	List<ObservationTarget> targets             = this.service.getObservationTargetsNotInCurrentBatch(db, this.batchId);
    	List<MolgenisBatchEntity> entities          = this.service.getBatchEntities(db, this.batchId);

    	for (int i = 0; i < targets.size(); i++)
    	{
    		ObservationTarget target   = targets.get(i);

    		Table table                = (Table) ((DivPanel) batchEntitySelectForm.get("panel")).get("entitiesDbTable");
    		table.addRow("");

    		Vector<ValueLabel> options = new Vector<ValueLabel>();
    		options.add(new ValueLabel(target.getId(), ""));
    		CheckboxInput checkbox     = new CheckboxInput("addId", "", "", options, new Vector<String>());
   
    		table.setCell(0, i, checkbox);
    		table.setCell(1, i, target.getName());
    	}

    	for (int i = 0; i < entities.size(); i++)
    	{
    		MolgenisBatchEntity entity = entities.get(i);

    		Table table                = (Table) ((DivPanel) batchEntitySelectForm.get("panel")).get("entitiesBatchTable");
    		table.addRow("");

    		Vector<ValueLabel> options = new Vector<ValueLabel>();
    		options.add(new ValueLabel(entity.getId(), ""));
    		CheckboxInput checkbox     = new CheckboxInput("removeId", "", "", options, new Vector<String>());
   
    		table.setCell(0, i, checkbox);
    		table.setCell(1, i, entity.getName());
    	}

    	this.container = batchEntitySelectForm;
    }
    
}
