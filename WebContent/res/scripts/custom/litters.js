function updateStartNumber(nameBase) {
	var nameBases = document.getElementById("namebase").options;
	var startNumberString = document.getElementById("startnumberhelper").value;
	var startNumbers = startNumberString.split(";");
	// Find position of selected name base
	var index = 0;
	for (; index < nameBases.length; index++) {
		if (nameBases[index].value === nameBase) {
			break;
		}
	}
	var startNumber = startNumbers[index];
	document.getElementById("startnumber").value = startNumber;
}
