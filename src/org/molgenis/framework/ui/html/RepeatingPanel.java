package org.molgenis.framework.ui.html;

import org.apache.commons.lang.StringEscapeUtils;
import org.molgenis.framework.ui.html.ActionInput.Type;

/**
 * This panel comes with a [+] and [-] button so one can (un)repeat the contents
 * If used in a 'label'-'value' panel the the repeatable elements will be nested into the value panel.
 * 
 * Features:
 * <li>can clone itself for repeat
 * <li>can remove clone
 * <li>TODO set option how many clones to show at start
 * <li>TODO set option minimum / maximum number of clones (default 0,unlimited respectively)
 * <li>TODO set custom labels to the 'add row' and 'remove row'
 */
public class RepeatingPanel extends TablePanel
{
	public RepeatingPanel(String name)
	{
		super(name);
	}
	
	public RepeatingPanel(String name, Object value)
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toHtml()
	{
		//remove button for each row to remove the div shown above
		ActionInput removeButton = new ActionInput(this.getName()+"_remove", Type.CUSTOM);
		removeButton.setJavaScriptAction("this.parentNode.parentNode.removeChild(this.parentNode); return false;");
		removeButton.setLabel("Remove row");
		
		//repeating block
		String repeatableDiv = "<div>"+super.toHtml()+removeButton.toHtml()+
		"</div>";
		
		//add button to clone the div
		ActionInput addButton = new ActionInput(this.getName()+"_add", Type.CUSTOM);
		addButton.setLabel("Add row");
		addButton.setJavaScriptAction("var div = document.createElement('DIV'); this.parentNode.insertBefore(div,this); div.innerHTML = '"+StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(repeatableDiv))+"'; return false");
		
		//create a div to contain the panel
		return repeatableDiv + addButton.toHtml();
	}

}
