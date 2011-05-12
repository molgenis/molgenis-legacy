
package plugins.system;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisEntity;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.util.PasswordHasher;
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
	private List<String> userNames;
	private List<String> invNames;
	private List<Investigation> investigations;

	public AnimalDbUsers(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public List<String> getUserNames() {
		return userNames;
	}
	
	public String getInvestigationName(int idx) {
		return invNames.get(idx);
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) {	
		
		userNames = new ArrayList<String>();
		invNames = new ArrayList<String>();
		setInvestigations(new ArrayList<Investigation>());
		// Load user names and investigations list
		try {
			List<MolgenisUser> userList = db.query(MolgenisUser.class).find();
			for (MolgenisUser u : userList) {
				userNames.add(u.getName());
				int userId = u.getId();
				
				Query<Investigation> q = db.query(Investigation.class);
				q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
				List<Investigation> invList = q.find();
				String invs = "";
				for (Investigation i : invList) {
					invs += (i.getName() + ", ");
				}
				if (invs.length() > 0) {
					invs = invs.substring(0, invs.length() - 2);
				}
				invNames.add(invs);
			}
			
			setInvestigations(db.query(Investigation.class).find());
			
		} catch (Exception e) {
			this.setMessages(new ScreenMessage("Something went wrong while loading user list", false));
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleRequest(Database db, Tuple request) {
		
		try {
			action = request.getAction();
			
			if (action.equals("init")) {
				//
			}
			if (action.equals("New")) {
				//
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
				newUser.setFirstname(firstname);
				newUser.setLastname(lastname);
				newUser.setEmailaddress(email);
				newUser.setPassword(passwordHashed);
				newUser.setActive(true); // no extra registration required
				db.add(newUser);
				int userId = newUser.getId();
				
				// Make owner of chosen investigation
				Investigation chosenInv;
				if (request.getInt("investigation") != null) {
					int invId = request.getInt("investigation");
					if (invId == 0) {
						String newinv = request.getString("newinv");
						if (newinv == null) {
							throw new Exception("No name given for new investigation");
						}
						chosenInv = new Investigation();
						chosenInv.setName(newinv);
						db.add(chosenInv); // owner is now defaulted to the one logged in!
						
					} else {
						Query<Investigation> q = db.query(Investigation.class);
						q.addRules(new QueryRule(Investigation.ID, Operator.EQUALS, invId));
						List<Investigation> invList = q.find();
						if (invList.size() == 1) {
							chosenInv = invList.get(0);
						} else {
							throw new Exception("No (valid) investigation chosen");
						}
					}
					chosenInv.setOwns(userId);
					db.update(chosenInv);
				} else {
					throw new Exception("No (valid) investigation chosen");
				}
				
				// Give rights on entities, forms, menus and plugins
				List<MolgenisPermission> permList = new ArrayList<MolgenisPermission>();
				permList.add(makePermission(db, userId, "write","org.molgenis.core.OntologyTerm"));
				permList.add(makePermission(db, userId, "write","org.molgenis.core.Ontology"));
				permList.add(makePermission(db, userId, "write","org.molgenis.organization.Investigation"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.ObservationElement"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.ObservationTarget"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.ObservableFeature"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Measurement"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Individual"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Location"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Panel"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Code"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.ObservedValue"));
				permList.add(makePermission(db, userId, "write","org.molgenis.protocol.Protocol"));
				permList.add(makePermission(db, userId, "write","org.molgenis.protocol.ProtocolApplication"));
				permList.add(makePermission(db, userId, "write","org.molgenis.data.ObservedInference"));
				permList.add(makePermission(db, userId, "write","org.molgenis.news.MolgenisNews"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisRole"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisGroup"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisUserGroupLink"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisUser"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisEntity"));
				permList.add(makePermission(db, userId, "write","org.molgenis.auth.MolgenisPermission"));
				permList.add(makePermission(db, userId, "write","org.molgenis.batch.MolgenisBatch"));
				permList.add(makePermission(db, userId, "write","org.molgenis.batch.MolgenisBatchEntity"));
				permList.add(makePermission(db, userId, "write","org.molgenis.animaldb.CustomLabelFeature"));
				permList.add(makePermission(db, userId, "write","org.molgenis.organization.Investigation_Contacts"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Panel_Individuals"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Panel_FounderPanels"));
				permList.add(makePermission(db, userId, "write","org.molgenis.pheno.Code_Feature"));
				permList.add(makePermission(db, userId, "write","org.molgenis.protocol.Protocol_Features"));
				permList.add(makePermission(db, userId, "write","org.molgenis.protocol.ProtocolApplication_Performer"));
				permList.add(makePermission(db, userId, "write","org.molgenis.data.ObservedInference_DerivedFrom"));
				permList.add(makePermission(db, userId, "write","app.ui.AnimalDBHeaderPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.mainmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.AnimalDBWelcomeScreenPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.NewsPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.PreFillDatabasePlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.securitymenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.UserLoginPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.usermanagementMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.PermissionManagementPluginPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.AnimalDbUsersPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.systemmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.FillDatabasePlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.CascadingDeleteAnimalsPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.SetCustomLabelFeaturePlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.batchesMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.ManageBatchesFormController"));
				permList.add(makePermission(db, userId, "write","app.ui.BatchPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.BatchViewPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.animalmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.AddAnimalPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.breedingmoduleMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.ViewFamilyPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.ManageLinesPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.ManageParentgroupsPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.ManageLittersPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.RemAnimalPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.PrintLabelsPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.AddSpecialGroupPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.locationmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.AddLocationPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.LocationInfoPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.valuemenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.ApplyProtocolPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.ListPluginPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.EventViewerPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.projectmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.DecStatusPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.AddProjectPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.AddSubprojectPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.AnimalsInSubprojectsPlugin"));
				permList.add(makePermission(db, userId, "write","app.ui.YearlyReportModulePlugin"));
				permList.add(makePermission(db, userId, "read","app.ui.searchmenuMenu"));
				permList.add(makePermission(db, userId, "write","app.ui.SimpleDbSearchPlugin"));
				db.add(permList);
				
				action = "init";
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setMessages(new ScreenMessage("Error adding user: " + e.getMessage(), false));
			}
		}
	}
	
	private MolgenisPermission makePermission(Database db, int userId, String right, String entityClassName) 
		throws DatabaseException, ParseException {
		
		// TODO: find out why newPerm.setEntity_ClassName() doesn't work!
		
		Query<MolgenisEntity> q = db.query(MolgenisEntity.class);
		q.addRules(new QueryRule(MolgenisEntity.CLASSNAME, Operator.EQUALS, entityClassName));
		List<MolgenisEntity> entList = q.find();
		
		MolgenisPermission newPerm = new MolgenisPermission();
		newPerm.setRole(userId);
		newPerm.setPermission(right);
		newPerm.setEntity(entList.get(0).getId());
		return newPerm;
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

}
