package org.molgenis.framework.ui;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;

/**
 * Simplified controller that handles a lot of the hard stuff in handleRequest.
 */
public abstract class EasyPluginController<M extends ScreenModel> extends
		SimpleScreenController<M>
{
	private static final long serialVersionUID = 1L;

	public EasyPluginController(String name, M model, ScreenController<?> parent)
	{
		super(name, model, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * If a user sends a request it can be handled here. Default, it will be
	 * automatically mapped to methods based request.getAction();
	 */
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// automatically calls functions with same name as action
		delegate(request.getAction(), db, request);
	}

	public void delegate(String action, Database db, Tuple request)
	{
		// try/catch for db.rollbackTx
		try
		{
			// try/catch for method calling
			try
			{
				db.beginTx();
				logger.debug("trying to use reflection to call "
						+ this.getClass().getName() + "." + action);
				Method m = this.getClass().getMethod(action, Database.class,
						Tuple.class);
				m.invoke(this, db, request);
				logger.debug("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action + " completed");
				db.commitTx();
			}
			catch (NoSuchMethodException e1)
			{
				this.getModel().setMessages(
						new ScreenMessage("Unknown action: " + action, false));
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action
						+ "(db,tuple) failed: " + e1.getMessage());
				db.rollbackTx();
			}
			catch (Exception e)
			{
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action + " failed: "
						+ e.getMessage());
				e.printStackTrace();
				this.getModel().setMessages(
						new ScreenMessage(e.getCause().getMessage(), false));
				db.rollbackTx();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
