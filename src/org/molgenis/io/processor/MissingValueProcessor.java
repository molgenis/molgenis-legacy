package org.molgenis.io.processor;

public class MissingValueProcessor extends AbstractCellProcessor
{
	private final String missingValue;
	private final boolean emptyIsMissing;

	public MissingValueProcessor(String missingValue, boolean emptyIsMissing)
	{
		super();
		this.missingValue = missingValue;
		this.emptyIsMissing = emptyIsMissing;
	}

	public MissingValueProcessor(boolean processHeader, boolean processData, String missingValue, boolean emptyIsMissing)
	{
		super(processHeader, processData);
		this.missingValue = missingValue;
		this.emptyIsMissing = emptyIsMissing;
	}

	@Override
	public String process(String value)
	{
		return value == null ? missingValue : (emptyIsMissing && value.isEmpty() ? missingValue : value);
	}
}
