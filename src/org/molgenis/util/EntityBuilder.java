package org.molgenis.util;

public class EntityBuilder<E extends Entity>
{
	protected E object;
	
	public EntityBuilder(E object)
	{
		this.object = object;
	}
}
