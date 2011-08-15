#!/usr/bin/env perl

=head1 NAME

Europhenome Transform

=head1 SYNOPSIS

Transform from Europhenome mart export to PhenoflowTAB format

=cut

use strict;
use warnings;

use Text::CSV_XS;
use Data::Dumper;
use Log::Log4perl qw(:easy);
Log::Log4perl->easy_init(
	{
		level  => $DEBUG,
		layout => '%-5p - %m%n',
		file   => ">EurophenomeTransform.log",
	},
	{
		level  => $DEBUG,
		layout => '%-5p - %m%n',
		file   => "STDOUT",
	}
);

=head1 DESCRIPTION

=head2 Function list

=over

=item main()

Main function. Nothing fancy. 

=cut

my @datapoints;
my %warn;
my $basedir  = '../../../../../phenoflow_data/Europhenome2';
my @features = (
	'Line name',
	'Sex',
	'Zygosity',
	'MGI Gene Symbol',
	'Emma ID',
	'International Strain Name',
	'MouseBook Stock Identifer',
	'EScell Clone',
	'MGI Allele Name',
	'MGI Gene Name',
	'Ensembl Gene ID',
);
my %terms = (
	'Mammalian Phenotype Ontology Term Name' => 'Mammalian Phenotype Ontology Term ID',
	'MGI Allele Name'                        => 'MGI Allele Accession ID',
	'MGI Gene Name'                          => 'MGI Gene Accession ID',
	'Ensembl Gene ID'                        => 'Ensembl Gene ID',
	'Phenotype Procedure'                    => 'Phenotype Procedure ESLIM ID',
	'Parameter Name'                         => 'Parameter ESLIM ID',
);

sub main() {

	# Print usage
	usage();

	# load data into respective hashes
	#fix_export();

	load_datapoints();

	# write data to molgenis import format
	write_ontologyterm();
	
	write_investigation();

	write_panel();

	write_features();
	
	write_observedvalue();

	write_protocolapplication();

	write_protocol();

	print_warnings();
	exit 0;
}

=item usage()

Prints script usage. 

=cut

sub usage() {
	print <<'USAGE';
Usage:   EurophenomeTransform.pl

Brief summary:

Transform from Europhenome mart export to MOLGENIS tab delimited import

USAGE
}

=item transform_files()

Opens appropriate filehandles

=cut

sub write_protocol($$) {
	local $\ = "\n";    # do the magic of println

	# write protocols
	open my $fh1, ">:utf8", "$basedir/protocol.txt" or die "$!";

	# write headers
	print $fh1 join ( "\t", qw/name description ontologyreference_name features_name/ );

	# load protocols into hash
	my %protocol;
	my %temp;
	for my $datapoint (@datapoints) {
		my $name    = $datapoint->{'Phenotype Procedure'};
		my $feature = $datapoint->{'Parameter Name'};
		$temp{$name}->{description} = $datapoint->{'Phenotype Pipeline'};
		$temp{$name}->{features}->{$feature}++;
	}

	# concatanate features
	for my $name ( keys %temp ) {
		my $description = $temp{$name}->{description};
		my $features;
		for my $feature ( keys %{ $temp{$name}->{features} } ) {
			$features = $features . "$feature|";
		}
		chop $features;
		my $key = join( "\t", $name, $description, $name, $features );
		$protocol{$key}++;
	}

	for my $key ( keys %protocol ) {
		print $fh1 $key;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %protocol ) . ' Protocols' );
}

sub write_features() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/observationelement.txt" or LOGDIE "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/name ontologyreference_name/
	);

	my %observationelement;

	# panel characteristics
	for my $feature_name (@features) {
		my $key = join( "\t", $feature_name, q{N/A} );
		$observationelement{$key}++;
	}

	# protocol parameters
	for my $datapoint (@datapoints) {
		my $feature_name = $datapoint->{'Parameter Name'};
		my $ontology_ref = $datapoint->{'Parameter Name'};
		my $key          = join( "\t", $feature_name, $ontology_ref );
		$observationelement{$key}++;
	}

	for my $key ( keys %observationelement ) {
		print $fh1 $key;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %observationelement ) . ' Features' );
}

