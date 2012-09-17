<#macro screens_CalculateDesignScreen screen>
<hr>

<#if screen.isBCalculationFail() >


<i>	<h2>
		Your calculation failed!
	</h2>
	<h3> 
		Please review your parameters and push BACK to try again.
	</h3>
</i>
<hr>

<#else>

<h2><i>
 	<#if screen.getBCooking() >				
 		Processing...  <br>
 		<br>
 		<center> <table border=0>
 		<tr>
 			<td> Current progress: </td>
 			<td> ${screen.getProgressPercentage()} % <img src="images/wait16.gif"/></td>
 		</tr>
 		<tr>
 			<td> Estimated waiting time: </td>
 			<td> ${screen.getWaitingTime()} </td>
 		</tr>
 		<!--<tr>
 			<td> Estimated finish time: </td>
 			<td> ${screen.getEstimatedEndTime()} </td>
 		</tr>-->
 		<tr><td colspan="2">R-script used:<br/>
 		<div style="font:menu">${screen.getRScript()}</div></tr></tr>
 		</table></center> 			
	<#else>
		Processing...<br>
 		<br>
 		<center> <table border=0>
 		<tr>
 			<td> Current progress: </td>
 			<td> 0 % <img src="images/wait16.gif"/></td>
 		</tr>
 		<tr>
 			<td> Estimated waiting time: </td>
 			<td> estimating... </td>
 		</tr>
 		<!--<tr>
 			<td> Estimated finish time: </td>
 			<td> estimating... </td>
 		</tr>-->
 		</table></center> 				
	</#if>
</i></h2>
<hr>
<center><h5>This page will automatically refresh in 5 secs.</h5></center>

</#if>

</#macro>