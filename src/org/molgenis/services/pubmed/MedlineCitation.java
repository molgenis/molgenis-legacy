package org.molgenis.services.pubmed;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class MedlineCitation
{
	@XmlElement
	String PMID;

	@XmlElement(name = "Article")
	Article article;

	@XmlElementWrapper(name="MeshHeadingList")
	@XmlElement(name="MeshHeading")
	List<MeshHeading> MeshHeadings = new ArrayList<MeshHeading>();
	
	List<Author> authors = new ArrayList<Author>();

	public String toString()
	{
		String result = "";
		result += "pmid="+PMID;
		if(article != null)
		{
			result += ", title="+article.ArticleTitle;
			
			if(article.Journal != null)
			{
				result += ", journal="+article.Journal.Title;
				if(article.Journal.JournalIssue != null)
				{
					result += ", volume="+article.Journal.JournalIssue.Volume;
					result += ", issue="+article.Journal.JournalIssue.Issue;
					
					if(article.Journal.JournalIssue.PubDate != null)
					{
						result += ", year="+article.Journal.JournalIssue.PubDate.Year;
						result += ", month="+article.Journal.JournalIssue.PubDate.Month;
					}
				}
				
			}
			for(Author au: article.Authors)
			{
				result+="\n"+au.toString();
			}
			if(article.Abstract != null)
			{
				result +="\nabstract="+article.Abstract.AbstractText;
			}
		}
		for(MeshHeading mesh: this.MeshHeadings)
		{
			result+="\nmesh="+mesh.DescriptorName;
		}
		
		return result;
	}
}
