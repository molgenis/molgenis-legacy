package org.molgenis.sandbox.plugins;


import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.organization.Investigation;

import app.JDBCDatabase;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import commonservice.CommonService;

public class VaadinTest extends Application {

	private static final long serialVersionUID = 1222222222222299L;
	
	private static String[] fields  = {"First Name", "Last Name", "Company", "Mobile Phone", "Work Phone", 
            "Home Phone", "Work Email", "Home Email", "Street", "Zip", "City", "State", "Country"};
	
	private static String[] visibleCols = new String[] {"Last Name", "First Name", "Company" };
	
	private static Table contactList = new Table();
	private static Form contactEditor  = new Form();  
	private static HorizontalLayout bottomleftCorner = new HorizontalLayout();
	private static Button contactRemovalButton;
	private static IndexedContainer addressBookData = createDummyData();
	
	//BBMRI variables
	private static Database db;
	private static CommonService ct;

	private static String[] BBMRIFields = {"Cohort", "Category", "SubCategory", "Topic", "Institutes", "Coordinators", "Current n=", "Biodata", 
        "GWA data n=", "GWA platform", "GWA comments", "General comments", "Publications"}; 
	private static String[] BBMRIvisibleCols = new String[] {"Cohort", "Category", "SubCategory", "Topic"};
	
	private static Table InvestigationData = new Table();
    private static Form InvestigationEditor = new Form();
    private HorizontalLayout bottomleftCorner2 = new HorizontalLayout();
    private static Button InvestigationRemovalButton;

    private static IndexedContainer BBMRIData ;
    
	@Override
	public void init() {
        
        //BBMRI database
		JDBCDatabase db;
		try {
			
			this.db = new JDBCDatabase("/Users/despoina/Documents/GCC_workspace/molgenis_apps/handwritten/apps/org/molgenis/biobank/bbmri.molgenis.properties");
			ct = CommonService.getInstance();
			ct.setDatabase(this.db);
			BBMRIData = FillBBMRIData(this.db, ct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initLayout();
		initInvestigationAddremoveButtons();
        //initAddressList();
        initInvestigationData();
        initFilteringControls();
		
	}
	
	private void 	initLayoutOld() {
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setMainWindow(new Window("Address Book", splitPanel));
		VerticalLayout left = new VerticalLayout();
		left.setSizeFull();
		left.addComponent(contactList);
		
		contactList.setSizeFull();
		left.setExpandRatio(contactList, 1);
		
		splitPanel.addComponent(left);
		splitPanel.addComponent(contactEditor);

		contactEditor.setSizeFull();
		contactEditor.getLayout().setMargin(true);
		contactEditor.setImmediate(true);
		bottomleftCorner.setWidth("100%");
        left.addComponent(bottomleftCorner);
			
	}
	
	private void 	initLayout() {
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setMainWindow(new Window("BBMRI Data", splitPanel));
		VerticalLayout left = new VerticalLayout();
		left.setSizeFull();
		left.addComponent(InvestigationData);

		InvestigationData.setSizeFull();
		left.setExpandRatio(InvestigationData, 1);

		splitPanel.addComponent(left);
		splitPanel.addComponent(InvestigationEditor);

		InvestigationEditor.setSizeFull();  
		InvestigationEditor.getLayout().setMargin(true);  
		InvestigationEditor.setImmediate(true); 
		bottomleftCorner2.setWidth("100%");    
        left.addComponent(bottomleftCorner2);
			
	}
	
	private void initInvestigationAddremoveButtons() {
		 // New item button
        bottomleftCorner2.addComponent(new Button("+",
                new Button.ClickListener() {
                    /**
					 * 
					 */
					private static final long serialVersionUID = -4733253206094668823L;

					public void buttonClick(ClickEvent event) {
                    	Object id = InvestigationData.addItem(); 
                    	InvestigationData.setValue(id);   
                    }
                }));
				
        //Remove item button
        InvestigationRemovalButton = new Button("-", new Button.ClickListener() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 5013426493875747495L;

			public void buttonClick(ClickEvent event) {
        		InvestigationData.removeItem(InvestigationData.getValue());
        		InvestigationData.select(null);  
        	}        	
        });
    
        InvestigationRemovalButton.setVisible(false);    
        bottomleftCorner2.addComponent(InvestigationRemovalButton); 
    }
	
	private void initContactAddremoveButtons() {
		 // New item button
       bottomleftCorner.addComponent(new Button("+",
               new Button.ClickListener() {
                   
				private static final long serialVersionUID = 7864016725547282812L;

				public void buttonClick(ClickEvent event) {
                   	Object id = contactList.addItem();
                   	contactList.setValue(id);
                   }
               }));
				
       //Remove item button
       contactRemovalButton = new Button("-", new Button.ClickListener() {
       	/**
		 * 
		 */
		private static final long serialVersionUID = -6434061026400433823L;

		public void buttonClick(ClickEvent event) {
       		contactList.removeItem(contactList.getValue());
       		contactList.select(null);
       	}        	
       });
   
       contactRemovalButton.setVisible(false);
       bottomleftCorner.addComponent(contactRemovalButton);
   }
	
	private String[] initInvestigationData() {
		InvestigationData.setContainerDataSource(BBMRIData);
		InvestigationData.setVisibleColumns(BBMRIvisibleCols);
		InvestigationData.setSelectable(true);
		InvestigationData.setImmediate(true);
        
		InvestigationData.addListener(new Property.ValueChangeListener() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -4769557808638003813L;

			public void valueChange(ValueChangeEvent event) {
                Object id = InvestigationData.getValue();
                InvestigationEditor.setItemDataSource(id == null ? null : InvestigationData
                        .getItem(id));
                InvestigationRemovalButton.setVisible(id != null);
            }
        });
        return visibleCols;
	}
	
