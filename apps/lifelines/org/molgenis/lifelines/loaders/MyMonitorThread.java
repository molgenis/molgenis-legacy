package org.molgenis.lifelines.loaders;

import java.util.concurrent.ThreadPoolExecutor;

public class MyMonitorThread implements Runnable
{
    private ThreadPoolExecutor executor;

    public MyMonitorThread(ThreadPoolExecutor executor)
    {
        this.executor = executor;
    }

    @Override
    public void run()
    {
        try
        {
            do
            {
                System.out.println(
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.executor.getPoolSize(),
                        this.executor.getCorePoolSize(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        this.executor.getTaskCount(),
                        this.executor.isShutdown(),
                        this.executor.isTerminated()));
                Thread.sleep(3000);
            }
            while (this.executor.getCompletedTaskCount() - this.executor.getTaskCount() != 0);
            System.out.println("-----------MyMonitorThread exit!!!!!!!!!");
            System.out.println(
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.executor.getPoolSize(),
                        this.executor.getCorePoolSize(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        this.executor.getTaskCount(),
                        this.executor.isShutdown(),
                        this.executor.isTerminated()));            
        }
        catch (Exception e)
        {
        }
    }
}