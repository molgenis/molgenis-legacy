/* Date:        June 15, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.IntInput;

/**
 * StartNgsModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 *
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class StartNgsView extends EasyPluginModel
{

    private ActionInput buttonStart = new ActionInput("buttonStart", "Start NGS pipeline for selected lanes");
    private ActionInput buttonTest = new ActionInput("buttonTest", "Test Step");
    private ActionInput buttonGenerate = new ActionInput("buttonGenerate", "Generate Pipeline");
    private IntInput inputStep = new IntInput("inputStep");

    private IntInput inputFromStep = new IntInput("inputFromStep");
    private ActionInput buttonTestFrom = new ActionInput("buttonTestFrom", "Test From Step");



    //a system veriable that is needed by tomcat
    private static final long serialVersionUID = 1L;

    //another example, you can also use getInvestigations() and setInvestigations(...)
    //public List<Investigation> investigations = new ArrayList<Investigation>();

    public StartNgsView(StartNgs controller)
    {
        //each Model can access the controller to notify it when needed.
        super(controller);
    }

    public ActionInput getButtonStart()
    {
        return buttonStart;
    }

    public void setButtonStart(ActionInput buttonStart)
    {
        this.buttonStart = buttonStart;
    }


    public ActionInput getButtonTest()
    {
        return buttonTest;
    }

    public void setButtonTest(ActionInput buttonTest)
    {
        this.buttonTest = buttonTest;
    }

    public IntInput getInputStep()
    {
        return inputStep;
    }

    public void setInputStep(IntInput i)
    {
        this.inputStep = i;
    }

    public ActionInput getButtonGenerate()
    {
        return buttonGenerate;
    }

    public void setButtonGenerate(ActionInput buttonGenerate)
    {
        this.buttonGenerate = buttonGenerate;
    }

    public ActionInput getButtonTestFrom()
    {
        return buttonTestFrom;
    }

    public void setButtonTestFrom(ActionInput buttonTestFrom)
    {
        this.buttonTestFrom = buttonTestFrom;
    }

    public IntInput getInputFromStep()
    {
        return inputFromStep;
    }

    public void setInputFromStep(IntInput i)
    {
        this.inputFromStep = i;
    }
}
