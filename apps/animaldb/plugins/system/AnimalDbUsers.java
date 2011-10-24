
package plugins.system;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.util.PasswordHasher;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * AnimalDbUsersController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>AnimalDbUsersModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>AnimalDbUsersView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class AnimalDbUsers extends PluginModel<Entity>
{
	private static final long serialVersionUID = 3660487327165570585L;
	private String action = "init";
	private List<Investigation> investigations;
	private List<MolgenisUser> users;
	private Database db;

	public AnimalDbUsers(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getUserName() {
		return this.getLogin().getUserName();
	}
	
	public String getInvestigationSharers(String invName, boolean canWrite) {
		try {
			Query<Investigation> q = db.query(Investigation.class);
			q.addRules(new QueryRule(Investigation.NAME, Operator.EQUALS, invName));
			List<Investigation> invList = q.find();
			if (invList.size() == 1) {
				Investigation inv = invList.get(0);
				if (canWrite == true) {
					return inv.getCanWrite_Name();
				} else {
					return inv.getCanRead_Name();
				}
			} else {
				return null;
			}
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) {
		
		this.db = db;
		
		try {
			users = db.find(MolgenisUser.class);
		} catch (DatabaseException e1) {
			this.setMessages(new ScreenMessage("Something went wrong while loading user list", false));
			e1.printStackTrace();
		}
		
		int userId = this.getLogin().getUserId();
		// Load user's investigation list
		try {
			Query<Investigation> q = db.query(Investigation.class);
			q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
			List<Investigation> invList = q.find();
			setInvestigations(invList);
			
		} catch (Exception e) {
			this.setMessages(new ScreenMessage("Something went wrong while loading investigation list", false));
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleRequest(Database db, Tuple request) {
		
		this.db = db;
		
		try {
			action = request.getAction();
			
			if (action.equals("init")) {
				//
			}
			if (action.equals("New")) {
				//
			}
			if (action.equals("Cancel")) {
				action = "init";
			}
			if (action.startsWith("ShareRead")) {
				int invNr = Integer.parseInt(action.substring(9));
				Investigation inv = investigations.get(invNr);
				int userId = request.getInt("shareread");
				inv.setCanRead_Id(userId);
				db.update(inv);
				this.setMessages(new ScreenMessage("Investigation succesfully shared.", true));
			}
			if (action.startsWith("ShareWrite")) {
				int invNr = Integer.parseInt(action.substring(10));
				Investigation inv = investigations.get(invNr);
				int userId = request.getInt("sharewrite");
				inv.setCanWrite_Id(userId);
				db.update(inv);
				this.setMessages(new ScreenMessage("Investigation succesfully shared.", true));
			}
			if (action.equals("Add")) {
				
				String firstname = request.getString("firstname");
				if (firstname == null) {
					throw new Exception("No first name given");
				}
				String lastname = request.getString("lastname");
				if (lastname == null) {
					throw new Exception("No last name given");
				}
				
				String email = request.getString("email");
				if (email == null) {
					throw new Exception("No email given");
				}
				
				String username = request.getString("username");
				if (username == null) {
					throw new Exception("No user name given");
				}
				
				String password1 = request.getString("password1");
				if (password1 == null) {
					throw new Exception("No password given");
				}
				String password2 = request.getString("password2");
				if (password2 == null) {
					throw new Exception("Password not repeated");
				}
				if (!password1.equals(password2)) {
					throw new Exception("Passwords not identical");
				}
				PasswordHasher hasher = new PasswordHasher();
				String passwordHashed = hasher.toMD5(password1);
				
				// Make user
				MolgenisUser newUser = new MolgenisUser();
				newUser.setName(username);
				newUser.setFirstName(firstname);
				newUser.setLastName(lastname);
				newUser.setEmail(email);
				newUser.setPassword(passwordHashed);
				newUser.setActive(true); // no extra registration required
				db.add(newUser);
				int userId = newUser.getId();
				
				// Make new user owner of new or selected investigation
				Investigation chosenInv = null;
				if (request.getInt("investigation") != null) {
					int invId = request.getInt("investigation");
					if (invId == -1) {
						String newinv = request.getString("newinv");
						if (newinv == null) {
							throw new Exception("No name given for new investigation");
						}
						chosenInv = new Investigation();
						chosenInv.setName(newinv);
						chosenInv.setOwns_Id(userId);
						db.add(chosenInv);
						
					} else {
						Query<Investigation> q = db.query(Investigation.class);
						q.addRules(new QueryRule(Investigation.ID, Operator.EQUALS, invId));
						List<Investigation> invList = q.find();
						if (invList.size() == 1) {
							chosenInv = invList.get(0);
						} else {
							throw new Exception("No (valid) investigation chosen");
						}
						chosenInv.setOwns_Id(userId);
						db.update(chosenInv);
					}
				} else {
					throw new Exception("No (valid) investigation chosen");
				}
				
				// Give new user rights on ALL entities, forms, menus and plugins (TODO: leave some out?)
				List<MolgenisPermission> permList = new ArrayList<MolgenisPermission>();
				Query<MolgenisEntity> q = db.query(MolgenisEntity.class);
				List<MolgenisEntity> entList = q.find();
				if (entList != null) {
					for (MolgenisEntity ent : entList) {
						MolgenisPermission newPerm = new MolgenisPermission();
						newPerm.setRole_Id(userId);
						newPerm.setPermission("write");
						newPerm.setEntity_Id(ent.getId());
						permList.add(newPerm);
					}
				}
				db.add(permList);
				
				this.setMessages(new ScreenMessage("User " + username + 
						" successfully added and assigned ownership of investigation " + 
						chosenInv.getName(), true));
				
				action = "init";
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage("Error: " + e.getMessage(), false));
			}
		}
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String getViewName() {
		return "plugins_system_AnimalDbUsers";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/system/AnimalDbUsersView.ftl";
	}
	
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	public void setInvestigations(List<Investigation> investigations) {
		this.investigations = investigations;
	}

	public List<Investigation> getInvestigations() {
		return investigations;
	}
	
	public List<MolgenisUser> getUsers() {
		return users;
	}

}
