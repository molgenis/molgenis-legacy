package org.molgenis.pheno.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProtocolApplicationDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -3679327345743958936L;

	private String name;
	private Date time;
	private Integer protocolId;
	private List<Integer> performerIdList;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}
	public List<Integer> getPerformerIdList() {
		return performerIdList;
	}
	public void setPerformerIdList(List<Integer> performerIdList) {
		this.performerIdList = performerIdList;
	}
}
