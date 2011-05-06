package org.molgenis.framework.ui;

public class ApplicationModel extends SimpleScreenModel
{
	private static final long serialVersionUID = 1L;

	/** The version used to generate this MOLGENIS */
	private String version;
	/** Show, if whole app or only target should be shown */
	private String show = "root";
	/** Target, for dialogs */
	private ScreenModel target = null;

	public ApplicationModel(ScreenController<?> controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getShow()
	{
		return show;
	}

	public void setShow(String show)
	{
		this.show = show;
	}

	public String getCustomHtmlHeaders()
	{
		return this.getController().getCustomHtmlHeaders();
	}

	public String getCustomHtmlBodyOnLoad()
	{
		return this.getController().getCustomHtmlBodyOnLoad();
	}

	public void setTarget(ScreenController<?> target)
	{
		this.target = target.getModel();
	}

	public ScreenModel getTarget()
	{
		return this.target;
	}
}
