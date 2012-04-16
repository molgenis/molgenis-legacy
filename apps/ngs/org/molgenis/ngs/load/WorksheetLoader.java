package org.molgenis.ngs.load;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleReader;

public class WorksheetLoader
{
	public List<Project> loadProjects(TupleReader reader)
	{
		//assume 'project' only for now
		Map<String,Project> projectMap = new LinkedHashMap<String,Project>();
		
		for(Tuple t: reader)
		{
			
		}
		
		
		return new ArrayList<Project>(projectMap.values());
	}
}
