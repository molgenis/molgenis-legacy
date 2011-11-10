var xmlhttp;

function getExperiment(animalbox) {
	if (!xmlhttp) xmlhttp = aaGetXmlHttpRequest();
	if (!xmlhttp) return;
	var url = 'TerminateAnimalsService?animal='+animalbox.value;
	xmlhttp.open('GET', url, true);
	xmlhttp.onreadystatechange = printMatrix;
	xmlhttp.send(null);
	if (evnt && evnt.preventDefault)
		evnt.preventDefault();
	return false;
}

function printMatrix() {
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		var experimentbox = document.getElementById("experimentfields");
		experimentbox.innerHTML = xmlhttp.responseText;
	}
}
