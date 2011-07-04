//package org.molgenis.framework.ui.html.test;
//
//import javax.swing.text.html.ListView;
//
//import org.molgenis.framework.ui.html.ActionInput;
//import org.molgenis.framework.ui.html.DateInput;
//import org.molgenis.framework.ui.html.HtmlForm;
//import org.molgenis.framework.ui.html.StringInput;
//
//public class TestListView
//{
//	public static void main(String[] args)
//	{
//		//of course you will get this from MOLGENIS
//		HtmlForm f1 = new HtmlForm();
//
//		StringInput nameInput1 = new StringInput("name","John Doe");
//		DateInput dateInput1 = new DateInput("birthday");
//		
//		ActionInput edit1 = new ActionInput("edit");
//		edit1.setIcon("generated-res/img/edit.gif");
//		//edit1.setParameter("id",1);
//		
//		f1.addInput(nameInput1,dateInput1);
//		f1.addAction(edit1);
//		
//		HtmlForm f2 = new HtmlForm();
//
//		StringInput nameInput2 = new StringInput("name","Jane Doe");
//		DateInput dateInput2 = new DateInput("birthday");
//		
//		f2.addInput(nameInput2,dateInput2);
//		
//		HtmlForm f3 = new HtmlForm();
//
//		StringInput nameInput3 = new StringInput("name","Jack Doe");
//		DateInput dateInput3 = new DateInput("birthday");
//		
//		ActionInput edit3 = new ActionInput("edit");
//		edit1.setIcon("generated-res/img/edit.gif");
//		//edit1.setParameter("id",1);
//		
//		f3.addInput(nameInput3,dateInput3);
//		f3.addAction(edit3);
//		
//		//here we do some real work
//		ListView v = new ListView("myList");
//		
//		//todo: make default icons so we don't have to be so difficult
//
//		v.addRow(f1,f2,f3);
//		v.setReadonly(false);
//		
//		System.out.println(v.toHtml());
//		
//		
//	}
//}