	private String[] initAddressList() {
		contactList.setContainerDataSource(addressBookData);
        contactList.setVisibleColumns(visibleCols);
        contactList.setSelectable(true);
        contactList.setImmediate(true);
        
        contactList.addListener(new Property.ValueChangeListener() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
                Object id = contactList.getValue();
                contactEditor.setItemDataSource(id == null ? null : contactList
                        .getItem(id));
                contactRemovalButton.setVisible(id != null);
            }
        });
        return visibleCols;
	}
	
	private void initFilteringControls() {
        for (final String pn : BBMRIvisibleCols) {
            final TextField sf = new TextField();
            bottomleftCorner2.addComponent(sf);
            sf.setWidth("100%");
            sf.setInputPrompt(pn);
            sf.setImmediate(true);
            bottomleftCorner2.setExpandRatio(sf, 1);
            sf.addListener(new Property.ValueChangeListener() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 7463427190739856945L;

				public void valueChange(ValueChangeEvent event) {
                	BBMRIData.removeContainerFilters(pn);
                    if (sf.toString().length() > 0 && !pn.equals(sf.toString())) {
                    	BBMRIData.addContainerFilter(pn, sf.toString(),
                                true, false);
                    }
                    getMainWindow().showNotification(
                            "" + BBMRIData.size() + " matches found");
                }
            });
        }
    }
	private void initFilteringControlsOLD() {
        for (final String pn : visibleCols) {
            final TextField sf = new TextField();
            bottomleftCorner.addComponent(sf);
            sf.setWidth("100%");
            sf.setInputPrompt(pn);
            sf.setImmediate(true);
            bottomleftCorner.setExpandRatio(sf, 1);
            sf.addListener(new Property.ValueChangeListener() {
                /**
				 * 
				 */
				private static final long serialVersionUID = -7109060462810833475L;

				public void valueChange(ValueChangeEvent event) {
                    addressBookData.removeContainerFilters(pn);
                    if (sf.toString().length() > 0 && !pn.equals(sf.toString())) {
                        addressBookData.addContainerFilter(pn, sf.toString(),
                                true, false);
                    }
                    getMainWindow().showNotification(
                            "" + addressBookData.size() + " matches found");
                }
            });
        }
    }
	
	private static IndexedContainer createDummyData() {

        String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
                "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
                "Lisa", "Marge" };
        String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
                "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
                "Barks", "Ross", "Schneider", "Tate" };

        IndexedContainer ic = new IndexedContainer();

        for (String p : fields) {
            ic.addContainerProperty(p, String.class, "");
        }

        for (int i = 0; i < 1000; i++) {
            Object id = ic.addItem();
            ic.getContainerProperty(id, "First Name").setValue(
                    fnames[(int) (fnames.length * Math.random())]);
            ic.getContainerProperty(id, "Last Name").setValue(
                    lnames[(int) (lnames.length * Math.random())]);
        }

        return ic;
    }
	
	private static IndexedContainer FillBBMRIData(Database db, CommonService ct) {
		
        IndexedContainer ic = new IndexedContainer();

		
        for (String p : BBMRIFields) {
            ic.addContainerProperty(p, String.class, "");
        }
        
		System.out.println(">>>>>>Start");
		try {
			List<Investigation> invList   = db.query(Investigation.class).find();
			
			//Query<Investigation> q = db.query(Investigation.class);
			//List<Investigation> result = q.find();
			
			for (int i=0; i<invList.size(); i++) {
				Object id = ic.addItem();
			        
	            //ic.getContainerProperty(id, "Cohort").setValue(invList.get(i).getDescription());
	            System.out.println("Description>>>>>"+invList.get(i).getDescription());
	           

	            //ic.getContainerProperty(id, "Topic").setValue(invList.get(i).get__Type());
		        // ic.getContainerProperty(id, "Category").setValue(invList.get(0).getName());

	            System.out.println(">>>>>"+invList.get(0).getDescription());
	            System.out.println(">>>>>"+invList.get(0).get__Type());
	            System.out.println(">>>>>"+invList.get(i).getStartDate());
	            System.out.println(">>>>>"+invList.get(i).getEndDate());
	            System.out.println(">>>>>"+invList.get(i).getContacts_LastName());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return ic;
    }
	
	
	
	
	
}
