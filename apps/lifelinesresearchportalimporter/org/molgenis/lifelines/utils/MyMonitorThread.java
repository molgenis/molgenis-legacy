package org.molgenis.lifelines.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

public class MyMonitorThread implements Runnable
{
	private static final Logger log = Logger.getLogger(MyMonitorThread.class);
    private final ThreadPoolExecutor executor;
    private final String tableName;

    public MyMonitorThread(ThreadPoolExecutor executor, String tableName)
    {
        this.executor = executor;
        this.tableName = tableName;
    }

    @Override
    public void run()
    {
        try
        {
            do
            {
                log.info(
                    String.format("[monitor-%s] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.tableName,
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
            
            log.info(
                    String.format("[monitor-s] [%s/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                    	this.tableName,
                    	this.executor.getPoolSize(),
                        this.executor.getCorePoolSize(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        this.executor.getTaskCount(),
                        this.executor.isShutdown(),
                        this.executor.isTerminated()));
            log.info(String.format("Table %s is processed", tableName));
        }
        catch (Exception e)
        {
        	log.error(e);
        }
    }
}