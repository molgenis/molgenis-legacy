<#assign cols = model.visibleCols>
<#assign rows = model.visibleRows>
<#assign matrix = model.visibleMatrix>

<table>
<#-- column headers -->
<thead><tr><th>&nbsp;</th><#list cols?values as col><th>${col.toHtml()}</th></#list></tr></thead>
<#-- row headers plus row values -->
<tbody><#list rows?keys as rowkey>
<tr><td>${rows[rowkey].toHtml()}</td><#list matrix.getRowByName(rowkey) as row><td>${row}</td></#list></tr>
</#list></tbody>
</table>
