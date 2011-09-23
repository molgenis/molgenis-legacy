package org.molgenis.matrix.component.test;

import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.molgenis.matrix.component.legacy.MatrixRenderer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMatrixComponent
{
	
	MatrixRenderer<SomeRowType, SomeColType, SomeValueType> renderer;
	MatrixRendererHelper<SomeRowType, SomeColType, SomeValueType> helper;
	
	/*
	 
	Reference: original complete matrix (without col/row attributes)
	 
			MYB28	HOX3	CCR1	SRF2	AOP2	COL7A1	RAS
	Laura	56		112		168		224		280		336		392
	Karl	57		114		171		228		285		342		399
	Bill	58		116		174		232		290		348		406
	Lee		59		118		177		236		295		354		413
	Kara	60		120		180		240		300		360		420
	Gaius	61		122		183		244		305		366		427
	Saul	62		124		186		248		310		372		434
	Sharon	63		126		189		252		315		378		441
	 
	 */

	@BeforeClass
	public void setup() throws Exception
	{
		TestImpl t = new TestImpl();
		renderer = new MatrixRenderer<SomeRowType, SomeColType, SomeValueType>(
				"testName", t, t, "noScreenName");
		helper = new MatrixRendererHelper<SomeRowType, SomeColType, SomeValueType>();
	}
	
	@Test
	public void creation() throws Exception
	{
		Assert.assertEquals(renderer.getRendered().getValues()[0][0].getValue().intValue(), 56);
		Assert.assertEquals(renderer.getRendered().getValues()[1][1].getValue().intValue(), 114);

		
		//etc
		
		//Assert.assertEquals(helper.colHeaderToStringForTest(renderer.getRendered()), "MYB28 HOX3 CCR1 SRF2 AOP2 ");
		//Assert.assertEquals(helper.rowHeaderToStringForTest(renderer.getRendered()), "Laura Karl Bill Lee Kara Gaius Saul Sharon ");	
		
		//remove filters and print original
		//renderer.removeFilter(0);
		//renderer.removeFilter(0);
		//renderer.filterAndRender();
		//System.out.println(helper.matrixToString(renderer.getRendered()));
		
//		System.out.println("column headers: " + helper.colHeaderToStringForTest(renderer.getRendered()));
//		System.out.println("row headers: " + helper.rowHeaderToStringForTest(renderer.getRendered()));
//		System.out.println("values: " + helper.valuesToStringForTest(renderer.getRendered()));
	}
	
	@Test(dependsOnMethods="creation")
	public void paging1() throws Exception
	{
		renderer.updatePaging(1, 1);
		renderer.filterAndRender();
		
	//	System.out.println(renderer.getRendered().toString());
		
		Assert.assertEquals(renderer.getRendered().getRowHeaders().size(), 1);
		Assert.assertEquals(renderer.getRendered().getColHeaders().size(), 1);
		Assert.assertEquals(renderer.getRendered().getValues().length, 1);
		Assert.assertEquals(renderer.getRendered().getValues()[0].length, 1);
		Assert.assertEquals(renderer.getRendered().getValues()[0][0].getValue().intValue(), 56);
		
		//etc
	}
	
	@Test(dependsOnMethods="paging1")
	public void paging2() throws Exception
	{
			
		renderer.updatePaging(1, 1, 2);
		renderer.filterAndRender();
		renderer.moveRight();
		renderer.moveDown();
		renderer.filterAndRender();
			
		Assert.assertEquals(renderer.getRendered().getValues()[0][0].getValue().intValue(), 174);
	
		//etc
	}
	
	/**
	 * brainstormy...
	 * 
	 * Questions:
	 * 
	 * Behaviour of filters: need clear definition for some cases:
	 * 
	 * Should filters always be 'rollbacked' when the apply fails? or keep bad filters and let people sort it out?
	 * 		or evaluate filters until exception, and report status back in gui?
	 * Can people drag the paging filters 'through' the other filters? Ie. rowlimit -> colvalue -> collimit
	 * 		instead of always moving paging filters to the back of the stack
	 * Can people filter using an already excluded subset? Ie. remove a row with a rowheader filter, but
	 * 		use the values of that row still to slice the remaining columns
	 * 
	 * 
	 * Tests needed:
	 * creation
	 * paging per-step
	 * paging per-step out of range
	 * paging to top / bottom
	 * paging to far left/right
	 * paging with large stepsize (out of range) vs big/small window
	 * paging with small stepsize vs big/small window
	 * ....
	 * 
	 * index filtering in various combinations eg.
	 * just 1 dimension, 1 filter
	 * 
	 * 2+ filters, behaviour tests..
	 * 
	 * 2+ filters interacting as inclusion ( > i, <  i+x )
	 * 2+ filters interacting as exclusion ( < i, >  i+x )
	 * ....
	 * 
	 * two dimensional filters:
	 * inclusion/exclusion scenarios such as..
	 * > i, <  i+x & > n, < n+x
	 * > i, <  i+x & < n, > n+x
	 * < i, >  i+x & > n, < n+x
	 * < i, >  i+x & < n, > n+x
	 * ....
	 * 
	 * column value filters
	 * ....
	 * 
	 * row value filters
	 * ....
	 * 
	 * header value filters
	 * ....
	 * 
	 * INTEGRATION:
	 * 
	 * paging on index filter results!
	 * (paging on) index filters + value filters
	 * (paging on) index filters + value filters + header filters
	 * 
	 * etc :)
	 * 
	 */

}
