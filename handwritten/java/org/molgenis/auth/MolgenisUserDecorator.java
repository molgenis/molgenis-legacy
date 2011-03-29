/* Date:        February 1, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.auth;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.util.PasswordHasher;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.framework.db.jdbc.JDBCMapper;


public class MolgenisUserDecorator<E extends org.molgenis.auth.MolgenisUser> extends MappingDecorator<E>
{
	//JDBCMapper is the generate thing
	public MolgenisUserDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here, e.g.
		for (org.molgenis.auth.MolgenisUser e : entities)
		{
			this.hashPassword(e);
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

		// add your pre-processing here, e.g.
		for (org.molgenis.auth.MolgenisUser e : entities)
		{
			this.hashPassword(e);
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
	
	private void hashPassword(org.molgenis.auth.MolgenisUser user)
	{
		// if password already encrypted, nothing changed -> return
		if (StringUtils.startsWith(user.getPassword(), "md5_"))
			return;

		try
		{
			PasswordHasher md5 = new PasswordHasher();

			String newPassword = md5.toMD5(user.getPassword());

			user.setPassword(newPassword);
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

