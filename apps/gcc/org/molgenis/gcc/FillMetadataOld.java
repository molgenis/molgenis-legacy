package org.molgenis.gcc;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisGroup;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisRoleGroupLink;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jpa.JpaUtil;

import app.DatabaseFactory;

public class FillMetadataOld {
	public static void main(String[] args) throws DatabaseException {
		Database db = DatabaseFactory.create();		
		JpaUtil.dropAndCreateTables(db);	
		
		
		app.FillMetadata.fillMetadata(db);
		
	}
}
