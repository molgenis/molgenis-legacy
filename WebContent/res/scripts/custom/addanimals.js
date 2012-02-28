function showHideGenotypeDiv(animalType) {
	if (animalType == "B. Transgeen dier" && document.getElementById("divGMO").style.display == "none") {
		document.getElementById("divGMO").style.display = "block";
	} else {
		document.getElementById("divGMO").style.display = "none";
	}
}

function updateNamePrefixBox() {
	var species = document.getElementById("species")[document.getElementById("species").selectedIndex].innerHTML;
	if (species == "House mouse") {
		selectPrefix("mm_");
	}
	if (species == "Brown rat") {
		selectPrefix("rn_");
	}
	if (species == "Common vole") {
		selectPrefix("mar_");
	}
	if (species == "Tundra vole") {
		selectPrefix("mo_");
	}
	if (species == "Syrian hamster") {
		selectPrefix("ma_");
	}
	if (species == "European groundsquirrel") {
		selectPrefix("sc_");
	}
	if (species == "Siberian hamster") {
		selectPrefix("ps_");
	}
	if (species == "Domestic guinea pig") {
		selectPrefix("cp_");
	}
	if (species == "Fat-tailed dunnart") {
		selectPrefix("sg_");
	}
}

// TODO: why doesn't the method below work? The statements are executed but there is no effect on the screen...
function selectPrefix(prefix) {
	for (var i = 0; i < document.getElementById("namebase").options.length; i++) {
		if (document.getElementById("namebase").options[i].value === prefix) {
			document.getElementById("namebase").selectedIndex = i;
			$("#namebase").chosen();
			return;
		}
	}
}

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
