import sys;

print "Content-type: text/html\n";
print "Hello, World!<br/>";
if len(sys.argv) > 1:
	stringArray = sys.argv[1].rsplit(";");
	for parameter in stringArray:
  		duoArray = parameter.rsplit("=");
  		if duoArray[0] != "":
			sys.stdout.write(duoArray[0]);
		if len(duoArray) > 1 and duoArray[1] != "":
			sys.stdout.write(" = " + duoArray[1]);
		if duoArray[0] != "":
			sys.stdout.write("<br/>\n");