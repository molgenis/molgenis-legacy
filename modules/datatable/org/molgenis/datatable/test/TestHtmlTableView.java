package org.molgenis.datatable.test;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.HtmlTableView;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestHtmlTableView
{
	@Test
	public void test2()
	{
		TupleTable table = MemoryTableFactory.create();
		
		HtmlTableView view = new HtmlTableView("test", table);

		String html = view.render();
		
		System.out.println("HtmlTableView generated: "+html);
		
		//how to test properly?
		Assert.assertEquals(html.contains("firstName"), true);
		Assert.assertEquals(html.contains("lastName"), true);
		Assert.assertEquals(html.contains("first1"), true);
	}
}
