package org.molgenis.pheno.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.dto.IndividualDTO;
import org.molgenis.pheno.dto.FeatureDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolApplicationDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhenoService
{
	private Database db;
//	private EntityManager em;
	
	@Autowired
	public PhenoService(Database db)
	{
		this.db = db;
//		this.em = db.getEntityManager();
	}
	
	/**
	 * Find phenotypic details (ObservedValue) for an Individual
	 * @param id
	 * @return IndividualDetailsDTO
	 * @throws PhenoServiceException
	 */
	public IndividualDTO findPhenotypeDetails(final Integer id) throws PhenoServiceException
	{
		try
		{
			IndividualDTO individualDetailsDTO = new IndividualDTO();
			individualDetailsDTO.setIndividualId(id);
			individualDetailsDTO.setObservedValues(new HashMap<String, HashMap<String, List<ObservedValueDTO>>>());

			List<Protocol> protocolList        = this.db.query(Protocol.class).find();
			individualDetailsDTO.setProtocolList(this.protocolListToProtocolDTOList(protocolList));

			for (Protocol protocol : protocolList)
			{
				if (CollectionUtils.isEmpty(protocol.getFeatures_Id()))
					continue;

				List<ObservedValue> observedValueList = this.db.query(ObservedValue.class).equals(ObservedValue.TARGET, id).in(ObservedValue.FEATURE, protocol.getFeatures_Id()).find();
				
				if (observedValueList.size() == 0)
					continue;

				String protocolKey = "Protocol" + protocol.getId();

				if (!individualDetailsDTO.getObservedValues().containsKey(protocolKey))
				{
					individualDetailsDTO.getObservedValues().put(protocolKey, new HashMap<String, List<ObservedValueDTO>>());
				}

				for (ObservedValue observedValue : observedValueList)
				{
					String protocolApplicationKey = "ProtocolApplication" + observedValue.getProtocolApplication_Id();

					if (!individualDetailsDTO.getObservedValues().get(protocolKey).containsKey(protocolApplicationKey))
					{
						individualDetailsDTO.getObservedValues().get(protocolKey).put(protocolApplicationKey, new ArrayList<ObservedValueDTO>());
					}
					individualDetailsDTO.getObservedValues().get(protocolKey).get(protocolApplicationKey).add(this.observedValueToObservedValueDTO(observedValue));
				}
			}

//			List<ObservedValue> observedValueList = this.db.query(ObservedValue.class).equals(ObservedValue.TARGET, id).sortASC(ObservedValue.PROTOCOLAPPLICATION).find();
//			individualDetailsDTO.setObservedValueDTOList(this.observedValueListToObservedValueDTOList(observedValueList));
//			for (ObservedValue observedValue : observedValueList)
//			{
//				String key = "Protocol" + observedValue.getProtocol_Id();
//				if (!individualDetailsDTO.getObservedValues().containsKey(key))
//				{
//					individualDetailsDTO.getObservedValues().put(key, new ArrayList<ObservedValueDTO>());
//				}
//				individualDetailsDTO.getObservedValues().get(key).add(observedValueToObservedValueDTO(observedValue));
//			}

			return individualDetailsDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Find a protocol by primary key
	 * @param id
	 * @return protocol
	 */
	public ProtocolDTO findProtocol(Integer id)
	{
		try
		{
			Protocol protocol = this.db.findById(Protocol.class, id);
			
			if (protocol == null)
				throw new PhenoServiceException("No protocol found for " + id);

			return this.protocolToProtocolDTO(protocol);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Find all protocols
	 * @return list of protocols
	 */
	public List<ProtocolDTO> findProtocols()
	{
		try
		{
			return this.protocolListToProtocolDTOList(this.db.query(Protocol.class).sortASC(Protocol.NAME).find());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * find an ObservedValue
	 * @param id
	 * @return ObservedValueDTO
	 */
	public ObservedValueDTO findObservedValue(final Integer id)
	{
		try
		{
			return this.observedValueToObservedValueDTO(this.db.findById(ObservedValue.class, id));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Get all distinct values of a given ObservableFeature
	 * @return values
	 */
	public List<String> findObservedValues(final String featureName)
	{
		try
		{
			String sql = "SELECT DISTINCT ov.value FROM ObservedValue ov JOIN ov.feature f WHERE f.name = :feature OR f.description = :feature ORDER BY ov.value";
			TypedQuery<String> query = this.db.getEntityManager().createQuery(sql, String.class);
			query.setParameter("feature", featureName);
			return query.getResultList();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Insert a list of ObservedValueDTOs
	 * @param observedValueDTOList
	 * @return number of inserted ObservedValue's
	 */
	public int insert(List<ObservedValueDTO> observedValueDTOList)
	{
		try
		{
			return this.db.add(this.observedValueDTOListToObservedValueList(observedValueDTOList));
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}
	
	/**
	 * Insert an ObservedValueDTO
	 * @param observedValueDTO
	 */
	public void insert(ObservedValueDTO observedValueDTO)
	{
		List<ObservedValueDTO> observedValueDTOList = new ArrayList<ObservedValueDTO>();
		observedValueDTOList.add(observedValueDTO);
		this.insert(observedValueDTOList);
	}

	/**
	 * Insert a new ProtocolApplication
	 * @param name
	 * @param time
	 * @return primary key of the new ProtocolApplication
	 */
	public Integer insert(ProtocolApplicationDTO paDTO)
	{
		try
		{
			ProtocolApplication pa = this.protocolApplicationDTOToProtocolApplication(paDTO);
			this.db.add(pa);

			return pa.getId();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Update a list of ObservedValueDTOs
	 * @param observedValueDTOList
	 */
	public void update(List<ObservedValueDTO> observedValueDTOList)
	{
		try
		{
			this.db.update(this.observedValueDTOListToObservedValueList(observedValueDTOList));
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			throw new PhenoServiceException(e.getMessage());
		}
	}

	/**
	 * Update an ObservedValueDTO
	 * @param observedValueDTO
	 */
	public void update(ObservedValueDTO observedValueDTO)
	{
		List<ObservedValueDTO> observedValueDTOList = new ArrayList<ObservedValueDTO>();
		observedValueDTOList.add(observedValueDTO);
		this.update(observedValueDTOList);
	}

	public List<ProtocolDTO> protocolListToProtocolDTOList(List<Protocol> protocolList)
	{
		List<ProtocolDTO> protocolDTOList = new ArrayList<ProtocolDTO>();

		for (Protocol protocol : protocolList)
		{
			protocolDTOList.add(this.protocolToProtocolDTO(protocol));
		}
		return protocolDTOList;
	}

	public ProtocolDTO protocolToProtocolDTO(Protocol protocol)
	{
		ProtocolDTO protocolDTO = new ProtocolDTO();
		
		protocolDTO.setProtocolId(protocol.getId());
		protocolDTO.setProtocolKey("Protocol" + protocol.getId());
		protocolDTO.setProtocolName(protocol.getName());

		protocolDTO.setFeatureDTOList(new ArrayList<FeatureDTO>());

		try
		{
			List<Measurement> measurementList = this.db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id()).find();

			for (Measurement measurement : measurementList)
			{
				FeatureDTO featureDTO = new FeatureDTO();
				featureDTO.setFeatureId(measurement.getId());
//				featureDTO.setFeatureKey(protocolDTO.getProtocolKey() + ".Feature" + measurement.getId());
				featureDTO.setFeatureKey("Feature" + measurement.getId());
				featureDTO.setFeatureName(measurement.getName());
				featureDTO.setFeatureType(measurement.getDataType());
				
				protocolDTO.getFeatureDTOList().add(featureDTO);
			}
		}
		catch (DatabaseException e)
		{
			throw new PhenoServiceException("No measurements available for protocol");
		}

		return protocolDTO;
	}

	public ProtocolApplication protocolApplicationDTOToProtocolApplication(ProtocolApplicationDTO paDTO)
	{
		ProtocolApplication pa = new ProtocolApplication();
		pa.setName(paDTO.getName());
		pa.setTime(paDTO.getTime());
		pa.setPerformer_Id(paDTO.getPerformerIdList());
		pa.setProtocol_Id(paDTO.getProtocolId());
		return pa;
	}

	public List<ObservedValueDTO> observedValueListToObservedValueDTOList(List<ObservedValue> observedValueList) throws DatabaseException
	{
		List<ObservedValueDTO> observedValueDTOList = new ArrayList<ObservedValueDTO>();

		for (ObservedValue observedValue : observedValueList)
		{
			ObservedValueDTO observedValueDTO = this.observedValueToObservedValueDTO(observedValue);
			observedValueDTOList.add(observedValueDTO);
		}

		return observedValueDTOList;
	}

	public ObservedValueDTO observedValueToObservedValueDTO(ObservedValue observedValue) throws DatabaseException
	{
		ObservedValueDTO observedValueDTO = new ObservedValueDTO();
		observedValueDTO.setObservedValueId(observedValue.getId());
		
		if (observedValue.getProtocolApplication_Id() != null)
		{
			ProtocolApplication protocolApplication = this.db.findById(ProtocolApplication.class, observedValue.getProtocolApplication_Id());
			observedValueDTO.setProtocolApplicationId(protocolApplication.getId());
			observedValueDTO.setProtocolApplicationName(protocolApplication.getName());
			observedValueDTO.setProtocolApplicationTime(protocolApplication.getTime());
			observedValueDTO.setPerformerNameList(protocolApplication.getPerformer_Name());
			observedValueDTO.setProtocolId(protocolApplication.getProtocol_Id());
		}

		observedValueDTO.setTargetId(observedValue.getTarget_Id());
		observedValueDTO.setTargetName(observedValue.getTarget_Name());
		observedValueDTO.setValue(ObjectUtils.toString(observedValue.getValue(), "null"));
		
		Measurement measurement = this.db.findById(Measurement.class, observedValue.getFeature_Id());
		FeatureDTO featureDTO = new FeatureDTO();
		featureDTO.setFeatureId(measurement.getId());
		featureDTO.setFeatureKey("Feature" + measurement.getId());
		featureDTO.setFeatureName(measurement.getName());
		featureDTO.setFeatureType(measurement.getDataType());
		observedValueDTO.setFeatureDTO(featureDTO);
		return observedValueDTO;
	}

	public List<ObservedValue> observedValueDTOListToObservedValueList(List<ObservedValueDTO> observedValueDTOList)
	{
		List<ObservedValue> observedValueList = new ArrayList<ObservedValue>();

		for (ObservedValueDTO observedValueDTO : observedValueDTOList)
			observedValueList.add(this.observedValueDTOToObservedValue(observedValueDTO));

		return observedValueList;
	}

	public ObservedValue observedValueDTOToObservedValue(ObservedValueDTO observedValueDTO)
	{
		ObservedValue observedValue = new ObservedValue();
		
		if (observedValueDTO.getFeatureDTO() != null && observedValueDTO.getFeatureDTO().getFeatureId() != null)
			observedValue.setFeature_Id(observedValueDTO.getFeatureDTO().getFeatureId());
		if (observedValueDTO.getObservedValueId() != null)
			observedValue.setId(observedValueDTO.getObservedValueId());
		if (observedValueDTO.getProtocolApplicationId() != null)
			observedValue.setProtocolApplication_Id(observedValueDTO.getProtocolApplicationId());
		if (observedValueDTO.getTargetId() != null)
			observedValue.setTarget_Id(observedValueDTO.getTargetId());
		if (StringUtils.isNotEmpty(observedValueDTO.getValue()))
			observedValue.setValue(observedValueDTO.getValue());

		return observedValue;
	}
}
