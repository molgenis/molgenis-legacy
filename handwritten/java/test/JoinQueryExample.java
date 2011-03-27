package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.JoinQuery;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryJoinRule;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

public class JoinQueryExample
{
	public static void main(String[] args) throws DatabaseException,
			FileNotFoundException, IOException, ParseException
	{
		Database db = new JDBCDatabase(
				"handwritten/apps/org/molgenis/xgap/xqtlworkbench_euratrans/xqtleuratrans.properties");
		
		//example 0: traditional queries
		Query<Individual> jq0 = db.query(Individual.class);
		for (Individual t : jq0.find())
		{
			System.out.println(t);
		}

		// example 1; just paste some random entities
		JoinQuery jq1 = new JoinQuery(db, Individual.class,
				Investigation.class);

		for (Tuple t : jq1.find())
		{
			System.out.println(t);
		}

		// example 2: add the rule yourself, and using other syntax
		JoinQuery jq2 = db.join(Individual.class,
				Investigation.class);
		jq2.addJoinRule(new QueryJoinRule("individual", "investigation",
				"investigation", "id"));

		for (Tuple t : jq2.find())
		{
			System.out.println(t);
		}
		
		//example 3: have the results as objects
		JoinQuery jq3 = new JoinQuery(db, Individual.class,
				Investigation.class);

		List<Map<Class<? extends Entity>, ? extends Entity>> result = jq3.findObjects();
		for (Map<Class<? extends Entity>, ? extends Entity> keyValue : result)
		{
			for(Class k: keyValue.keySet())
			{
				System.out.println(k.getSimpleName() +"="+keyValue.get(k));
			}
		}
		
		//example 4: or you can take care of objectifiying yourself
		JoinQuery jq4 = new JoinQuery(db, Individual.class,
				Investigation.class);

		for (Tuple t : jq4.find())
		{
			Individual ind = new Individual();
			ind.set(t);
			Investigation inv = new Investigation();
			inv.set(t);
			System.out.println(inv + "\n"+ ind);
		}
		
	}

	
}
