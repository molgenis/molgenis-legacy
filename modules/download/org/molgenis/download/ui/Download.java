package org.molgenis.download.ui;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.services.SchedulingService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class Download extends EasyPluginController<DownloadModel>
{
	private static final long serialVersionUID = 1L;
	protected final String tempDir = System.getProperty("java.io.tmpdir");

	public Download(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new DownloadModel(this));
		this.setView(new FreemarkerView("Download.ftl", getModel()));
		this.getModel().setState(DownloadModel.INIT);
		this.getModel().setOutput("");
	}

	public void start(Database db, Tuple request)
	{
		try
		{
			this.getModel().setSchedulingService(new SchedulingService());
			
			//generate a unique download name
			this.getModel().setDownloadName("molgenis_" + UUID.randomUUID().toString());
			this.getModel().setDownloadPath(this.tempDir + File.separator + this.getModel().getDownloadName());

			if (StringUtils.isNotEmpty(request.getString("__download")))
				this.getModel().setKlazz(Class.forName(request.getString("__download")));

			this.getModel().setJobData(new HashMap<Object, Object>());
			
			//put auth information into hash for usage inside the job
			this.getModel().getJobData().put("__auth", this.getApplicationController().getLogin());
			
			//put download path into hash for usage inside the job
			this.getModel().getJobData().put("__path", this.getModel().getDownloadPath());

			//put request parameters into hash for usage inside the job
			for (int i = 0; i < request.getNrColumns(); i++)
				this.getModel().getJobData().put(request.getColName(i), request.getString(i));

			this.init(db, request);

			//schedule the job
			this.getModel().getSchedulingService().schedule(this.getModel().getJobData(), this.getModel().getKlazz());
			this.getModel().setState(DownloadModel.STARTED);

			this.check(db, request);
		}
		catch (Exception e)
		{
			this.getModel().getMessages().add(new ScreenMessage("FATAL: Could not schedule download.", true));
		}
	}

	protected void init(Database db, Tuple request)
	{
	}

	public void check(Database db, Tuple request)
	{
		try
		{
			File file = new File(this.getModel().getDownloadPath());
			if (file.exists() && file.length() > 0L)
			{
				this.getModel().setState(DownloadModel.FINISHED);
				this.getModel().getSchedulingService().shutdown();
			}
			
			HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
			HttpServletResponse httpResponse = rt.getResponse();

			switch (this.getModel().getState())
			{
			case DownloadModel.STARTED:
				httpResponse.setHeader("Refresh", "5; URL=molgenis.do?select=" + this.getName() + "&__target=Download&__action=check&__show=popup");
				this.getModel().setOutput(
						"<p>Your download is being processed...</p>\n" +
						"<img src=\"res/img/loading.gif\">");
				break;
			case DownloadModel.FINISHED:
				httpResponse.setHeader("Refresh", "0; URL=tmpfile/" + this.getModel().getDownloadName());
				this.getModel().setOutput(
						"Your download is processed and should appear in a few seconds.<br>" +
						"If this does not work click <a href=\"tmpfile/" +
						this.getModel().getDownloadName() +
						"\"><u>here</u></a>.");
				this.getModel().setState(DownloadModel.INIT);
				break;
			}
		}
		catch (Exception e)
		{
			this.getModel().getMessages().add(new ScreenMessage("ERROR: " + e.getMessage(), true));
		}
	}

	@Override
	public void reload(Database db) throws Exception
	{
		if (this.getModel().getState() == DownloadModel.INIT)
		{
			if (this.getModel().getKlazz() == null)
				this.getModel().getMessages().add(new ScreenMessage("Missing parameter __download", true));
		}
	}
}
