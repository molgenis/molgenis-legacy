var xmlhttp;

function getLocationInformation(locationbox) {
	if (!xmlhttp) xmlhttp = aaGetXmlHttpRequest();
	if (!xmlhttp) return;
	//var et = eventtypebox.options(eventtypebox.selectedIndex).text;
	var url = 'LocationInformationService?loc='+locationbox.value;
	xmlhttp.open('GET', url, true);
	xmlhttp.onreadystatechange = printLocationInformation;
	xmlhttp.send(null);
	if (evnt && evnt.preventDefault)
		evnt.preventDefault();
	return false;
}

function printLocationInformation() {
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		var locinfobox = document.getElementById("locationinfo");
		locinfobox.innerHTML = xmlhttp.responseText;
		
		/*var body = document.GetElementsByTagName('body');
		
		if (document.GetElementById('helptextbox')) {
			body[0].removeChild(document.GetElementById('helptextbox'));
		}
		
		var helptextbox = document.createElement('div');
		helptextbox.id = 'helptextbox';
		helptextbox.classname = 'helptextbox';
		helptextbox.innerhtml = xmlhttp.responseText;
		body[0].appendChild(helptextbox);*/
	}
}
