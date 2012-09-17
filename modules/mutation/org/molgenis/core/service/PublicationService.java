package org.molgenis.core.service;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.core.Publication;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class PublicationService
{
	private Database db                                  = null;
	private static PublicationService publicationService = null;

	public static final String PUBMED_URL                = "http://www.ncbi.nlm.nih.gov/pubmed/";

	// private constructor, use singleton instance
	public PublicationService(Database db)
	{
		this.db = db;
	}
	
	public static PublicationService getInstance(Database db)
	{
		//if (publicationService == null)
		publicationService = new PublicationService(db);
		
		return publicationService;
	}

	public List<PublicationVO> getAll() throws DatabaseException
	{
		return this.toPublicationVOList(this.db.query(Publication.class).sortASC(Publication.TITLE).find());
	}
	
	public void insert(PublicationVO publicationVO) throws DatabaseException
	{
		Publication publication = this.toPublication(publicationVO);
		this.db.add(publication);
	}

	private List<PublicationVO> toPublicationVOList(List<Publication> publications)
	{
		List<PublicationVO> result = new ArrayList<PublicationVO>();

		for (Publication publication : publications)
			result.add(this.toPublicationVO(publication));
		
		return result;
	}
	
	private PublicationVO toPublicationVO(Publication publication)
	{
		PublicationVO publicationVO = new PublicationVO();
		publicationVO.setAuthors(publication.getAuthorList());
		publicationVO.setName(publication.getName());
		publicationVO.setPubmedId(publication.getPubmedID_Name());
		publicationVO.setPubmedUrl(PublicationService.PUBMED_URL + publication.getPubmedID_Name());
		publicationVO.setStatus(publication.getStatus_Name());
		publicationVO.setTitle(publication.getTitle());
		
		return publicationVO;
	}
	
	private Publication toPublication(PublicationVO publicationVO)
	{
		Publication publication = new Publication();
		publication.setAuthorList(publicationVO.getAuthors());
		publication.setName(publicationVO.getName());
		publication.setPubmedID_Name(publicationVO.getPubmedId());
		publication.setTitle(publicationVO.getTitle());
		
		return publication;
	}
}
