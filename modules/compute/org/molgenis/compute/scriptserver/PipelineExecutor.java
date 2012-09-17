package org.molgenis.compute.scriptserver;

import java.util.concurrent.Executor;
//for docs look @ http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/Executor.html
public class PipelineExecutor implements Executor
{
    public void execute(Runnable r)
    {
        new Thread(r).start();
    }
}
