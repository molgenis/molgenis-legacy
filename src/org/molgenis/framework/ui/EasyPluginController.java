package org.molgenis.framework.ui;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.molgenis.framework.db.Database;
import org.molgenis.util.RedirectedException;
import org.molgenis.util.Tuple;

/**
 * Simplified controller that handles a lot of the hard stuff in handleRequest.
 */
public abstract class EasyPluginController<M extends ScreenModel> extends
		SimpleScreenController<M>
{
	private static final long serialVersionUID = 1L;

	// hack to be able to 'correctly' handle redirects (do not continue handling
	// this request after HandleRequest in AbstMolgServlet is done - contrary to
	// usual response serving which is 'fall through' and therefore wrong) and
	// at the same time allow EasyPlugins to throw exceptions which are all
	// thrown as InvocationTargetException due to reflection, while being able
	// to render the resulting page + the exception on screen
	public static Boolean HTML_WAS_ALREADY_SERVED;

	public EasyPluginController(String name, M model, ScreenController<?> parent)
	{
		super(name, model, parent);
	}

	/**
	 * If a user sends a request it can be handled here. Default, it will be
	 * automatically mapped to methods based request.getAction();
	 * 
	 * @throws RedirectedException
	 */
	@Override
	public void handleRequest(Database db, Tuple request)
			throws RedirectedException
	{
		// automatically calls functions with same name as action
		delegate(request.getAction(), db, request);
	}

	@Override
	public void handleRequest(Database db, Tuple request, OutputStream out)
			throws RedirectedException
	{
		// automatically calls functions with same name as action
		delegate(request.getAction(), db, request);
	}

	public void delegate(String action, Database db, Tuple request)
			throws RedirectedException
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
				if(db.inTx())
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
				// useless - can't do this on every error! we cannot distinguish
				// exceptions because they are all InvocationTargetException
				// anyway
				// }catch (InvocationTargetException e){
				// throw new RedirectedException(e);
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
		// catch (RedirectedException e){
		// throw e;
		// }
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setError(String message)
	{
		this.getModel().setMessages(new ScreenMessage(message, false));
	}

	public void setSucces(String message)
	{
		this.getModel().setMessages(new ScreenMessage(message, true));
	}

}
