package org.molgenis.matrix.browser;

import org.molgenis.data.Data;
import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.matrix.Matrix;
import org.molgenis.pheno.ObservationElement;

/**
 * Browser that takes a 'Data' object and creates a browser object for it.
 * 
 */
public class ObservedValueBrowser<R extends ObservationElement, C extends ObservationElement>
{
	//Data object that feeds this class
	Data data;
	//all rows in this matrix
	private DatabasePager<R> rowPager;
	//all columns in this matrix
	private DatabasePager<C> colPager;
	//matrix with all the values
	private Matrix matrix;
	
	public ObservedValueBrowser(Data data)
	{
		this.data = data;
		
		//rowPager = new DatabasePager();
	}
	
	public Matrix getValues()
	{
		return null;
	}
	
	public DatabasePager<R> getRowPager()
	{
		return rowPager;
	}
	
	public DatabasePager<C> getColPager()
	{
		return colPager;
	}
}
