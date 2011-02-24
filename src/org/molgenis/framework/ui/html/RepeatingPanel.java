package org.molgenis.framework.ui.html;

import org.apache.commons.lang.StringEscapeUtils;
import org.molgenis.framework.ui.html.ActionInput.Type;

/**
 * Extension of TablePanel that comes with a [+] and [-] button so one can
 * (un)repeat its contents If used in a 'label'-'value' panel the the repeatable
 * elements will be nested into the value panel. Useful for subforms with
 * repeating information.
 * 
 * Features: <li>can clone itself for repeat <li>can remove clone <li>TODO set
 * option how many clones to show at start <li>TODO set option minimum / maximum
 * number of clones (default 0,unlimited respectively) <li>TODO set custom
 * labels to the 'add row' and 'remove row'
 */
public class RepeatingPanel extends TablePanel
{
	public RepeatingPanel(String name, String label)
	{
		super(name, label);
	}

	@Override
	public String toHtml()
	{
		// remove button for each row to remove the div shown above
		ActionInput removeButton = new ActionInput(this.getName() + "_remove", "Remove row", "Remove");
		removeButton.setJavaScriptAction("this.parentNode.parentNode.removeChild(this.parentNode); return false;");

		// repeating block
		String repeatableDiv = super.toHtml() + removeButton.toHtml();

		// add button to clone the div
		ActionInput addButton = new ActionInput(this.getName() + "_add", "Add row", "Add");
		addButton.setJavaScriptAction("var div = document.createElement('DIV'); this.parentNode.insertBefore(div,this); div.innerHTML = '"
						+ StringEscapeUtils.escapeJavaScript(StringEscapeUtils
								.escapeHtml(repeatableDiv)) + "'; return false");

		// create a div to contain the panel
		return "<div style=\"clear:both; display:block\">" + repeatableDiv + addButton.toHtml() + "</div>";
	}

}
