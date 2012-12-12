package org.molgenis.io.processor;

public class LowerCaseProcessor extends AbstractCellProcessor
{
	public LowerCaseProcessor()
	{
		super();
	}

	public LowerCaseProcessor(boolean processHeader, boolean processData)
	{
		super(processHeader, processData);
	}

	@Override
	public String process(String value)
	{
		return value != null ? value.toLowerCase() : null;
	}
}
