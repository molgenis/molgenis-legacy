package org.molgenis.framework.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.db.jdbc.JDBCConnectionHelper;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

/**
 * This class allows to add two or classes<Entity> together and get them joined
 * automatically. Optionally you can define the join rules yourself (and it will
 * correct if you actually should joined on the superclass). Also it will
 * automatically join with the superclasses so you get all the colummns.
 * Optionally, you can use 'findObjects' to get List<Map<Class,Object>> where
 * each index holds a map with objects of your data. For example:
 * 
 * <pre>
 * // example 1; just paste some random entities
 * JoinQuery jq1 = new JoinQuery(db, Individual.class, Investigation.class);
 * 
 * for (Tuple t : jq1.find())
 * {
 * 	System.out.println(t);
 * }
 * 
 * // example 2: add the rule yourself
 * JoinQuery jq2 = new JoinQuery(db, Individual.class, Investigation.class);
 * jq2.addJoinRule(new QueryJoinRule(&quot;individual&quot;, &quot;investigation&quot;,
 * 		&quot;investigation&quot;, &quot;id&quot;));
 * 
 * for (Tuple t : jq2.find())
 * {
 * 	System.out.println(t);
 * }
 * 
 * //example 3: have the results as objects
 * JoinQuery jq3 = new JoinQuery(db, Individual.class, Investigation.class);
 * 
 * for (Map&lt;Class&lt;? extends org.molgenis.util.Entity&gt;, ? extends org.molgenis.util.Entity&gt; t : jq3
 * 		.findObjects())
 * {
 * 	for (Class k : t.keySet())
 * 	{
 * 		System.out.println(k.getSimpleName() + &quot;=&quot; + t.get(k));
 * 	}
 * }
 * </pre>
 * 
 */
public class JoinQuery extends QueryImp
{
	private List<Entity> entities = new ArrayList<Entity>();
	private List<QueryJoinRule> joinRules = new ArrayList<QueryJoinRule>();
	private Database db = null;

	public JoinQuery(Database db,
			Class<? extends org.molgenis.util.Entity>... entities)
			throws DatabaseException
	{

		this.db = db;
		for (Class<? extends org.molgenis.util.Entity> e : entities)
		{
			this.entities.add(db.getMetaData().getEntity(e.getSimpleName()));
		}
	}

	public void addJoinRule(QueryJoinRule rule)
	{
		joinRules.add(rule);
	}

	public List<Map<Class<? extends org.molgenis.util.Entity>, ? extends org.molgenis.util.Entity>> findObjects()
			throws DatabaseException
	{
		List<Map<Class<? extends org.molgenis.util.Entity>, ? extends org.molgenis.util.Entity>> result = new ArrayList<Map<Class<? extends org.molgenis.util.Entity>, ? extends org.molgenis.util.Entity>>();
		List<Class<? extends org.molgenis.util.Entity>> classes = new ArrayList<Class<? extends org.molgenis.util.Entity>>();
		for (Entity e : entities)
		{
			classes.add((Class<? extends org.molgenis.util.Entity>) db
					.getClassForName(e.getName()));
		}

		for (Tuple t : this.find())
		{
			Map<Class<? extends org.molgenis.util.Entity>, org.molgenis.util.Entity> row = new LinkedHashMap<Class<? extends org.molgenis.util.Entity>, org.molgenis.util.Entity>();
			for (Class<? extends org.molgenis.util.Entity> c : classes)
			{
				org.molgenis.util.Entity e;
				try
				{
					e = c.newInstance();
					e.set(t);
				}
				catch (Exception e1)
				{

					e1.printStackTrace();
					throw new DatabaseException(e1.getMessage());
				}

				row.put(c, e);
			}
			result.add(row);

		}

		return result;
	}

	public List<Tuple> find() throws DatabaseException
	{
		List<Tuple> result = null;

		try
		{

			// get the join paths unless already set, the validate
			if (joinRules.size() == 0)
			{
				this.guessJoinRules();
			}
			else
			{
				this.validateJoinRules();
			}

			// if no join rules, fire error
			if (joinRules.size() < entities.size() - 1)
			{
				// throw new DatabaseException("Cannot find join rules");
			}

			// debug
			for (QueryRule r : joinRules)
			{
				//System.out.println("searching using joinrule: " + r);
			}

			// add all superclasses and joins necessary
			this.addSuperClasses();

			for (Entity e : entities)
			{
				//System.out.println("adding entity: " + e.getName());
			}

			for (QueryRule r : joinRules)
			{
				//System.out.println("searching using joinrule: " + r);
			}

			// execute the query
			// assemble the sql
			String sql = "SELECT ";
			for (Entity e : entities)

			{

				for (Field f : e.getAllFields())
				{
					sql += f.getEntity().getName()
							+ "."
							+ f.getName()
							+ " as "
							+ GeneratorHelper.getJavaName(e.getName() + "_"
									+ f.getName()) + ", ";
				}
			}
			sql = sql.substring(0, sql.length() - 2) + " FROM ";
			// get rid of trailing ', '
			for (Entity md : entities)
			{
				sql += md.getName() + ", ";
			}
			sql = sql.substring(0, sql.length() - 2);

			sql += JDBCConnectionHelper.createWhereSql(null, false, true,
					joinRules.toArray(new QueryRule[joinRules.size()]));

			return ((JDBCDatabase) db).sql(sql);

		}
		catch (MolgenisModelException e1)
		{
			e1.printStackTrace();
			throw new DatabaseException(e1.getMessage());
		}
	}

