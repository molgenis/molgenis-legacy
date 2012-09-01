package org.molgenis.compute.test.pilot;

import org.apache.commons.io.FileUtils;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 20/07/2012
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class PilotService implements MolgenisService
{

    public PilotService(MolgenisContext mc)
    {
        //super(mc);
    }

    public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException, DatabaseException, IOException
    {
        System.out.println(request);


        if("started".equals(request.getString("status")))
        {
            System.out.println(">>> looking for the task");

            ComputeTask task = request.getDatabase().query(ComputeTask.class).eq(ComputeTask.STATUSCODE,"ready").limit(1).find().get(0);

            if(task != null)
            {
                String taskName = task.getName();
                String taskScript = task.getComputeScript();

                //we add task id to the run listing to identify task when it is done
                taskScript = "echo TASKID:" + taskName + "\n" + taskScript;
                //change status to running
                System.out.println("script " + taskScript);
                task.setStatusCode("running");
                request.getDatabase().update(task);

                //send response
                response.getResponse().getWriter().write(taskScript);
            }
        }
        else if("done".equals(request.getString("status")))
        {
            String results = FileUtils.readFileToString(request.getFile("log_file"));
            //parsing for TaskID
            int idPos = results.indexOf("TASKID:") + 7;
            int endPos = results.indexOf("\n");

            String taskID = results.substring(idPos, endPos).trim();
            System.out.println(">>> task " + taskID + " is finished");

            ComputeTask task = request.getDatabase().query(ComputeTask.class).eq(ComputeTask.NAME, taskID).limit(1).find().get(0);

            if(task != null)
            {
                task.setStatusCode("done");
                task.setRunLog(results);
                request.getDatabase().update(task);
            }
        }
    }
}
