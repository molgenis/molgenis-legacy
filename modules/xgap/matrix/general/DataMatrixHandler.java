package matrix.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.csv.CSVDataMatrixInstance;
import matrix.implementations.database.DatabaseDataMatrixInstance;

import org.molgenis.core.MolgenisFile;
import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.util.Entity;
import org.molgenis.util.ValueLabel;

import app.DatabaseFactory;
import decorators.MolgenisFileHandler;
import decorators.NameConvention;

/**
 * Class to handle the coupling of 'Data' objects and storage of the actual data
 * values
 * 
 * @author joerivandervelde
 * 
 */
public class DataMatrixHandler extends MolgenisFileHandler
{

	public DataMatrixHandler(Database db)
	{
		super(db);
	}

	/**
	 * Create a DataMatrix instance from this 'DataMatrix' object, regardless of
	 * its source.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public DataMatrixInstance createInstance(Data data, Database db) throws Exception
	{
		DataMatrixInstance instance = null;
		File source = null;

		if (data.getStorage().equals("Database"))
		{
			instance = new DatabaseDataMatrixInstance(db, data);
		}
		else
		{
			source = findSourceFile(data, db);
			if (source == null)
			{
				throw new FileNotFoundException("No MolgenisFile found referring to DataMatrix '" + data.getName()
						+ "'");
			}
			if (data.getStorage().equals("Binary"))
			{
				instance = new BinaryDataMatrixInstance(source);
			}
			if (data.getStorage().equals("CSV"))
			{
				instance = new CSVDataMatrixInstance(data, source);
			}
		}
		return instance;
	}

	/**
	 * Wrapper/helper function to attempt to remove a datamatrix. This means
	 * removing both the source and the 'Data' definition. WARNING: Removing
	 * 'Data' fails if other entities are still referring to this 'Data', but
	 * the source is already deleted by then. Only use if you are sure this
	 * 'Data' can be removed entirely. TODO: Somehow improve this behaviour.
	 * Make sure there are no other XREF's when considering datasource links,
	 * then remove source, then remove 'Data'.
	 * 
	 * @param dm
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public void deleteDataMatrix(Data dm, Database db) throws Exception
	{
		try
		{
			deleteDataMatrixSource(dm, db);
		}
		catch (FileNotFoundException fnfe)
		{
			// no source found, continue to delete 'Data'
		}
		db.remove(dm);
	}

	/**
	 * Delete the source for this DataMatrix. Can be 'Database' or any kind of
	 * MolgenisFile subclass. (ie. 'Binary', 'CSV')
	 * 
	 * @param dm
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public void deleteDataMatrixSource(Data dm, Database db) throws Exception
	{
		String verifiedSource = findSource(dm, db);

		if (verifiedSource.equals("null"))
		{
			throw new FileNotFoundException("No source to delete, appears to be null.");
		}
		else
		{
			if (verifiedSource.equals("Database"))
			{
				if (dm.getValueType().equals("Decimal"))
				{
					List<DecimalDataElement> dde = db.find(DecimalDataElement.class,
							new QueryRule("data_id", Operator.EQUALS, dm.getId()));
					db.remove(dde);
				}
				else
				{
					List<TextDataElement> tde = db.find(TextDataElement.class,
							new QueryRule("data_id", Operator.EQUALS, dm.getId()));
					db.remove(tde);
				}
			}
			else
			{
				db.remove(findMolgenisFile(dm, db));
			}
		}
	}

	/**
	 * Simple check to find out if this 'DataMatrix' has any kind of data source
	 * attached. (database, file, etc)
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public boolean hasSource(Data data, Database db) throws Exception
	{
		if (data.getStorage().equals("Database"))
		{
			if (this.isDataMatrixStoredInDatabase(data, db))
			{
				return true;
			}
		}
		else
		{
			if (this.findSourceFile(data, db) != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Enquire if this 'DataMatrix' is stored in a certain way. For example,
	 * 'Binary'.
	 * 
	 * @param data
	 * @param source
	 * @return
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public boolean isDataStoredIn(Data data, String source, Database db) throws Exception
	{
		ArrayList<String> options = new ArrayList<String>();
		for (ValueLabel option : data.getStorageOptions())
		{
			options.add(option.getValue().toString());
		}

		if (!options.contains(source))
		{
			throw new DatabaseException("Invalid source type: " + source);
		}

		if (source.equals("Database"))
		{
			return isDataMatrixStoredInDatabase(data, db);
		}
		else
		{
			String matrixSource = source + "DataMatrix";
			// FIXME: pull out?
			List<? extends Entity> test = db.find(db.getClassForName(matrixSource));
			for (Entity e : test)
			{
				// used to be: if ((e.get("data_name").toString()).equals(data.getName()))
				if ((db instanceof JDBCDatabase && new Integer(e.get("data_id").toString()).intValue() == data.getId().intValue()) ||
				(db instanceof JpaDatabase && ((Data) e.get("data")).getId().intValue() == data.getId().intValue()))
				{
					try
					{
						this.getFile(e.get("name").toString(), db);
						return true;
					}
					catch (FileNotFoundException fnf)
					{
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Helper-like function to find out if a 'DataMatrix' has values stored
	 * inside the database
	 * 
	 * @param data
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public boolean isDataMatrixStoredInDatabase(Data data, Database db) throws DatabaseException, ParseException
	{
		Query<DecimalDataElement> dde = db.query(DecimalDataElement.class);
		dde.limit(1);
		dde.equals("data_id", data.getId());
		Query<TextDataElement> tde = db.query(TextDataElement.class);
		tde.limit(1);
		tde.equals("data_id", data.getId());
		boolean hasDataElements = (dde.find().size() > 0 || tde.find().size() > 0) ? true : false;
		return hasDataElements;
	}

	/**
	 * Find a 'DataMatrix' object that possibly belongs as a field in this
	 * subtype of MolgenisFile
	 * 
	 * @param mf
	 * @return
	 * @throws DatabaseException
	 */
	public Data findData(MolgenisFile mf, Database db) throws DatabaseException
	{

		QueryRule mfName = new QueryRule("name", Operator.EQUALS, mf.getName());
		List<? extends Entity> mfToEntity = db.find(db.getClassForName(mf.get__Type()), mfName);

		if (mfToEntity.size() == 0)
		{
			throw new DatabaseException("No entities found for subclass '" + mf.get__Type() + "' and name '"
					+ mf.getName() + "'");
		}
		else if (mfToEntity.size() > 1)
		{
			throw new DatabaseException("SEVERE: Multiple entities found for subclass '" + mf.get__Type()
					+ "' and name '" + mf.getName() + "'");
		}
		String dataMatrixName = mfToEntity.get(0).get("dataMatrix_name").toString();
		QueryRule dataName = new QueryRule("name", Operator.EQUALS, dataMatrixName);
		List<Data> dataList = db.find(Data.class, dataName);

		if (dataList.size() == 0)
		{
			throw new DatabaseException("No matrix found for name '" + dataName + "'");
		}
		else if (dataList.size() > 1)
		{
			throw new DatabaseException("SEVERE: Multiple matrices found for name '" + dataName + "'");
		}
		else
		{
			return dataList.get(0);
		}

	}

