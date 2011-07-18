/* Date:        November 16, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.jdbc.JDBCMapper;

public class NameableDecorator<E extends Nameable> extends decorators.NameableDecorator<E>
{
	public NameableDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}

