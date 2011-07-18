package org.molgenis.compute.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityInput;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.*;
import org.molgenis.util.Tuple;


public class ComputePlugin extends GenericPlugin
{
    private enum Mode
    {
        START, SELECTED, RUN
    }


    private Workflow currentWorkflow = null;

    private TablePanel tableStart = new TablePanel();
    private TablePanel tableSelected = new TablePanel();
    private TablePanel tableRun = new TablePanel();


    private EntityInput workflowXrefInput = new XrefInput("Select workflow protocol", Workflow.class);
    private ActionInput buttonDone = new ActionInput("buttonDone", "Done");
    private ActionInput buttonSave = new ActionInput("buttonSave", "Start");

    private LabelInput labelSelected = new LabelInput("Selected pipeline: ");
    private LabelInput labelSubmitted = new LabelInput("Pipeline is submitted");


    private StringInput pipelineName = new StringInput("Set application name");

    LabelInput empty = new LabelInput("");


    private static final long serialVersionUID = 1144922325862349495L;

    private Mode mode = Mode.START;

    private HashMap<String, ComputeFeature> computeFeatures = new HashMap<String, ComputeFeature>();

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public static String now()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }


    public ComputePlugin(String name, ScreenController<?> parent)
    {
        super(name, parent);
    }

    public String render()
    {

        TablePanel result = null;

        workflowXrefInput.setXrefEntity(Workflow.class);

        workflowXrefInput.setXrefField(Workflow.ID);
        workflowXrefInput.setXrefLabel(Workflow.NAME);


        if (currentWorkflow != null)
        {
            //workflowXrefInput.setValue(currentWorkflow.getName());
            workflowXrefInput.setLabel(currentWorkflow.getName());
        }

        if (mode == Mode.START)
        {
            buttonDone.setLabel("               ");

            System.out.println(">>>>> START MODE");
            tableStart.add(workflowXrefInput);
            tableStart.add(buttonDone);
            result = tableStart;

        }

        if (mode == Mode.SELECTED)
        {
            System.out.println(">>>>> SELECTED MODE " + currentWorkflow.getName());
            labelSelected.setValue(currentWorkflow.getName());
            tableSelected.add(labelSelected);

            tableSelected.add(empty);
            tableSelected.add(pipelineName);

            for (ComputeFeature feature : computeFeatures.values())
            {
                if (feature.getIsUser())
                {
                    StringInput input = new StringInput(feature.getName());
                    input.setValue(feature.getDefaultValue());
                    tableSelected.add(input);
                }

            }

            tableSelected.add(empty);

            buttonSave.setLabel("Start pipeline");
            tableSelected.add(buttonSave);
            result = tableSelected;
        }

        if (mode == Mode.RUN)
        {
            tableRun.add(labelSubmitted);
            result = tableRun;
        }


        return result.toHtml();
    }

    public void handleRequest(Database db, Tuple request)
    {

        try
        {
            String action = request.getString("__action");
            if (action.equals("buttonDone"))
            {
                mode = Mode.SELECTED;
                handleDoneRequest(db, request);
            } else if (action.equals("buttonSave"))
            {
                mode = Mode.RUN;
                handleStartRequest(db, request);
            }
        }
        catch (Exception e)
        {
            try
            {
                db.rollbackTx();
            } catch (DatabaseException e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();
            this.getMessages().clear();
            if (e.getMessage() != null)
            {
                this.getMessages().add(new ScreenMessage(e.getMessage(), false));
            }
        }

    }

    private void handleStartRequest(Database db, Tuple request)
    {

        try
        {
            db.beginTx();


            ObservationTarget target = new ObservationTarget();
            target.setName("SVETLANA");
            target.setId(1);
            
            ComputeApplication app = new ComputeApplication();

            Calendar cal = Calendar.getInstance();

            System.out.println(">>> currentWorkflow==" + currentWorkflow);
            app.setProtocol((Protocol) currentWorkflow);
            app.setComputeResource("gridgain");//for time being
            app.setName(request.getString(pipelineName.getName()));
            //app.setStartTime(cal.getTime());

            System.out.println("" + request.getString(pipelineName.getName()));

            db.add(app);
            db.add(target);

            System.out.println(">>>> computefeatures size: " + computeFeatures.size());

            for (ComputeFeature computeFeature : computeFeatures.values())
            {
                ObservedValue value = new ObservedValue();
                if (computeFeature.getIsUser())
                {
                    value.setValue(request.getString(computeFeature.getName()));
                } else
                {
                    value.setValue(computeFeature.getDefaultValue());
                }
                value.setProtocolApplication(app);
                value.setTarget(target);
                value.setFeature(computeFeature);

                db.add(value);

            }

            db.commitTx();


        } catch (DatabaseException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    private void handleDoneRequest(Database db, Tuple request)
    {
        try
        {
            db.beginTx();

            Integer indId = request.getInt("Select workflow protocol");
            //workflowXrefInput.getValue()

            List<Workflow> indList;

            indList = db.query(Workflow.class).eq(Workflow.ID, indId).find();

            currentWorkflow = indList.get(0);

            System.out.println(">>>>>>>>>>>>>>" + currentWorkflow.getName());

            this.getMessages().add(new ScreenMessage("workflow selected", true));

            List<WorkflowElement> workflowElements = db.query(WorkflowElement.class).equals(WorkflowElement.WORKFLOW, currentWorkflow.getId()).find();

            //List<Protocol> protocols = null;

            for (int i = 0; i < workflowElements.size(); i++)
            {
                ComputeProtocol protocol = db.findById(ComputeProtocol.class, workflowElements.get(i).getProtocol_Id());
                //protocols.add(protocol);

                if (protocol.getInputs() != null)
                {
                    List<Integer> inputIDs = protocol.getInputs_Id();
                    System.out.println(">>>>>>>>>>> inputs size: " + inputIDs.size());
                    for (int j = 0; j < inputIDs.size(); j++)
                    {
                        ComputeFeature computeFeature = db.findById(ComputeFeature.class, inputIDs.get(j));

                        computeFeatures.put(computeFeature.getName(), computeFeature);
                    }
                }

                if (protocol.getOutputs() != null)
                {

                    List<Integer> outputIDs = protocol.getOutputs_Id();
                    System.out.println(">>>>>>>>>>> output size: " + outputIDs.size());
                    for (int j = 0; j < outputIDs.size(); j++)
                    {
                        ComputeFeature computeFeature = db.findById(ComputeFeature.class, outputIDs.get(j));
                        computeFeatures.put(computeFeature.getName(), computeFeature);
                    }
                }

            }

        }
        catch (Exception e)
        {
            try
            {
                db.rollbackTx();
            }
            catch (DatabaseException e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }


    }



}
