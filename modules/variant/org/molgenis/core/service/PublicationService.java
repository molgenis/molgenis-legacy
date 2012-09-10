package org.molgenis.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.Publication;
import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.services.PubmedService;
import org.molgenis.services.pubmed.Author;
import org.molgenis.services.pubmed.PubmedArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicationService
{
	private Database db;
	private EntityManager em;

	private final int BATCH_SIZE = 50;
	public static final String PUBMED_URL = "http://www.ncbi.nlm.nih.gov/pubmed/";

	@Autowired
	public PublicationService(final Database db)
	{
		this.db = db;
		this.em = db.getEntityManager();
	}

	public List<PublicationDTO> getAll() throws DatabaseException
	{
		return this.publicationListToPublicationDTOList(this.db.query(Publication.class).sortASC(Publication.TITLE).find());
	}

	public int insert(final List<PublicationDTO> publicationDTOList)
	{
		try
		{
			int count = 0;

			for (PublicationDTO publicationDTO : publicationDTOList)
			{
				Publication publication = this.publicationDTOToPublication(publicationDTO);
				this.em.persist(publication.getPubmedID());
				this.em.persist(publication);
				count += 2;
				
				if (count % BATCH_SIZE == 0)
				{
					this.em.flush();
					this.em.clear();
				}
			}
			
			this.em.flush();
			this.em.clear();

			return count;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			throw new PublicationServiceException("Not a valid number: " + e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PublicationServiceException(e.getMessage());
		}
	}

	public void insert(final PublicationDTO publicationDTO)
	{
		List<PublicationDTO> publicationDTOList = new ArrayList<PublicationDTO>();
		
		publicationDTOList.add(publicationDTO);
		
		this.insert(publicationDTOList);
	}

	public List<PublicationDTO> publicationListToPublicationDTOList(final List<Publication> publications)
	{
		List<PublicationDTO> result = new ArrayList<PublicationDTO>();

		for (Publication publication : publications)
			result.add(this.publicationToPublicationDTO(publication));
		
		return result;
	}
	
	public PublicationDTO publicationToPublicationDTO(final Publication publication)
	{
		PublicationDTO publicationDTO = new PublicationDTO();
		publicationDTO.setId(publication.getId());
		publicationDTO.setAuthors(publication.getAuthorList());
		publicationDTO.setFirstAuthor(StringUtils.split(publication.getAuthorList(), ",")[0]);
		publicationDTO.setJournal(publication.getJournal());
		publicationDTO.setName(publication.getName());
		publicationDTO.setPubmedId(publication.getName());
		publicationDTO.setPubmedUrl(PublicationService.PUBMED_URL + publicationDTO.getPubmedId());
//		if (publication.getPubmedID_Id() != null)
//		{
//			try
//			{
//				OntologyTerm pubmedId = this.db.findById(OntologyTerm.class, publication.getPubmedID_Id());
//				publicationVO.setPubmedId(pubmedId.getName());
//				publicationVO.setPubmedUrl(PublicationService.PUBMED_URL + pubmedId.getName());
//			}
//			catch (DatabaseException e)
//			{
//				publicationVO.setPubmedId("NA");
//			}
//		}
		publicationDTO.setStatus(publication.getStatus_Name());
		publicationDTO.setTitle(publication.getTitle());
		publicationDTO.setYear(publication.getYear());
		
		return publicationDTO;
	}
	
	public Publication publicationDTOToPublication(final PublicationDTO publicationDTO)
	{
		Publication publication = new Publication();
		publication.setAuthorList(publicationDTO.getAuthors());
		publication.setJournal(publicationDTO.getJournal());
		publication.setName(publicationDTO.getName());
		publication.setTitle(publicationDTO.getTitle());
		publication.setYear(publicationDTO.getYear());
		
		OntologyTerm ontologyTerm = new OntologyTerm();
		ontologyTerm.setName(publicationDTO.getPubmedId());
		publication.setPubmedID(ontologyTerm);

		return publication;
	}
	
	public PublicationDTO pubmedArticleToPublicationDTO(final PubmedArticle pubmedArticle)
	{
		List<Author> authorList = pubmedArticle.MedlineCitation.article.Authors;
		List<String> authors    = new ArrayList<String>();
		for (Author author : authorList)
			authors.add(author.toInitials());

		PublicationDTO publicationDTO = new PublicationDTO();
		publicationDTO.setAuthors(StringUtils.join(authors, "; "));
		publicationDTO.setName(pubmedArticle.MedlineCitation.PMID);
		publicationDTO.setPubmedId(pubmedArticle.MedlineCitation.PMID);
		publicationDTO.setTitle(pubmedArticle.MedlineCitation.article.ArticleTitle);
		publicationDTO.setJournal(pubmedArticle.MedlineCitation.article.Journal.Title);
		publicationDTO.setYear(pubmedArticle.MedlineCitation.article.Journal.JournalIssue.PubDate.Year);
		if (publicationDTO.getYear() == null)
			publicationDTO.setYear("");
		
		return publicationDTO;
	}
	
	public Publication pubmedArticleToPublication(final PubmedArticle pubmedArticle)
	{
		List<Author> authorList = pubmedArticle.MedlineCitation.article.Authors;
		List<String> authors    = new ArrayList<String>();
		for (Author author : authorList)
			authors.add(author.toInitials());

		Publication publication = new Publication();
		publication.setAuthorList(StringUtils.join(authors, "; "));
		publication.setName(pubmedArticle.MedlineCitation.PMID);
		publication.setTitle(pubmedArticle.MedlineCitation.article.ArticleTitle);
		publication.setJournal(pubmedArticle.MedlineCitation.article.Journal.Title);
		publication.setYear(pubmedArticle.MedlineCitation.article.Journal.JournalIssue.PubDate.Year);
		if (publication.getYear() == null)
			publication.setYear("");
		
//		OntologyTerm ontologyTerm = new OntologyTerm();
//		ontologyTerm.setName(pubmedArticle.MedlineCitation.PMID);
//		publication.setPubmedID(ontologyTerm);

		return publication;
	}

	public List<PublicationDTO> pubmedIdListToPublicationDTOList(final List<String> pubmedStringList)
	{
		try
		{
			PubmedService pubmedService = new PubmedService();
			List<Integer> pubmedIdList  = new ArrayList<Integer>();
	
			for (String pubmed : pubmedStringList)
				pubmedIdList.add(Integer.parseInt(pubmed));
	
			List<PubmedArticle> pubmedArticles      = pubmedService.getPubmedArticlesForIds(pubmedIdList);
	
			List<PublicationDTO> publicationDTOList = new ArrayList<PublicationDTO>();
	
			for (PubmedArticle pubmedArticle : pubmedArticles)
			{
				PublicationDTO publicationDTO = this.pubmedArticleToPublicationDTO(pubmedArticle);
				publicationDTOList.add(publicationDTO);
			}

			return publicationDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PublicationServiceException(e.getMessage());
		}
	}

	public List<Publication> pubmedIdListToPublicationList(final List<String> pubmedStringList)
	{
		try
		{
			PubmedService pubmedService = new PubmedService();
			List<Integer> pubmedIdList  = new ArrayList<Integer>();
	
			for (String pubmed : pubmedStringList)
			{
				if (StringUtils.isEmpty(pubmed))
					continue;
				pubmedIdList.add(Integer.parseInt(pubmed));
			}

			List<PubmedArticle> pubmedArticles = pubmedService.getPubmedArticlesForIds(pubmedIdList);
	
			List<Publication> publicationList  = new ArrayList<Publication>();
	
			for (PubmedArticle pubmedArticle : pubmedArticles)
			{
				Publication publication = this.pubmedArticleToPublication(pubmedArticle);
				publicationList.add(publication);
			}

			return publicationList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PublicationServiceException(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	public List<Publication> pubmedIdListToPublicationListLocal(final List<String> pubmedStringList)
	{
		try
		{
			List<Integer> pubmedIdList  = new ArrayList<Integer>();
	
			for (String pubmed : pubmedStringList)
			{
				if (StringUtils.isEmpty(pubmed))
					continue;
				pubmedIdList.add(Integer.parseInt(pubmed));
			}

			List<Publication> publicationList  = new ArrayList<Publication>();

			for (Integer pubmedId : pubmedIdList)
			{
				Publication publication = new Publication();
				publication.setName(pubmedId.toString());
				publication.setTitle("The Pubmed article formerly known as " + pubmedId);
				
				publicationList.add(publication);
			}

			return publicationList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PublicationServiceException(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
}