sub write_observedvalue() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/observedvalue.txt" or LOGDIE "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/investigation_name protocolapplication_name feature_name target_name ontologyreference_name value/
	);

	my %observedvalue;

	# characteristics
	for my $feature_name (@features) {

		for my $datapoint (@datapoints) {
			my $investigation_name = get_investigation_nameDP($datapoint);
			my $target_name        = get_panel_nameDP($datapoint);

			my $key;

			# no ontology term
			unless ( exists $terms{$feature_name} ) {
				my $value = $datapoint->{$feature_name};
				next unless defined $value;
				$key = join( "\t",
					$investigation_name, q{N/A}, $feature_name, $target_name, q{N/A}, $value );
			}

			# feature connected to ontology term
			else {
				my $ref = $datapoint->{ $feature_name };
				next unless defined $ref;
				$key = join( "\t",
					$investigation_name, q{N/A}, $feature_name, $target_name, $ref, q{N/A} );
			}

			$observedvalue{$key}++;
		}
	}

	# values
	for my $datapoint (@datapoints) {
		my $investigation_name = get_investigation_nameDP($datapoint);
		my $target_name        = get_panel_nameDP($datapoint);
		my $feature_name       = $datapoint->{'Parameter Name'};
		my $protocol_app = get_protocolapplication_nameDP($datapoint);
		my $ref = $datapoint->{ 'Mammalian Phenotype Ontology Term Name' };
		next unless defined $ref;
		my $key = join( "\t", $investigation_name, $protocol_app, $feature_name, $target_name, $ref, q{N/A} );
		$observedvalue{$key}++;
	}
	
	# statistics 
	#'Statistical Signifiance',
	#'Statistical Effect size',

	for my $key ( keys %observedvalue ) {
		print $fh1 $key;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %observedvalue ) . ' ObservedValues' );
}

sub write_panel() {
	local $\ = "\n";    # do the magic of println
	my %panel;          # store unique panel names

	# create hashes
	for my $datapoint (@datapoints) {
		WARN "PANEL "
		  . get_panel_nameDP($datapoint)
		  . " REUSED BETWEEN "
		  . $panel{ get_panel_nameDP($datapoint) }
		  . get_investigation_nameDP($datapoint)
		  if exists $panel{ get_panel_nameDP($datapoint) }
		  && $panel{ get_panel_nameDP($datapoint) } ne get_investigation_nameDP($datapoint);

		$panel{ get_panel_nameDP($datapoint) } = get_investigation_nameDP($datapoint);
	}

	open my $fh1, ">:utf8", "$basedir/panel.txt" or LOGDIE "$!";

	# write headers
	print $fh1 join ( "\t", qw/name investigation_name/ );

	while ( my ( $panel_name, $investigation_name ) = each %panel ) {
		print $fh1 join ( "\t", $panel_name, $investigation_name );
	}

	close $fh1;

	INFO( 'Wrote ' . scalar( keys %panel ) . ' Panels' );
}

sub write_investigation() {
	local $\ = "\n";    # do the magic of println

	open my $fh_out, ">:utf8", "$basedir/investigation.txt" or die "$!";
	print $fh_out join ( "\t", qw/name description accession/ );    # write headers

	# create a hash of uniqe project names
	my %project;
	for my $datapoint (@datapoints) {
		$project{ $datapoint->{'Europhenome ID'} }++;
	}

	my $description_suffix = q{[Source: http://www.europhenome.org/]};

	for my $name ( keys %project ) {
		print $fh_out join ( "\t",
			get_investigation_name($name),
			$description_suffix,
			'http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&l=' . $name );
	}
	close $fh_out;
	INFO( 'Wrote ' . scalar( keys %project ) . ' Investigations' );
}

sub write_protocolapplication() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/protocolapplication.txt"
	  or get_logger->logdie($!);

	# write headers
	print $fh1 join ( "\t", qw/name protocol_name/ );

	my %protocolapp;
	for my $datapoint (@datapoints) {
		my $name = get_protocolapplication_nameDP($datapoint);
		my $protocol = $datapoint->{'Phenotype Procedure'};
		my $key = join ( "\t", $name, $protocol );
		$protocolapp{$key}++;
	}

	for my $key ( keys %protocolapp ) {
		print $fh1 $key;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %protocolapp ) . ' ProtocolApplications' );
}

sub write_ontologyterm() {
	local $\ = "\n";    # do the magic of println

	#	my @ontologysource = ( [ 'name', 'ontologyuri' ], [ 'EFO', 'http://www.ebi.ac.uk/efo' ] );
	#	open my $fh_out, ">:utf8", "$basedir/ontologyterm.txt"
	#	  or LOGDIE($!);
	#	for my $line (@ontologysource) {
	#		print $fh_out join ( "\t", @$line );
	#	}
	#	close $fh_out;
	#
	#	my @ontologyterm = (
	#		[ 'term', 'termLabel', 'termAccession', 'termSource_name' ],
	#		[ 'mouse strain', 'mouse strain', 'http://www.ebi.ac.uk/efo/EFO_0000607', 'EFO' ],
	#	);

	open my $fh_out, ">:utf8", "$basedir/ontologyterm.txt"
	  or LOGDIE($!);

	# write headers
	print $fh_out join ( "\t", qw/name termaccession/ );

	my %ontologyterm;
	while ( my ( $key, $value ) = each(%terms) ) {
		for my $datapoint (@datapoints) {
			my $name = $datapoint->{$key};
			my $termaccession = $datapoint->{$value};
			next unless defined $name;
			$termaccession = 'N/A' unless defined $termaccession;
			my $key = join( "\t", $name, $termaccession );
			$ontologyterm{$key}++;
		}
	}

	for my $key ( keys %ontologyterm ) {
		print $fh_out $key;
	}

	close $fh_out;
	INFO( ' Wrote ' . scalar( keys %ontologyterm ) . ' OntologyTerms ' );
}

