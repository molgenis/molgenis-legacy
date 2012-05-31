package org.molgenis.mutation.ui;

import java.util.List;

import org.apache.log4j.Logger;

public class LimitOffsetPager<E>
{
	private List<E> entities;
	private int limit  = 20;
	private int offset = 0;

	public LimitOffsetPager()
	{
	}

	public LimitOffsetPager(List<E> entities, int limit, int offset)
	{
		this.entities = entities;
		this.limit    = limit;
		this.offset   = offset;
	}

	public List<E> getEntities() {
		return entities;
	}
	public void setEntities(List<E> entities) {
		this.entities = entities;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public List<E> getPage()
	{
		if (this.offset + this.limit < this.entities.size())
			return this.entities.subList(this.offset, this.offset + this.limit);
		else
			return this.entities.subList(this.offset, this.entities.size());
	}
	
	public List<E> first()
	{
		this.offset = 0;
		return this.getPage();
	}
	
	public List<E> prev()
	{
		if (this.offset - this.limit >= 0)
			this.offset = this.offset - this.limit;
		return this.getPage();
	}

	public List<E> next()
	{
		if (this.offset + this.limit < this.entities.size())
			this.offset = this.offset + this.limit;
		return this.getPage();
	}

	public List<E> last()
	{
		this.offset = this.entities.size() - this.limit;
		return this.getPage();
	}
	
	public int getCount()
	{
		return this.entities.size();
	}
	
	public String getLabel()
	{
		int start = this.offset + 1;
		int end   = (this.offset + this.limit < this.entities.size() ? this.offset + this.limit : this.entities.size());
		return start + "-" + end + " of " + this.entities.size();
	}
}
