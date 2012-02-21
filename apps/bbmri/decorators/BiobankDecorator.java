/* Date:        May 20, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.util.Date;
import java.util.List;

import org.molgenis.auth.MolgenisGroup;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.bbmri.Biobank;
import org.molgenis.bbmri.ChangeLog;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.security.SimpleLogin;

public class BiobankDecorator<E extends Biobank> extends MapperDecorator<E>
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
		// add your pre-processing here
		Date date = new Date(); 
		Login login = this.getDatabase().getSecurity();
		if (login != null && !(login instanceof SimpleLogin)) {
			if (login.getUserId() != null) {
				int userId = this.getDatabase().getSecurity().getUserId();
				
				for (Biobank e : entities)
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
		
		// add your post-processing here
		// if you throw and exception the previous add will be rolled back
		// First check to see if Hudson is running (if so, there will be no user "admin" present).
		// If yes, bail out now, because Hudson cannot handle the ChangeLog entries.
		try {
			if (getDatabase().query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().size() == 0) {
				return count;
			}
		} catch (Exception e1) {
			return count;
		}
		for (Biobank e : entities) {
			//on every new entity update changelog table 
			try {
				
				ChangeLog changeLog = new ChangeLog();
				changeLog.setChangeDate(date.toString());
				changeLog.setEntity_Id(e.getId());
				this.getDatabase().add(changeLog);
				
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{	
		// add your pre-processing here

		// here we call the standard 'update'
		int count = super.update(entities);
		
		// First a check to see if Hudson is running (if so, there will be no user "admin" present).
		// If yes, bail out now, because Hudson cannot handle the ChangeLog entries.
		try {
			if (getDatabase().query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().size() == 0) {
				return count;
			}
		} catch (Exception e1) {
			return count;
		}
		
		// add your post-processing here
		// if you throw and exception the previous add will be rolled back
		Date date = new Date(); 
		for (Biobank e : entities)
		{
			//on every entity update, update changelog table 
			try {
				
				ChangeLog changeLog = new ChangeLog();
				changeLog.setChangeDate(date.toString());
				changeLog.setEntity_Id(e.getId());
				this.getDatabase().update(changeLog);
				
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}

		return count;
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here
		// First remove corresponding ChangeLog entries, so we will be allowed to remove the entities themselves
		// First a check to see if Hudson is running (if so, there will be no user "admin" present).
		if (getDatabase().query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().size() > 0) {
			for (Biobank e : entities)
			{
				try {
					List<ChangeLog> changelogList = getDatabase().query(ChangeLog.class).eq(ChangeLog.ENTITY_ID,
							e.getId()).find();
					getDatabase().remove(changelogList);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		
		// here we call the standard 'remove'
		int count = super.remove(entities);
		
		// add your post-processing here

		return count;
	}
}

