package org.molgenis.matrix.component.test;

import org.molgenis.matrix.component.MatrixRenderer;
import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMatrixComponent
{
	
	MatrixRenderer<SomeRowType, SomeColType, SomeValueType> renderer;
	MatrixRendererHelper<SomeRowType, SomeColType, SomeValueType> helper;

	@BeforeClass
	public void setup() throws Exception
	{
		TestImpl t = new TestImpl();
		renderer = new MatrixRenderer<SomeRowType, SomeColType, SomeValueType>(
				"testName", t, t, "noScreenName");
		helper = new MatrixRendererHelper<SomeRowType, SomeColType, SomeValueType>();
	}
	
	@Test
	public void a() throws Exception
	{
		Assert.assertEquals(56, renderer.getRendered().getVisibleValues()[0][0].getValue().intValue());
		Assert.assertEquals(114, renderer.getRendered().getVisibleValues()[1][1].getValue().intValue());

		Assert.assertEquals(helper.colHeaderToStringForTest(renderer.getRendered()), "MYB28 HOX3 CCR1 SRF2 AOP2 ");
		Assert.assertEquals(helper.rowHeaderToStringForTest(renderer.getRendered()), "Laura Karl Bill Lee Kara Gaius Saul Sharon ");	
		
//		System.out.println("column headers: " + helper.colHeaderToStringForTest(renderer.getRendered()));
//		System.out.println("row headers: " + helper.rowHeaderToStringForTest(renderer.getRendered()));
//		System.out.println("values: " + helper.valuesToStringForTest(renderer.getRendered()));
	}
	
	@Test
	public void b() throws Exception
	{
		renderer.updatePaging(1, 1);
		renderer.filterAndRender();
		
	//	System.out.println(renderer.getRendered().toString());
		
//		Assert.assertEquals(1, renderer.getRendered().getVisibleRows().size());
//		Assert.assertEquals(1, renderer.getRendered().getVisibleCols().size());
//		Assert.assertEquals(1, renderer.getRendered().getVisibleValues().length);
//		Assert.assertEquals(1, renderer.getRendered().getVisibleValues()[0].length);
//		Assert.assertEquals(10, renderer.getRendered().getVisibleValues()[0][0].getValue().intValue());
		
	}
	
	@Test
	public void c() throws Exception
	{
		renderer.updatePaging(1, 1, 2);
		renderer.moveRight();
		renderer.moveDown();
		
//		System.out.println(renderer.getRendered().toString());
		
//		Assert.assertEquals(14, renderer.getRendered().getVisibleValues()[0][0].getValue().intValue());
	}

}
