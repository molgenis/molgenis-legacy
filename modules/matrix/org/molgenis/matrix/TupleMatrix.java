package org.molgenis.matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Tuple;

/**
 * Matrix for a uniform set of tuples. 
 * Column names will be the tuple.getFields().
 * Row names will just be ids
 * @author mswertz
 *
 */
public class TupleMatrix extends MemoryMatrix<String,String,String>
{
	public TupleMatrix(List<Tuple> values) throws MatrixException
	{
		if(values.size() == 0) throw new MatrixException("values should have 1 or more tuples");
		
		//columns
		Tuple first = values.get(0);
		this.setColNames(first.getFields());
		
		//rows
		List<String> rownames = new ArrayList<String>();
		for(Tuple v: values) rownames.add(v.getString(first.getColName(0)));
		this.setRowNames(rownames);
		
		//values
		String[][] valueArray = this.create(values.size(), this.getColCount(), String.class);
		int row = 0;
		for(Tuple v: values)
		{
			for(int col = 0; col < v.size(); col++)
				valueArray[row][col] = v.getString(col);
			row++;
		}
	}
}
