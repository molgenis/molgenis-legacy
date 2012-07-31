<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="publicationDTOList" pagesize="20" defaultsort="4" defaultorder="descending" export="true" sort="list" class="listtable" id="current">
<display:setProperty name="paging.banner.full"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0} <a href="{3}">Next</a> <a href="{4}">Last</a></span></display:setProperty>  
<display:setProperty name="paging.banner.first"><span class="pagelinks">{0} <a href="{3}">Next</a> <a href="{4}">Last</a> </span></display:setProperty>
<display:setProperty name="paging.banner.last"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0}</span></display:setProperty>
<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
<display:setProperty name="paging.banner.item_name" value="publication"/>
<display:setProperty name="paging.banner.items_name" value="publications"/>
<display:setProperty name="paging.banner.page.separator" value=" "/>
<display:setProperty name="export.banner"><div class="exportlinks">Export: {0}</div></display:setProperty>
<display:setProperty name="export.csv.filename" value="publications.csv"/>
<display:setProperty name="export.excel.filename" value="publications.xls"/>
<display:setProperty name="export.pdf.filename" value="publications.pdf"/>

<display:column title="Title" property="title" sortable="true"/>
<display:column title="Authors" property="authors" sortable="true" maxWords="6"/>
<display:column title="Journal" property="journal" sortable="true"/>
<display:column title="Year" property="year" sortable="true"/>
<display:column media="html" title="External link" sortable="true">
	<a href="${current.pubmedUrl}" title="${current.title}" target="_new">PM:<c:out value="${current.pubmedId}"/></a>
</display:column>
<display:column media="csv excel pdf" title="External link" sortable="true">
	PM:<c:out value="${current.pubmedId}"/>
</display:column>
</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>