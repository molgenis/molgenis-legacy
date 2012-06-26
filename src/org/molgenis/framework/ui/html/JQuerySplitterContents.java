package org.molgenis.framework.ui.html;

public class JQuerySplitterContents
{
	private static String leftPane;
	private static String rightTopPane;
	private static String rightBottomPane;
	
	public void setLeftPane(String leftPane)
	{
		JQuerySplitterContents.leftPane = leftPane;
	}
	public String getLeftPane()
	{
		return leftPane;
	}
	public void setRightTopPane(String rightTopPane)
	{
		JQuerySplitterContents.rightTopPane = rightTopPane;
	}
	public String getRightTopPane()
	{
		return rightTopPane;
	}
	public void setRightBottomPane(String rightBottomPane)
	{
		JQuerySplitterContents.rightBottomPane = rightBottomPane;
	}
	public String getRightBottomPane()
	{
		return rightBottomPane;
	}
	
	
	

	
}
