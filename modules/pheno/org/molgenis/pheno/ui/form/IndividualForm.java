package org.molgenis.pheno.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.Input;
import org.molgenis.framework.ui.html.TextLineInput;

public class IndividualForm extends Container
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -4335059390415978096L;

	public IndividualForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", ""));
//		
//		this.add(new ActionInput("add"));
//		((ActionInput) this.get("add")).setButtonValue("Add");
//		((ActionInput) this.get("add")).setLabel("Add");
//		((ActionInput) this.get("add")).setTooltip("Add");
//
//		this.add(new ActionInput("new"));
//		((ActionInput) this.get("new")).setButtonValue("New");
//		((ActionInput) this.get("new")).setLabel("New");
//		((ActionInput) this.get("new")).setTooltip("New");
//
//		this.add(new ActionInput("update"));
//		((ActionInput) this.get("update")).setButtonValue("Update");
//		((ActionInput) this.get("update")).setLabel("Update");
//		((ActionInput) this.get("update")).setTooltip("Update");
	}
	
	public void addTextLineInput(Integer observedValueId, String value)
	{
		TextLineInput<String> valueInput = new TextLineInput<String>(observedValueId.toString());
		valueInput.setValue(value);
		this.add(valueInput);
	}
	
	public Input<?> showInput(String name)
	{
		return this.get(name);
	}
}
