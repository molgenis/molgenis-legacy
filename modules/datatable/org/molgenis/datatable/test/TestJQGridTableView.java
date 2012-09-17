package org.molgenis.datatable.test;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.ui.ApplicationController;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestJQGridTableView
{

	@Test
	public void testMemoryTableOutput() throws TableException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		TupleTable table = MemoryTableFactory.create();
		
		JQGridView view = new JQGridView("test", null, table);

		String html = view.render();
		
		System.out.println("HtmlTableView generated: "+html);
		
		Assert.assertEquals(html.contains("firstName"), true);
		Assert.assertEquals(html.contains("lastName"), true);
		
		//should have selenium test to properly verify!
	}

}
