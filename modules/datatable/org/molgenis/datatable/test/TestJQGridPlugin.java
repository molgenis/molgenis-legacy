package org.molgenis.datatable.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.molgenis.datatable.plugin.JQGridPlugin;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

import com.google.gson.Gson;

public class TestJQGridPlugin {

	private JQGridPlugin	plugin;
	private Database		db;

	@Before
	public void setUp() throws DatabaseException {
		plugin = new JQGridPlugin("test", null);
		db = DatabaseFactory.create();
	}

	@Test
	public void testTreeOutput() throws Exception {
		// mock request and response
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);

		// servlet mock responses
		final Map<String, String> map = new HashMap<String, String>();
		map.put("__action", "download_json");
		map.put("__target", "jqGridView");
		map.put("Operation", "LOAD_TREE");
		for (final Entry<String, String> entry : map.entrySet()) {
			when(request.getParameter(entry.getKey())).thenReturn(entry.getValue());
		}
		when(request.getParameterMap()).thenReturn(map);
		when(request.getMethod()).thenReturn("GET");

		final ServletOutputStream mockOutstream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(mockOutstream);

		final Tuple molRequest = new MolgenisRequest(request, response);
		plugin.handleRequest(db, molRequest, System.out);

		verify(mockOutstream)
				.print("[{\"title\" : \"Country\", \"isFolder\": \"true\",\"children\" : "
						+ "[{\"title\" : \"Code\", \"path\" : \"Country.Code\"},{\"title\" : \"Name\", \"path\" : \"Country.Name\"},"
						+ "{\"title\" : \"Continent\", \"path\" : \"Country.Continent\"},{\"title\" : \"Region\", \"path\" : \"Country.Region\"},"
						+ "{\"title\" : \"SurfaceArea\", \"path\" : \"Country.SurfaceArea\"},{\"title\" : \"IndepYear\", \"path\" : \"Country.IndepYear\"},"
						+ "{\"title\" : \"Population\", \"path\" : \"Country.Population\"},"
						+ "{\"title\" : \"LifeExpectancy\", \"path\" : \"Country.LifeExpectancy\"},"
						+ "{\"title\" : \"GNP\", \"path\" : \"Country.GNP\"},{\"title\" : \"GNPOld\", \"path\" : \"Country.GNPOld\"},"
						+ "{\"title\" : \"LocalName\", \"path\" : \"Country.LocalName\"},"
						+ "{\"title\" : \"GovernmentForm\", \"path\" : \"Country.GovernmentForm\"},"
						+ "{\"title\" : \"HeadOfState\", \"path\" : \"Country.HeadOfState\"},"
						+ "{\"title\" : \"Capital\", \"path\" : \"Country.Capital\"},{\"title\" : \"Code2\", \"path\" : \"Country.Code2\"}]},"
						+ "{\"title\" : \"City\", \"isFolder\": \"true\",\"children\" : [{\"title\" : \"ID\", \"path\" : \"City.ID\"},"
						+ "{\"title\" : \"Name\", \"path\" : \"City.Name\"},{\"title\" : \"CountryCode\", \"path\" : \"City.CountryCode\"},"
						+ "{\"title\" : \"District\", \"path\" : \"City.District\"},{\"title\" : \"Population\", \"path\" : \"City.Population\"}]}]");
		verifyNoMoreInteractions(mockOutstream);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGridOutput() throws Exception {
		// mock request and response
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);

		// servlet mock responses
		final Map<String, String> map = new HashMap<String, String>();
		map.put("__action", "download_json");
		map.put("__target", "jqGridView");
		map.put("Operation", "LOAD_CONFIG");
		for (final Entry<String, String> entry : map.entrySet()) {
			when(request.getParameter(entry.getKey())).thenReturn(entry.getValue());
		}
		when(request.getParameterMap()).thenReturn(map);
		when(request.getMethod()).thenReturn("GET");

