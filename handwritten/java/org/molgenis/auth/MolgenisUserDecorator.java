/* Date:        February 1, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.auth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.util.PasswordHasher;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.MappingDecorator;
import org.molgenis.framework.db.jdbc.JDBCMapper;


public class MolgenisUserDecorator<E extends org.molgenis.auth.MolgenisUser> extends MappingDecorator<E>
{
	//JDBCMapper is the generate thing
	public MolgenisUserDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
	}
	
	//new kind of constructor to work with latest DB changes
	public MolgenisUserDecorator(Mapper<E> generatedMapper)
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
		for (org.molgenis.auth.MolgenisUser e : entities)
		{
			// Try to add the user to the AllUsers group
			MolgenisGroup mg;
			try {
				mg = getDatabase().find(MolgenisGroup.class, new QueryRule(MolgenisGroup.NAME, Operator.EQUALS, "AllUsers")).get(0);
			} catch (DatabaseException dbe) {
				// When running from Hudson, there will be no group "AllUsers" so we return without giving
				// an error, to keep our friend Hudson from breaking
				return count;
			}
			
			MolgenisUserGroupLink mugl = new MolgenisUserGroupLink();
			mugl.setUser_Id(e.getId());
			mugl.setGroup_Id(mg.getId());
			try {
				getDatabase().add(mugl);
			} catch (IOException e1) {
				throw new DatabaseException(e1.getMessage());
			}
		}

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
			e.printStackTrace();
		}
	}
}

