/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.molgenis.framework.db.lifewise;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SendList")
@XmlAccessorType(XmlAccessType.FIELD)
public class SendList<E> {
    
    @XmlElement(name="elem")
    private List<E> list;

    public SendList() {
        list = new ArrayList<E>();
    }

    public SendList(List<E> list) {
        this.list = list;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }
}
