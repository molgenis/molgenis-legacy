#!/usr/bin/env perl

=head1 NAME

Europhenome Transform

=head1 SYNOPSIS

Transform from Europhenome mart export to MOLGENIS tab delimited import

=cut

use strict;
use warnings;

use Text::CSV_XS;
use Data::Dumper;
use Term::ProgressBar;
use Log::Log4perl qw(:easy);
Log::Log4perl->easy_init(
						  {
							 level  => $DEBUG,
							 layout => '%-5p - %m%n',
							 file   => ">EurophenomeTransform.log",
						  },
						  {
							 level  => $INFO,
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

our @datapoints;
our %warn;

sub main() {

	# Print usage
	usage();

	# load data into respective hashes
	load_datapoints();

	# write data to molgenis import format
	write_ontologysource_term();
	write_variabledefinition();

	write_investigation();
	write_individual_panel();

	write_observedvalue();
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

	# load protocols into hash
	my %protocol;
	for my $datapoint (@datapoints) {
		$protocol{ $datapoint->{'Procedure'} }->{ $datapoint->{'Parameter name'} }++;
	}

	# write protocols
	open my $fh1, ">:utf8", "../../../data/Europhenome/protocol.txt" or die "$!";
	open my $fh3, ">:utf8", "../../../data/Europhenome/protocol_observableFeatures.txt" or die "$!";

	# write headers
	print $fh1 join ( "\t", qw/name/ );
	print $fh3 join ( "\t", qw/protocol_name observableFeature_name/ );

	# walk the tree write protocol and protocolcomponents
	while ( my ( $protocolName, $features ) = each(%protocol) ) {	
		print $fh1 join ( "\t", $protocolName );
		for my $featureName ( keys %$features ) {
			print $fh3 join ( "\t", $protocolName, $featureName );
		}
	}
	
	close $fh1;
	close $fh3;
	INFO( 'Wrote ' . scalar(keys %protocol) . ' Protocols' );
}

sub write_observedvalue() {
	my ( $datapoint_ref, $meas_ref, ) = @_;
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "../../../data/Europhenome/observedvalue.txt" or die "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/observationTarget_name observableFeature_name investigation_name value/
	);

	for my $datapoint (@datapoints) {

		print $fh1 join ( "\t",
						  $datapoint->{'Animal id'},   $datapoint->{'Parameter name'},
						  $datapoint->{'Centre Name'}, $datapoint->{'Value'} );
	}
	close $fh1;
	INFO( 'Wrote ' . scalar(@datapoints) . ' ObservedValues' );
}

sub write_individual_panel() {
	local $\ = "\n";    # do the magic of println
	my ( %panel, %ind_gender );    # store unique panel names

	# create hashes
	for my $datapoint (@datapoints) {
		if ( defined $ind_gender{ $datapoint->{'Animal id'} }
			 && $ind_gender{ $datapoint->{'Animal id'} } ne $datapoint->{'Sex'} )
		{
			LOGDIE "gender mismatch";
		}
		$ind_gender{ $datapoint->{'Animal id'} } = $datapoint->{'Sex'};
		$panel{ $datapoint->{'Strain name'} }->{ $datapoint->{'Animal id'} }++;
	}

	open my $fh1, ">:utf8", "../../../data/Europhenome/individual.txt"        or LOGDIE "$!";
	open my $fh2, ">:utf8", "../../../data/Europhenome/panel_individuals.txt" or LOGDIE "$!";
	open my $fh3, ">:utf8", "../../../data/Europhenome/panel.txt"             or LOGDIE "$!";

	# write headers
	print $fh1 join ( "\t", qw/name species_termLabel sex/ );
	print $fh2 join ( "\t", qw/panel_name individual_name/ );
	print $fh3 join ( "\t", qw/name/ );

	while ( my ( $animal_id, $gender ) = each(%ind_gender) ) {
		print $fh1 join ( "\t", $animal_id, 'mouse strain', $gender );

	}

	while ( my ( $strain, $animals ) = each(%panel) ) {
		print $fh3 join ( "\t", $strain );

		for my $animal_id ( keys %$animals ) {
			print $fh2 join ( "\t", $strain, $animal_id );
		}
	}

	close $fh1;
	close $fh2;
	close $fh3;
	INFO( 'Wrote ' . scalar( keys %ind_gender ) . ' Individuals' );
	INFO( 'Wrote ' . scalar( keys %panel ) . ' Panels' );
}

sub write_investigation() {
	local $\ = "\n";    # do the magic of println

	open my $fh_out, ">:utf8", "../../../data/Europhenome/investigation.txt" or die "$!";
	print $fh_out join ( "\t", qw/name description/ );    # write headers

	# create a hash of uniqe project names
	my %project;
	for my $datapoint (@datapoints) {
		$project{ $datapoint->{'Centre Name'} }++;
	}

	my $description_suffix = q{[Source: http://www.europhenome.org/]};

	for my $name ( keys %project ) {
		print $fh_out join ( "\t", $name, $description_suffix );
	}
	close $fh_out;
	INFO( 'Wrote ' . scalar( keys %project ) . ' Investigations' );
}

sub write_variabledefinition() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "../../../data/Europhenome/variabledefinition.txt"
	  or get_logger->logdie($!);

	# write headers
	print $fh1 join ( "\t", qw/name unit_termLabel investigation_name/ );

	my ( %Unit, %Parameter_name );    # stores unique values
	my $composite_key;
	for my $datapoint (@datapoints) {
		$composite_key = $datapoint->{'Parameter name'} . $datapoint->{'Centre Name'};
		print $fh1 join ( "\t",
						  $datapoint->{'Parameter name'},
						  $datapoint->{'Unit'}, $datapoint->{'Centre Name'} )
		  unless defined $Parameter_name{$composite_key};

		$Parameter_name{$composite_key}++;
		$Unit{ $datapoint->{'Unit'} }++;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %Parameter_name ) . ' variableDefinitions' );

	# add units as ontology terms to file
	open my $fh2, ">>:utf8", "../../../data/Europhenome/ontologyterm.txt" or LOGDIE($!);

	for my $key ( keys %Unit ) {

		# headers: term,termLabel,termAccession,termSource_name
		# data: mouse strain,mouse strain,http://www.ebi.ac.uk/efo/EFO_0000607,EFO
		print $fh2 join ( "\t", $key, $key, q{}, q{} );
	}
	close $fh2;
	INFO( 'Wrote ' . scalar( keys %Unit ) . ' units' );
}

sub write_ontologysource_term() {
	local $\ = "\n";    # do the magic of println

	my @ontologysource = ( [ 'name', 'ontologyuri' ], [ 'EFO', 'http://www.ebi.ac.uk/efo' ] );
	open my $fh_out, ">:utf8", "../../../data/Europhenome/ontologysource.txt"
	  or LOGDIE($!);
	for my $line (@ontologysource) {
		print $fh_out join ( "\t", @$line );
	}
	close $fh_out;

	my @ontologyterm = (
						 [ 'term', 'termLabel', 'termAccession', 'termSource_name' ],
						 [
							'mouse strain',                         'mouse strain',
							'http://www.ebi.ac.uk/efo/EFO_0000607', 'EFO'
						 ],
	);

	open $fh_out, ">:utf8", "../../../data/Europhenome/ontologyterm.txt"
	  or LOGDIE($!);
	for my $line (@ontologyterm) {
		print $fh_out join ( "\t", @$line );
	}
	close $fh_out;
}

sub load_datapoints($) {

	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "../../../data/Europhenome/orig/mart_export.txt"
	  or LOGDIE("$!");

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	my $c;
	my $progress = Term::ProgressBar->new(
										   {
											 count  => 35276,
											 name   => 'Loading datapoints',
											 ETA    => 'linear',
											 remove => 0,
										   }
	);

	until ( $csv->eof() ) {
		my $row = trim_row( $csv->getline_hr($fh_in) );
		check_parser_for_errors( \$csv, \$row );

		# modify some values on the fly
		$row->{'Animal id'}      = 'EUROPHENOME' . $row->{'Animal id'};
		$row->{'Centre Name'}    = 'Europhenome @' . $row->{'Centre Name'};
		$row->{'Parameter name'} =
		    $row->{'Parameter name'} . ' - '
		  . $row->{'Procedure'} . '('
		  . $row->{'Empress SOP Link'} . ')';
		$row->{'Procedure'} =
		  $row->{'Procedure'} . '  - Empress SOP Link: ' . $row->{'Empress SOP Link'};
		$row->{'Sex'} = 'female' if $row->{'Sex'} eq 'F';
		$row->{'Sex'} = 'male'   if $row->{'Sex'} eq 'M';

		# add to array
		push @datapoints, $row;
		$progress->update( $c++ );
	}
	close($fh_in);
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
	while ( my ( $msg, $no ) = each(%warn) ) {
		WARN( $msg . " - " . $no );
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
	if ( !( defined $$row_ref ) and !( $$csv_ref->eof() ) ) {
		my $bad_argument = $$csv_ref->error_input();    # get the most recent bad argument
		my $diag         = $$csv_ref->error_diag();
		LOGDIE "WARNING: CSV parser error <$diag> on line - $bad_argument.\n";
	}
}

=back

=cut

=head1 AUTHOR

Tomasz Adamusiak 2009

=cut

main();
