package plugins.data;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class MeasurementsOrderForm extends PluginModel<Entity>{


	private static final long serialVersionUID = -8140222842047905408L;
	
	private String Status = "Welcome to Measurements Order Form";

	public MeasurementsOrderForm(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_data_MeasurementsOrderForm";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/MeasurementsOrderForm.ftl";
	}

	
	@Override
	public void handleRequest(Database db, Tuple request) throws HandleRequestDelegationException, Exception {
		
	}

	@Override
	public void reload(Database db) {
		
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

}