		final ServletOutputStream mockOutstream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(mockOutstream);

		final Tuple molRequest = new MolgenisRequest(request, response);
		plugin.handleRequest(db, molRequest, mockOutstream);

		final String out = "{\"id\":\"test\",\"url\":\"molgenis.do?__target\\u003dtest\\u0026__action\\u003ddownload_json\",\"pager\":\"#testPager\",\"datatype\":\"json\",\"colNames\":[\"Code\",\"Name\",\"Continent\",\"Region\",\"SurfaceArea\",\"IndepYear\",\"Population\",\"LifeExpectancy\",\"GNP\",\"GNPOld\",\"LocalName\",\"GovernmentForm\",\"HeadOfState\",\"Capital\",\"Code2\",\"ID\",\"Name\",\"CountryCode\",\"District\",\"Population\"],\"colModel\":[{\"name\":\"Country.Code\",\"index\":\"Country.Code\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Code\",\"isFolder\":false,\"path\":\"Country.Code\"},{\"name\":\"Country.Name\",\"index\":\"Country.Name\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Name\",\"isFolder\":false,\"path\":\"Country.Name\"},{\"name\":\"Country.Continent\",\"index\":\"Country.Continent\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Continent\",\"isFolder\":false,\"path\":\"Country.Continent\"},{\"name\":\"Country.Region\",\"index\":\"Country.Region\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Region\",\"isFolder\":false,\"path\":\"Country.Region\"},{\"name\":\"Country.SurfaceArea\",\"index\":\"Country.SurfaceArea\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":true,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.SurfaceArea\",\"isFolder\":false,\"path\":\"Country.SurfaceArea\"},{\"name\":\"Country.IndepYear\",\"index\":\"Country.IndepYear\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":true,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.IndepYear\",\"isFolder\":false,\"path\":\"Country.IndepYear\"},{\"name\":\"Country.Population\",\"index\":\"Country.Population\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":true,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Population\",\"isFolder\":false,\"path\":\"Country.Population\"},{\"name\":\"Country.LifeExpectancy\",\"index\":\"Country.LifeExpectancy\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":true,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.LifeExpectancy\",\"isFolder\":false,\"path\":\"Country.LifeExpectancy\"},{\"name\":\"Country.GNP\",\"index\":\"Country.GNP\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":true,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.GNP\",\"isFolder\":false,\"path\":\"Country.GNP\"},{\"name\":\"Country.GNPOld\",\"index\":\"Country.GNPOld\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":true,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.GNPOld\",\"isFolder\":false,\"path\":\"Country.GNPOld\"},{\"name\":\"Country.LocalName\",\"index\":\"Country.LocalName\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.LocalName\",\"isFolder\":false,\"path\":\"Country.LocalName\"},{\"name\":\"Country.GovernmentForm\",\"index\":\"Country.GovernmentForm\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.GovernmentForm\",\"isFolder\":false,\"path\":\"Country.GovernmentForm\"},{\"name\":\"Country.HeadOfState\",\"index\":\"Country.HeadOfState\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.HeadOfState\",\"isFolder\":false,\"path\":\"Country.HeadOfState\"},{\"name\":\"Country.Capital\",\"index\":\"Country.Capital\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":true,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Capital\",\"isFolder\":false,\"path\":\"Country.Capital\"},{\"name\":\"Country.Code2\",\"index\":\"Country.Code2\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"Country.Code2\",\"isFolder\":false,\"path\":\"Country.Code2\"},{\"name\":\"City.ID\",\"index\":\"City.ID\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":true,\"email\":false,\"date\":false,\"time\":false},\"title\":\"City.ID\",\"isFolder\":false,\"path\":\"City.ID\"},{\"name\":\"City.Name\",\"index\":\"City.Name\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"City.Name\",\"isFolder\":false,\"path\":\"City.Name\"},{\"name\":\"City.CountryCode\",\"index\":\"City.CountryCode\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"City.CountryCode\",\"isFolder\":false,\"path\":\"City.CountryCode\"},{\"name\":\"City.District\",\"index\":\"City.District\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"bw\",\"bn\",\"ew\",\"en\",\"cn\",\"nc\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":false,\"email\":false,\"date\":false,\"time\":false},\"title\":\"City.District\",\"isFolder\":false,\"path\":\"City.District\"},{\"name\":\"City.Population\",\"index\":\"City.Population\",\"width\":100,\"sortable\":true,\"search\":true,\"searchoptions\":{\"required\":true,\"stype\":\"text\",\"sopt\":[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"],\"dataInit\":\"function(elem){ $(elem).datepicker({dateFormat:\\\"mm/dd/yyyy\\\"});}}\"},\"searchrules\":{\"number\":false,\"integer\":true,\"email\":false,\"date\":false,\"time\":false},\"title\":\"City.Population\",\"isFolder\":false,\"path\":\"City.Population\"}],\"rowNum\":10,\"rowList\":[10,20,30],\"viewrecords\":true,\"sortorder\":\"desc\",\"caption\":\"test\",\"autowidth\":true,\"sortable\":true,\"jsonReader\":{\"id\":\"Name\",\"repeatitems\":false},\"settings\":{\"del\":false,\"add\":false,\"edit\":false,\"search\":true},\"toolbar\":[true,\"top\"]}";

		// test whether the desired json data is written
		verify(mockOutstream).println(out);

		// some tests to check the structure of the json
		final Gson gson = new Gson();
		final Map<String, Object> map2 = gson.fromJson(out, Map.class);
		assertEquals("test", map2.get("id"));
		assertEquals("molgenis.do?__target\u003dtest\u0026__action\u003ddownload_json", map2.get("url"));
		assertEquals(Arrays.asList("Code", "Name", "Continent", "Region", "SurfaceArea", "IndepYear", "Population", "LifeExpectancy", "GNP",
				"GNPOld", "LocalName", "GovernmentForm", "HeadOfState", "Capital", "Code2", "ID", "Name", "CountryCode", "District", "Population"),
				map2.get("colNames"));
		final Map<?, ?> countryCodeColumn = ((ArrayList<Map<?, ?>>) map2.get("colModel")).get(0);
		assertEquals("Country.Code", countryCodeColumn.get("name"));
		assertEquals("Country.Code", countryCodeColumn.get("index"));
		assertEquals("Country.Code", countryCodeColumn.get("title"));
		assertEquals("Country.Code", countryCodeColumn.get("path"));
		assertEquals(Arrays.asList("eq", "ne", "bw", "bn", "ew", "en", "cn", "nc"), ((Map<?, ?>) countryCodeColumn.get("searchoptions")).get("sopt"));
		assertEquals(10.0, map2.get("rowNum"));
		assertEquals(Arrays.asList(10.0, 20.0, 30.0), map2.get("rowList"));
		assertEquals("desc", map2.get("sortorder"));
	}

	@Test
	public void testRenderData() throws Exception {
		// mock request and response
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);

		// servlet mock responses
		final Map<String, String> map = new HashMap<String, String>();
		map.put("__action", "download_json");
		map.put("__target", "jqGridView");
		map.put("Operation", "RENDER_DATA");
		map.put("rows", "10");
		map.put("page", "0");
		for (final Entry<String, String> entry : map.entrySet()) {
			when(request.getParameter(entry.getKey())).thenReturn(entry.getValue());
		}
		when(request.getParameterMap()).thenReturn(map);
		when(request.getMethod()).thenReturn("GET");

		final ServletContext context = mock(ServletContext.class);
		final HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getServletContext()).thenReturn(context);

		final ServletOutputStream mockOutstream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(mockOutstream);

		final Tuple molRequest = new MolgenisRequest(request, response);
		plugin.handleRequest(db, molRequest, mockOutstream);
	}
}
