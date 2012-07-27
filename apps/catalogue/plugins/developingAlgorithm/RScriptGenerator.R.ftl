
${model.getTestString()}

${model.getHeader()}

<#assign listOfVariable = model.getListOfVariable()>
<#list listOfVariable?keys as variable>

${listOfVariable[variable]}

</#list>

${model.getAddingObservedValue()}

${model.getEndOfScript()}