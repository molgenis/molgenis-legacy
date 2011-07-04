package org.molgenis.matrix;

import java.io.OutputStream;


/**
 * For writing matrices to backends.
 */
public interface MatrixWriter
{
	/** Write any matrix to the outputstream 
	 * 
	 * @param matrix
	 * @param out outputstream
	 * @throws MatrixException 
	 */
	public void write(Matrix<?> m) throws MatrixException;
}
