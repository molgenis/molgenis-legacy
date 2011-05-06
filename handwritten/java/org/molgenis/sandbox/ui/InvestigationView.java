//package org.molgenis.sandbox.ui;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import javax.servlet.http.HttpSession;
//import javax.sql.DataSource;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.framework.ui.FormModel;
//import org.molgenis.framework.ui.UserInterface;
//import org.molgenis.framework.ui.html.HtmlForm;
//import org.molgenis.framework.ui.html.HtmlInput;
//
//import com.vaadin.Application;
//import com.vaadin.data.Container;
//import com.vaadin.data.Item;
//import com.vaadin.data.Property;
//import com.vaadin.terminal.gwt.server.WebApplicationContext;
//import com.vaadin.ui.Label;
//import com.vaadin.ui.MenuBar;
//import com.vaadin.ui.Table;
//import com.vaadin.ui.Window;
//
//public class InvestigationView extends Application
//{
//	private static final long serialVersionUID = -8644242899269779294L;
//
//	@Override
//	public void init()
//	{
//		try
//		{
//			// this guy will reuse the generated Investigations form for logic,
//			// map to handlerequest.
//			WebApplicationContext context = (WebApplicationContext) getContext();
//			HttpSession session = context.getHttpSession();
//			UserInterface<?> molgenis = (UserInterface<?>) session
//					.getAttribute("application");
//
//			// will give nullpointer if not run inside MOLGENIS!!!
//
//			app.ui.InvestigationsFormModel inv = (app.ui.InvestigationsFormModel) molgenis
//					.get("Investigations");
//
//			// get the database
//			DataSource dataSource = (DataSource) session.getServletContext()
//					.getAttribute("DataSource");
//			Database db = new app.JDBCDatabase(dataSource, new File("ngs"));
//
//			inv.getController().reload(db);
//
//			Window main = new Window("My first Investigation Viewer");
//			this.setMainWindow(main);
//
//			final MenuBar menubar = new MenuBar();
//			main.addComponent(menubar);
//
//			final Label myLabel = new Label();
//			myLabel.setValue("hello " + inv.getCurrent());
//			main.addComponent(myLabel);
//
//			Table table = new Table("Investigations");
//
//			/*
//			 * Define the names and data types of columns. The "default value"
//			 * parameter is meaningless here.
//			 */
//			for (String header : inv.getHeaders())
//			{
//				table.addContainerProperty(header, String.class, null);
//			}
//			setValues(inv, table);
//			main.addComponent(table);
//		}
//		catch (DatabaseException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public void setValues(FormModel<?> molgenisForm, Table vaadinTable)
//	{
//		// add the values
//		int index = 0;
//		for (HtmlForm form : molgenisForm.getRecordInputs())
//		{
//			List<String> values = new ArrayList<String>();
//			for (HtmlInput input : form.getInputs())
//			{
//				values.add(input.getValue());
//			}
//			vaadinTable.addItem(values.toArray(), index++);
//		}
//	}
//
//	private class MolgenisDataContainer implements Container,
//			Container.Ordered, Container.Indexed, Container.Sortable,
//			Container.ItemSetChangeNotifier
//	{
//		//the Form of MOLGENIS that is adapted here.
//		FormModel molgenisForm = null;
//		
//		private ArrayList<String> propertyIds = new ArrayList<String>();
//		//private LinkedHashMap<Object, RowMapItem> itemMap = new LinkedHashMap<Object, RowMapItem>();
//		private LinkedHashMap<String, Class> propertyTypes = new LinkedHashMap<String, Class>();
//		private ArrayList<Object> sortableContainerPropertyIds = new ArrayList<Object>();
//
//		public MolgenisDataContainer()
//		{
//
//		}
//
//		@Override
//		public boolean addContainerProperty(Object propertyId, Class<?> type,
//				Object defaultValue) throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public Object addItem() throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Item addItem(Object itemId) throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public boolean containsId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public Property getContainerProperty(Object itemId, Object propertyId)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Collection<?> getContainerPropertyIds()
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Item getItem(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Collection<?> getItemIds()
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Class<?> getType(Object propertyId)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public boolean removeAllItems() throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean removeContainerProperty(Object propertyId)
//				throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean removeItem(Object itemId)
//				throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public int size()
//		{
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public Object addItemAfter(Object previousItemId)
//				throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Item addItemAfter(Object previousItemId, Object newItemId)
//				throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Object firstItemId()
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public boolean isFirstId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean isLastId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public Object lastItemId()
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Object nextItemId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Object prevItemId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Object addItemAt(int index) throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Item addItemAt(int index, Object newItemId)
//				throws UnsupportedOperationException
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Object getIdByIndex(int index)
//		{
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int indexOfId(Object itemId)
//		{
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public Collection<?> getSortableContainerPropertyIds()
//		{
//			List<String> result = new ArrayList<String>();
//			try
//			{
//				for(HtmlInput input: this.molgenisForm.getNewRecordForm().getInputs())
//				{
//					result.add(input.getName());
//				}
//			}
//			catch (DatabaseException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return result;
//		}
//
//		@Override
//		public void sort(Object[] propertyId, boolean[] ascending)
//		{
//			for(int i = 0; i < propertyId.length; i++)
//			{
//				this.molgenisForm.setSort(propertyId[i].toString());
//				this.molgenisForm.setSortMode(ascending[i] ? Operator.SORTASC : Operator.SORTDESC);
//			}
//
//		}
//
//		@Override
//		public void addListener(ItemSetChangeListener listener)
//		{
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void removeListener(ItemSetChangeListener listener)
//		{
//			// TODO Auto-generated method stub
//
//		}
//
//	}
//}