	private void addSuperClasses() throws MolgenisModelException
	{
		List<Entity> superEntities = new ArrayList<Entity>();
		for (Entity e : entities)
		{
			for (Entity parent : e.getAllAncestors())
			{
				if (!superEntities.contains(parent))
				{
					superEntities.add(parent);
					joinRules.add(new QueryJoinRule(e.getName(), e
							.getPrimaryKey().getName(), parent.getName(),
							parent.getPrimaryKey().getName()));
				}
			}
		}
		entities.addAll(superEntities);

	}

	private void validateJoinRules()
	{
		// this takes the rules, optionally rewriting it to a superclass
		for (QueryJoinRule jr : joinRules)
		{
			// check one field
			try
			{
				Field f1 = db.getMetaData().getEntity(jr.getEntity1())
						.getAllField(jr.getField1());
				Field f2 = db.getMetaData().getEntity(jr.getEntity2())
						.getAllField(jr.getField2());
				jr.setEntity1(f1.getEntity().getName());
				jr.setEntity2(f2.getEntity().getName());

			}
			catch (MolgenisModelException e)
			{
				e.printStackTrace();
			}
			catch (DatabaseException e)
			{
				e.printStackTrace();
			}
		}

	}

	private void guessJoinRules() throws DatabaseException
	{
		// find the shortest path between each entity pair in our list
		for (int i = 0; i < entities.size(); i++)
		{
			// get two entities from metadata
			Entity ed1 = entities.get(i);
			for (int j = i + 1; j < entities.size(); j++)
			{
				Entity ed2 = entities.get(j);
				//System.out.println("finding path for " + ed1.getName() + " "
				//		+ ed2.getName());
				if (ed1 != ed2)
				{
					try
					{
						List<QueryJoinRule> path1 = findShortestJoinPath(ed1,
								ed2);
						List<QueryJoinRule> path2 = findShortestJoinPath(ed2,
								ed1);
						if (path1.size() > 0
								&& (path1.size() < path2.size() || path2.size() == 0))
						{
							for (QueryJoinRule jr : path1)
							{
								if (!joinRules.contains(jr))
								{
									//System.out.println("added joinrule: " + jr);
									joinRules.add(jr);
								}
							}
						}
						else
						{
							for (QueryJoinRule jr : path2)
							{
								if (!joinRules.contains(jr))
								{
									//System.out.println("added joinrule: " + jr);
									joinRules.add(jr);
								}
							}
						}
					}
					catch (MolgenisModelException e)
					{
						e.printStackTrace();
						throw new DatabaseException(e.getMessage());
					}
				}
			}
		}

	}

	private List<QueryJoinRule> findShortestJoinPath(Entity e1, Entity e2)
			throws MolgenisModelException, DatabaseException
	{
		List<QueryJoinRule> shortestPath = new ArrayList<QueryJoinRule>();
		if (e1.getName().equals(e2.getName()))
		{
			return shortestPath;
		}

		// direct path via xref
		for (Field fd : e1.getAllFields())
		{
			if (fd.getType() instanceof XrefField)
			{
				//System.out.println("testing " + fd.getEntity().getName() + "."
				//		+ fd.getName());
				List<QueryJoinRule> path = new ArrayList<QueryJoinRule>();
				path.add(new QueryJoinRule(fd.getEntity().getName(), fd
						.getName(), fd.getXrefEntity().getName(), fd
						.getXrefField().getName()));
				// direct pat from e1 to e2 (or its superclass)?
				if (e2.equals(fd.getXrefEntity())
						|| e2.getAllAncestors().contains(fd.getXrefEntity()))
				{
					//System.out.println("found direct path from " + e1.getName()
					//		+ " to " + e2.getName());
					return path;
				}
				// cyclic path
				else if (fd.getXrefEntity().equals(e1.getName()))
				{
					;
				}
				// indirect path, recurse following the xrefentity
				else
				{
					path.addAll(findShortestJoinPath(fd.getXrefEntity(), e2));
					if (path.size() < shortestPath.size()
							|| shortestPath.size() == 0) shortestPath = path;
				}
			}
		}

		if (shortestPath.size() > 1)
		{
			// otherwise it was a dead end
			return shortestPath;
		}
		return new ArrayList<QueryJoinRule>();
	}
}
