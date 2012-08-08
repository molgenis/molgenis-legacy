///* Date:        August 3, 2011
// * Template:	EasyPluginModelGen.java.ftl
// * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
// * 
// * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
// */
//
//package org.molgenis.compute.ui;
//
//import java.util.Date;
//
//import org.molgenis.framework.ui.EasyPluginModel;
//
///**
// * PbsSubmitApplicationModel takes care of all state and it can have helper methods to query the database.
// * It should not contain layout or application logic which are solved in View and Controller.
// * @See org.molgenis.framework.ui.ScreenController for available services.
// */
//public class PbsSubmitApplicationModel extends EasyPluginModel
//{
//	//a system veriable that is needed by tomcat
//	private static final long serialVersionUID = 1L;
//	//this string can be referenced from PbsSubmitApplicationView.ftl template as ${model.helloWorld}
//	public String helloWorld = "hello World";
//	//this date can be referenced from PbsSubmitApplicationView.ftl template as ${model.date}
//	public Date date = new Date();
//	
//	//another example, you can also use getInvestigations() and setInvestigations(...)
//	//public List<Investigation> investigations = new ArrayList<Investigation>();
//
//	public PbsSubmitApplicationModel(PbsSubmitApplication controller)
//	{
//		//each Model can access the controller to notify it when needed.
//		super(controller);
//	}
//	
//	
//}
