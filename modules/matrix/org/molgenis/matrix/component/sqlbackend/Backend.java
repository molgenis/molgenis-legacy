package org.molgenis.matrix.component.sqlbackend;

import java.util.List;
import org.molgenis.matrix.component.general.MatrixQueryRule;

/**
 *
 * @author jorislops
 */
public interface Backend {
    public String createQuery(boolean count, List<MatrixQueryRule> rules);    
}
