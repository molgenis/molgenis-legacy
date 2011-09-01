<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="patientSummaryVOs" pagesize="20" export="true" sort="list" class="listtable" id="current">
<display:setProperty name="paging.banner.full"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0} <a href="{3}">Next</a> <a href="{4}">Last</a></span></display:setProperty>  
<display:setProperty name="paging.banner.first"><span class="pagelinks">{0} <a href="{3}">Next</a> <a href="{4}">Last</a> </span></display:setProperty>
<display:setProperty name="paging.banner.last"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0}</span></display:setProperty>
<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
<display:setProperty name="paging.banner.item_name" value="patient"/>
<display:setProperty name="paging.banner.items_name" value="patients"/>
<display:setProperty name="paging.banner.page.separator" value=" "/>
<display:setProperty name="export.banner"><div class="exportlinks">Export: {0}</div></display:setProperty>
<display:setProperty name="export.csv.filename" value="patients.csv"/>
<display:setProperty name="export.excel.filename" value="patients.xls"/>
<display:setProperty name="export.pdf.filename" value="patients.pdf"/>

<display:column title="No."><c:out value="${current_rowNum}"/></display:column>
<display:column property="patientIdentifier" title="Patient ID" sortable="true" headerClass="sortable" href="molgenis.do?__target=SearchPlugin&__action=showPatient&pid=#results" paramId="pid" paramProperty="patientIdentifier"/>
<display:column title="Phenotype" sortable="true">
	<c:out value="${current.phenotypeMajor}"/><c:if test="${fn:length(current.phenotypeSub) > 1}">, <c:out value="${current.phenotypeSub}"/></c:if>
</display:column>
<display:column media="html" title="cDNA change" sortable="true" headerClass="sortable">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${current.variantSummaryVOList[0].identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantSummaryVOList[0].cdnaNotation}"/></a>
</display:column>
<display:column media="html" title="Protein change" sortable="true" headerClass="sortable">
	<c:out value="${current.variantSummaryVOList[0].aaNotation}"/>
</display:column>
<display:column media="html" title="Exon" sortable="true" headerClass="sortable">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${current.variantSummaryVOList[0].exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantSummaryVOList[0].exonName}"/></a>
</display:column>
<display:column media="html" title="Pathogenicity" sortable="true" headerClass="sortable">
	<c:out value="${current.variantSummaryVOList[0].pathogenicity}"/>
</display:column>
<display:column media="html" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationVOList) > 0}">
	<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<a href="${current.pubmedURL}${publicationVO.pubmed}" target="_new"><c:out value="${publicationVO.name}"/></a><br/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>
	</c:otherwise>
	</c:choose>
</display:column>

<display:column media="csv excel pdf" title="cDNA change">
	<c:out value="${current.variantSummaryVOList[0].cdnaNotation}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Protein change">
	<c:out value="${current.variantSummaryVOList[0].aaNotation}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Exon/Intron">
	<c:out value="${current.variantSummaryVOList[0].exonName}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Pathogenicity">
	<c:out value="${current.variantSummaryVOList[0].pathogenicity}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Reference">
<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<c:out value="${publicationVO.name}" escapeXml="false"/>;
</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="PubMed ID">
<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<c:out value="PubMed ${publicationVO.pubmed}" escapeXml="false"/>;
</c:forEach>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>