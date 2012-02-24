function showDeathDatetime(removalType) {
	if (removalType == "C. Na einde proef in leven gelaten") {
		document.getElementById("deathdatediv").style.display = "none";
	} else {
		document.getElementById("deathdatediv").style.display = "block";
	}
}
