function showHideGenotypeDiv(animalType) {
	if (animalType == "B. Transgeen dier" && document.getElementById("GMO").style.display == "none") {
		document.getElementById("GMO").style.display = "block";
	} else {
		document.getElementById("GMO").style.display = "none";
	}
}
