/* Date:        November 1, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.jdbc.JDBCMapper;

public class MolgenisFileDecorator<E extends MolgenisFile> extends decorators.MolgenisFileDecorator<E>
{
	public MolgenisFileDecorator(JDBCMapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}
