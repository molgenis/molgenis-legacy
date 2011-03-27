var xmlhttp;

function getRestOfEventMenu(eventtypebox) {
	if (!xmlhttp) xmlhttp = aaGetXmlHttpRequest();
	if (!xmlhttp) return;
	var sepval = document.getElementById("sepvaltoggle").checked;
	var nrofanimals = document.getElementById("animallist").length;
	var url = 'AddEventMenuServlet?etype=' + eventtypebox.value + '&sepval=' + sepval + '&nrofan=' + nrofanimals;
	xmlhttp.open('GET', url, true);
	xmlhttp.onreadystatechange = printRestOfEventMenu;
	xmlhttp.send(null);
	if (evnt && evnt.preventDefault)
		evnt.preventDefault();
	return false;
}

function printRestOfEventMenu() {
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		var featurevaluebox = document.getElementById("featurevalues");
		featurevaluebox.innerHTML = xmlhttp.responseText;
	}
}
