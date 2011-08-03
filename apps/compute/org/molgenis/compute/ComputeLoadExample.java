package org.molgenis.compute;


import java.util.Arrays;
import java.util.Date;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;

import app.DatabaseFactory;

public class ComputeLoadExample
{
	public static void main(String[] args) throws DatabaseException 
	{
		Database db = null; 
		
		try
		{
			db = DatabaseFactory.create("handwritten/apps/org/molgenis/compute/compute.properties");
			
			db.beginTx();
			
			//get the target of analys
			Panel panel = new Panel();
			panel.setName("LifeLines Cohort1");
			db.add(panel);
			
			//feature: chr
			ComputeFeature f1 = new ComputeFeature();
			//Measurement f1 = new Measurement();
			f1.setName("chr");
			f1.setDescription("Chromosome");
			//should be an xref to Chromosome??? depends on species???

			db.add(f1);
			
			//feature: pedfile
			ComputeFeature f2 = new ComputeFeature();
			//Measurement f2 = new Measurement();
			f2.setName("pedfile");
			
			db.add(f2);
			
			//feature: imputed.plink
			ComputeFeature f3 = new ComputeFeature();
			//Measurement f2 = new Measurement();
			f3.setName("imputedfile");
			
			db.add(f3);
			
			//protocol: beagle (fake)
			ComputeProtocol p1 = new ComputeProtocol();
			p1.setName("beagle");
			p1.setScriptTemplate("beagle ${chr1} ${pedfile} > ${imputedfile}");
			p1.setInterpreter("bash");
			p1.setInputs_Id(Arrays.asList(new Integer[]{f1.getId(), f2.getId()}));
			p1.setFeatures_Id(Arrays.asList(new Integer[]{f3.getId()}));
			
			db.add(p1);
			
			//protocol: impute (fake)
			ComputeProtocol p2 = new ComputeProtocol();
			p2.setName("impute");
			p2.setScriptTemplate("impute ${chr1} ${pedfile}  > ${imputedfile}");
			p2.setInterpreter("bash");
			p2.setInputs_Id(Arrays.asList(new Integer[]{f1.getId(), f2.getId()}));
			
			p2.setFeatures_Id(Arrays.asList(new Integer[]{(f3.getId())}));
		
			
			db.add(p2);
			
			//workflow: impute (fake)
			Workflow wf1 = new Workflow();
			wf1.setName("imputeAll");
			
			db.add(wf1);
			
			//workflow elements
			WorkflowElement e1 = new WorkflowElement();
			e1.setName("step1");
		    e1.setWorkflow_Id(wf1.getId()); //big discussion, element repeat between workflows!!!
			e1.setProtocol(p1);
			
			db.add(e1);
			
			WorkflowElement e2 = new WorkflowElement();
			e2.setName("step2");
			e2.setWorkflow_Id(wf1.getId());
			e2.setProtocol(p2);
			e2.setPreviousSteps_Id(e1.getId());
			
			db.add(e2);
			
			//application of wf1 == chain of protocolApps, backward chaining
			ComputeApplication wf1_e1_run1 = new ComputeApplication();
			wf1_e1_run1.setName("wf1_e1_run1");
			wf1_e1_run1.setProtocol(e2.getProtocol());
			wf1_e1_run1.setTime(new Date());
			wf1_e1_run1.setComputeScript("beagle chr1 file1beagle.map > result1beagle.imputed");
			
			db.add(wf1_e1_run1);
			
			ComputeApplication wf1_e2_run1 = new ComputeApplication();
			wf1_e2_run1.setName("wf1_e2_run1");
			wf1_e2_run1.setProtocol(e2.getProtocol());
		    wf1_e2_run1.setPrevSteps_Id(wf1_e1_run1.getId());
		    wf1_e2_run1.setTime(new Date());
		    wf1_e2_run1.setComputeScript("impute chr1 file1impute.map > result1impute.imputed");
		    
		    db.add(wf1_e2_run1);
		    
		    //set the values
		    //ComputeValue wf1_e1_run1_f1 = new ComputeValue();
		    ObservedValue wf1_e1_run1_f1 = new ObservedValue();
		    wf1_e1_run1_f1.setFeature_Id(f1.getId());
		    wf1_e1_run1_f1.setValue("chr1");
		    wf1_e1_run1_f1.setProtocolApplication(wf1_e1_run1);
		    wf1_e1_run1_f1.setTarget(panel);
		    wf1_e1_run1_f1.setTime(new Date());
		    
		    //ComputeValue wf1_e1_run1_f2 = new ComputeValue();
		    ObservedValue wf1_e1_run1_f2 = new ObservedValue();
		    wf1_e1_run1_f2.setFeature_Id(f2.getId());
		    wf1_e1_run1_f2.setValue("file1beagle.map");
		    wf1_e1_run1_f2.setProtocolApplication(wf1_e1_run1);
		    wf1_e1_run1_f2.setTarget(panel);
		    wf1_e1_run1_f2.setTime(new Date());
		    
		    //ComputeValue wf1_e1_run1_f3 = new ComputeValue();
		    ObservedValue wf1_e1_run1_f3 = new ObservedValue();
		    wf1_e1_run1_f3.setFeature_Id(f3.getId());
		    wf1_e1_run1_f3.setValue("result1beagle.imputed");
		    wf1_e1_run1_f3.setProtocolApplication(wf1_e1_run1);
		    wf1_e1_run1_f3.setTarget(panel);
		    wf1_e1_run1_f3.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run1_f1 = new ComputeValue();
		    ObservedValue wf1_e2_run1_f1 = new ObservedValue();
		    wf1_e2_run1_f1.setFeature_Id(f1.getId());
		    wf1_e2_run1_f1.setValue("chr1");
		    wf1_e2_run1_f1.setProtocolApplication(wf1_e2_run1);
		    wf1_e2_run1_f1.setTarget(panel);
		    wf1_e2_run1_f1.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run1_f2 = new ComputeValue();
		    ObservedValue wf1_e2_run1_f2 = new ObservedValue();
		    wf1_e2_run1_f2.setFeature_Id(f2.getId());
		    wf1_e2_run1_f2.setValue("file1impute.ped");
		    wf1_e2_run1_f2.setProtocolApplication(wf1_e2_run1);
		    wf1_e2_run1_f2.setTarget(panel);
		    wf1_e2_run1_f2.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run1_f3 = new ComputeValue();
		    ObservedValue wf1_e2_run1_f3 = new ObservedValue();
		    wf1_e2_run1_f3.setFeature_Id(f3.getId());
		    wf1_e2_run1_f3.setValue("result1impute.imputed");
		    wf1_e2_run1_f3.setProtocolApplication(wf1_e2_run1);
		    wf1_e2_run1_f3.setTarget(panel);
		    wf1_e2_run1_f3.setTime(new Date());
		    
		    db.add(wf1_e1_run1_f1);
		    db.add(wf1_e1_run1_f2);
		    db.add(wf1_e1_run1_f3);
		    db.add(wf1_e2_run1_f1);
		    db.add(wf1_e2_run1_f2);
		    db.add(wf1_e2_run1_f3);
		    
		    //second run
			ComputeApplication wf1_e1_run2 = new ComputeApplication();
			wf1_e1_run2.setName("wf1_e1_run2");
			wf1_e1_run2.setProtocol(e2.getProtocol());
			wf1_e1_run2.setTime(new Date());
			wf1_e1_run2.setComputeScript("beagle chr2 file2beagle.map > result2beagle.imputed");
			db.add(wf1_e1_run2);
			
			ComputeApplication wf1_e2_run2 = new ComputeApplication();
			wf1_e2_run2.setName("wf1_e2_run2");
			wf1_e2_run2.setProtocol(e2.getProtocol());
		    wf1_e2_run2.setPrevSteps_Id(wf1_e1_run2.getId());
		    wf1_e2_run2.setTime(new Date());
		    wf1_e1_run1.setComputeScript("impute chr2 file2impute.map > result2impute.imputed");
		    db.add(wf1_e2_run2);
		    
		    //set the values
		    //ComputeValue wf1_e1_run2_f1 = new ComputeValue();
		    ObservedValue wf1_e1_run2_f1 = new ObservedValue();
		    wf1_e1_run2_f1.setFeature_Id(f1.getId());
		    wf1_e1_run2_f1.setValue("chr2");
		    wf1_e1_run2_f1.setProtocolApplication(wf1_e1_run2);
		    wf1_e1_run2_f1.setTarget(panel);
		    wf1_e1_run2_f1.setTime(new Date());
		    
		    //ComputeValue wf1_e1_run2_f2 = new ComputeValue();
		    ObservedValue wf1_e1_run2_f2 = new ObservedValue();
		    wf1_e1_run2_f2.setFeature_Id(f2.getId());
		    wf1_e1_run2_f2.setValue("file2beagle.map");
		    wf1_e1_run2_f2.setProtocolApplication(wf1_e1_run2);
		    wf1_e1_run2_f2.setTarget(panel);
		    wf1_e1_run2_f2.setTime(new Date());
		    
		    //ComputeValue wf1_e1_run2_f3 = new ComputeValue();
		    ObservedValue wf1_e1_run2_f3 = new ObservedValue();
		    wf1_e1_run2_f3.setFeature_Id(f3.getId());
		    wf1_e1_run2_f3.setValue("result2beagle.imputed");
		    wf1_e1_run2_f3.setProtocolApplication(wf1_e1_run2);
		    wf1_e1_run2_f3.setTarget(panel);
		    wf1_e1_run2_f3.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run2_f1 = new ComputeValue();
		    ObservedValue wf1_e2_run2_f1 = new ObservedValue();
		    wf1_e2_run2_f1.setFeature_Id(f1.getId());
		    wf1_e2_run2_f1.setValue("chr2");
		    wf1_e2_run2_f1.setProtocolApplication(wf1_e2_run2);
		    wf1_e2_run2_f1.setTarget(panel);
		    wf1_e2_run2_f1.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run2_f2 = new ComputeValue();
		    ObservedValue wf1_e2_run2_f2 = new ObservedValue();
		    wf1_e2_run2_f2.setFeature_Id(f2.getId());
		    wf1_e2_run2_f2.setValue("file2impute.map");
		    wf1_e2_run2_f2.setProtocolApplication(wf1_e2_run2);
		    wf1_e2_run2_f2.setTarget(panel);
		    wf1_e2_run2_f2.setTime(new Date());
		    
		    //ComputeValue wf1_e2_run2_f3 = new ComputeValue();
		    ObservedValue wf1_e2_run2_f3 = new ObservedValue();
		    wf1_e2_run2_f3.setFeature_Id(f2.getId());
		    wf1_e2_run2_f3.setValue("result2impute.imputed");
		    wf1_e2_run2_f3.setProtocolApplication(wf1_e2_run2);
		    wf1_e2_run2_f3.setTarget(panel);
		    wf1_e2_run2_f3.setTime(new Date());
		    
		    db.add(wf1_e1_run2_f1);
		    db.add(wf1_e1_run2_f2);
		    db.add(wf1_e1_run2_f3);
		    db.add(wf1_e2_run2_f1);
		    db.add(wf1_e2_run2_f2);
		    db.add(wf1_e2_run2_f3);
		    
		    //Discussion: do we want a way to reuse values????
		
			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
			e.printStackTrace();
		}
		

		
		
	}
}
