package org.molgenis.designgg;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

/**
 * This screen aims to ask parameters. If valid, the parameters are saved.
 * 
 * @author Morris Swertz
 * 
 */
public class AskParametersScreen extends PluginModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3386893658115378317L;
	private DesignParameters designParameters;
	private boolean bArgumentsOK = true;
	private boolean bReady2Go = false;
	private String argMissing = null;

	public AskParametersScreen(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		logger.debug(">handling request: " + request);
		try
		{
			// can we create a set of parameters?
			DesignParameters p = new DesignParameters();
			p.set(request);

			// test?
			if (request.getString("test") != null)
			{
				p.setNoIterations(20);
			}
			else
			{
				p.setNoIterations(1000);
			}

			// CHECKING PARAMETERS
			if (p.getGenotype() == null)
			{
				this.setArgMissing("You must provide a valid file with genotype data.");
				bArgumentsOK = false;
			}
			else if (!rangeIsValid(p))
			{
				bArgumentsOK = false;
			}
			else
			{
				bArgumentsOK = true;
				this.setDesignParameters(p);
				bReady2Go = true;
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // get from request.

	}

	private boolean rangeIsValid(DesignParameters p)
	{
		try
		{
		CsvReader fileReader = new CsvFileReader(new File(p.getGenotype()));
		int rows = fileReader.rownames().size();
		if(p.getRangeStart() != null && p.getRangeEnd() != null) for (int i = 0; i < p.getRangeStart().size(); i++)
		{
			int start = Integer.parseInt((String) p.getRangeStart().get(i));
			int end = Integer.parseInt((String) p.getRangeEnd().get(i));
			logger.debug("" + start + "-" + end);
			if (start < 1 || end > rows)
			{
				this.setArgMissing("Marker range should be in [1,maxindex makers="+rows+")], found [start=" + start + ",end=" + end + "]");
				return false;
			}
			else if (end < start)
			{
				this.setArgMissing("marker range 'start' should be lower than 'end', found [start=" + start + ",end=" + end + "]");
				return false;
			}
		}
		} catch(Exception e)
		{
			e.printStackTrace();
			this.setArgMissing(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void reload(Database db)
	{
		// TODO Auto-generated method stub
	}

	public DesignParameters getDefaultParameters()
	{
		// TODO: also use this when user fills in wrong parameters and need to
		// edit again.
		return new DesignParameters();
	}

	public DesignParameters getDesignParameters()
	{
		return this.designParameters;
	}

	public void setDesignParameters(DesignParameters designParameters)
	{
		this.designParameters = designParameters;
	}

	/**
	 * @return the bArgumentsOK
	 */
	public boolean isBArgumentsOK()
	{
		return bArgumentsOK;
	}

	/**
	 * @param argumentsOK
	 *            the bArgumentsOK to set
	 */
	public void setBArgumentsOK(boolean argumentsOK)
	{
		bArgumentsOK = argumentsOK;
	}

	/**
	 * @return the bReady2Go
	 */
	public boolean isBReady2Go()
	{
		return bReady2Go;
	}

	/**
	 * @param ready2Go
	 *            the bReady2Go to set
	 */
	public void setBReady2Go(boolean ready2Go)
	{
		bReady2Go = ready2Go;
	}

	/**
	 * @return the argMissing
	 */
	public String getArgMissing()
	{
		return argMissing;
	}

	/**
	 * @param argMissing
	 *            the argMissing to set
	 */
	public void setArgMissing(String argMissing)
	{
		this.argMissing = argMissing;
	}

	@Override
	public String getViewName()
	{
		return "screens_AskParametersScreen";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/designgg/AskParametersScreen.ftl";
	}
}
