/* Date:        July 28, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.2-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.pheno.Measurement;

public class MeasurementDecorator<E extends Measurement> extends MapperDecorator<E> {

	public MeasurementDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

}