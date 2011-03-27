package org.molgenis.mutation.service;

import org.molgenis.util.SimpleEmailService;

public class MutationEmailService extends SimpleEmailService
{
	public MutationEmailService()
	{
		super();
		//TODO: Grab from MolgenisOptions
		this.setSmtpFromAddress("molgenis@gmail.com");
		this.setSmtpHostname("smtp.gmail.com");
		this.setSmtpPassword("molgenispass");
		this.setSmtpPort(465);
		this.setSmtpProtocol("smtps");
		this.setSmtpUser("molgenis");
	}
}