# fixes broken lines in input, unsure why this is happening
sub fix_export($) {
	open my $fh_in,  "<:utf8", "$basedir/orig/mart_export.txt";
	open my $fh_out, ">:utf8", "$basedir/orig/mart_export_fixed.txt";

	my $c     = 0;
	my @lines = <$fh_in>;
	my @newlines;
	for my $line (@lines) {
		INFO "Processed $c lines" if ++$c % 10000 == 0;
		chomp $line;
		$warn{"REMOVING CR ON line $c"}++ if $line =~ /\r/;
		$line =~ s/\r//;
		if ( $line !~ /^\d+/ && $c != 1 ) {
			$warn{"MERGING on line $c"}++;
			my $lastline = pop @newlines;
			chomp $lastline;
			push @newlines, $lastline . $line . "\n";
		}
		else {
			push @newlines, $line . "\n";
		}
	}

	print $fh_out @newlines;
	INFO "Processed $c lines";

	close($fh_in);
	close($fh_out);

}

sub load_datapoints($) {

	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "$basedir/orig/mart_export_fixed.txt"
	  or LOGDIE("$!");

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	my $c        = 0;
	my $progress = 159202;

	until ( $csv->eof() ) {
		my $row = trim_row( $csv->getline_hr($fh_in) );
		check_parser_for_errors( \$csv, \$row );

		# skip empty lines
		unless ( defined $row->{'Europhenome ID'} ) {
			WARN "Skipping row $c";
			next;
		}

		# modify some values on the fly
		for my $heading ( keys %$row ) {
			if ( defined $row->{$heading}
				&& $row->{$heading} =~ /[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/ )
			{
				$warn{ "Substituting illegal character in >>> " . $& }++;
				$row->{$heading} =~ s/[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/ /g;
			}
		}

		# add to array
		push @datapoints, $row;

		#print Dumper($row) if $row->{'Europhenome id'} eq '27';
		INFO $c . "/$progress" if ++$c % 20000 == 0;
	}
	close($fh_in);
	
	# fix duplicated parameter names
	my %parameters;
	for my $datapoint (@datapoints){
		my $param = $datapoint->{'Parameter Name'};
		next unless defined $param;
		if ( defined $parameters{ uc($param) } && $param ne $parameters{ uc($param) }){
			$warn {"duplicated param name " . $param . " - " . $parameters{ uc($param) } }++;
			$datapoint->{'Parameter Name'} = $datapoint->{'Parameter Name'} . "_";
		} else {
			$parameters{ uc($param) } = $param;
		}
	} 
	
	
	INFO( 'Loaded ' . scalar @datapoints . ' datapoints' );
}

sub trim_row ($) {
	my $row = shift;
	local $\ = "\n";    # do the magic of println

	for my $key ( keys %$row ) {
		if ( defined $row->{$key} && $row->{$key} =~ s/\s{2,}// ) {
			$warn{"MID-TRIMMING $row->{$key}"}++;
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/^\s+// ) {
			$warn{"LEFT-TRIMMING $row->{$key}"}++;
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/\s+$// ) {
			$warn{"RIGHT-TRIMMING $row->{$key}"}++;
		}
	}

	return $row;
}

sub print_warnings() {
	for my $msg ( sort keys %warn ) {
		WARN($msg);
	}
}

sub make_csv_parser {
	my $csv = Text::CSV_XS->new(
		{
			sep_char    => qq{\t},
			quote_char  => qq{"},    # default
			escape_char => qq{"},    # default
			binary      => 1,

			# modified settings below
			blank_is_undef     => 1,
			allow_loose_quotes => 1,
		}
	);
}

sub check_parser_for_errors {
	my ( $csv_ref, $row_ref ) = @_;
	if ( !( defined $$row_ref ) and ( $$csv_ref->eof() ) ) {
		my $bad_argument = $$csv_ref->error_input();    # get the most recent bad argument
		my $diag         = $$csv_ref->error_diag();
		LOGDIE "WARNING: CSV parser error <$diag> on line - $bad_argument.\n";
	}
}

# NOTE: europhenomeID required as e.g. Line: Gnas Sex: Male reused between
# Europhenome ID: 10221 and Europhenome ID: 127
sub get_panel_nameDP {
	my $datapoint = shift;
	return get_investigation_nameDP($datapoint)
	  . " Line: "
	  . $datapoint->{'Line name'}
	  . " Sex: "
	  . $datapoint->{'Sex'}
	  . " Zygosity: "
	  . $datapoint->{'Zygosity'};
}

sub get_protocolapplication_nameDP {
	my $datapoint = shift;
	return $datapoint->{'Phenotype Pipeline'}
	  . " " . $datapoint->{'Phenotype Procedure'}
	  . " " . $datapoint->{'Parameter Name'};
}

sub get_investigation_name {
	my $name = shift;
	return "Europhenome ID: " . $name;
}

sub get_investigation_nameDP {
	my $datapoint = shift;
	return get_investigation_name( $datapoint->{'Europhenome ID'} );
}

=back

=cut

=head1 AUTHOR

Tomasz Adamusiak 2009

=cut

main();
