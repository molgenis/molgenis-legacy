package plugins.batch;

import java.io.IOException;
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
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;


public class BatchPlugin extends GenericPlugin {

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
    			service.setDatabase(db, this.getLogin().getUserId());
    			this.populateBatchSelectForm();
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }

    @Override
    public void handleRequest(Database db, Tuple request)
    {
    	try
    	{
    		this.action = request.getString("__action");

    		if ( action.equals("Select") )
    		{
    			this.handleSelectRequest(request);
    		}
    		else if (action.equals("Add"))
    		{
    			this.handleAddRequest(request);
    		}
    		else if (action.equals("Remove"))
    		{
    			this.handleRemoveRequest(request);
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
    }

	/**
     * Add an ObservationTarget to the Batch
     * @param request
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws DatabaseException 
     */
    private void handleAddRequest(Tuple request) throws DatabaseException, ParseException, IOException
    {
    	List<Integer> ids = new ArrayList<Integer>();
    	
    	for (Object o : request.getList("addId"))
    		ids.add(Integer.parseInt(o.toString()));
		
		this.service.addToBatch(this.batchId, ids);
		
		this.populateBatchEntitySelectTable();
    }

    /**
     * Remove an ObservationElement from the Batch
     * @param request
     * @throws IOException 
     * @throws DatabaseException 
     * @throws ParseException 
     */
	private void handleRemoveRequest(Tuple request) throws DatabaseException, IOException, ParseException
	{
		List<Integer> ids = new ArrayList<Integer>();
    	
    	for (Object o : request.getList("removeId"))
    		ids.add(Integer.parseInt(o.toString()));
		
		this.service.removeFromBatch(this.batchId, ids);
		
		this.populateBatchEntitySelectTable();
	}

	/**
     * Render the html
     */
    public String render()
    {
    	return this.container.toHtml();
    }

    private void handleSelectRequest(Tuple request) throws DatabaseException, ParseException
    {
    	this.batchId = request.getInt("batches");
    	
    	this.populateBatchEntitySelectTable();
    }

    private void populateBatchSelectForm() throws DatabaseException, ParseException
    {
    	BatchSelectForm batchSelectForm = new BatchSelectForm();
    	
    	List<MolgenisBatch> batches = service.getBatches(this.getLogin().getUserId());
    	((SelectInput) ((DivPanel) batchSelectForm.get("batchPanel")).get("batches")).setOptions(batches, "id", "name"); 
    	
    	this.container = batchSelectForm;
    }

    private void populateBatchEntitySelectTable() throws DatabaseException, ParseException
    {
    	BatchEntitySelectForm batchEntitySelectForm = new BatchEntitySelectForm();

    	List<ObservationTarget> targets             = this.service.getObservationTargetsNotInCurrentBatch(this.batchId);
    	List<MolgenisBatchEntity> entities          = this.service.getBatchEntities(this.batchId);

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
    		//table.setCell(1, i, service.getTargetLabel(target.getId()));
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
    		//table.setCell(1, i, service.getTargetLabel(entity.getObjectId()));
    	}

    	this.container = batchEntitySelectForm;
    }
    
}
