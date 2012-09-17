function showXrefInput(input, xref_entity, xref_field, xref_label){
	showXrefInput(input, xref_entity, xref_field, xref_label, null);
}

function showXrefInput(input, xref_entity, xref_field, xref_label, xref_filters) {
	var id = input.id + "_" + input.form.name;

	//hide previously open div
	if (window.xrefInputDiv && window.xrefInputDiv.id != id)
	{
		//alert("hiding "+window.xrefInputDiv.id);
		window.xrefInputDiv.style.display = "none";
	}

	//create if not exists
	if (document.getElementById(id) == null) {
		//alert("create"); 
		var myInput = new XrefInput(input, xref_entity, xref_field, xref_label, xref_filters);
		window.xrefInputDiv = myInput.xrefDiv;		
	} 
	else
	{
		window.xrefInputDiv = document.getElementById(id);
			
		//hide if shown,
		if (window.xrefInputDiv.style.display == "block")
		{
			window.xrefInputDiv.style.display = "none";
			input.focus();
			input.blur();
		}
		// else show 
		else
		{
			window.xrefInputDiv.style.display = "block";
			input.focus();
			input.blur();			
			searchbox = xrefInputDiv.getElementsByTagName("input")[0];
			searchbox.focus();		
	}
	}
}

//constructor
function XrefInput(input, xref_entity, xref_field, xref_label, xref_filters) {
	//alert("constructor"); 
	this.init(input, xref_entity, xref_field, xref_label, xref_filters);
}

//define class prototype
XrefInput.prototype = {		
	init : function(input, xref_entity, xref_field, xref_label, xref_filters) {
		//alert("init");
	this.input = input;
	
	this.xref_entity = xref_entity;
	this.xref_field = xref_field;
	this.xref_label = xref_label;
	this.xref_filters = xref_filters;

	//create the xref div
	this.xrefDiv = document.createElement("div");
	this.xrefDiv.className = "xrefinput";
	this.xrefDiv.style.display = "block";
	this.xrefDiv.id = input.id + "_" + input.form.name;
	
	//add the div as child of the input
	this.input.parentNode.insertBefore(this.xrefDiv, this.input.nextSibling);
	this.xrefDiv.setAttribute("XrefInputObject", this); //???

	//add the search box
	this.searchLabel = document.createElement("label");
	this.searchLabel.appendChild(document.createTextNode("search:"));
	this.xrefDiv.appendChild(this.searchLabel);
	this.searchInput = document.createElement("input");
	this.xrefDiv.appendChild(this.searchInput);
	this.searchInput.focus();
	
	this.xrefDiv.appendChild(document.createElement("br"));

	//initialize with current selection if any 
	this.xrefDiv.value = this.input.value;
	if(this.input.options.length > 0)
	{
		this.searchInput.value = this.input.options[0].text;
	} 


	//add a handler to the search box to update select when changed 
	var _this = this;
	this.addEvent(this.searchInput, "keyup", function(e) {
		_this.reload();
		
		//if only one option auto-select
		if(_this.selectInput.options.length == 1)
		{
			_this.selectInput.options[0].style.background = "blue";
			_this.selectInput.options[0].style.color = "white";
		}
		
		//on 'enter' and if only one option, choose that
		if( (e.which == 13 || e.keyCode == 13) && _this.selectInput.options.length == 1)
		{
			_this.selectInput.selectedIndex = 0;
			_this.select(_this);
			return;
		}
		//on 'esc' close dialog
		if(e.which == 27)
		{
			_this.xrefDiv.style.display = "none";
		}
	});

	//add the select box 
	this.selectInput = document.createElement("select");	
	//this.selectInput.multiple = "true";
	
	this.selectInput.style.width = "100%";
	this.xrefDiv.appendChild(this.selectInput);

	//add handler so the input is updated when clicking one select option
	var _this = this;	
	this.addEvent(this.selectInput, "click", function(e) {
		_this.select(_this);
	});	
	
	this.reload();
	this.searchInput.focus();
},
/*Copy the current selected item to 'input' and close dialog*/
select : function(_this)
{
	if(_this.selectInput.options.length > 0)
	{
		//alert("clicked option "+ _this.selectInput.options[_this.selectInput.selectedIndex].value);
		_this.input.options[0].value = _this.selectInput.options[_this.selectInput.selectedIndex].value;
		_this.input.options[0].text = _this.selectInput.options[_this.selectInput.selectedIndex].text;
		_this.xrefDiv.style.display = "none";
	}	
},
/* reload function*/
reload : function() {
	//alert("reload"); 

	//load the select box contents via AJAX 
	var url = "xref/find?xref_entity="+this.xref_entity+"&xref_field="+this.xref_field+"&xref_label="+this.xref_label+"&xref_label_search="+this.searchInput.value;
	if(this.xref_filters != null) url += "&xref_filters="+this.xref_filters;
	//alert(url);

	// branch for native XMLHttpRequest object
	var _this = this;

	req = false;
	if (window.XMLHttpRequest  && !(window.ActiveXObject)) //NOT IE
	{
		req = new XMLHttpRequest();
	}
	else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
	}

	if (req) {
		req.onreadystatechange = function(e) {
			// only if req shows "complete" 
			if (req.readyState == 4) {
				// only if "OK" 
			if (req.status == 200) {
				// ...processing statements go here...
				var options = eval('(' + req.responseText + ')');
				_this.redrawOptions(options);
			} else {
				alert("There was a problem retrieving the XML data:\n"
						+ req.statusText);
			}
		}
		};
		req.open("GET", url, true);
		req.send("");
	}
},
redrawOptions : function(options) {
	//remove existing options this.selectInput.options; 
	for (i = this.selectInput.options.length - 1; i >=0; i--) 
	{
		this.selectInput.removeChild(this.selectInput.options[i]);
	}

	//add empty option to set 'null' when search is empty
	if(this.searchInput.value == "")
	{
		this.selectInput.appendChild(document.createElement("option"));
	}	
	
	//add the current options
	for ( var i in options) {
		var option = document.createElement("option");

		//add the value
		option.value = i;

		//add option to select box
		this.selectInput.appendChild(option);

		option.text = options[i];	
	}
	

	
	//resize select to fit
	this.selectInput.size = options.length <= 10 ? options.length : 10;
},
handleAjax: function(e) {
	//alert("render");
		// only if req shows "complete" 
		if (req.readyState == 4) 
		{
			// only if "OK" 
			if (req.status == 200) 
			{
				var options = eval('(' + req.responseText + ')');
				this.redrawOptions(options);
			} else 
			{
			alert("There was a problem retrieving the XML data:\n"
					+ req.statusText);
			}
		}
	},
/*helper function to add events for both IE and FF in one call
@obj = the oject to add the event ont
@eventname = name of the event minus the 'on', e.g. 'click' means 'onclick'
@func = the function to be called if this event happens
 */
addEvent : function(obj, eventname, func) {
	//alert(eventname);
	if (navigator.userAgent.match(/MSIE/)) {
		obj.attachEvent("on" + eventname, func);
	} else {
		obj.addEventListener(eventname, func, true);
	}
}
}