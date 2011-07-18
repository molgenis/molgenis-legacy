package org.molgenis.auth.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisRole;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.MolgenisRoleGroupLink;
import org.molgenis.auth.util.PasswordHasher;
import org.molgenis.auth.vo.MolgenisUserSearchCriteriaVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;

public class MolgenisUserService
{
	private Database db                                    = null;
	private static MolgenisUserService molgenisUserService = null;
	//TODO: Danny: Use or loose
	//private static final transient Logger logger = Logger.getLogger(JDBCConnectionHelper.class.getSimpleName());

	// private constructor, use singleton instance
	private MolgenisUserService(Database db)
	{
		this.db = db;
	}
	
	/**
	 * Get an instance of MolgenisUserService
	 * @param JDBCDatabase object
	 * @return MolgenisUserService object
	 */
	public static MolgenisUserService getInstance(Database db)
	{
		if (molgenisUserService == null)
			molgenisUserService = new MolgenisUserService(db);
		
		return molgenisUserService;
	}

	public List<MolgenisUser> find(MolgenisUserSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		Query<MolgenisUser> query = this.db.query(MolgenisUser.class);
		
		if (criteria.getName() != null)
			query = query.equals("name", criteria.getName());
		if (criteria.getActivationCode() != null)
			query = query.equals("activationCode", criteria.getActivationCode());
		
		return query.find();
	}

	public MolgenisUser findById(Integer id) throws DatabaseException
	{
		return this.db.findById(MolgenisUser.class, id);
	}

	/**
	 * Get a list of group ids for the groups a user is member of
	 * @param role
	 * @return list of group ids
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> findGroupIds(MolgenisRole role) throws DatabaseException, ParseException
	{
		List<Integer> roleIdList          = new ArrayList<Integer>();
		roleIdList.add(role.getId());

		List<MolgenisRoleGroupLink> links = this.db.query(MolgenisRoleGroupLink.class).equals(MolgenisRoleGroupLink.ROLE_, role.getId()).find();

		for (MolgenisRoleGroupLink link : links) {
			//roleIdList.add(link.getGroup_Id());
			roleIdList.addAll(findGroupIds(db.findById(MolgenisRole.class, link.getGroup_Id())));
		}
		
		System.out.println();
		
		return roleIdList;
	}

	public void insert(MolgenisUser user) throws DatabaseException, IOException
	{
		if (StringUtils.isEmpty(user.getPassword()))
			user.setPassword(UUID.randomUUID().toString());
		
		try
		{
			this.db.beginTx();
			this.db.add(user);
			this.db.commitTx();
		}
		catch (DatabaseException e)
		{
			this.db.rollbackTx();
			throw e;
		}
	}

	public void update(MolgenisUser user) throws DatabaseException, IOException
	{
		try
		{
			this.db.beginTx();
			this.db.update(user);
			this.db.commitTx();
		}
		catch (DatabaseException e)
		{
			this.db.rollbackTx();
			throw e;
		}
	}
	
	public void checkPassword(String userName, String oldPwd, String newPwd1, String newPwd2) throws MolgenisUserException, DatabaseException, ParseException, NoSuchAlgorithmException
	{
		if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd1) || StringUtils.isEmpty(newPwd2))
			throw new MolgenisUserException("Passwords empty");

		if (!StringUtils.equals(newPwd1, newPwd2))
			throw new MolgenisUserException("Passwords do not match");

		List<MolgenisUser> users = this.db.query(MolgenisUser.class).equals(MolgenisUser.NAME, userName).find();
		
		if (users.size() != 1)
			throw new MolgenisUserException("User not found");
		
		MolgenisUser user        = users.get(0);

		PasswordHasher hasher    = new PasswordHasher();
		if (!StringUtils.equals(user.getPassword(), hasher.toMD5(oldPwd)))
			throw new MolgenisUserException("Wrong password");
	}
}
