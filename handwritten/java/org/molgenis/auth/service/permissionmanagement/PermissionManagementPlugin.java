/**
 * @author Jessica Lundberg
 * @author Robert Wagner
 * @author Erik Roos
 * @date 04-02-2011
 * 
 * This class is a controller for the PermissionManagementPlugin, which allows
 * users to see their rights and also the rights of others on entities
 * they own. 
 */
package org.molgenis.auth.service.permissionmanagement;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class PermissionManagementPlugin extends PluginModel<Entity> {

    private static final long serialVersionUID = -9150476614594665384L;
    private PermissionManagementModel model;
    private PermissionManagementService service;
    private static Logger logger = Logger.getLogger(PermissionManagementPlugin.class);

    public PermissionManagementPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
		this.model = new PermissionManagementModel(this);
    }

    @Override
    public String getViewName()
    {
    	return "org_molgenis_auth_service_permissionmanagement_PermissionManagementPlugin";
    }

    @Override
    public String getViewTemplate()
    {
    	return "org/molgenis/auth/service/permissionmanagement/PermissionManagementPlugin.ftl";
    }

    @Override
    public void handleRequest(Database db, Tuple request) {
    	try {
    		model.setAction(request.getAction());
    		
    		if (model.getAction().equals("AddEdit")) {
    		    model.setPermId(request.getInt("id"));
    		} else if (model.getAction().equals("Remove")) {
    		    model.setPermId(request.getInt("id"));
    		    service.remove(model.getRole().getId(), model.getPermId());
    		    this.setMessages(new ScreenMessage("Removal successful", true));
    		} else if (model.getAction().equals("AddPerm")) {
    		    service.insert(model.getRole().getId(), addPermission(request));
    		    this.setMessages(new ScreenMessage("Adding successful", true));
    		} else if (model.getAction().equals("UpdatePerm")){
    		    service.update(model.getRole().getId(), updatePermission(request));
    		    this.setMessages(new ScreenMessage("Update successful", true));
    		}
    			
    	} catch (Exception e) {
		    logger.error("Error occurred: ", e);
		    this.setMessages(new ScreenMessage(e != null ? e.getMessage() : "null", false));
    	}
    }

    /** Update a permission based on request
     * 
     * @param request
     * @return
     * @throws DatabaseException
     * @throws ParseException
     */
    private MolgenisPermission updatePermission(Tuple request) throws DatabaseException, ParseException {
		if (request.getString("entity") != null) {
			MolgenisPermission perm = new MolgenisPermission();
			perm.setEntity(Integer.parseInt(request.getString("entity")));
			perm.setRole(Integer.parseInt(request.getString("role")));
			perm.setPermission(request.getString("permission"));
			return perm;
		} else {
			throw new DatabaseException("Cannot update permission: no entity set");
		}
    }

    /** Insert (add) a permission based on request
     * 
     * @param request
     * @return
     */
    public MolgenisPermission addPermission(Tuple request) throws DatabaseException {
		if (request.getString("entity") != null) {
			MolgenisPermission perm = new MolgenisPermission();
			perm.setEntity(Integer.parseInt(request.getString("entity")));
			perm.setRole(Integer.parseInt(request.getString("role")));
			perm.setPermission(request.getString("permission"));
			return perm;
		} else {
			throw new DatabaseException("Cannot add permission: no entity set");
		}
    }

    @Override
    public void reload(Database db) {
		service = PermissionManagementService.getInstance(db);
		try {
		    model.setRole(service.findRole((db.getSecurity().getUserId())));
		} catch (Exception e) {
		    //TODO: add logger + screen message
		}
    }
    
    public PermissionManagementModel getModel() {
    	return this.model;
    }  
    
    public PermissionManagementService getService() {
    	return service;
    }

    public void setService(PermissionManagementService service) {
    	this.service = service;
    }

    public void clearMessage()
    {
    	this.setMessages();
    }

}
