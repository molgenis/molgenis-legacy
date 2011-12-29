<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* Date:        ${date}
 * Template:	${template}
 * generator:   ${generator} ${version}
 */

package ${package};

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.CsvReader;
import org.molgenis.util.TupleWriter;

import java.text.ParseException;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.service.MolgenisUserService;
import org.molgenis.framework.security.SimpleLogin;

<#if databaseImp = 'jpa'>
import org.molgenis.framework.db.jpa.JpaMappingDecorator;
<#else>
import org.molgenis.framework.db.jdbc.MappingDecorator;
</#if>

public class ${clazzName}<E extends ${entityClass}> extends <#if databaseImp = 'jpa'>JpaMappingDecorator<E><#else>MappingDecorator<E></#if>
{
	public ${clazzName}(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");

<#if authorizable??>
			//this.addRowLevelSecurityDefaults(entities); Commented out 07-10-2011 by ER because of unwanted behavior
</#if>
			//TODO: Add column level security filters
		}
		return super.add(entities);
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");

<#if authorizable??>
			this.addRowLevelSecurityFilters(entities);
</#if>
			//TODO: Add column level security filters
		}
		return super.update(entities);
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");
				
<#if authorizable??>
			this.addRowLevelSecurityFilters(entities);
</#if>
		}
		return super.remove(entities);
	}

	@Override
	public int add(CsvReader reader, TupleWriter writer) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");

			//TODO: Add column level security filters
		}
		return super.add(reader, writer);
	}

	@Override
	public int count(QueryRule... rules) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canRead(${entityClass}.class))
				return 0;

<#if authorizable??>
			rules = this.addRowLevelSecurityFilters(${entityClass}.CANREAD, rules);
</#if>
		}
		return super.count(rules);
	}

	@Override
	public List<E> find(QueryRule ...rules) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canRead(${entityClass}.class))
				return new ArrayList<E>();

<#if authorizable??>
			rules = this.addRowLevelSecurityFilters(${entityClass}.CANREAD, rules);
</#if>
		}

		List<E> result = super.find(rules);

		//TODO: Add column level security filters

		return result;
	}

	@Override
	public void find(TupleWriter writer, QueryRule ...rules) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canRead(${entityClass}.class))
				return;

<#if authorizable??>
			rules = this.addRowLevelSecurityFilters(${entityClass}.CANREAD, rules);
</#if>
		}

		super.find(writer, rules);
		//TODO: Add column level security filters. How???
	}

	@Override
	public int remove(CsvReader reader) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");

			//TODO: Add row level security filters
		}
		return super.remove(reader);
	}

	@Override
	public int update(CsvReader reader) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canWrite(${entityClass}.class))
				throw new DatabaseException("No write permission on ${entityClass}");

			//TODO: Add row level security filters
			//TODO: Add column level security filters
		}
		return super.update(reader);
	}

	@Override
	public void find(TupleWriter writer, List<String> fieldsToExport, QueryRule ...rules) throws DatabaseException
	{
		if (this.getDatabase().getSecurity() != null && !(this.getDatabase().getSecurity() instanceof SimpleLogin))
		{
			if (!this.getDatabase().getSecurity().canRead(${entityClass}.class))
				return;

<#if authorizable??>
			rules = this.addRowLevelSecurityFilters(${entityClass}.CANREAD, rules);
</#if>
		}

		super.find(writer, fieldsToExport, rules);
		//TODO: Add column level security filters. How???
	}

<#if authorizable??>
	//TODO: Move this to Login interface
	private QueryRule[] addRowLevelSecurityFilters(String permission, QueryRule ...rules) throws DatabaseException
	{
		if (this.getDatabase().getSecurity().isAuthenticated() && this.getDatabase().getSecurity().getUserName().equals("admin"))
			return rules;

		MolgenisUserService service = MolgenisUserService.getInstance(this.getDatabase());
		MolgenisUser user           = service.findById(this.getDatabase().getSecurity().getUserId());
		
		List<Integer> roleIdList;
		try
		{
			roleIdList              = service.findGroupIds(user);
		}
		catch (ParseException e)
		{
			return rules;
		}

		List<QueryRule> rulesList = new ArrayList<QueryRule>();
		org.apache.commons.collections.CollectionUtils.addAll(rulesList, rules);
		if (permission.equals(${entityClass}.CANREAD))
		{
			QueryRule rule1 = new QueryRule(${entityClass}.CANWRITE, org.molgenis.framework.db.QueryRule.Operator.IN, roleIdList);
			QueryRule rule2 = new QueryRule(${entityClass}.CANREAD, org.molgenis.framework.db.QueryRule.Operator.IN, roleIdList);
			QueryRule rule4 = new QueryRule(${entityClass}.OWNS, org.molgenis.framework.db.QueryRule.Operator.IN, roleIdList);
			QueryRule rule3 = new QueryRule(org.molgenis.framework.db.QueryRule.Operator.OR);
			rulesList.add(new QueryRule(rule1, rule3, rule2, rule3, rule4));
		}
		return rulesList.toArray(new QueryRule[0]);
	}
	
	private void addRowLevelSecurityFilters(List<E> entities) throws DatabaseException
	{
		for (E entity : entities)
		{
			if (!(this.getDatabase().getSecurity().canWrite(entity) || entity.getOwns().equals(this.getDatabase().getSecurity().getUserId())))
			{
				throw new DatabaseException("No row level write permission on ${entityClass}");
			}
		}
	}

	/*private void addRowLevelSecurityDefaults(List<E> entities)
	{
		for (E entity : entities)
		{
			entity.setOwns(this.getDatabase().getSecurity().getUserId());
		}
	}*/
</#if>
}