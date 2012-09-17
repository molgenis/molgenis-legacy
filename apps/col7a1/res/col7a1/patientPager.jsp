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
<display:column media="csv excel pdf" property="patientIdentifier" title="Patient ID" escapeXml="false"/>
<display:column title="Phenotype" sortable="true">
	<c:out value="${current.phenotypeMajor}"/><c:if test="${fn:length(current.phenotypeSub) > 1}">, <c:out value="${current.phenotypeSub}"/></c:if>
</display:column>
<display:column media="html" title="Mutation" class="nowrap" sortable="false">
<div class="unwrapped">First Mutation</div>
<div class="unwrapped">Second Mutation</div>
</display:column>
<display:column media="html" title="cDNA change">
<div class="unwrapped">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${current.variantDTOList[0].identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantDTOList[0].cdnaNotation}"/></a>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantDTOList) > 1}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${current.variantDTOList[1].identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantDTOList[1].cdnaNotation}"/></a>
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${fn:contains(current.variantDTOList[0].inheritance, 'dominant')}">
			NA
		</c:when>
		<c:otherwise>
			Unknown
		</c:otherwise>
		</c:choose>
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Protein change">
<div class="unwrapped">
	<c:out value="${current.variantDTOList[0].aaNotation}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].aaNotation}"/>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Exon">
<div class="unwrapped">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${current.variantDTOList[0].exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantDTOList[0].exonName}"/></a>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantDTOList) > 1}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${current.variantDTOList[1].exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.variantDTOList[1].exonName}"/></a>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Consequence">
<div class="unwrapped">
	<c:out value="${current.variantDTOList[0].consequence}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].consequence}"/>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Reference">
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.publicationDTOList) > 0}">
	<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<a href="${current.pubmedURL}${publicationDTO.pubmedId}" title="${publicationDTO.title}" target="_new"><c:out value="${publicationDTO.firstAuthor} (${publicationDTO.year}) ${publicationDTO.journal}"/></a><br/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/><br/>
	<c:out value="${current.submitterDepartment}, ${current.submitterInstitute}, ${current.submitterCity}, ${current.submitterCountry}"/>
	</c:otherwise>
	</c:choose>
</div>
</display:column>

<display:column media="csv excel pdf" title="cDNA change 1">
	<c:out value="${current.variantDTOList[0].cdnaNotation}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Protein change 1">
	<c:out value="${current.variantDTOList[0].aaNotation}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Exon/Intron 1">
	<c:out value="${current.variantDTOList[0].exonName}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="Consequence 1">
	<c:out value="${current.variantDTOList[0].observedValue}" escapeXml="false"/>
</display:column>
<display:column media="csv excel pdf" title="cDNA change 2">
	<c:if test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].cdnaNotation}" escapeXml="false"/>
	</c:if>
</display:column>
<display:column media="csv excel pdf" title="Protein change 2">
	<c:if test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].aaNotation}" escapeXml="false"/>
	</c:if>
</display:column>
<display:column media="csv excel pdf" title="Exon/Intron 2">
	<c:if test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].exonName}" escapeXml="false"/>
	</c:if>
</display:column>
<display:column media="csv excel pdf" title="Consequence 2">
	<c:if test="${fn:length(current.variantDTOList) > 1}">
	<c:out value="${current.variantDTOList[1].observedValue}" escapeXml="false"/>
	</c:if>
</display:column>
<display:column media="csv excel pdf" title="PubMed ID">
<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<c:out value="${publicationDTO.pubmedId}" escapeXml="false"/>;
</c:forEach>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>
