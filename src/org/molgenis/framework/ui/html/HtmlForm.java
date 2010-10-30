package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class HtmlForm
{
	/** The entity to be shown in this form*/
	private Entity entity = null;
	/** Some columns can be hidden, using the name*/
	private List<String> hiddenColumns = new ArrayList<String>();
	/** A form may be readonly*/
	private boolean readonly = false;
	/** A form may be used to add a new record. This may result in readonly fields, e.g. for autoid.*/
	private boolean newRecord = false;
	/** Inputs */
	private List<HtmlInput> inputs = new ArrayList<HtmlInput>();
	
	public HtmlForm()
	{
		
	}
	
	public boolean isNewRecord()
	{
		return newRecord;
	}

	public void setNewRecord(boolean newRecord)
	{
		this.newRecord = newRecord;
	}

	public List<String> getHiddenColumns()
	{
		return hiddenColumns;
	}

	public void setHiddenColumns(List<String> hiddenColumns)
	{
		if(hiddenColumns != null)
			this.hiddenColumns = hiddenColumns;
	}

	public HtmlForm(Entity entity)
	{
		this.entity = entity;
	}
	
	/**Create the inputs to be shown*/
	public List<HtmlInput> getInputs()
	{
		return this.inputs;
	}
	
	public void setInputs(List<HtmlInput> inputs)
	{
		this.inputs = inputs;
	}

	public Entity getEntity()
	{
		return entity;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	public boolean isReadonly()
	{
		return readonly;
	}

	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}
	
	/**
	 * Set the value of a particular input by name.
	 * @param name
	 * @param value
	 * @throws ParseException
	 */
	public void set(String name, Object value) throws ParseException
	{
		Tuple t = new SimpleTuple();
		t.set(name,value);
		this.getEntity().set(t,false);
	}
	
	public String getEntityName()
	{
		return this.getEntity().getClass().getSimpleName();
	}
}
