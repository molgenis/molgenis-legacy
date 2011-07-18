package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class PatientSearchCriteriaVO implements Serializable
{
	private static final long serialVersionUID = -8296063132415520100L;
	String pid;
	Integer mutationId;
	String mid;
	Boolean consent;
	Integer submissionId;
	Integer userId;

	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public Integer getMutationId() {
		return mutationId;
	}
	public void setMutationId(Integer mutationId) {
		this.mutationId = mutationId;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Boolean getConsent() {
		return consent;
	}
	public void setConsent(Boolean consent) {
		this.consent = consent;
	}
	public Integer getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Integer submissionId) {
		this.submissionId = submissionId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String toString()
	{
		List<String> result = new ArrayList<String>();

		if (this.pid != null)
			result.add("PID = '" + this.pid + "'");
		if (this.mutationId != null)
			result.add("mutation id = " + this.mutationId);
		if (this.mid != null)
			result.add("MID = '" + this.mid + "'");
		if (this.consent != null)
			result.add("consent = '" + this.consent.toString() + "'");
		if (this.submissionId != null)
			result.add("submission id = '" + this.submissionId + "'");
		if (this.userId != null)
			result.add("user id = '" + this.userId + "'");

		return StringUtils.join(result, " and ");
	}
}
