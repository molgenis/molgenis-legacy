package org.molgenis.datatable.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;

public class downloadpheno implements MolgenisService
{

	// private static Logger logger = Logger.getLogger(downloadpheno.class);

	// private MolgenisContext mc;

	public downloadpheno(MolgenisContext mc)
	{
		// this.mc = mc;
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		PrintWriter out = response.getResponse().getWriter();
		response.getResponse().setContentType("text/plain");
		Database db = request.getDatabase();

		try
		{

			String name = request.getString("name");
			if (name == null)
			{
				throw new NullPointerException("Not specified: 'name'");
			}

			String plinkFormat = request.getString("plinkformat");
			if (plinkFormat == null)
			{
				throw new NullPointerException("Not specified: 'plinkformat'");
			}
			if (!(plinkFormat.equals("true") || plinkFormat.equals("false")))
			{
				throw new NullPointerException("Bad argument for 'plinkformat', must be true or false");
			}
			boolean plink = plinkFormat.equals("true") ? true : false;

			List<ObservableFeature> ofl = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.NAME,
					Operator.EQUALS, name));
			if (ofl.size() == 0)
			{
				throw new Exception("No phenotype named '" + name + "' found");
			}
			if (ofl.size() > 1)
			{
				throw new Exception("SEVERE: multiple database hits for name '" + name + " found!");
			}

			if (!plink)
			{
				out.print("\t" + ofl.get(0).getName() + "\n");
			}

			List<ObservedValue> ovl = db.find(ObservedValue.class, new QueryRule(ObservedValue.FEATURE,
					Operator.EQUALS, ofl.get(0).getId()));

			for (ObservedValue ov : ovl)
			{
				if (plink)
				{
					out.print(ov.getTarget_Name() + "\t" + ov.getTarget_Name() + "\t" + ov.getValue() + "\n");
				}
				else
				{
					out.print(ov.getTarget_Name() + "\t" + ov.getValue() + "\n");
				}

			}

			out.close();

		}
		catch (Exception e)
		{
			displayUsage(out, db);
			out.print("\n\n");
			e.printStackTrace(out);
			out.close();
		}

	}

	public void displayUsage(PrintWriter out, Database db) throws DatabaseException
	{
		String usage = "To download phenotypes, please specify 'name' and 'plinkformat' (ie. downloadpheno?name=weight&plinkformat=false) \n\n";
		usage += "You are logged in as " + db.getLogin().getUserName() + " and can download these phenotypes:\n\n";
		for (ObservableFeature of : db.find(ObservableFeature.class))
		{
			usage += "\t* " + of.getName() + "\n";
		}
		out.print(usage);
	}

}
