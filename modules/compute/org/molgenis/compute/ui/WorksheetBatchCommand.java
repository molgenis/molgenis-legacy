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
    private ActionInput runButton2;

    public WorksheetBatchCommand(ScreenController<?> parentController)
    {
        super("runbatch", parentController);
        this.setLabel("Run analysis on selected");
        this.setMenu("Edit");
        this.setDialog(true);
        this.setToolbar(true);
        this.setIcon("generated-res/img/run.png");

        runButton = new ActionInput("knop1");
        runButton.setButtonValue("Generate");
        runButton.setLabel("Generate code for selected lines!");
        
        runButton2 = new ActionInput("knop2");
        runButton2.setButtonValue("Generate2");
        runButton2.setLabel("Generate code for selected lines!");
    }

    @Override
    public List<HtmlInput<?>> getInputs() throws DatabaseException
    {

    	List<HtmlInput<?>> inputs = new ArrayList<HtmlInput<?>>();

        TextInput request = new TextInput("request");
        request.setValue(currentRequest);
        request.setDescription("");
        inputs.add(request);

        return inputs;
    }


    @Override
    public List<ActionInput> getActions()
    {
        List<ActionInput> inputs = new ArrayList<ActionInput>();
        inputs.add(runButton);
        inputs.add(runButton2);
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

        String action = request.getString("__action");
        String lines = request.getString("massUpdate");
        System.out.println(">> Selected lines: " + lines);

        System.out.println(">> Action: " + action);
        System.out.println(">> request==" + request.toString());

        for(int i = 0; i < this.getActions().size(); i++)
        {
            ActionInput input = this.getActions().get(i);
            System.out.println(">> >> Action " + input.toString());
        }


        return ScreenModel.Show.SHOW_MAIN; //  ScreenModel.Show.SHOW_DIALOG;
    }
}


