/* Date:        February 15, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.framework.db.jdbc.JDBCMapper;

public class DataDecorator<E extends org.molgenis.data.Data> extends decorators.DataDecorator<E>
{
	public DataDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}
