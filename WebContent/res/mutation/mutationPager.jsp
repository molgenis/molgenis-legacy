<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="mutationSummaryVOList" pagesize="20" export="true" sort="list" class="listtable" id="current">
<display:setProperty name="paging.banner.full"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0} <a href="{3}">Next</a> <a href="{4}">Last</a></span></display:setProperty>  
<display:setProperty name="paging.banner.first"><span class="pagelinks">{0} <a href="{3}">Next</a> <a href="{4}">Last</a> </span></display:setProperty>
<display:setProperty name="paging.banner.last"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0}</span></display:setProperty>
<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
<display:setProperty name="paging.banner.item_name" value="patient"/>
<display:setProperty name="paging.banner.items_name" value="patients"/>
<display:setProperty name="paging.banner.page.separator" value=" "/>
<display:setProperty name="export.banner"><div class="exportlinks">Export: {0}</div></display:setProperty>
<display:setProperty name="export.csv.filename" value="mutations.csv"/>
<display:setProperty name="export.excel.filename" value="mutations.xls"/>
<display:setProperty name="export.pdf.filename" value="mutations.pdf"/>

<display:column media="html" property="identifier" title="Mutation ID" sortable="true" headerClass="sortable" href="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=#results" paramId="mid" paramProperty="identifier"/>
<display:column property="cdnaNotation" title="cDNA change" sortable="true" headerClass="sortable"/>
<display:column property="aaNotation" title="Protein change" sortable="true" headerClass="sortable"/>
<display:column property="exonName" title="Exon/Intron" sortable="true" headerClass="sortable"/>
<display:column property="pathogenicity" title="Pathogenicity" sortable="true" headerClass="sortable"/>
<display:column media="html" title="Patient ID">
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showPatient&pid=${patientVO.patientIdentifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${patientVO.patientIdentifier}"/></a>
	</c:forEach>
</display:column>
<display:column media="html" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationVOList) > 0}">
	<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<a href="${current.pubmedURL}${publicationVO.pubmedId}" title="${publicationVO.title}" target="_new"><c:out value="PM:${publicationVO.pubmedId}"/></a><br/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>
	</c:otherwise>
	</c:choose>
</display:column>

<display:column media="csv excel pdf" property="identifier" title="Mutation ID" sortable="true" headerClass="sortable"/>
<display:column media="csv excel pdf" title="Patient ID">
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}"><c:out value="${patientVO.patientIdentifier}"/> </c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationVOList) > 0}">
	<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<c:out value="${publicationVO.name}"/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>
	</c:otherwise>
	</c:choose>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>