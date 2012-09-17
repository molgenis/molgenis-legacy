/* Date:        June 15, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.TextInput;

/**
 * StartNgsModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 *
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class HelloGridWorldView extends EasyPluginModel
{

    private ActionInput buttonRunGrid = new ActionInput("buttonRunTest", "Run Test");
    private TextInput outputGridName = new TextInput("outputGridName");

    //a system veriable that is needed by tomcat

    public HelloGridWorldView(HelloGridWorld controller)
    {
        //each Model can access the controller to notify it when needed.
        super(controller);
    }

    public ActionInput getButtonRunGrid()
    {
        return buttonRunGrid;
    }

    public void setButtonRunGrid(ActionInput buttonRunGrid)
    {
        this.buttonRunGrid = buttonRunGrid;
    }

    public TextInput getOutputGridName()
    {
        return outputGridName;
    }

    public void setOutputGridName(TextInput outputGridName)
    {
        this.outputGridName = outputGridName;
    }
}
