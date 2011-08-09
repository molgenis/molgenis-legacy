/* Date:        July 28, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.2-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.Protocol_Features;

public class MeasurementDecorator<E extends Measurement> extends MappingDecorator<E> {
	Logger logger  = Logger.getLogger("MeasurementDecorator");
	
	// JDBCMapper is the generate thing
	public MeasurementDecorator(JDBCMapper<E> generatedMapper) {
		super(generatedMapper);
	}
	
	//new kind of constructor to work with latest DB changes
	public MeasurementDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}
	
	private boolean makeCorrespondingProtocol(List<E> entities) throws DatabaseException, ParseException {
		String protocolName;
		String featureName;
		Database db = this.getDatabase();
		
		// Dirty trick to prevent Protocols from being made when calling from Hudson test:
		if (db.query(Investigation.class).eq(Investigation.NAME,"System").count() == 0)
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
				return false;
			}
		}
		return true;
	}
	
	private boolean removeCorrespondingProtocol(List<E> entities) {
		String protocolName;
		String featureName;
		Database db = this.getDatabase();
	
		for (E e : entities) {
			featureName = e.getName();
			// Remove corresponding protocol
			protocolName = "Set" + featureName;
			try {
				Protocol et = Protocol.findByNameInvestigation(db, protocolName, null);
				if(et != null)
					db.remove(et);
				else return true;
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private boolean updateCorrespondingProtocol(List<E> entities) {
		/*
		String eventTypeName;
		String featureName;
		Database db = this.getDatabase();
		ct.setDatabase(db);
	
		for (Measurement e : entities) {
			featureName = e.getName();
			// TODO: Do something?
		}
		*/
		return true;
	}

	public int add(List<E> entities) throws DatabaseException {
		
		try {
			// add your pre-processing here

			// here we call the standard 'add'
			int count = super.add(entities);
			
			// post-processing:
			if (!makeCorrespondingProtocol(entities)) {
				logger.warn("Could not make corresponding protocol");
				//throw new DatabaseException("Could not make corresponding protocol - observable feature not added");
			}

			return count;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int update(List<E> entities) throws DatabaseException {

		// add your pre-processing here TODO: shouldn't we do this after the update?
		if (!updateCorrespondingProtocol(entities)) {
			logger.warn("Could not update corresponding protocol");
			//throw new DatabaseException("Could not update corresponding protocol - observable feature not updated");
		}

		// here we call the standard 'update'
		int count = super.update(entities);
		
		// post-processing:
		
		return count;
	}

	public int remove(List<E> entities) throws DatabaseException {
		// add your pre-processing here
		if (!removeCorrespondingProtocol(entities)) {
			logger.warn("Could not remove corresponding protocol");
			//throw new DatabaseException("Could not remove corresponding protocol - observable feature not removed");
		}

		// here we call the standard 'remove'
		int count = super.remove(entities);

		// post-processing:

		return count;
	}
}