package org.molgenis.framework.ui.html;

public class JQuerySplitterContents
{
	private String leftPane;
	private String rightTopPane;
	private String rightBottomPane;
	private JQuerySplitterContents jqsplcon = new JQuerySplitterContents();

	public void setLeftPane(String leftPane)
	{
		jqsplcon.leftPane = leftPane;
	}

	public String getLeftPane()
	{
		return leftPane;
	}

	public void setRightTopPane(String rightTopPane)
	{
		jqsplcon.rightTopPane = rightTopPane;
	}

	public String getRightTopPane()
	{
		return rightTopPane;
	}

	public void setRightBottomPane(String rightBottomPane)
	{
		jqsplcon.rightBottomPane = rightBottomPane;
	}

	public String getRightBottomPane()
	{
		return jqsplcon.rightBottomPane;
	}

}
