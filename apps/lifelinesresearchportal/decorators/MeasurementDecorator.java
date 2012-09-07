/* Date:        November 16, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;

public class MeasurementDecorator<E extends Nameable> extends MapperDecorator<E>
{

	public MeasurementDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

}
