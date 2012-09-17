package org.molgenis.gridhandler;

import org.molgenis.util.Tuple;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 03/05/2012
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class CommandLineGridHandler implements GridHandler
{
    protected List<Tuple> worksheet = null;

    public void setWorksheet(List<Tuple> worksheet)
    {
        this.worksheet = worksheet;
    }
}
