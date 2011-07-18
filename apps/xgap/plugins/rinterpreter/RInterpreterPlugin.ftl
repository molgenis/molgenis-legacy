<#macro plugins_rinterpreter_RInterpreterPlugin screen>
<#assign model = screen.model>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">

	<label>Simple calculator using R</label><br>
	Uses org.molgenis.framework.R.RScript to execute input.<br>
	
	<input type="text" name="input" value="${model.getInput()}"/>
	
	<#--input type="submit" onclick="document.forms.${screen.name}.__action.value = 'interpret';" /-->
	
	<#assign pt1 = "<input type=\"submit\" value=\"">
	<#assign pt2 = "\" onclick=\"document.forms.${screen.name}.__action.value = '">
	<#assign pt3 = "';\" />">
	
	<table>
		<tr>
			<td>
				${pt1}7${pt2}add7${pt3}
			</td>
			<td>
				${pt1}8${pt2}add8${pt3}
			</td>
			<td>
				${pt1}9${pt2}add9${pt3}
			</td>
			<td>
				${pt1}*${pt2}addMultiply${pt3}
			</td>
			<td>
				${pt1}(${pt2}addLeftParenthesis${pt3}
			</td>
		</tr>
		<tr>
			<td>
				${pt1}4${pt2}add4${pt3}
			</td>
			<td>
				${pt1}5${pt2}add5${pt3}
			</td>
			<td>
				${pt1}6${pt2}add6${pt3}
			</td>
			<td>
				${pt1}/${pt2}addDivide${pt3}
			</td>
			<td>
				${pt1})${pt2}addRightParenthesis${pt3}
			</td>
		</tr>
		<tr>
			<td>
				${pt1}1${pt2}add1${pt3}
			</td>
			<td>
				${pt1}2${pt2}add2${pt3}
			</td>
			<td>
				${pt1}3${pt2}add3${pt3}
			</td>
			<td>
				${pt1}-${pt2}addMinus${pt3}
			</td>
			<td>
				${pt1}C${pt2}clear${pt3}
			</td>
		</tr>
		<tr>
			<td>
				${pt1}0${pt2}add0${pt3}
			</td>
			<td>
				${pt1}.${pt2}addDot${pt3}
			</td>
			<td>
				
			</td>
			<td>
				${pt1}+${pt2}addPlus${pt3}
			</td>
			<td>
				${pt1}=${pt2}solve${pt3}
			</td>
		</tr>
	</table>
	
	<br><br>
	
	</form>
</#macro>