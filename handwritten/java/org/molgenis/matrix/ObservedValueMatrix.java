package org.molgenis.matrix;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.pheno.ObservationElement;

/** Try 2: can we make the ObservedValue space leading?
 * Basically, all ObservedValue in the database are candidate for this matrix.
 * Only by binding Feature, Target or ProtocolApplication this set is reduced.\
 * These limits can be either by query criteria and/or object ids.
 */
public class ObservedValueMatrix
{
	private Class<? extends ObservationElement> featureClass = ObservationElement.class;
	private Class<? extends ObservationElement> targetClass = ObservationElement.class;
	
	/** Filter and sort to select features*/
	private QueryRule featureRules;
	/** Filter and sort to select targets*/
	private QueryRule targetRules;
	/** Filter and sort to select protocolapplications*/
	private QueryRule protocolappRules;
	
	public ObservedValueMatrix(Database db, Class<? extends ObservationElement> targetClass, Class<? extends ObservationElement> featureClass)
	{
		this.targetClass = targetClass;
		this.featureClass = featureClass;
	}
	
	
	
	
}
