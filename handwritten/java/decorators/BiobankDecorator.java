/* Date:        May 20, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.bbmri.ChangeLog;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;

public class BiobankDecorator<E extends org.molgenis.bbmri.BiobankPanel> extends MappingDecorator<E>
{
	//JDBCMapper is the generate thing
//	public BiobankDecorator(JDBCMapper generatedMapper)
//	{
//		super(generatedMapper);
//	}

	//Mapper is the generate thing
	public BiobankDecorator(Mapper generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		org.molgenis.bbmri.ChangeLog chl = null;
		Date date = new Date(); 
		
		//retrieve the user who uploaded/added the record
		Login login = this.getDatabase().getSecurity();
		if (login != null) {
			if (login.getUserId() != null) {
				int userId = this.getDatabase().getSecurity().getUserId();
				
				// add your pre-processing here, e.g.
				for (org.molgenis.bbmri.BiobankPanel e : entities)
				{
					e.setOwns_Id(userId);
				}
			}
		}
		
		

		
		// here we call the standard 'add'
		int count = super.add(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		Date date = new Date(); 
		Database db = this.getDatabase();
		
		try {
			
			ChangeLog changeLog = new ChangeLog();
			changeLog.setDate(date.toString());
			changeLog.setEntity("Entity TEST");
		
			db.add(changeLog);
			
		} catch (IOException e) {
			e.printStackTrace();
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

