<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="sessionScope.patientSummaryVOs" pagesize="20" export="true" sort="list" class="listtable" id="current">  
<display:setProperty name="paging.banner.item_name" value="patient"/>
<display:setProperty name="paging.banner.items_name" value="patients"/>
<display:setProperty name="export.csv.filename" value="patients.csv"/>
<display:setProperty name="export.excel.filename" value="patients.xls"/>
<display:setProperty name="export.pdf.filename" value="patients.pdf"/>

<display:column title="No."><c:out value="${current_rowNum}"/></display:column>
<display:column property="patientIdentifier" title="Patient ID" sortable="true" headerClass="sortable" href="molgenis.do?__target=SearchPlugin&__action=showPatient&pid=#results" paramId="pid" paramProperty="patientIdentifier"/>
<display:column title="Phenotype" sortable="true">
	<c:out value="${current.phenotypeMajor}"/>, <c:out value="${current.phenotypeSub}"/>
</display:column>
<display:column media="html" title="Mutation" class="nowrap" sortable="false">
<div class="unwrapped">First Mutation</div>
<div class="unwrapped">Second Mutation</div>
</display:column>
<display:column media="html" title="cDNA change">
<div class="unwrapped">
	<c:out value="${current.variantSummaryVOList[0].cdnaNotation}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].cdnaNotation}"/>
	</c:when>
	<c:otherwise>
	<c:out value="${current.variantComment}"/>
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Protein change">
<div class="unwrapped">
	<c:out value="${current.variantSummaryVOList[0].aaNotation}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].aaNotation}"/>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Exon">
<div class="unwrapped">
	<c:out value="${current.variantSummaryVOList[0].exonName}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].exonName}"/>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Consequence">
<div class="unwrapped">
	<c:out value="${current.variantSummaryVOList[0].consequence}"/>
</div>
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].consequence}"/>
	</c:when>
	<c:otherwise>
	&nbsp;
	</c:otherwise>
	</c:choose>
</div>
</display:column>
<display:column media="html" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationVOList) > 0}">
	<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<a href="${current.pubmedURL}${publicationVO.pubmed}" target="_new"><c:out value="${publicationVO.name}"/></a><br/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/><br/>
	<c:if test="current.submitterDepartment != ''">
	<c:out value="${current.submitterDepartment}, ${current.submitterInstitute}, ${current.submitterCity}, ${current.submitterCountry}"/>
	</c:if>
	</c:otherwise>
	</c:choose>
</display:column>

<display:column media="csv excel" title="cDNA change 1">
	<c:out value="${current.variantSummaryVOList[0].cdnaNotation}"/>
</display:column>
<display:column media="csv excel" title="Protein change 1">
	<c:out value="${current.variantSummaryVOList[0].aaNotation}"/>
</display:column>
<display:column media="csv excel" title="Exon/Intron 1">
	<c:out value="${current.variantSummaryVOList[0].exonName}"/>
</display:column>
<display:column media="csv excel" title="Consequence 1">
	<c:out value="${current.variantSummaryVOList[0].consequence}"/>
</display:column>
<display:column media="csv excel" title="cDNA change 2">
	<c:if test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].cdnaNotation}"/>
	</c:if>
</display:column>
<display:column media="csv excel" title="Protein change 2">
	<c:if test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].aaNotation}"/>
	</c:if>
</display:column>
<display:column media="csv excel" title="Exon/Intron 2">
	<c:if test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].exonName}"/>
	</c:if>
</display:column>
<display:column media="csv excel" title="Consequence 2">
	<c:if test="${fn:length(current.variantSummaryVOList) > 1}">
	<c:out value="${current.variantSummaryVOList[1].consequence}"/>
	</c:if>
</display:column>
<display:column media="csv excel" title="Reference">
<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<c:out value="${publicationVO.name}"/>;
</c:forEach>
</display:column>
<display:column media="csv excel" title="PubMed ID">
<c:forEach var="publicationVO" items="${current.publicationVOList}">
	<c:out value="PubMed ${publicationVO.pubmed}"/>;
</c:forEach>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>