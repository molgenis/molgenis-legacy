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
<display:column media="html" property="patientIdentifier" title="Patient ID" sortable="true" headerClass="sortable" href="molgenis.do?__target=SearchPlugin&__action=showPatient&pid=#results" paramId="pid" paramProperty="patientIdentifier"/>
<display:column media="csv excel pdf" property="patientIdentifier" title="Patient ID" sortable="true" headerClass="sortable"/>
<display:column title="Onset" sortable="true">
	<c:out value="${current.phenotypeMajor}"/><c:if test="${fn:length(current.phenotypeSub) > 1}">, <c:out value="${current.phenotypeSub}"/></c:if>
</display:column>
<display:column media="html" title="cDNA change" sortable="true" headerClass="sortable" sortProperty="gdnaStart">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${variantDTO.identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${variantDTO.cdnaNotation}"/></a><br>
	</c:forEach>
</display:column>
<display:column media="html" title="Protein change" sortable="true" headerClass="sortable" sortProperty="gdnaStart">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.aaNotation}"/><br>
	</c:forEach>
</display:column>
<display:column media="html" title="Exon" sortable="true" headerClass="sortable">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${variantDTO.exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${variantDTO.exonName}"/></a><br>
	</c:forEach>
</display:column>
<display:column media="html" title="Consequence" sortable="true" headerClass="sortable">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.observedValue}"/><br>
	</c:forEach>
</display:column>
<display:column media="html" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationDTOList) > 0}">
	<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<a href="${current.pubmedURL}${publicationDTO.pubmedId}" title="${publicationDTO.title}" target="_new"><c:out value="PM:${publicationDTO.pubmedId}"/></a><br/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>
	</c:otherwise>
	</c:choose>
</display:column>

<display:column media="csv excel pdf" title="cDNA change">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.cdnaNotation}" escapeXml="false"/>
	</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Protein change">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.aaNotation}" escapeXml="false"/>
	</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Exon/Intron">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.exonName}" escapeXml="false"/>
	</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Consequence">
	<c:forEach var="variantDTO" items="${current.variantDTOList}">
	<c:out value="${variantDTO.observedValue}" escapeXml="false"/>
	</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Reference">
<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<c:out value="${publicationDTO.name}" escapeXml="false"/>;
</c:forEach>
</display:column>
<display:column media="csv excel pdf" title="PubMed ID">
<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<c:out value="PM:${publicationDTO.pubmedId}" escapeXml="false"/>;
</c:forEach>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>