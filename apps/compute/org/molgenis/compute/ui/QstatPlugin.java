package org.molgenis.compute.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.Pbs;
import org.molgenis.util.PbsJob;
import org.molgenis.util.Tuple;

/**
 * QstatPluginController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>QstatPluginModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>QstatPluginView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class QstatPlugin extends EasyPluginController<QstatPluginModel>
{
	String host;
	String username;
	String password;

	String ownerfilter;

	List<PbsJob> jobs = new ArrayList<PbsJob>();

	public QstatPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new QstatPluginModel(this)); // the default model
		this.setView(new FreemarkerView("QstatPluginView.ftl", getModel())); // <plugin
		// flavor="freemarker"
	}

	public void refresh(Database db, Tuple request)
	{
		// cache host, username, password
		this.host = request.getString("host");
		this.username = request.getString("username");
		this.password = request.getString("password");
		this.ownerfilter = request.getString("ownerfilter");

		// refresh
		try
		{
			jobs = new Pbs(host, username, password).getQstat();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	public String render()
	{
		// show inputs for host, username, password

//		MolgenisForm f = new MolgenisForm(this.getModel());
//
//		DivPanel panel = new DivPanel("pbs_panel", null);
//
//		// create inputs
//		panel.add(new StringInput("host", this.host));
//		panel.add(new StringInput("username", this.username));
//		panel.add(new StringInput("password", this.password));
//		//this should of course be delegated to the matrix viewer...
//		panel.add(new StringInput("ownerfilter", this.ownerfilter));
//
//		f.add(panel);
//
//		// create refresh button
//		f.add(new ActionInput("refresh"));
//
//		// show log, now using primitive matrix
//		try
//		{
//			List<String> rowIds = new ArrayList<String>();
//			for (PbsJob job : jobs)
//			{
//				if (this.ownerfilter != null
//						&& !job.getOwner().contains(this.ownerfilter))
//				{
//					continue;
//				}
//				else
//				{
//					rowIds.add(job.getId());
//				}
//			}
//			List<String> colIds = Arrays.asList(new String[]
//			{ "owner", "status", "walltime","queue","mem" });
			//StringMemoryMatrix matrix = new StringMemoryMatrix(rowIds, colIds);
//
//			for (PbsJob job : jobs)
//			{
//				if (this.ownerfilter != null
//						&& !job.getOwner().contains(this.ownerfilter))
//				{
//					continue;
//				}
//				else
//				{
//					matrix.setValue(job.getId(), "owner", job.getOwner());
//					matrix.setValue(job.getId(), "status", job.getState()
//							.toString());
//					matrix.setValue(job.getId(), "walltime", job.getWalltime());
//					matrix.setValue(job.getId(), "queue", job.getQueue());
//					matrix.setValue(job.getId(), "mem", job.getMem());
//				}
//			}
//
//			StringMatrixView v = new StringMatrixView("pbs_matrix", matrix);
//
//			f.add(v);
//
//		}
//		catch (MatrixException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		return f.render();
			return "TODO";
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// nothing to do
	}
}