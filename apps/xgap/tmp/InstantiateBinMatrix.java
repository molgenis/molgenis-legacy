package tmp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Entity;

import app.JDBCDatabase;

public class InstantiateBinMatrix
{

	public InstantiateBinMatrix() throws FileNotFoundException, IOException, DatabaseException, InstantiationException, IllegalAccessException{
		
		Database db = new JDBCDatabase("handwritten/properties/gcc.properties");
		
		List<? extends Entity> test = db.find(db.getClassForName("BinaryDataMatrix"));
		for(Entity e : test){
			System.out.println(e.toString());
		}
	}
	
	
	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException, InstantiationException, IllegalAccessException
	{
		new InstantiateBinMatrix();

	}

}
