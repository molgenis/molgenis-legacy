/**
 * @author Jessica Lundberg
 * @author Erik Roos
 * @author Robert Wagner
 * @date 04-02-2011
 * 
 * This is the model for the PermissionManagementPlugin, which allows
 * users to see their rights and also the rights of others on entities
 * they own. 
 */
package org.molgenis.auth.service.permissionmanagement;

import org.molgenis.auth.MolgenisRole;
import org.molgenis.auth.MolgenisUser;

public class PermissionManagementModel {


    private MolgenisRole role;
    private String action = "init";
    private int permId = 0;
    
    public PermissionManagementModel() {
	
    }

    public void setRole(MolgenisRole molgenisRole) {
	this.role = molgenisRole;
    }

    public MolgenisRole getRole() {
	return role;
    }

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setPermId(int permId) {
		this.permId = permId;
	}

	public int getPermId() {
		return permId;
	}
}
