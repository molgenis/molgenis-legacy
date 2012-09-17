package org.molgenis.generator;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 20/04/2012
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public class FoldingUniqueContainer
{
    private String key;
    private String hasOne;

    private Vector<Hashtable> hashtables = new Vector<Hashtable>();

    public FoldingUniqueContainer(String keyCombination, String hasOneCombination, Hashtable initialLine)
    {
        this.key = keyCombination;
        this.hasOne = hasOneCombination;
        this.hashtables.add(initialLine);
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getHasOne()
    {
        return hasOne;
    }

    public void setHasOne(String hasOne)
    {
        this.hasOne = hasOne;
    }

    public Vector<Hashtable> getAllHashtablles()
    {
        return hashtables;
    }

    public void addElement(Hashtable eleHashtable)
    {
        hashtables.add(eleHashtable);
    }

}
