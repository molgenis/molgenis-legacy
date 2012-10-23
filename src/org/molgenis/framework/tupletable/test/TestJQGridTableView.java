package org.molgenis.framework.tupletable.test;

import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.MemoryTableFactory;
import org.molgenis.framework.tupletable.view.JQGridView;
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
