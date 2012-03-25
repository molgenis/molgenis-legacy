package org.molgenis.mutation.service;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FastaService
{
	private Database db;
	private final int ROWSIZE = 60;
	
	@Autowired
	public FastaService(Database db)
	{
		this.db = db;
	}
	
	public String exportGene() throws DatabaseException
	{
//		StringBuilder b     = new StringBuilder();
//		
//		List<Gene> geneList = this.db.query(Gene.class).find();
//
//		Gene gene           = geneList.get(0);
//
//		b.append(">" + gene.getName() + "\n");
//		
//		for (int i = 0; i < gene.getNuclSequence().length(); i += ROWSIZE)
//		{
//			int startPos = i;
//			int endPos   = (i + ROWSIZE < gene.getNuclSequence().length() ? i + ROWSIZE : gene.getNuclSequence().length());
//			b.append(gene.getNuclSequence().substring(startPos, endPos) + "\n");
//		}
//		
//		return b.toString();
		return "";
	}
}
