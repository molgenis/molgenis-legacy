package org.molgenis.compute.ui;

import org.molgenis.compute.host.ComputeHost;
import org.molgenis.compute.host.Glite;
import org.molgenis.compute.host.Job;
import org.molgenis.compute.host.Pbs;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.*;
import org.molgenis.util.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This view enables:
 * <ul>
 * <li>To define a new script
 * <li>To submit it to either PBS or Grid
 * <li>List currently running jobs that have been submitted from this app
 * </ul>
 */
public class JobHostTester extends EasyPluginController<JobHostTester>
{
	List<Job> jobs = new ArrayList<Job>();
	Map<String, ComputeHost> backends = new LinkedHashMap<String, ComputeHost>();

	public JobHostTester(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this);
	}

	public ScreenView getView()
	{
		MolgenisForm form = new MolgenisForm(this);

		VerticalLayout view = new VerticalLayout();
		form.add(view);

		// ///////
		view.add(new Paragraph("<h1>Define hosts:</h1>"));

		SelectInput sel = new SelectInput("type", "pbs");
		sel.setNillable(false);
		sel.addOption("pbs", "pbs");
		sel.addOption("grid", "grid");
		view.add(sel);

		view.add(new StringInput("hostname", ""));
		view.add(new StringInput("username", ""));
		view.add(new StringInput("password", ""));
		view.add(new StringInput("workingDir", ""));

		view.add(new ActionInput("addHost", "Add host"));

		view.add(new Paragraph("Current hosts:"));
		for (String host : backends.keySet())
		{
			view.add(new Paragraph(host));
		}

		// ///////
		view.add(new Paragraph("<h1>Define and start a new job below</h1>"));

		view.add(new TextInput("script"));

		SelectInput hostSel = new SelectInput("backend", "");
		hostSel.setNillable(false);
		for (String name : backends.keySet())
			hostSel.addOption(name, name);
		view.add(hostSel);
		
		view.add(new ActionInput("submitJob","Submit Job"));
		view.add(new ActionInput("submitPilot", "Submit Pilot"));

		view.add(new Paragraph("<h1>Currently running jobs</h1>"));

		for (Job j : jobs)
		{
			view.add(new Paragraph(j.getName() + ":" + j.getState() + ", host=" + j.getHost() + ", output="
					+ j.getOutput_log()));
		}
		view.add(new ActionInput("refreshJobs", "Refresh job statuses"));

		return form;
	}

	public void refreshJobs(Database db, Tuple request) throws Exception
	{
		for (Job job : jobs)
		{
			ComputeHost h = backends.get(job.getHost());
			h.refresh(job);
		}
	}

	public void addHost(Database db, Tuple request) throws Exception
	{
		String hostname = request.getString("hostname");
		String username = request.getString("username");
		String password = request.getString("password");
		String workingDir = request.getString("workingDir");

		ComputeHost m = null;
		if ("pbs".equals(request.getString("type")))
		{
			m = new Pbs(hostname, username, password);
		}
		else
		{
			m = new Glite(hostname, username, password);
		}
		m.setWorkingDir(workingDir);

		backends.put(username + "@" + hostname + ":" + workingDir, m);
	}

	public void submitJob(Database db, Tuple request) throws IOException
	{
		Job j = new Job();
		j.setScript(request.getString("script"));

		// get suitable backend
		String host = request.getString("backend");
		backends.get(host).submit(j);

		// remember ...
		j.setHost(host);
		jobs.add(j);
	}

    public void submitPilot(Database db, Tuple request) throws IOException
    	{
    		Job j = new Job();
    		j.setScript(request.getString("script"));

    		//get suitable backend
    		String host = request.getString("backend");
    		backends.get(host).submitPilot(j);

    		//remember ...
    		j.setHost(host);
    		jobs.add(j);
    	}


	@Override
	public void reload(Database db) throws Exception
	{
		// //example: update model with data from the database
		// Query q = db.query(Person.class);
		// q.like("name", "john");
		// getModel().investigations = q.find();
	}
}
