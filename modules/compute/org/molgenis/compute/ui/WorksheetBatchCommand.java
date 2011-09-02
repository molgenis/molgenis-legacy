package org.molgenis.compute.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.FormModel.Mode;
import org.molgenis.framework.ui.commands.SimpleCommand;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 28/07/2011
 * Time: 09:13
 * To change this template use File | Settings | File Templates.
 */
public class WorksheetBatchCommand extends SimpleCommand
{
    String currentRequest = "";

    private ActionInput runButton;

    public WorksheetBatchCommand(ScreenController<?> parentController)
    {
        super("runbatch", parentController);
        this.setLabel("Run analysis on selected");
        this.setMenu("Edit");
        this.setDialog(true);
        this.setToolbar(true);
        this.setIcon("/");

        runButton = new ActionInput("run", ActionInput.Type.NEXT);
//        runButton.setLabel("Run analysis for selected lanes");
    }

    @Override
    public List<HtmlInput<?>> getInputs() throws DatabaseException
    {

    	List<HtmlInput<?>> inputs = new ArrayList<HtmlInput<?>>();

        TextInput request = new TextInput("request");
        request.setValue(currentRequest);
        inputs.add(request);

        return inputs;
    }


    @Override
    public List<ActionInput> getActions()
    {
        List<ActionInput> inputs = new ArrayList<ActionInput>();
        inputs.add(runButton);
        return inputs;
    }

	@Override
	public boolean isVisible()
	{
		FormModel<? extends Entity> view = this.getFormScreen();
		return view.getMode().equals(Mode.LIST_VIEW);
	}
    
    public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream)
    {
    	System.out.println(">> In handleRequest!");
        logger.debug("worksheet batch command button clicked: " + request.toString());

        currentRequest = request.toString();

        System.out.println("here handleRequest");

        String action = request.getString("__action");


        if (action.equals("run"))
        {
            System.out.println("... pushed");
        }
        else
        {
            System.out.println("what the fuck: " + action);
        }

        for(int i = 0; i < this.getActions().size(); i++)
        {
            ActionInput input = this.getActions().get(i);
            System.out.println("action " + input.toString());
        }


        return ScreenModel.Show.SHOW_MAIN; //  ScreenModel.Show.SHOW_DIALOG;
    }
}


