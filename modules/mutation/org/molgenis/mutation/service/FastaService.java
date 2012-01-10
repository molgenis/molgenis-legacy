package org.molgenis.mutation.service;

import java.io.Serializable;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.xgap.Gene;

public class FastaService implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Database db                        = null;
	private final int ROWSIZE                  = 60;
	
	public void setDatabase(Database db)
	{
		this.db = db;
	}
	
	public String exportGene() throws DatabaseException
	{
		StringBuilder b     = new StringBuilder();
		
		List<Gene> geneList = this.db.query(Gene.class).find();

		Gene gene           = geneList.get(0);

		b.append(">" + gene.getName() + "\n");
		
		for (int i = 0; i < gene.getSeq().length(); i += ROWSIZE)
		{
			int startPos = i;
			int endPos   = (i + ROWSIZE < gene.getSeq().length() ? i + ROWSIZE : gene.getSeq().length());
			b.append(gene.getSeq().substring(startPos, endPos) + "\n");
		}
		
		return b.toString();
	}
}
