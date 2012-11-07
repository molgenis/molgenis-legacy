
/* File:        org.molgenis/model/Identifiable.java
 * Copyright:   GBIC 2000-2012, all rights reserved
 * Date:        November 6, 2012
 * Generator:   org.molgenis.generators.DataTypeGen 4.0.0-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package org.molgenis.test;

/**
 * Identifiable: .
 * @version November 6, 2012 
 * @author MOLGENIS generator
 */
public interface Identifiable extends  org.molgenis.test.Autoid
{
	public Integer getId();
	public void setId(Integer id);
	public String getIdentifier();
	public void setIdentifier(String identifier);
	public String getName();
	public void setName(String name);
}

