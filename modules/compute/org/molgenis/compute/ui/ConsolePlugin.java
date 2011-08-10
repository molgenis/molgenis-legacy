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
    private static final String COLOR_SUMMARY_FINISHED = "#99FF66";
    private static final String COLOR_SUMMARY_ACTIVE = "#8BFEA8";
    private static final String COLOR_TITLE = "#666666";
    private static final String COLOR_READY = "#008000";
    private static final String COLOR_ACTIVE = "#F0E68C";
    private static final String COLOR_WAITING = "#FFA500";


    private MCF mcf = null;
    private TablePanel tablePanel = new TablePanel();

    private ActionInput buttonRefresh = new ActionInput("buttonRefresh", "Refresh");


    public ConsolePlugin(String name, ScreenController parent)
    {
        super(name, parent);
    }

    public String render()
    {
        if (mcf != null)
        {
            int numberOfActive = mcf.getNumberActivePipelines();

            Table finishedTable = new Table("Finished Pipelines");
            finishedTable.addRow("");
            finishedTable.addColumn("");
            finishedTable.setCellStyle(0, 0, "background-color: " + COLOR_SUMMARY_FINISHED + ";");
            finishedTable.setCell(0,0, "pipelines finished: " + mcf.getNumberFinishedPipelines());
            tablePanel.add(finishedTable);

            Table activeTable = new Table("Active Pipelines");
            activeTable.addRow("");
            activeTable.addColumn("");
            activeTable.setCellStyle(0, 0, "background-color: " + COLOR_SUMMARY_ACTIVE + ";");
            activeTable.setCell(0,0, "pipelines active: " + mcf.getNumberActivePipelines());
            tablePanel.add(activeTable);

            if (numberOfActive > 0)
            {
                Table progressTable = new Table("progress");

                int numberOfRows = -1;

                //set table size
                for (int i = 0; i < numberOfActive; i++)
                {
                    Pipeline pipeline = mcf.getActivePipeline(i);
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
                    Pipeline pipeline = mcf.getActivePipeline(i);
                    progressTable.setCell(i, 0, pipeline.getId());
                    progressTable.setCellStyle(i, 0, "background-color: " + COLOR_TITLE + ";color: white;");

                    for (int j = 0; j < pipeline.getNumberOfSteps(); j++)
                    {
                        Step step = pipeline.getStep(j);

                        if (step.isFinished())
                        {
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_READY + ";color: white;");
                            progressTable.setCell(i, j + 1, step.getId() + " Finished # " + step.getNumberOfScripts());
                        }
                        else if (step.isActive())
                        {
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_ACTIVE + ";");
                            progressTable.setCell(i, j + 1, step.getId() + " Started: " + step.getScriptsStarted() +
                                    " Finished: " + step.getScriptsFinished() + "  of " + step.getNumberOfScripts());
                        }
                        else
                        {
                            progressTable.setCellStyle(i, j + 1, "background-color: " + COLOR_WAITING + ";");
                            progressTable.setCell(i, j + 1, step.getId() + " # " + step.getNumberOfScripts());
                        }
                    }
                }

                tablePanel.add(progressTable);

                Table legendTable = new Table("legend");
                for (int i = 0; i < 0; i++)
                    legendTable.addRow("");
                for (int i = 0; i < 3; i++)
                    legendTable.addColumn("");

                {
                    legendTable.setCellStyle(0, 0, "background-color: " + COLOR_READY + ";color: white;");
                    legendTable.setCell(0, 0, "FINISHED");
                    legendTable.setCellStyle(1, 0, "background-color: " + COLOR_ACTIVE + ";");
                    legendTable.setCell(1, 0, "RUNNING");
                    legendTable.setCellStyle(2, 0, "background-color: " + COLOR_WAITING + ";");
                    legendTable.setCell(2, 0, "IN QUEUE");
                }

                tablePanel.add(legendTable);
                tablePanel.add(buttonRefresh);
            }
        }
        else
        {
            Table finishedTable = new Table("Finished Pipelines");
            finishedTable.addRow("");
            finishedTable.addColumn("");
            finishedTable.setCellStyle(0, 0, "background-color: " + COLOR_SUMMARY_FINISHED + ";");
            finishedTable.setCell(0,0, "pipelines finished: 0");
            tablePanel.add(finishedTable);

            Table activeTable = new Table("Active Pipelines");
            activeTable.addRow("");
            activeTable.addColumn("");
            activeTable.setCellStyle(0, 0, "background-color: " + COLOR_SUMMARY_ACTIVE + ";");
            activeTable.setCell(0,0, "pipelines active: 0");
            tablePanel.add(activeTable);
        }


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
