//package org.molgenis.compute.ui;
//
//import java.io.IOException;
//
//import org.molgenis.compute.host.AbstractComputeHost;
//import org.molgenis.compute.host.Job;
//import org.molgenis.compute.host.Pbs;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.ui.EasyPluginController;
//import org.molgenis.framework.ui.FormModel;
//import org.molgenis.framework.ui.FreemarkerView;
//import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.ScreenView;
//import org.molgenis.framework.ui.html.ActionInput;
//import org.molgenis.framework.ui.html.DivPanel;
//import org.molgenis.framework.ui.html.MolgenisForm;
//import org.molgenis.framework.ui.html.Paragraph;
//import org.molgenis.framework.ui.html.StringInput;
//import org.molgenis.framework.ui.html.XrefInput;
//import org.molgenis.util.Tuple;
//
///**
// * This plugin takes care of the submission of one compute appliction to the
// * cluster and then to monitor its progress and finally to retrieve logs
// */
//public class PbsSubmitApplication extends
//		EasyPluginController<PbsSubmitApplicationModel>
//{
//	ComputeResource resource;
//	String username;
//	String password;
//	Job currentjob;
//	AbstractComputeHost pbs = null;
//
//	public PbsSubmitApplication(String name, ScreenController<?> parent)
//	{
//		super(name, parent);
//		this.setModel(new PbsSubmitApplicationModel(this)); // the default model
//	}
//
//	public ScreenView getView()
//	{
//		return new FreemarkerView("PbsSubmitApplicationView.ftl",
//				getModel());
//	}
//	
//	public void refresh(Database db, Tuple request)
//	{
//		// nothing to do, because reload does all the work
//	}
//
//	public void remove(Database db, Tuple request) {
//		// we want to kill current job
//		try
//		{
//			pbs.remove(currentjob);
//			this.currentjob = null;
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			this.setError(e.getMessage());
//		}
//	}
//	
//	/**
//	 * Handle the submit
//	 */
//	public void submit(Database db, Tuple request)
//	{
//		try
//		{
//			// on submit want to submit the script to the Pbs cluster
//			// cache host, username, password
//			this.resource = db.findById(ComputeResource.class, request
//					.getString("resource"));
//			this.username = request.getString("username");
//			this.password = request.getString("password");
//
//			// Create a Pbs and submit the script
//			// get the ComputeApplication
//			FormModel<ComputeJob> parentForm = (FormModel<ComputeJob>) this.getParent().getModel();
//			ComputeJob app = parentForm.getCurrent();
//
//			// create the Job
//			currentjob = new Job(app.getComputeScript());
//			currentjob.setQueue(app.getQueue());
//			currentjob.setName("app" + System.currentTimeMillis());
//
//			if (pbs == null) pbs = new Pbs(resource.getName(), username, password);
//
//			pbs.submit(currentjob);
//			
//			app.setJobID(currentjob.getId());
//			db.update(app);
//
//		}
//		catch (Exception e)
//		{
//			// if things go wrong we setError so you get a nice red bar with the
//			// message
//			e.printStackTrace();
//			this.setError(e.getMessage());
//		}
//	}
//
//	@Override
//	public String render()
//	{
//		MolgenisForm mf = new MolgenisForm(this);
//
//		// add the inputs for user,password,hostname of the cluster
//		// we use a 'DivPanel' so that we get two column layout
//		DivPanel panel = new DivPanel("pbs_panel", null);
//		panel.add(new XrefInput("resource", ComputeResource.class, this.resource));
//		panel.add(new StringInput("username", this.username));
//		panel.add(new StringInput("password", this.password));
//		mf.add(panel);
//
//		// submit button
//		if (currentjob == null)
//		{
//			ActionInput submit = new ActionInput("submit");
//			mf.add(submit);
//		}
//		else
//		{
//			ActionInput refresh = new ActionInput("refresh");
//			mf.add(refresh);
//			
//			ActionInput remove = new ActionInput("remove");
//			mf.add(remove);
//		}
//		// if already submitted, we expect here to see it running?
//		if (currentjob != null)
//		{
//			mf.add(new Paragraph("removeme", currentjob.toString()));
//		}
//
//		return mf.render();
//
//	}
//
//	@Override
//	public void reload(Database db) throws Exception
//	{
//		FormModel<ComputeJob> parentForm = (FormModel<ComputeJob>) this
//				.getParent().getModel();
//		ComputeJob app = parentForm.getCurrent();
//
//		if (currentjob != null && currentjob.getState() != AbstractComputeHost.JobState.COMPLETED)
//		{
//			if (pbs == null) pbs = new Pbs(this.resource.getName(), this.username,
//					this.password);
//			pbs.refresh(currentjob);
//
//			if (currentjob.getState().equals(AbstractComputeHost.JobState.COMPLETED))
//			{
//				app.setErrorFile(currentjob.getError_log());
//				app.setOutputFile(currentjob.getOutput_log());
//				db.update(app);
//
//				currentjob = null;
//			}
//		}
//	}
//}
