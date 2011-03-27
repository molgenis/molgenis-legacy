package plugins.rinterpreter;

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.RScript;
import org.molgenis.util.Tuple;

public class RInterpreterPlugin<E extends Entity> extends PluginModel
{

	private static final String	PACKAGE						= "xgap4human";

	private static final long		serialVersionUID	= 8092415619774443643L;

	private RInterpreterModel		screenModel				= new RInterpreterModel();

	public RInterpreterPlugin(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
	}

	@Override
	public String getViewName()
	{
		return "plugins_rinterpreter_RInterpreterPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/rinterpreter/RInterpreterPlugin.ftl";
	}

	enum Action
	{
		add0, add1, add2, add3, add4, add5, add6, add7, add8, add9, addDot, addPlus, addMinus, addMultiply, addDivide, addLeftParenthesis, addRightParenthesis, solve, clear;
	};

	public RInterpreterModel getModel()
	{
		return screenModel;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.handleRequest(request, null);

	}

	public void handleRequest(Tuple request, PrintWriter out)
	{
		screenModel.setInput(request.getString("input"));

		if (request.getObject(ScreenModel.INPUT_ACTION) != null)
		{
			switch (Action.valueOf(request.getString(ScreenModel.INPUT_ACTION)))
			{
				case solve:
					solve(request);
					break;
				case clear:
					screenModel.setInput("");
					break;
				case add0:
					addToInput(request, "0");
					break;
				case add1:
					addToInput(request, "1");
					break;
				case add2:
					addToInput(request, "2");
					break;
				case add3:
					addToInput(request, "3");
					break;
				case add4:
					addToInput(request, "4");
					break;
				case add5:
					addToInput(request, "5");
					break;
				case add6:
					addToInput(request, "6");
					break;
				case add7:
					addToInput(request, "7");
					break;
				case add8:
					addToInput(request, "8");
					break;
				case add9:
					addToInput(request, "9");
					break;
				case addDot:
					addToInput(request, ".");
					break;
				case addPlus:
					addToInput(request, "+");
					break;
				case addMinus:
					addToInput(request, "-");
					break;
				case addMultiply:
					addToInput(request, "*");
					break;
				case addDivide:
					addToInput(request, "/");
					break;
				case addLeftParenthesis:
					addToInput(request, "(");
					break;
				case addRightParenthesis:
					addToInput(request, ")");
					break;
				default:
					break;
			}
		}
	}

	private void solve(Tuple request)
	{
		String input = screenModel.getInput();
		String output = "";
		RScript r = new RScript();

		try
		{
			r.append(input);
			r.execute();
			output = r.getResult();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("output: " + output + ", length: " + output.length());

		// UNIX?
		// > 3+3
		// [1] 6
		// > q("no",status=0, FALSE)
		if (output.length() > input.length() + 31)
		{
			output = output.substring((2 + input.length() + 5), output.length() - 27);
			screenModel.setInput(output);
		} else
		{
			// WINDOWS / MAC OS X
			// [1] 6
			if (output.length() > 4)
			{
				screenModel.setInput(output.substring(4, output.length()));
			} else
			{
				screenModel.setInput("error");
			}
		}

	}

	private void addToInput(Tuple request, String addThis)
	{
		String input = screenModel.getInput();
		if (input == null || input.equals("error"))
		{
			input = addThis;
		} else
		{
			input += addThis;
		}
		screenModel.setInput(input);
	}

	private void interpret(Tuple request)
	{

		// r.append("a <- 1+1");
		// r.append("b <- 1+2");
		// r.append("file <- "+ File.createTempFile("mytest","png"));
		// r.execute();
	}

	@Override
	public void reload(Database db)
	{

	}

}
