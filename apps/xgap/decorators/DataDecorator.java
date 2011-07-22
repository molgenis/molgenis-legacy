/* Date:        February 15, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import matrix.general.DataMatrixHandler;

import org.molgenis.core.MolgenisFile;
import org.molgenis.data.Data;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;

public class DataDecorator<E extends org.molgenis.data.Data> extends MappingDecorator<E>
{
	
	protected boolean strict = false;
	
	// JDBCMapper is the generate thing
	//TODO: Danny Parameterize the JDBCMapper object <Object> ??
	public DataDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
	}
	
	//new kind of constructor to work with latest DB changes
	public DataDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		System.out.println("** DataDecorator - ADD");
		// default check
		if(strict){
			System.out.println("** DataDecorator - strict");
			NameConvention.validateEntityNamesStrict(entities);
		}else{
			System.out.println("** DataDecorator - loose");
			NameConvention.validateEntityNames(entities);
		}

		int count = super.add(entities);
		return count;
	}
	
	/**
	 * Update the MolgenisFile with the name of the Data object so filenames
	 * stay the same as the matrix names.
	 */
	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		System.out.println("** DataDecorator - UPDATE");
		// default check
		if(strict){
			System.out.println("** DataDecorator - strict");
			NameConvention.validateEntityNamesStrict(entities);
		}else{
			System.out.println("** DataDecorator - loose");
			NameConvention.validateEntityNames(entities);
		}

		// get datamatrixhandler and make molgenisfile list
		DataMatrixHandler dmh = new DataMatrixHandler(this.getDatabase());
		List<MolgenisFile> mfList = new ArrayList<MolgenisFile>();

		// try to get the files for the matrices and change their names
		for (Data dm : entities)
		{
			MolgenisFile mf = null;
			try{
				mf = dmh.findMolgenisFile(dm);
			}catch(NullPointerException npe){
				//backend not a file
			}
			if (mf != null)
			{
				mf.setName(dm.getName());
				mfList.add(mf);
			}
		}

		// try to update the (molgenis)files, decorated to also change the
		// filenames
		try
		{
			this.getDatabase().update(mfList);
		}
		catch (IOException e)
		{
			throw new DatabaseException(e);
		}

		// if it works, continue to update the Data entities
		int count = super.update(entities);
		return count;
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		System.out.println("** DataDecorator - REMOVE");
		if(entities.size() > 1){
			throw new DatabaseException("You can only remove one 'Data' at a time");
		}
		
		//try to find a MolgenisFile for this Data
		Data dm = entities.get(0);
		DataMatrixHandler dmh = new DataMatrixHandler(this.getDatabase());
		MolgenisFile mf = null;
		try{
			mf = dmh.findMolgenisFile(dm);
		}catch(NullPointerException npe){
			//backend not a file
		}
		
		//if there is one, throw error and do not remove Data
		if (mf != null)
		{
			throw new DatabaseException("This 'Data' still has a file source associated with it. Remove this file first.");
		}
		
		int count = super.remove(entities);
		return count;
	}

}
