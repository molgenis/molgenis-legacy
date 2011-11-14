/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.matrix.component.sqlbackend;

import java.util.List;
import org.molgenis.matrix.component.general.MatrixQueryRule;

/**
 *
 * @author jorislops
 */
public interface Backend {

    String createQuery(boolean count, List<MatrixQueryRule> rules) throws Exception;
    
}
