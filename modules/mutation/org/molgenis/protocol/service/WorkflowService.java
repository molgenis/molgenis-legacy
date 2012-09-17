package org.molgenis.protocol.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

public class WorkflowService implements Serializable
{
	private static final long serialVersionUID = -262659898183648343L;
	private Database db                        = null;

	public void setDatabase(Database db)
	{
		this.db = db;
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
		List<WorkflowElement> result = new ArrayList<WorkflowElement>();

		List<Integer> weIds          = new ArrayList<Integer>();
		List<WorkflowElement> elements;

		String sql                   = "SELECT DISTINCT we.id FROM WorkflowElement we JOIN WorkflowElement_Workflow wew ON (we.id = wew.WorkflowElement) LEFT JOIN WorkflowElement_PreviousSteps wep ON (we.id = wep.WorkflowElement) WHERE wep.WorkflowElement IS NULL AND wew.Workflow = " + workflow.getId();

		if (this.db instanceof JDBCDatabase)
		{
			List<Tuple> ids = ((JDBCDatabase) this.db).sql(sql);
			
			for (Tuple entry : ids)
				weIds.add(entry.getInt(0));
			
		}
		else if (this.db instanceof JpaDatabase)
		{
			weIds = this.db.getEntityManager().createNativeQuery(sql).getResultList();
		}
		else
			throw new UnsupportedOperationException("Unsupported database mapper");

		elements = this.db.query(WorkflowElement.class).in(WorkflowElement.ID, weIds).find();

		while (CollectionUtils.isNotEmpty(elements))
		{
			result.addAll(elements);
			elements = this.findNextWorkflowElements(elements);
		}

		return result;
	}

	/**
	 * Helper to select the next following WorkflowElements, i.e. where the previous steps are the current ones
	 * @param elements
	 * @return List of next WorkflowElements
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	private List<WorkflowElement> findNextWorkflowElements(List<WorkflowElement> elements) throws DatabaseException, ParseException
	{
		List<WorkflowElement> nextElements = new ArrayList<WorkflowElement>();

		for (WorkflowElement element : elements)
		{
			List<Integer> weIds = new ArrayList<Integer>();

			String sql          = "SELECT WorkflowElement FROM WorkflowElement_PreviousSteps WHERE PreviousSteps = " + element.getId();

			if (this.db instanceof JDBCDatabase)
			{
				List<Tuple> ids = ((JDBCDatabase) this.db).sql(sql);
				
				for (Tuple entry : ids)
					weIds.add(entry.getInt(0));
			}
			else if (this.db instanceof JpaDatabase)
			{
				weIds = this.db.getEntityManager().createNativeQuery(sql).getResultList();
			}
			else
				throw new UnsupportedOperationException("Unsupported database mapper");

			if (weIds.size() > 0)
				nextElements.addAll(this.db.query(WorkflowElement.class).in(WorkflowElement.ID, weIds).find());
		}
		return nextElements;
	}
}
