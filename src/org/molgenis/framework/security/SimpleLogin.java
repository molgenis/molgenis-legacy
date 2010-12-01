package org.molgenis.framework.security;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.Entity;

public class SimpleLogin<E extends Entity> implements Login<E>
{

	@Override
	public void reload(Database db)
	{
	}

	public void logout()
	{
	}

	@Override
	public boolean isAuthenticated()
	{
		return true;
	}

	@Override
	public boolean canRead(Class<E> entity)
	{
		return true;
	}

	@Override
	public boolean canWrite(Class<E> entity)
	{
		return true;
	}

	public boolean hasRowEditRights(Entity entity)
	{
		return true;
	}

	public boolean hasRowReadRights(Entity entity)
	{
		return true;
	}

	public boolean isOwner(Entity entity)
	{
		return true;
	}

	public boolean isSelf(Entity entity)
	{
		return true;
	}

	public boolean lastSuperuser()
	{
		return false;
	}

	public String getUserName()
	{
		return "";
	}

	public Integer getUserId()
	{
		return 0;
	}

	@Override
	public boolean login(Database db, String name, String password)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoginRequired()
	{
		// TODO Auto-generated method stub
		return false;
	}

	// door Martijn erbij gezet 3 juli 2009
	@Override
	public boolean canWrite(E entity)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canRead(Entity entity)
	{
		return true;
	}

	@Override
	public QueryRule getRowlevelSecurityFilters(Class klazz)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
