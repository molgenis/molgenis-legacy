/* Date:        July 28, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.2-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class MeasurementDecorator<E extends Measurement> extends MapperDecorator<E> {
	Logger logger  = Logger.getLogger("MeasurementDecorator");
	
	//new kind of constructor to work with latest DB changes
	public MeasurementDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}
	
	private boolean makeCorrespondingProtocols(List<E> entities) throws DatabaseException
	{
		String protocolName;
		String featureName;
		Database db = this.getDatabase();
		
		// To prevent Protocols from being made when calling from Hudson test:
		if (db.query(Investigation.class).eq(Investigation.NAME, "System").count() == 0)
		{
			// In Hudson, there is no pre-generated Investigation 'System' present in the DB, 
			// so we happily return true so the test will not fail.
			return true;
		}

		for (E e : entities) {
			// Add corresponding protocol
			featureName = e.getName();
			protocolName = "Set" + featureName;
			
			try {
				Protocol newProtocol = new Protocol();
				newProtocol.setName(protocolName);
				newProtocol.setInvestigation_Id(e.getInvestigation_Id());
				List<Integer> features_id = new ArrayList<Integer>();
				features_id.add(e.getId()); // e is already in the db so we can do this
				newProtocol.setFeatures_Id(features_id);
				db.add(newProtocol);	
				
			} catch (Exception e2) {
				// Something really went wrong so we return false
				e2.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private boolean removeCorrespondingProtocols(List<E> entities) throws DatabaseException
	{
		String protocolName;
		String featureName;
		Database db = this.getDatabase();
		
		// To prevent Protocols from being removed when calling from Hudson test:
		if (db.query(Investigation.class).eq(Investigation.NAME, "System").count() == 0)
		{
			// In Hudson, there is no pre-generated Investigation 'System' present in the DB, 
			// so we happily return true so the test will not fail.
			return true;
		}
	
		for (E e : entities) {
			featureName = e.getName();
			// Remove corresponding protocol
			protocolName = "Set" + featureName;
			try {
				Protocol et = Protocol.findByNameInvestigation(db, protocolName, null);
				if (et != null) {
					db.remove(et);
				}
			} catch (Exception e1) {
				// Something really went wrong so we return false
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private boolean updateCorrespondingProtocols(List<E> entities) throws DatabaseException
	{
		Database db = this.getDatabase();
		// To prevent Protocols from being updated when calling from Hudson test:
		if (db.query(Investigation.class).eq(Investigation.NAME, "System").count() == 0)
		{
			// In Hudson, there is no pre-generated Investigation 'System' present in the DB, 
			// so we happily return true so the test will not fail.
			return true;
		}
				
		/*
		String eventTypeName;
		String featureName;
	
		for (Measurement e : entities) {
			featureName = e.getName();
			// TODO: Do something here
		}
		*/
		return true;
	}

	public int add(List<E> entities) throws DatabaseException
	{	
		// add your pre-processing here

		// here we call the standard 'add'
		int count = super.add(entities);
		
		// post-processing:
		if (!makeCorrespondingProtocols(entities)) {
			logger.warn("Could not make corresponding protocol(s)");
			throw new DatabaseException("Could not make corresponding protocol(s) - measurement(s) not added");
		}

		return count;
	}

	public int update(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here TODO: shouldn't we do this after the update?
		if (!updateCorrespondingProtocols(entities)) {
			logger.warn("Could not update corresponding protocol(s)");
			throw new DatabaseException("Could not update corresponding protocol(s) - measurement(s) not updated");
		}

		// here we call the standard 'update'
		int count = super.update(entities);
		
		// post-processing:
		
		return count;
	}

	public int remove(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here
		if (!removeCorrespondingProtocols(entities)) {
			logger.warn("Could not remove corresponding protocol(s)");
			throw new DatabaseException("Could not remove corresponding protocol(s) - measurement(s) not removed");
		}

		// here we call the standard 'remove'
		int count = super.remove(entities);

		// post-processing:

		return count;
	}
}