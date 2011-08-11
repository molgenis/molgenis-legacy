package org.molgenis.matrix.component;

import org.molgenis.framework.db.QueryRule;

public interface SliceableMatrix {
	
	public RenderableMatrix getSubMatrixByOffset(RenderableMatrix matrix, int rowStart, int rowLength, int colStart, int colLength) throws Exception;
	
	public RenderableMatrix getSubMatrixByRowValueFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	public RenderableMatrix getSubMatrixByRowHeaderFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	public RenderableMatrix getSubMatrixByColValueFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	public RenderableMatrix getSubMatrixByColHeaderFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
}