	/**
	 * Find a 'MolgenisFile' object that possibly belongs to DataMatrix.
	 * 
	 * @param dm
	 * @return
	 * @throws DatabaseException
	 */
	public MolgenisFile findMolgenisFile(Data dm, Database db) throws DatabaseException
	{
		String matrixSource = dm.getStorage() + "DataMatrix";
		List<? extends Entity> test = db.find(db.getClassForName(matrixSource));
		for (Entity e : test)
		{
			if (Integer.valueOf(e.get("data_id").toString()).intValue() == dm.getId().intValue())
			{
				QueryRule mfId = new QueryRule("id", Operator.EQUALS, e.get(e.getIdField()));
				return db.find(MolgenisFile.class, mfId).get(0);
				// alternative: by name
				// db.find(mfClass, new QueryRule(MolgenisFile.NAME, Operator.EQUALS, NameConvention.escapeFileName(data.getName())));

			}
		}
		return null;
	}

	/**
	 * Find the source file that belongs to this 'DataMatrix'. Returns null if
	 * not found or not applicable.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public File findSourceFile(Data data, Database db) throws Exception
	{
		List<? extends Entity> mfSubclasses = db.find(
				db.getClassForName(data.getStorage() + "DataMatrix"));
		for (Entity e : mfSubclasses)
		{
			// used to be: if ((e.get("data_name").toString()).equals(data.getName()))
			if ((db instanceof JDBCDatabase && new Integer(e.get("data_id").toString()).intValue() == data.getId().intValue()) ||
			(db instanceof JpaDatabase && ((Data) e.get("data_id")).getId().intValue() == data.getId().intValue()))
			{
				return this.getFile(e.get("name").toString(), db);
			}

		}
		return null;
	}

	/**
	 * Iterate through all possible sources and return the source option (ie.
	 * 'Database', 'CSV') if there is a confirmed backend for this option.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @throws XGAPStorageException
	 */
	public String findSource(Data data, Database db) throws Exception
	{
		ArrayList<String> options = new ArrayList<String>();
		for (ValueLabel option : data.getStorageOptions())
		{
			options.add(option.getValue().toString());
		}

		for (String option : options)
		{
			if (isDataStoredIn(data, option, db))
			{
				return option;
			}
		}
		return "null";
	}

