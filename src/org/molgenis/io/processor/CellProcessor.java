package org.molgenis.io.processor;

public interface CellProcessor
{
	public String process(String value);

	public boolean processHeader();

	public boolean processData();
}
