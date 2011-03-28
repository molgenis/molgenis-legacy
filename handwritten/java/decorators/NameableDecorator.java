/* Date:        November 16, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.util.List;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.db.jdbc.MappingDecorator;

public class NameableDecorator<E extends Nameable> extends MappingDecorator<E>
{
	//JDBCMapper is the generate thing
	//TODO: Danny Parameterize the JDBCMapper object <Object> ??
	public NameableDecorator(JDBCMapper generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		NameConvention.validateEntityNames(entities);
		
		int count = super.add(entities);

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		NameConvention.validateEntityNames(entities);
		
		int count = super.update(entities);

		return count;
	}
	
}

