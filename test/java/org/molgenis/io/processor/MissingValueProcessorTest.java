package org.molgenis.io.processor;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MissingValueProcessorTest
{
	@Test
	public void processNull()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", false).process(null), "unknown");
	}

	@Test
	public void processNull_processEmpty()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", true).process(null), "unknown");
	}

	@Test
	public void processEmpty()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", true).process(""), "unknown");
	}

	@Test
	public void processEmpty_processEmpty()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", false).process(""), "");
	}

	@Test
	public void process()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", true).process("value"), "value");
	}

	@Test
	public void process_processEmpty()
	{
		Assert.assertEquals(new MissingValueProcessor("unknown", true).process("value"), "value");
	}
}
