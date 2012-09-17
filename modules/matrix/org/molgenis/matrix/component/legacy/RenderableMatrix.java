package org.molgenis.matrix.component.legacy;

import java.util.List;

import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;


/**
 * Tells the Matrix browser what to render. We leave it up to the user to construct or manipulate RenderableMatrix.
 * 
 * @author erikroos, joerivandervelde
 *
 * @param <R>
 * @param <C>
 * @param <V>
 */
public interface RenderableMatrix<R, C, V> extends BasicMatrix<R, C, V>, SourceMatrix<R, C, V>{
	
	/**
	 * The list of filters that was applied to reach the currently rendered matrix.
	 */
	public List<MatrixQueryRule> getRules();
	
	/**
	 * The constraint logic that was used to parse the filters in a special way.
	 */
	public String getConstraintLogic();
	
	/**
	 * Show the stepsize on the screen. This is the number of cells, or 'speed' with
	 * which the matrix moves when move (panning) actions are performed.
	 */
	public int getStepSize();

	/**
	 * Name of the screen. Needed to send non-input type requests.
	 * @return
	 */
	public String getScreenName();
}
