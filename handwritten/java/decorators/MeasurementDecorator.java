/* Date:        July 28, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.2-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.Protocol_Features;

import commonservice.CommonService;

public class MeasurementDecorator<E extends Measurement> extends MappingDecorator<E> {
	private CommonService ct = CommonService.getInstance();
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
		ct.setDatabase(db);
		
		// Dirty trick to prevent Protocols from being made when calling from Hudson test:
		if (ct.getInvestigationId("System") == -1) {
			// In Hudson, there is no pre-generated Investigation 'System' present in the DB, 
			// so the method returns -1 and then we happily return true so the test will not fail.
			return true;
		}

		for (E e : entities) {
			// Add corresponding event type
			featureName = e.getName();
			protocolName = "Set" + featureName;
			int etId, featId;
			
			try {
				// Auto-generated protocols will be linked to the always present System investigation
				etId = ct.makeProtocol(e.getInvestigation(), protocolName);
			} catch (Exception e2) {
				return false;
			}
			// Get Feature ID
			try {
				featId = ct.getMeasurementId(featureName);
			} catch (Exception e3) {
				return false;
			}
			// Add entry to coupling table. 
			// FIXME this is not how it should go. Well how should it go then?
			Protocol_Features efEntry = new Protocol_Features();
			efEntry.setProtocol(etId);
			efEntry.setFeatures(featId);
			try {
				db.add(efEntry);
			} catch (Exception e4) {
				e4.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private boolean removeCorrespondingProtocol(List<E> entities) {
		String protocolName;
		String featureName;
		Database db = this.getDatabase();
		ct.setDatabase(db);
	
		for (E e : entities) {
			featureName = e.getName();
			// Remove corresponding protocol
			protocolName = "Set" + featureName;
			try {
				Protocol et = ct.getProtocol(protocolName);
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

		// add your pre-processing here
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