	/**
	 * Example usage
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Database db = DatabaseFactory.create("gcc.properties");
		DataMatrixHandler mh = new DataMatrixHandler(db);
		Data dm = db.find(Data.class).get(0);
		mh.isDataStoredIn(dm, "Binary", db);
	}

	/**
	 * Attempt to link this 'Data' object to a backend file that might be there,
	 * but is not properly attached via MolgenisFile.
	 * @param data
	 * @param storage
	 * @param db
	 * @throws Exception 
	 */
	public boolean attemptStorageRelink(Data data, String storage, Database db) throws Exception
	{
		if(storage.equals("Database"))
		{
			return false;
		}
		
		boolean relinked = false;
		boolean mfPresent = false;
		
		//found out if there already is a MolgenisFile for this 'Data'
		if(findMolgenisFile(data, db) != null)
		{
			mfPresent = true;
		}
		
		//find out if there is a file that can be coupled using a new MolgenisFile object
		boolean filePresent;
		String type = data.getStorage() + "DataMatrix";
		try
		{
			this.getFileDirectly(NameConvention.escapeFileName(data.getName()), getExtension(data.getStorage()), type, db);
			filePresent = true;
		}
		catch(FileNotFoundException fnfe)
		{
			filePresent = false;
		}
		
		// find out if there is a MolgenisFile for the escaped file name already, else adding of a Data record
		// with a name that is allowed at first still fails due to relinking, e.g. an immediate error after adding:
		// File name 'metaboliteExpression' already exists in database when escaped to filesafe format. ('metaboliteexpression')
		boolean escapedMolgenisFileNameExists = false;
		if(db.find(db.getClassForName(type), new QueryRule(MolgenisFile.NAME, Operator.EQUALS, NameConvention.escapeFileName(data.getName()))).size() > 0)
		{
			escapedMolgenisFileNameExists = true;
		}
		
		//if there is no MolgenisFile, but there is a file present, make the link
		if(!mfPresent && filePresent && !escapedMolgenisFileNameExists)
		{
			MolgenisFile mfAdd = (MolgenisFile) db.getClassForName(type).newInstance();
			
			mfAdd.setName(data.getName());
			mfAdd.setExtension(getExtension(data.getStorage()));
            mfAdd.set("data_" + Data.ID, data.getId().toString());
            mfAdd.set("data_" + Data.NAME, data.getName());
            
			db.add(mfAdd);
			relinked = true;			
		}
		return relinked;
	}
	
	public String getExtension(String storage) throws Exception
	{
		if(storage.equals("Binary"))
		{
			return "bin";
		}
		else if(storage.equals("CSV"))
		{
			return "txt";
		}
		else
		{
			throw new Exception("No extension for '" + storage + "'");
		}
	}

}
