/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.molgenis.framework.db.lifewise;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jorislops
 */
@XmlRootElement
public class QueryResult {
    private int count;

    public QueryResult() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}