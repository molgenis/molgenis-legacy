package org.molgenis.protocol.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;

public class WorkflowService implements Serializable
{
	private static final long serialVersionUID     = -262659898183648343L;
	private Database db                            = null;
	private static WorkflowService workflowService = null;

	// private constructor, use singleton instance
	private WorkflowService(Database db)
	{
		this.db = db;
	}

	public static WorkflowService getInstance(Database db)
	{
		if (workflowService == null)
			workflowService = new WorkflowService(db);

		return workflowService;
	}
	
	/**
	 * Find workflow elements in the order they have been specified inside a workflow
	 * @param workflow
	 * @return workflow elements
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<WorkflowElement> findWorkflowElements(Workflow workflow) throws DatabaseException, ParseException
	{
		List<WorkflowElement> result   = new ArrayList<WorkflowElement>();

		List<WorkflowElement> elements = this.db.query(WorkflowElement.class).equals(WorkflowElement.WORKFLOW, workflow.getId()).find();
		
		// TODO: How to query for IS NULL???
		List<WorkflowElement> remove = new ArrayList<WorkflowElement>();
		
		for (WorkflowElement element : elements)
			if (CollectionUtils.isNotEmpty(element.getPreviousSteps_Id()))
				remove.add(element);
		
		elements.removeAll(remove);

		while (CollectionUtils.isNotEmpty(elements))
		{
			result.addAll(elements);
			elements = this.findNextWorkflowElements(elements);
		}

		return result;
	}

	private List<WorkflowElement> findNextWorkflowElements(List<WorkflowElement> elements) throws DatabaseException, ParseException
	{
		List<WorkflowElement> nextElements = new ArrayList<WorkflowElement>();

		for (WorkflowElement element : elements)
		{
			nextElements.addAll(this.db.query(WorkflowElement.class).equals(WorkflowElement.PREVIOUSSTEPS, element.getId()).find());
		}
		return nextElements;
	}
}
