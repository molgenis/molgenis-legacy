function updateStartNumberAndNewNameBase(nameBase) {
	// Update start number:
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
	
	// Update new name base:
	if (nameBase === "New") {
		document.getElementById("divnewnamebasePanel").style.display = "block";
	} else {
		document.getElementById("divnewnamebasePanel").style.display = "none";
	}
}
