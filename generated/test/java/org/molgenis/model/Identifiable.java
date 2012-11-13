
/* File:        org.molgenis/model/Identifiable.java
 * Generator:   org.molgenis.generators.DataTypeGen 4.0.0-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package org.molgenis.model;

/**
 * Identifiable: .
 * @author MOLGENIS generator
 */
public interface Identifiable extends  org.molgenis.model.Autoid
{
	@Override
	public Integer getId();
	@Override
	public void setId(Integer id);
	public String getIdentifier();
	public void setIdentifier(String identifier);
	public String getName();
	public void setName(String name);
}

