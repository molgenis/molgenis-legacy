<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="mutationSummaryVOList" pagesize="10" export="true" sort="list" class="listtable2" id="current">
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

<display:column media="html" title="Mutation ID">
<div class="unwrapped">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${current.identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.identifier}"/></a>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	+ 
	<c:if test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showMutation&mid=${patientVO.variantSummaryVOList[0].identifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${patientVO.variantSummaryVOList[0].identifier}"/></a>
	</c:if>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="cDNA change">
<div class="unwrapped">
	<c:out value="${current.cdnaNotation}"/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:out value="${patientVO.variantSummaryVOList[0].cdnaNotation}"/>
	</c:when>
	<c:otherwise>
	<c:out value="${patientVO.variantComment}"/>
	</c:otherwise>
	</c:choose>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Protein change">
<div class="unwrapped">
	<c:out value="${current.aaNotation}"/>
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:if test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:out value="${patientVO.variantSummaryVOList[0].aaNotation}"/>
	</c:if>
	<br/>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Exon/Intron">
<div class="unwrapped">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${current.exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${current.exonName}"/></a>
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:if test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=${patientVO.variantSummaryVOList[0].exonId}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${patientVO.variantSummaryVOList[0].exonName}"/></a>
	</c:if>
	<br/>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Consequence">
<div class="unwrapped">
	<c:out value="${current.consequence}"/>
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:if test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:out value="${patientVO.variantSummaryVOList[0].consequence}"/>
	</c:if>
	<br/>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Inheritance">
<div class="unwrapped">
	<c:out value="${current.inheritance}"/>
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:if test="${fn:length(patientVO.variantSummaryVOList) > 0}">
	<c:out value="${patientVO.variantSummaryVOList[0].inheritance}"/>
	</c:if>
	<br/>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Patient ID">
<div class="unwrapped">
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:url var="url" value="molgenis.do?__target=SearchPlugin&__action=showPatient&pid=${patientVO.patientIdentifier}#results"/>
	<a href="<c:out value="${url}"/>"><c:out value="${patientVO.patientIdentifier}"/></a>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Phenotype">
<div class="unwrapped">
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:out value="${patientVO.phenotypeMajor}"/><c:if test="${fn:length(patientVO.phenotypeSub) > 1}">, <c:out value="${patientVO.phenotypeSub}"/></c:if>
</div>
	</c:forEach>
</display:column>
<display:column media="html" title="Reference">
<div class="unwrapped">
	<br/>
</div>
	<c:forEach var="patientVO" items="${current.patientSummaryVOList}">
<div class="unwrapped">
	<c:choose>
	<c:when test="${fn:length(patientVO.publicationVOList) > 0}">
	<a href="${current.pubmedURL}${patientVO.publicationVOList[0].pubmed}" target="_new"><c:out value="${patientVO.publicationVOList[0].name}"/></a>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>, <c:out value="${patientVO.submitterDepartment}, ${patientVO.submitterInstitute}, ${patientVO.submitterCity}, ${patientVO.submitterCountry}"/>
	</c:otherwise>
	</c:choose>
</div>
	</c:forEach>
</display:column>

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