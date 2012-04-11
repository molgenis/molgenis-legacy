package org.molgenis.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 05/04/2012
 * Time: 09:46
 * To change this template use File | Settings | File Templates.
 */
public class GenericJobGenerator implements JobGenerator
{

    private Hashtable<String, String> config;

    public Vector<ComputeJob> generateComputeJobsWorksheet(Workflow workflow, List<Tuple> worksheet)
    {
        Vector<ComputeJob> computeJobs = new Vector<ComputeJob>();

        //because Hashtable does not allow null keys or values
        Hashtable<String, String> values = new Hashtable<String, String>();

        //parameters with templates
        Vector<ComputeParameter> complexParameters = new Vector<ComputeParameter>();

        //fill hashtable with workflow global parameters only once
        Collection<ComputeParameter> parameters = workflow.getWorkflowComputeParameterCollection();
        Iterator<ComputeParameter> itParameter = parameters.iterator();
        while (itParameter.hasNext())
        {
            ComputeParameter parameter = itParameter.next();
            if (parameter.getDefaultValue() != null)
            {
                if (parameter.getDefaultValue().contains("${"))
                {
                    complexParameters.addElement(parameter);
                }
                else
                {
                    values.put(parameter.getName(), parameter.getDefaultValue());
                }
            }
            else
                values.put(parameter.getName(), "");
       }

        //produce jobs for every worksheet record
        //for (int i = 0; i < worksheet.size(); i++)
        for (int i = 0; i < 1; i++)
        {
            //add parameters from worksheet to values
            Tuple tuple = worksheet.get(i);
            List<String> names = tuple.getFields();

            String id = "id";
            for (String name : names)
            {
                String value = tuple.getString(name);

                //to avoid empty worksheet fields
                if (value == null)
                    break;

                values.put(name, value);
                id += "_" + value;
            }

            //weave complex parameters
            //TODO it is not good that we have dependencies between parameter templates (can be a circular dependency)
            int count = 0;
            while (complexParameters.size() > 0)
            {
                Vector<ComputeParameter> toRemove = new Vector<ComputeParameter>();
                for (ComputeParameter computeParameter : complexParameters)
                {
                    String complexValue = weaveFreemarker(computeParameter.getDefaultValue(), values);
                    //                   values.put(computeParameter.getName(), complexValue);
                    //                   complexParameters.remove(computeParameter);

                    if (complexValue.contains("${"))
                    {
                        System.out.println(computeParameter.getName() + " -> " + complexValue);
                    }
                    else
                    {
                        values.put(computeParameter.getName(), complexValue);
                        toRemove.add(computeParameter);
                    }
                }
                complexParameters.removeAll(toRemove);
                System.out.println("loop " + count + " removed" + toRemove.size());
                count++;
            }
            //read all workflow elements
            Collection<WorkflowElement> workflowElements = workflow.getWorkflowWorkflowElementCollection();
            Iterator<WorkflowElement> itr = workflowElements.iterator();
            while (itr.hasNext())
            {
                WorkflowElement el = itr.next();
                ComputeProtocol protocol = (ComputeProtocol) el.getProtocol();
                String template = protocol.getScriptTemplate();

                String jobListing = weaveFreemarker(template, values);

                ComputeJob job = new ComputeJob();

                //TODO review ComputeJob model

                String jobName = config.get(JobGenerator.GENERATION_ID) + "_" +
                        workflow.getName() + "_" +
                        el.getName() + "_" + id;

                job.setName(jobName);
                job.setProtocol(protocol);
                job.setComputeScript(jobListing);
                computeJobs.add(job);

                System.out.println("----------------------------------------------------------------------");
                System.out.println(el.getName());
                System.out.println(jobListing);
                System.out.println("----------------------------------------------------------------------");

            }
        }
        return computeJobs;
    }

    public Vector<ComputeJob> generateComputeJobsDB(Workflow workflow, List<ObservationTarget> worksheet)
    {
        return null;
    }

    public boolean generateActualJobs(Vector<ComputeJob> computeJobs, String backend, Hashtable<String, String> config)
    {
        return false;
    }

    public void setConfig(Hashtable<String, String> config)
    {
        this.config = config;
    }

    public String weaveFreemarker(String strTemplate, Hashtable<String, String> values)
    {
        Template t = null;
        StringWriter out = new StringWriter();
        try
        {
            t = new Template("name", new StringReader(strTemplate), new Configuration());
            t.process(values, out);
        }
        catch (TemplateException e)
        {
            //e.printStackTrace();
        }
        catch (IOException e)
        {
            //e.printStackTrace();
        }

        return out.toString();
    }
}
