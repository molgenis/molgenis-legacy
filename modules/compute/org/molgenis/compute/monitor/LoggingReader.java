package org.molgenis.compute.monitor;

import org.molgenis.compute.pipelinemodel.Pipeline;
import org.molgenis.compute.pipelinemodel.Step;


public abstract class LoggingReader
{

    public static final String _STARTED = "_started";
    public static final String _FINISHED = "_finished";

    protected String log_location = null;

    protected Step currentStep = null;

    protected Summary summary = new Summary();
    protected String pipelineName;

    protected String logging = RemoteFileReader.FILE_IS_NOT_EXISTS;

    public void setPipeline(Pipeline pipeline)
    {
        pipelineName = pipeline.getId();
    }

    public class Summary
    {
        public int scripts_started;
        public int scripts_finished;
        public int scripts_all;
    }


    public void setStep(Step step)
    {
        currentStep = step;
        currentStep.setScriptsStarted(0);
        currentStep.setScriptsFinished(0);
    }

    public void setLogFile(String demoLog)
    {
        log_location = demoLog;
    }

    public abstract boolean isStepFinished();

    public String getLogFile()
    {
        return logging;
    }
}
