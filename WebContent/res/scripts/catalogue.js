//debugger;"
//This is for the generic importer

var dataTypeOptions = new Array();
var fieldName = "";
var fieldNameOptions = new Array();
var map = new HashMap();
var count = 0;
var index = 1;
var headerCount = 0;

function changeFieldContent(id)
{
	var classType = document.getElementsByName(id);

	if(classType[1].value.toString() == "Measurement:dataType")
	{
		makeTable(id);
	}else{
		destroyTable(id);
	}

	if(classType[0].value.toString() == "ObservedValue")
	{

		var select = document.getElementsByName(id);

		select = select[1];

		var observedValue = document.createElement('option');

		observedValue.innerHTML = "ObservedValue";

		var length = select.length;

		for(var i = 0; i < length; i++){
			select.options[0] = null
		}

		select.add(observedValue, 0);

	}else{

		var select = document.getElementsByName(id);

		select = select[1];

		if(select.length != fieldNameOptions.length)
		{
			select.options[0] = null

			var option = document.createElement('option');

			for(var i = 0; i < fieldNameOptions.length; i++)
			{	
				var option = document.createElement('option');
				option.innerHTML = fieldNameOptions[i];
				select.add(option, i);
			}
		}
	}
}

function destroyTable(id) {

	document.getElementById(id).innerHTML = "data type";
}

function greeting(){

	var table = document.getElementById(fieldName);
	makeTable(fieldName);

}

function makeTable(id) {

	var tab;

	tab = document.createElement('table');
	tab.setAttribute('id',id);
	tab.setAttribute('border','1');
	tableExisting = false;

	var row = new Array();
	var cell = new Array();

	row[0]=document.createElement('tr');

	cell[0]=document.createElement('td');

	cell[1]=document.createElement('td');

	cell[2]=document.createElement('td');

	var selection = document.createElement('select');

	selection.setAttribute("name", id + "_options_" + count);

	for(var index = 0; index < dataTypeOptions.length; index++)
	{
		var option = document.createElement('option');

		option.innerHTML = dataTypeOptions[index];

		selection.appendChild(option);
	}

	var textInput=document.createElement('input');

	textInput.setAttribute('type','text');

	textInput.setAttribute("name", id + "_input_" + count);

	textInput.setAttribute('size','15');

	var addButton = document.createElement('button');

	addButton.setAttribute('name','add');

	addButton.setAttribute('type','button');

	addButton.innerHTML = "add";

	fieldName = id;

	addButton.setAttribute("onclick", "greeting();");

	cell[0].appendChild(selection);

	cell[1].appendChild(textInput);

	cell[2].appendChild(addButton);

	row[0].appendChild(cell[0]);

	row[0].appendChild(cell[1]);

	row[0].appendChild(cell[2]);

	tab.appendChild(row[0]);

	var oldTable = document.getElementById('table');

	var cellActivated = document.getElementsByName(id);

	var cell = cellActivated[3];

	document.getElementById(id).appendChild(tab);

	count++;
}

function createSelection(option)
{

	if(contains(option, dataTypeOptions) == false){

		dataTypeOptions.push(option);
	}
}

function createFieldName(option)
{
	if(contains(option, fieldNameOptions) == false){
		fieldNameOptions.push(option);
	}
}

function contains(option, array){

	for(var i = 0; i < array.length; i++)
	{
		if(array[i] == option)
		{

			return true;
		}		
	}
	return false;	
}

function updateTableContent(option, array){


	var classType = document.getElementById("shortcutClassType");
	var fieldName = document.getElementById("shortcutFieldName");
	var enterIndex = document.getElementById("shortcut");
	var chosenTarget = document.getElementById("shortcutTarget");

	if(enterIndex.value != "")
	{
		var arrayIndex = enterIndex.value.split(";");

		for(var i = 0; i < arrayIndex.length; i++)
		{

			var indexRange = arrayIndex[i].split(">");

			var max = indexRange[1];
			alert(indexRange.length);

			if(max == "n")
			{	
				alert(index);
				max = index;
			}else{

				max++;

			}

			var min = indexRange[0];

			if(indexRange.length > 1)
			{

				for(var j = min; j < max;j++){

					var value = map.get(parseInt(j));
					var multipleCells = document.getElementsByName(value);
					multipleCells[0].value = classType.value;
					multipleCells[2].value = chosenTarget.value;
					changeFieldContent(value);
					multipleCells[1].value = fieldName.value;

				}
			}else{

				var value = map.get(parseInt(arrayIndex[i]));
				var multipleCells = document.getElementsByName(value);
				multipleCells[0].value = classType.value;
				multipleCells[2].value = chosenTarget.value;
				changeFieldContent(value);
				multipleCells[1].value = fieldName.value;
			}
		}

	}else{

		alert("Please fill in all the information on the shortcut");

	}
}


function createHashMap(element)
{
	map.put(index, element);
	index++;
}

function getCount(id)
{

	headerCount++;
	var div = document.getElementById(id);
	div.innerHTML = "The column index is " + headerCount;
}

//This hashmap contains the details of the measurements that are shown when we click on a tree leaf. 
HashMap = function(){
	this._dict = [];
}
HashMap.prototype._get = function(key){
	for(var i=0, couplet; couplet = this._dict[i]; i++){
		if(couplet[0] === key){
			return couplet;
		}
	}
}
HashMap.prototype.put = function(key, value){
	var couplet = this._get(key);
	if(couplet){
		couplet[1] = value;
	}else{
		this._dict.push([key, value]);
	}
	return this; // for chaining
}
HashMap.prototype.get = function(key){
	var couplet = this._get(key);
	if(couplet){
		return couplet[1];
	}
}

function createHashMap(key, content)	{
	map.put(key, content);

}

function getHashMapContent(key){

	var value = map.get(key);
	$('#details').empty();
	$('#details').append(value);

}

//adding css styling on click 
$("ul").delegate("li", "click", function() {
	$(this).addClass("active");
	//$(this).addClass("active").siblings().removeClass("active");"
});

//adding css styling on hover 
$("li>span").hover(function(){
	$(this).addClass("highlight");
},function() {
	$(this).removeClass("highlight");
});

$(document).ready(function(){
	$("#splitter").splitter();
	$("#browser").treeview({control: "#masstoggler"});

	var $scrollingDiv = $("#scrollingDiv");

	$(window).scroll(function(){			
		$scrollingDiv
		.stop()
		.animate({"marginTop": ($(window).scrollTop() + 30) + "px"}, "slow" );			
	});


});

$(document).unload(function() {
//	alert('Handler for .unload() called.');"
});





