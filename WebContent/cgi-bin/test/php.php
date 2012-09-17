<?php 
Print "Content-type: text/html"."\n\n";
Print "Hello, World!". "<br/>\n";
if(count($argv) > 1){
$stringArray = split(';',$argv[1]);
	foreach($stringArray as $parameter){
		$duoArray = split('=',$parameter);
		if($duoArray[0] != ""){
			echo $duoArray[0];
		}
		if($duoArray[1] != ""){
			echo " = " . $duoArray[1];
		}
		if($duoArray[0] != "") echo "<br/>\n";
	}
}
?> 