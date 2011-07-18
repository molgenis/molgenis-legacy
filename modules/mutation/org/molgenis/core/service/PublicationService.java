package org.molgenis.core.service;

import java.text.ParseException;
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
	private PublicationService(Database db)
	{
		this.db = db;
	}
	
	public static PublicationService getInstance(Database db)
	{
		if (publicationService == null)
			publicationService = new PublicationService(db);
		
		return publicationService;
	}

	public List<PublicationVO> getAll() throws DatabaseException, ParseException
	{
		return this.toPublicationVOList(this.db.query(Publication.class).sortASC(Publication.NAME).find());
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
		publicationVO.setName(publication.getName());
		publicationVO.setPubmed(PublicationService.PUBMED_URL + publication.getPubmedID_Name());
		publicationVO.setStatus(publication.getStatus_Name());
		publicationVO.setTitle(publication.getName());
		
		return publicationVO;
	}
}
