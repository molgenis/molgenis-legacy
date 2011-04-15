package org.molgenis.sandbox.plugins;


import java.util.List;

import org.molgenis.bbmri.Biobank;
import org.molgenis.bbmri.BiobankPanel;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Institute;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.Person;

import app.JDBCDatabase;
import app.ui.CoordinatorsFormModel;

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
	
	
	private Database db;
	private static CommonService ct;

	//TODO : we can do better that this !
	private static String[] BBMRIFields = {"id","Cohort", "Category", "SubCategory", "Topic", "Institutes", "Coordinators", "Current n=", "Biodata", 
															"GWA data n=", "GWA platform", "GWA comments", "General comments", "Publications"}; 
	private static String[] BBMRIvisibleCols = new String[] {"id","Cohort", "Category", "SubCategory", "Coordinators", "Institutes", "Topic","General comments"};
	
	private static Table InvestigationData = new Table();
    private static Form InvestigationEditor = new Form();
    private HorizontalLayout bottomLeftCorner = new HorizontalLayout();
    private static Button InvestigationRemovalButton;

    private static IndexedContainer BBMRIData ;
    
	@Override
	public void init() {
        

		try {
			
			//TODO : we can do better that this !
			this.db = new JDBCDatabase("/Users/despoina/Documents/GCC_workspace/molgenis_apps/handwritten/apps/org/molgenis/biobank/bbmri.molgenis.properties");
			ct = CommonService.getInstance();
			ct.setDatabase(this.db);
			BBMRIData = FillBBMRIData(this.db, ct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initLayout();
		initInvestigationAddremoveButtons();
        initBBMRIList();
        initInvestigationData();
        initFilteringControls();
		
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
		bottomLeftCorner.setWidth("100%");    
        left.addComponent(bottomLeftCorner);
			
	}
	
	private void initInvestigationAddremoveButtons() {
		 // New item button
        bottomLeftCorner.addComponent(new Button("+",
                new Button.ClickListener() {
                   
					private static final long serialVersionUID = -4733253206094668823L;

					public void buttonClick(ClickEvent event) {
                    	Object id = InvestigationData.addItem(); 
                    	InvestigationData.setValue(id);   
                    }
                }));
				
        //Remove item button
        InvestigationRemovalButton = new Button("-", new Button.ClickListener() {
        	
			private static final long serialVersionUID = 5013426493875747495L;

			public void buttonClick(ClickEvent event) {
        		InvestigationData.removeItem(InvestigationData.getValue());
        		InvestigationData.select(null);  
        	}        	
        });
    
        InvestigationRemovalButton.setVisible(false);    
        bottomLeftCorner.addComponent(InvestigationRemovalButton); 
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
        return BBMRIvisibleCols;
	}
	
	private String[] initBBMRIList() {
		InvestigationData.setContainerDataSource(BBMRIData);
		InvestigationData.setVisibleColumns(BBMRIvisibleCols);
		InvestigationData.setSelectable(true);
		InvestigationData.setImmediate(true);
        
		InvestigationData.addListener(new Property.ValueChangeListener() {
            
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
                Object id = InvestigationData.getValue();
                InvestigationEditor.setItemDataSource(id == null ? null : InvestigationData
                        .getItem(id));
                InvestigationRemovalButton.setVisible(id != null);
            }
        });
        return BBMRIvisibleCols;
	}
	
	private void initFilteringControls() {
        for (final String pn : BBMRIvisibleCols) {
            final TextField sf = new TextField();
            bottomLeftCorner.addComponent(sf);
            sf.setWidth("100%");
            sf.setInputPrompt(pn);
            sf.setImmediate(true);
            bottomLeftCorner.setExpandRatio(sf, 1);
            sf.addListener(new Property.ValueChangeListener() {
                
				private static final long serialVersionUID = 7463427190739856945L;

				public void valueChange(ValueChangeEvent event) {
                	BBMRIData.removeContainerFilters(pn);
                    if (sf.toString().length() > 0 && !pn.equals(sf.toString())) {
                    	BBMRIData.addContainerFilter(pn, sf.toString(),
                                true, false);
                    }
                    getMainWindow().showNotification( "" + BBMRIData.size() + " matches found");
                }
            });
        }
    }

	private static IndexedContainer FillBBMRIData(Database db, CommonService ct) {
		
        IndexedContainer ic = new IndexedContainer();
        String value;
		
        for (String p : BBMRIFields) {
            ic.addContainerProperty(p, String.class, "");
        }
        
		System.out.println(">>>>>>Start");
		try {
			List <Biobank> Biobank = db.query(Biobank.class).find();
			List<Investigation> investigationID   = db.query(Investigation.class).find();
			List <Institute> Institute = db.query(Institute.class).find();
			List <BiobankPanel> BiobankPanel = db.query(BiobankPanel.class).find();
			List <OntologyTerm> Category = db.query(OntologyTerm.class).find();
			
			//List <CoordinatorsFormModel> Coordinators =  (List<CoordinatorsFormModel>) CoordinatorsFormModel.class.getDeclaredFields(); 

			List <Person> Person = db.query(Person.class).find();
			
			//List  <CoordinatorsFormModel> Coordinators = db.query(CoordinatorsFormModel.class).find();

			//experiment 
			java.lang.reflect.Field[] Coordinators =  CoordinatorsFormModel.class.getDeclaredFields(); 
			System.out.println(">>>>>>>>>>>" + Coordinators.getClass().getName());
			
			//TODO : Is iteration for investigation right for all entities? What about BiobankPanel? 
			for (int i=0; i<investigationID.size(); i++) {
				Object id = ic.addItem();
			        
				
				
				if ((value = BiobankPanel.get(i).getName()) != null) 					ic.getContainerProperty(id, "id").setValue(value);
				if ((value = Biobank.get(i).getName()) != null) 							ic.getContainerProperty(id, "Cohort").setValue(value);
				if ((value = Category.get(i).getName()) != null) 							ic.getContainerProperty(id, "Category").setValue(value); //TODO : this is not a join query. In molgenis this is fixed by xrefs
				if ((value = BiobankPanel.get(i).getGeneralComments()) != null) 	ic.getContainerProperty(id, "General comments").setValue(value);
				if ((value = Institute.get(i).getName())!=null ) 							ic.getContainerProperty(id,"Institutes").setValue(value); //TODO : this is not a join query .In molgenis this is fixed by xrefs
				if ((value = Person.get(i).getLastName())!=null)							ic.getContainerProperty(id,"Coordinators").setValue(value); //TODO : this is not a join query .In molgenis this is fixed by xrefs


				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return ic;
    }
	
	
	
	
	
}
