function showHideGenotypeDiv(animalType) {
	if (animalType == "B. Transgeen dier" && document.getElementById("GMO").style.display == "none") {
		document.getElementById("GMO").style.display = "block";
	} else {
		document.getElementById("GMO").style.display = "none";
	}
}

function updateStartNumber(nameBase) {
	// TODO: find a way to get the proper highest number for the chosen label.
	// Maybe put a hidden bases-startnumbers list on the page?
	document.getElementById("startnumber").value = 'highest for base' + nameBase;
}