#!/usr/bin/perl -w

#
# Hello World + CGI Test
#
# copyright (c) 2009-2010, Danny Arends
# last modified Dec, 2010
# first written Dec, 2010
#

use CGI qw(:standard);

print("Content-type: text/html"."\n\n");
print("Hello, World!"."<br/>\n");
foreach my $p (param()) {
	print($p . "=" . param($p) . "<br/>\n");
}