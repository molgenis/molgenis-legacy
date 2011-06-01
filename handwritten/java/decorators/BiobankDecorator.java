/* Date:        May 20, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.molgenis.auth.MolgenisGroup;
import org.molgenis.bbmri.Biobank;
import org.molgenis.bbmri.ChangeLog;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.framework.security.Login;

public class BiobankDecorator<E extends Biobank> extends MappingDecorator<E>
{
	//JDBCMapper is the generate thing
//	public BiobankDecorator(JDBCMapper generatedMapper)
//	{
//		super(generatedMapper);
//	}

	//Mapper is the generate thing
	public BiobankDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		Date date = new Date(); 
		
		// add your pre-processing here
		Login login = this.getDatabase().getSecurity();
		if (login != null) {
			if (login.getUserId() != null) {
				int userId = this.getDatabase().getSecurity().getUserId();
				
				for (org.molgenis.bbmri.Biobank e : entities)
				{
					// Set ownership of new record to current user
					e.setOwns_Id(userId);
					
					// Give group "AllUsers" read-rights on the new record
					try {
						MolgenisGroup mg = getDatabase().find(MolgenisGroup.class, 
								new QueryRule(MolgenisGroup.NAME, Operator.EQUALS, "AllUsers")).get(0);
						e.setCanRead_Id(mg.getId());
					} catch (Exception ex) {
						// When running from Hudson, there will be no group "AllUsers" so we prevent
						// an error, to keep our friend Hudson from breaking
					}
				
					
				}
			}
		}
		
		// here we call the standard 'add'
		int count = super.add(entities);

		for (org.molgenis.bbmri.Biobank e : entities) {
	
			//on every new entity update changelog table 
			try {
				
				ChangeLog changeLog = new ChangeLog();
				changeLog.setDate(date.toString());
				changeLog.setEntity(e.getId());
				
				this.getDatabase().add(changeLog);
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}		
		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		//ChangeLog chl = null;
		Date date = new Date(); 
		
		// add your pre-processing here
		for (org.molgenis.bbmri.Biobank e : entities)
		{
			//on every entity update, update changelog table 
			try {
				
				ChangeLog changeLog = new ChangeLog();
				changeLog.setDate(date.toString());
				changeLog.setEntity_Id(e.getId());
				this.getDatabase().add(changeLog);
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		// here we call the standard 'update'
		int count = super.update(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here

		// here we call the standard 'remove'
		int count = super.remove(entities);

		// add your post-processing here, e.g.
		// if(true) throw new SQLException("Because of a post trigger the remove is cancelled.");

		return count;
	}
}

