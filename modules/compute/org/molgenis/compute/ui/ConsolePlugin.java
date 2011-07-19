/* Date:        February 17, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Step;
import org.molgenis.compute.scriptserver.MCF;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.util.Tuple;
import org.molgenis.util.HttpServletRequestTuple;

import javax.servlet.ServletContext;


public class ConsolePlugin extends GenericPlugin
{
    private static final String COLOR_TITLE = "#666666";
    private static final String COLOR_READY = "#008000";
    private static final String COLOR_ACTIVE = "#F0E68C";
    private static final String COLOR_WAITING = "#FFA500";


    private MCF mcf = null;
    private TablePanel tablePanel = new TablePanel();
    private LabelInput labelInfo = new LabelInput("Active pipelines: ");
    private ActionInput buttonRefresh = new ActionInput("buttonRefresh", "Refresh");


    public ConsolePlugin(String name, ScreenController parent)
    {
        super(name, parent);
    }

    public String render()
    {
        tablePanel.add(labelInfo);

        if (mcf != null)
        {
            int numberOfActive = mcf.getNumberActivePipelines();
            labelInfo.setValue("" + numberOfActive);

            if (numberOfActive > 0)
            {
                Table progressTable = new Table("progress");

                int numberOfRows = -1;

                //set table size
                for (int i = 0; i < numberOfActive; i++)
                {
                    Pipeline pipeline = mcf.getPipeline(i);
                    int n = pipeline.getNumberOfSteps();
                    if (n > numberOfRows)
                        numberOfRows = n;
                }
                //add one extra row to show pipeline name
                int numberOfColumn = numberOfActive;

                //create table
                for (int i = 0; i < numberOfRows + 1; i++)
                    progressTable.addRow("");
                for (int i = 0; i < numberOfColumn; i++)
                    progressTable.addColumn("");

                for (int i = 0; i < numberOfActive; i++)
                {
                    Pipeline pipeline = mcf.getPipeline(i);
                    progressTable.setCell(i, 0, pipeline.getId());
                    //progressTable.setCellColor(i, 0, COLOR_TITLE);
                    progressTable.setCellStyle(i, 0, "background-color: " + COLOR_TITLE + ";color: white;");
                    //progressTable.setCellStyle(i, 0, "color: white;");


                    for (int j = 0; j < pipeline.getNumberOfSteps(); j++)
                    {
                        Step step = pipeline.getStep(j);

                        if (step.isFinished())
                        {
                            //progressTable.setCellColor(i, j + 1, COLOR_READY);
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_READY + ";color: white;");
                            //progressTable.setCellStyle(i, j + 1, "color: white;");
                            progressTable.setCell(i, j + 1, step.getId() + " Finished # " + step.getNumberOfScripts());
                        }
                        else if (step.isActive())
                        {
                            //progressTable.setCellColor(i, j + 1, COLOR_ACTIVE);
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_ACTIVE + ";");
                            //progressTable.setCellStyle(i, j + 1, "color: white;");
                            progressTable.setCell(i, j + 1, step.getId() + " Started: " + step.getScriptsStarted() +
                                    " Finished: " + step.getScriptsFinished() + "  of " + step.getNumberOfScripts());
                        }
                        else
                        {
                            //progressTable.setCellColor(i, j + 1, COLOR_WAITING);
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_WAITING + ";");
                            //progressTable.setCellStyle(i, j + 1, "color: white;");
                            progressTable.setCell(i, j + 1, step.getId() + " # " + step.getNumberOfScripts());
                        }
                    }
                }

                tablePanel.add(progressTable);

                Table legendTable = new Table("legend");
                for (int i = 0; i < 3; i++)
                    legendTable.addRow("");
                for (int i = 0; i < 2; i++)
                    legendTable.addColumn("");

                {
                    legendTable.setCellStyle(0, 0, "background-color: " + COLOR_READY + ";");
                    legendTable.setCell(0, 1, "FINISHED");
                    legendTable.setCellStyle(1, 0, "background-color: " + COLOR_ACTIVE + ";");
                    legendTable.setCell(1, 1, "RUNNING");
                    legendTable.setCellStyle(2, 0, "background-color: " + COLOR_WAITING + ";");
                    legendTable.setCell(2, 1, "IN QUEUE");
                }
            }
        }
        tablePanel.add(buttonRefresh);

        return tablePanel.toHtml();
    }

    @Override
    public void handleRequest(Database db, Tuple request)
    {
        if (mcf == null)
        {
            HttpServletRequestTuple req = (HttpServletRequestTuple) request;
            ServletContext servletContext = req.getRequest().getSession().getServletContext();
            mcf = (MCF) servletContext.getAttribute("MCF");
        }
    }


}
