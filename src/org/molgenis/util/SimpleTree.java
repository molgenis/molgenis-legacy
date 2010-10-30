/**
 * File: invengine.tdg.TDG <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-05-01; 1.0.0; MA Swertz; Creation.
 * <li>2005-12-01; 1.0.0; RA Scheltema; Did some updating to the new
 * style-guide, however there still remain quite a few warnings to solve.
 * </ul>
 */

package org.molgenis.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;





/**
 * Impl
 */
@SuppressWarnings("unchecked")
public class SimpleTree<T extends Tree> implements Tree<T>,Serializable
{
	/** Unique name of this element */
	private String name;
	/** Optional, the value of this element*/
	private Object value;
	/** parent.name (or null if root element) */
	private String parentName;
	/** map of tree elements (ordered) */
	private Map<String, T> treeElements;
	/** Serializable id */
	static final long serialVersionUID = 7443849689931440159L;	
	
	/**
	 * Construct a new Tree
	 * @param name unique name
	 * @param parent the parent of this Tree. If null, then this is the root.
	 */
	public SimpleTree(String name, T parent)
	{
		//checks
		if (name == "" || name == null)
		{
			throw new IllegalArgumentException("name cannot be empty");
		}
		if (parent != null)
			try
			{
				if(parent.get(name) != null)
					throw new IllegalArgumentException("elements already exists with name = '" + name + "'");
			} catch(NullPointerException e)
			{
			}

		// body
		this.name = name;
		if (parent == null)
		{
			// this is the root element of the tree, the map is ordered
			treeElements = new LinkedHashMap<String, T>();
		}
		else
		{
			treeElements = parent.getTreeElements(); //get a pointer to tree elements.
			parentName = parent.getName();
		}
		treeElements.put(name, (T)this);
	}

	public final String getName()
	{
		return this.name;
	}
	
	public final void setName(String name)
	{
		if (name == "" || name == null)
		{
			throw new IllegalArgumentException("name cannot be empty");
		}		
		treeElements.remove(getName());
		this.name = name;
		treeElements.put(name,(T)this);
	}

	public T get(String name)
	{
		return treeElements.get(name);
	}

	public final T getParent()
	{
		if (parentName != null)
			return treeElements.get(parentName);
		else
			return null;
	}

	public void setParent(T parent)
	{
		// does the parent already contain an element with my name
		if (parent != null && parent.get(name) != null)
		{
			throw new IllegalArgumentException(this.toString() + ".setParent(" + parent.toString() + ") failed: a element already exists with name = '" + name + "', being " + parent.get(name));
		}

		// oh, and if there are any keys that are in both maps that map to
		// different objects, fail
		for (Object ckey : this.treeElements.keySet())
		{
			for (Object pkey : parent.getTreeElements().keySet())
			{
				if (pkey.equals(ckey) && !parent.getTreeElements().get(pkey).equals(this.treeElements.get(ckey)))
				{
					throw new IllegalArgumentException("setParent(" + parent.getName() + "): duplicate child '" + ckey + "'/'" + pkey + "'");
				}
			}
		}
		// should keep all children, so merge with parent map
		for (Object ckey : treeElements.keySet())
		{
			if (!parent.getTreeElements().containsValue(treeElements.get(ckey)))
			{
				parent.getTreeElements().put(ckey, this.treeElements.get(ckey));
			}
		}
		// parent.treeElements.putAll(this.treeElements);//damn things copies
		treeElements = parent.getTreeElements();
		parentName = parent.getName();
	}

	public T getRoot()
	{
		if (parentName == null)
		{
			return (T) this;
		}
		else
		{
			return (T) getParent().getRoot();
		}
	}
	
	public final List<T> getAllChildren()
	{
		return this.getAllChildren(false);
	}
	
	/** sort in order of dependency*/
	public final List<T> getAllChildren(boolean includeSelf)
	{
		ArrayList<T> all_children = new ArrayList<T>();
		if(includeSelf) all_children.add((T)this);
		for(T child: getChildren())
		{
			all_children.add(child);
			all_children.addAll(child.getAllChildren());
		}
		return all_children;
	}

	public Vector<T> getChildren()
	{
		Vector<T> children = new Vector<T>();
		for (T sc : treeElements.values())
		{
			if (sc.getParent() != null && sc.getParent() == this)
			{
				children.add(sc);
			}
		}
		return children;
	}

	public T getChild(String name)
	{
		T child = treeElements.get(name);

		if (child != null && child.getParent().equals(this))
		{
			return child;
		}
		else
		{
			return null;
		}
	}

	public String toString(boolean includeSubTree)
	{
		return toString(includeSubTree, 0);
	}

	public String toString(boolean includeSubTree, int level)
	{
		String str = toString();

		if (includeSubTree && getChildren().size() > 0)
		{
			String indent = "";
			for (int i = 0; i <= level; i++)
			{
				indent += "    ";
			}
			for (Tree element: getChildren())
			{
				str += "\n" + indent + element.toString(true, level + 1) + ",";
			}

			str = str.substring(0, str.length() - 1);
		}

		return str;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Map<String,T> getTreeElements()
	{
		return this.treeElements;
	}	
	
	public String toString()
	{
		return getClass().getSimpleName() + "(name='" + getName() + "')";
	}
	
	public boolean hasChildren()
	{
		if (this.getChildren().isEmpty())
			return false;
		return true;
	}
	
	public boolean hasParent()
	{
		if (this.getParent() == null)
			return false;
		return true;
	}
	
	public String getStringValue()
	{
		if (value == null)
			return "";
		return value.toString();		
	}
	
	public String getPath(String separator)
	{
		if(this.getParent() != null)
			return this.getParent().getPath(separator)+separator+this.getName();
		return this.getName();
	}
	
	public void remove()
	{
		for(T t: this.getAllChildren())
		{
			this.treeElements.remove(t.getName());
		}
		this.treeElements.remove(this.getName());
	}
}
