package org.molgenis.download.ui;

import java.util.HashMap;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.services.SchedulingService;

public class DownloadModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;
	protected static final int INIT = 1;
	protected static final int STARTED = 2;
	protected static final int FINISHED = 3;
	private int state = INIT;
	private String downloadName;
	private String downloadPath;
	private SchedulingService schedulingService;
	protected HashMap<Object, Object> jobData;
	private Class<?> klazz;
	private String output;

	public DownloadModel(ScreenController<?> controller)
	{
		super(controller);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDownloadName() {
		return downloadName;
	}

	public void setDownloadName(String downloadName) {
		this.downloadName = downloadName;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public SchedulingService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(SchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public HashMap<Object, Object> getJobData() {
		return jobData;
	}

	public void setJobData(HashMap<Object, Object> jobData) {
		this.jobData = jobData;
	}

	public Class<?> getKlazz() {
		return klazz;
	}

	public void setKlazz(Class<?> klazz) {
		this.klazz = klazz;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	@Override
	public boolean isVisible()
	{
		return false;
	}
}
