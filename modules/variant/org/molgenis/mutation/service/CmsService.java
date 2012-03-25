package org.molgenis.mutation.service;

import java.util.List;

import org.molgenis.cms.Paragraph;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmsService
{
	private Database db;

	@Autowired
	public CmsService(Database db)
	{
		this.db = db;
	}

	public String findContentByName(String name) throws DatabaseException
	{
		List<Paragraph> paragraphList = this.db.query(Paragraph.class).equals(Paragraph.NAME, name).find();
		
		if (paragraphList.size() == 1)
			return paragraphList.get(0).getContent();
		else
			return "Add your content here";
	}
}
