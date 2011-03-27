package org.molgenis.mutation.vo;

import org.molgenis.framework.security.Login;
import org.molgenis.mutation.ui.LimitOffsetPager;

public class MyCOL7A1VO
{
	private Login login;
	private LimitOffsetPager<PatientSummaryVO> pager;

	public Login getLogin()
	{
		return login;
	}
	public void setLogin(Login login)
	{
		this.login = login;
	}
	public LimitOffsetPager<PatientSummaryVO> getPager()
	{
		return pager;
	}
	public void setPager(LimitOffsetPager<PatientSummaryVO> pager)
	{
		this.pager = pager;
	}
}
