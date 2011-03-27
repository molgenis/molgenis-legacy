#!/usr/bin/perl -w

#
# CGI Index
#
# copyright (c) 2009-2010, Danny Arends
# last modified Jan, 2011
# first written Dec, 2010
#

use CGI qw(:standard);

print("Content-type: text/html"."\n\n");
print("An empty index"."<br/>\n");
foreach my $p (param()) {
	print($p . "=" . param($p) . "<br/>\n");
}