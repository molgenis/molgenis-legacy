package org.molgenis.matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Matrix for a uniform set of entities. 
 * Column names will be the tuple.getFields().
 * Row names will just be ids
 * @author mswertz
 *
 */
public class EntityMatrix extends MemoryMatrix<String,String,Object>
{
	public EntityMatrix(List<? extends Entity> values) throws MatrixException
	{
		if(values.size() == 0) throw new MatrixException("values should have 1 or more tuples");
		
		//columns
		Entity first = values.get(0);
		this.setColNames(first.getFields());
		
		//rows
		List<String> rownames = new ArrayList<String>();
		for(Entity v: values) rownames.add(v.get(first.getFields().get(0)).toString());
		this.setRowNames(rownames);
		
		//values
		Object[][] valueArray = this.create(values.size(), this.getColCount(), Object.class);
		int row = 0;
		for(Entity v: values)
		{
			for(int col = 0; col < this.getColCount(); col++)
				valueArray[row][col] = v.get(this.getColNames().get(col));
			row++;
		}
	}
}
