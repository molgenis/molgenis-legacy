package org.molgenis.util;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.testng.annotations.Test;

public class AbstractTupleReaderTest
{
	@Test
	public void testRenameField_single() throws Exception
	{
		AbstractTupleReader reader = mock(AbstractTupleReader.class);
		when(reader.colnames()).thenReturn(Arrays.asList("a", "b", "c"));
		doCallRealMethod().when(reader).renameField(anyString(), anyString());

		reader.renameField("a", "d");

		verify(reader).setColnames(Arrays.asList("d", "b", "c"));
	}

	@Test
	public void testRenameField_multiple() throws Exception
	{
		AbstractTupleReader reader = mock(AbstractTupleReader.class);
		when(reader.colnames()).thenReturn(Arrays.asList("a", "c", "c"));
		doCallRealMethod().when(reader).renameField(anyString(), anyString());

		reader.renameField("c", "d");

		verify(reader).setColnames(Arrays.asList("a", "d", "d")); // column names may have duplicates
	}
}
