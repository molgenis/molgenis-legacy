package plugins.rinterpreter;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class RInterpreterModel extends SimpleScreenModel
{

	public RInterpreterModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	String input = "";

	public String getInput()
	{
		return input;
	}

	public void setInput(String input)
	{
		this.input = input;
	}

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
