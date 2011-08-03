package org.molgenis.compute.ui;

import java.io.IOException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.protocol.ComputeApplication;
import org.molgenis.util.Pbs;
import org.molgenis.util.PbsJob;
import org.molgenis.util.Tuple;

/**
 * This plugin takes care of the submission of one compute appliction to the
 * cluster and then to monitor its progress and finally to retrieve logs
 */
public class PbsSubmitApplication extends EasyPluginController<PbsSubmitApplicationModel>
{
	String host;
	String username;
	String password;
	PbsJob currentjob;
	Pbs pbs = null;

	public PbsSubmitApplication(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new PbsSubmitApplicationModel(this)); // the default model
		this.setView(new FreemarkerView("PbsSubmitApplicationView.ftl", getModel())); // <plugin
																						// flavor="freemarker"
	}

	public void refresh(Database db, Tuple request)
	{
		// nothing to do, because reload does all the work
	}

	/**
	 * Handle the submit
	 */
	public void submit(Database db, Tuple request)
	{
		// on submit want to submit the script to the Pbs cluster
		// cache host, username, password
		this.host = request.getString("host");
		this.username = request.getString("username");
		this.password = request.getString("password");

		// Create a Pbs and submit the script
		try
		{
			// get the ComputeApplication
			FormModel<ComputeApplication> parentForm = (FormModel<ComputeApplication>) this.getParent().getModel();
			ComputeApplication app = parentForm.getCurrent();

			// create the Job
			currentjob = new PbsJob(app.getComputeScript());
			currentjob.setQueue("short");
			currentjob.setName("app" + System.currentTimeMillis());

			if(pbs == null) pbs = new Pbs(host, username, password);

			pbs.submit(currentjob);

			app.setClusterHost(this.host);
			app.setJobID(currentjob.getId());
			db.update(app);

		}
		catch (Exception e)
		{
			// if things go wrong we setError so you get a nice red bar with the
			// message
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	@Override
	public String render()
	{
		MolgenisForm mf = new MolgenisForm(this);

		// add the inputs for user,password,hostname of the cluster
		// we use a 'DivPanel' so that we get two column layout
		DivPanel panel = new DivPanel("pbs_panel", null);
		panel.add(new StringInput("host", this.host));
		panel.add(new StringInput("username", this.username));
		panel.add(new StringInput("password", this.password));
		mf.add(panel);

		// submit button
		if (currentjob == null)
		{
			ActionInput submit = new ActionInput("submit");
			mf.add(submit);
		}
		else
		{
			ActionInput refresh = new ActionInput("refresh");
			mf.add(refresh);
		}
		// if already submitted, we expect here to see it running?
		if (currentjob != null)
		{
			mf.add(new TextParagraph("removeme", currentjob.toString()));
		}

		return mf.render();

	}

	@Override
	public void reload(Database db) throws Exception
	{
		FormModel<ComputeApplication> parentForm = (FormModel<ComputeApplication>) this.getParent().getModel();
		ComputeApplication app = parentForm.getCurrent();

		if (currentjob != null && currentjob.getState() != Pbs.State.COMPLETED)
		{
			if(pbs == null) pbs = new Pbs(this.host, this.username, this.password);
			currentjob.refresh(pbs);

			if (currentjob.getState().equals(Pbs.State.COMPLETED))
			{
				app.setErrorFile(currentjob.getError_log());
				app.setOutputFile(currentjob.getOutput_log());
				db.update(app);
				
				currentjob = null;
			}
		}
	}
}
