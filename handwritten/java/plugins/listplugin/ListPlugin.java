/* Date:        February 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.HtmlTools;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import app.servlet.MolgenisServlet;

import commonservice.CommonService;

public class ListPlugin extends PluginModel<Entity> {
	private static final long serialVersionUID = -7341276676642021364L;
	private List<Measurement> featureList;
	private List<MolgenisBatch> batchList = new ArrayList<MolgenisBatch>();
	private CommonService ct = CommonService.getInstance();
	
	public ListPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/scripts/custom/jquery.dataTables.js\"></script>\n"
			//+ "<script type=\"text/javascript\" charset=\"utf-8\">jQuery.noConflict();</script>\n"
			//+ "<script src=\"res/scripts/custom/jquery-ui-1.8.6.custom.min.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			+ "<script src=\"res/scripts/custom/jquery.autocomplete.combobox.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_table.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_page.css\">\n";
			//+"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/ui-lightness/jquery-ui-1.8.6.custom.css\">";
		   //+ "<script>$(document).ready(function(){ $( \"#arf\" ).combobox();  }); </script>";
			//+ "<script> $(function() { $( \"#arf\" ).combobox();  });	</script>;";
	}

	@Override
	public String getViewName() {
		return "plugins_listplugin_ListPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/listplugin/ListPlugin.ftl";
	}
	
	// Feature related methods:
	public List<Measurement> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<Measurement> featureList) {
		this.featureList = featureList;
	}
	
	// Batch related methods:
	public List<MolgenisBatch> getBatchList() {
		return batchList;
	}

	public void setGroupList(List<MolgenisBatch> batchList) {
		this.batchList = batchList;
	}

	public void handleRequest(Database db, Tuple request) {
		try {
			String action = request.getString("__action");
			
			if (action.equals("saveBatch")) {
				int batchId;
				// Make or get Batch
				if (request.getString("newbatchname") != null) {
					String batchName = request.getString("newbatchname");
					MolgenisBatch newBatch = new MolgenisBatch();
					newBatch.setName(batchName);
					newBatch.setMolgenisUser(this.getLogin().getUserId());
					db.add(newBatch);
					batchId = newBatch.getId();
				} else {
					batchId = request.getInt("batch");
				}
				// Add visible targets to Batch
				List<?> nameList = request.getList("saveselection", ",");
				for (Object o : nameList) {
					int animalId = ct.getObservationTargetId(o.toString());
					MolgenisBatchEntity newBatchEntity = new MolgenisBatchEntity();
					newBatchEntity.setName(o.toString());
					newBatchEntity.setBatch(batchId);
					newBatchEntity.setObjectId(animalId);
					db.add(newBatchEntity);
				}
				
				this.setMessages(new ScreenMessage("Batch saved successfully", true));	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public void reload(Database db) {
		
		try {
			// Reset servlet so state of that remains consistent with ours
			String url = "http://localhost:8080/" + MolgenisServlet.getMolgenisVariantID() + 
				"/EventViewerJSONServlet?reset=1";
			URL servletURL = new URL(url);
			URLConnection servletConn = servletURL.openConnection();
			try {
				servletConn.getContent();
			} catch (IOException e) {
				this.setMessages(new ScreenMessage("Data source reset through localhost:8080", true));
				// getContent() will always throw an exception, so do nothing more here
			}

			// Populate measurement list
			int investigationId = ct.getUserInvestigationId(this.getLogin().getUserId());
			List<Measurement> featList = ct.getAllMeasurementsSorted(Measurement.NAME, "ASC", investigationId);
			if (featList.size() > 0) {
				this.setFeatureList(featList);
			} else {
				throw new DatabaseException("Something went wrong while loading Measurement list");
			}
		
			// Populate Batch list
			batchList = ct.getAllBatches();
			
		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}
	
	public int getUserId() {
		return this.getLogin().getUserId();
	}

}
