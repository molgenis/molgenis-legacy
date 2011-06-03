package compute.resourcemanager;

import compute.remoteexecutor.RemoteGridGainStarter;
import compute.remoteexecutor.RemoteResult;
import compute.remoteexecutor.RemoteScriptSubmitter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

import org.gridgain.grid.Grid;


public class ResourceManager
{
    private int delay = -1;
    private int period = -1;  // repeat every sec.

    private Timer timer = new Timer();

    private ExecutorService exec = null;
    private RemoteGridGainStarter remoteGridGainStarter = new RemoteGridGainStarter();

    public void start()
    {
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                Future<String> future = exec.submit(new RemoteGridGainStarter());

                try
                {
                    String result = future.get();
                    System.out.println(result);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        }, delay * 24 * 60 * 60 * 1000, period * 24 * 60 * 60 * 1000);
    }

    //set settings in days
    public void setSettings(int delay, int period)
    {
        this.delay = delay;
        this.period = period;
    }

    public void setGrid(Grid grid)
    {
        exec = grid.newGridExecutorService();
    }

}
