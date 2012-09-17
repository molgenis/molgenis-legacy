<#macro org_molgenis_scrum_plugins_BurndownChart screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!-- the current task -->
	<input type="hidden" name="__task">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<canvas id="burndownchart" width="10" height="10" style="border: solid thin black; background: white;"></canvas>
<script type="application/javascript">
/* graph is made up of 25px tiles; 
   width = length(values[]), 
   height = max(values[]) */
   
var burndown = eval(${screen.getBurndownJSON()}); //[25,19,15,17,13];
var unplanned = eval(${screen.getUnplannedJSON()}); //[0,0,0,2,2];
var xlabels = eval(${screen.getDaysJSON()}); //['1/3','2/3','5/3','6/3','7/3'];

/* method to calculate max value of an array */
Array.prototype.max = function() {
	var max = this[0];
	var len = this.length;
	for (var i = 1; i < len; i++) if (this[i] > max) max = this[i];
	return max;
}   
   
//basic canvas settings
var canvas = document.getElementById("burndownchart");
var padding = 30;
var ygrid = 20;
var xgrid = 40;
var max = burndown.max() + 1;
var width = xlabels.length;
var graphHEIGHT = ygrid * max;
var graphWIDTH = xgrid * width;

//update the canvas size
canvas.width = graphWIDTH+padding*2;
canvas.height = graphHEIGHT+padding*2;
canvas.margin = 10;

if (canvas.getContext) {
	var ctx = canvas.getContext("2d");
	
	ctx.strokeStyle = "black";
	ctx.lineWidth = 2;
	
	/* draw y axis */
	ctx.beginPath();
	ctx.moveTo(padding,padding);
	ctx.lineTo(padding, graphHEIGHT + padding);
	ctx.stroke();
	ctx.closePath();
	
	/* x axis */
	ctx.beginPath();
	ctx.moveTo(padding, graphHEIGHT + padding);
	ctx.lineTo(graphWIDTH + padding, graphHEIGHT + padding);
	ctx.stroke();	
	ctx.closePath();
	
	/* draw y-grid */
	ctx.lineWidth = 0.5;
	ctx.strokeStyle = "grey";
	for(i = 0; i < max; i++)
	{
		ctx.beginPath();
		ctx.moveTo(padding, i * ygrid + padding);
		ctx.lineTo(graphWIDTH + padding, i * ygrid + padding);
		ctx.stroke();
		ctx.closePath();	
		
		ctx.fillText  (max - i, 15, i * ygrid + padding + 5);
	}
	
	/* draw x-grid */
	for(i = 0; i <= width; i++)
	{
		ctx.beginPath();
		ctx.moveTo(i * xgrid + padding, padding);
		ctx.lineTo(i * xgrid + padding, graphHEIGHT + padding);
		ctx.stroke();
		ctx.closePath();		
		
		if(i < width) 
		{
			ctx.fillText(xlabels[i], i * xgrid + padding - 10, graphHEIGHT + padding + 20);
		}
	}	
	
	/* the burndown */
	
	ctx.lineWidth = 3;
	ctx.strokeStyle = "green";
	
	ctx.beginPath();
	ctx.moveTo(padding, padding + graphHEIGHT - burndown[0] * ygrid);
	for(var i = 0; i < burndown.length; i++) {
		ctx.lineTo(padding + i * xgrid, padding + graphHEIGHT - burndown[i] * ygrid);
	}
	ctx.stroke();	
	ctx.closePath();
	
	/* the unplanned (doesn't work properly, commented out for now)
	ctx.lineWidth = 3;
	ctx.strokeStyle = "blue";
	
	ctx.beginPath();
	var moveto = true;
	for(var i = 0; i < burndown.length; i++) {
		if(unplanned[i] > 0){
			if(moveto) {
				ctx.moveTo(padding + (i-1)*xgrid, padding + graphHEIGHT);
				moveto = false;
			}
			ctx.lineTo(padding + i*xgrid, padding + graphHEIGHT - unplanned[i]*ygrid);
		}

	}
	ctx.stroke();	
	ctx.closePath();*/
}
</script>

			</div>
		</div>
	</div>
</form>

</#macro>
