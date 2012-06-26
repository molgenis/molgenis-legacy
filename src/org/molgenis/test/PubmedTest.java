package org.molgenis.test;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.molgenis.services.PubmedService;
import org.molgenis.services.pubmed.PubmedArticle;

public class PubmedTest
{
	@Test
	public void test1()
	{
		PubmedService s = new PubmedService();
		
		List<Integer> ids = Arrays.asList(new Integer[]{21681854});
		
		try
		{
			List<PubmedArticle> result = s.getPubmedArticlesForIds(ids);
			for(PubmedArticle r: result)
			{
				System.out.println(r.MedlineCitation.article.ArticleTitle);
			}
			
			
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
