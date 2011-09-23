#!/usr/bin/env perl

=head1 NAME

MPDTransform

=head1 SYNOPSIS

Transform from Mouse Phenome Database internal format to MOLGENIS tab delimited import

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
		file   => "STDOUT",
	}
);

=head1 DESCRIPTION

=head2 Function list

=over

=item main()

Main function. Nothing fancy. 

=cut

my $basedir = '../../../../../phenoflow_data/MPD';

# Declare object that will hold the structure
my %datapoint   = ();
my %measurement = ();
my %warning     = ();

sub main() {

	# Print usage
	usage();

	# load data into respective hashes
	load_measurements();
	load_animaldatapoints();

	# print accumulated warnings
	for my $msg ( sort( keys %warning ) ) {

		#WARN $msg;
	}

	# write data to molgenis import format
	write_investigation();
	write_individual_panel();

	write_ontology_term();

	write_measurement();
	#write_features();

	write_observedvalue();

	write_protocol();
	write_protocolapplication();

	exit 0;
}

=item usage()

Prints script usage. 

=cut

sub usage() {
	print <<'USAGE';
Usage:   MPDTransform.pl

Brief summary:

Transform from Mouse Phenome Database internal format to MOLGENIS tab delimited import

USAGE
}

=item transform_files()

Opens appropriate filehandles

=cut

sub write_protocol($$) {
	local $\ = "\n";    # do the magic of println

	# write protocols
	open my $fh1, ">:utf8", "$basedir/protocol.txt" or LOGDIE "$!";

	# write headers
	print $fh1 join ( "\t", qw/name features_name/ );

	# load protocols into hash
	my %protocol;
	my %temp;

	while ( my ( $mesnum, $meas ) = each(%measurement) ) {
		my $name    = $meas->{protocol};
		my $feature = $meas->{varname};
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
		my $key = join( "\t", $name, $features );
		$protocol{$key}++;
	}

	for my $key ( keys %protocol ) {
		print $fh1 $key;
	}

	close $fh1;

	INFO( 'Wrote ' . scalar( keys %protocol ) . ' Protocols' );
}

sub write_protocolapplication() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/protocolapplication.txt"
	  or get_logger->logdie($!);

	# write headers
	print $fh1 join ( "\t", qw/name protocol_name investigation_name/ );

	my %protocolapp;
	while ( my ( $mesnum, $meas ) = each(%measurement) ) {
		my $name     = $meas->{protocolapp};
		my $protocol = $meas->{protocol};
		my $investigation = $meas->{projsym};
		my $key      = join( "\t", $name, $protocol, $investigation );
		$protocolapp{$key}++;
	}

	for my $key ( keys %protocolapp ) {
		print $fh1 $key;
	}

	close $fh1;
	INFO( 'Wrote ' . scalar( keys %protocolapp ) . ' ProtocolApplications' );
}

sub write_observedvalue($$) {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/observedvalue.txt" or die "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/investigation_name protocolapplication_name feature_name target_name ontologyreference_name value/
	);

	my $counter;
	while ( my ( $individual_name, $datapoint ) = each(%datapoint) ) {
		for my $measnum ( keys %{ $datapoint->{measnum} } ) {
			my $feature_name       = $measurement{$measnum}->{varname};
			my $investigation_name = $measurement{$measnum}->{projsym};
			my $protocolapp_name   = $measurement{$measnum}->{protocolapp};
			for my $value ( @{ $datapoint->{measnum}->{$measnum} } ) {
				print $fh1 join ( "\t",
					$investigation_name, $protocolapp_name, $feature_name, $individual_name, q{N/A},
					$value );
				$counter++;
			}
		}
	}
	close $fh1;

	INFO( 'Wrote ' . $counter . ' ObservedValues' );
}

sub write_measurement() {
	local $\ = "\n";    # do the magic of println
	my %unit;           # stores unique units
	open my $fh1, ">:utf8", "$basedir/measurement.txt" or die "$!";

	# write headers
	print $fh1 join ( "\t", qw/name investigation_name description unit_name/ );

	# write the fixed 'sex' and 'species' features which are standard
	# TODO

	# write the other features
	while ( my ( $id, $meas ) = each(%measurement) ) {
		print $fh1 join ( "\t", $meas->{varname}, $meas->{projsym}, $meas->{desc}, $meas->{units} );
		$unit{ $meas->{units} }++;
	}
	close $fh1;

	INFO( 'Wrote ' . scalar( keys %measurement ) . ' Measurements' );

	# add units as ontology terms to file
	open my $fh2, ">>:utf8", "$basedir/ontologyterm.txt" or die "$!";

	for my $key ( keys %unit ) {

		# headers: name,termAccession
		print $fh2 join ( "\t", $key, $key );
	}
	close $fh2;
}

sub write_features() {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/observationelement.txt" or LOGDIE "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/name investigation_name description/
	);

	# write the fixed 'sex' and 'species' features which are standard
	# TODO

	# write the other features
	while ( my ( $id, $meas ) = each(%measurement) ) {
		print $fh1 join ( "\t", $meas->{varname}, $meas->{projsym}, $meas->{desc} );
	}
	close $fh1;

	INFO( 'Wrote ' . scalar( keys %measurement ) . ' Features' );
}

sub write_individual_panel($$) {
	local $\ = "\n";    # do the magic of println
	my %panels = ();    # store unique panel names

	open my $fh1, ">:utf8", "$basedir/individual.txt" or die "$!";

	# write header
	print $fh1 join ( "\t", qw/name investigation_name/ );

	while ( my ( $individual_name, $animal_ref ) = each(%datapoint) ) {
		print $fh1 join ( "\t", $individual_name, $animal_ref->{projsym} );
		my $panel_name = uc( $animal_ref->{strain} . ' ' . $animal_ref->{projsym} . ' ' );

		$panels{$panel_name}->{projsym} = $animal_ref->{projsym};
		$panels{$panel_name}->{individuals}->{$individual_name}++;
	}
	close $fh1;

	open my $fh2, ">:utf8", "$basedir/panel.txt" or die "$!";

	# write header
	print $fh2 join ( "\t", qw/name investigation_name individuals_name/ );

	while ( my ( $panel_name, $panel ) = each %panels ) {
		my $individuals;
		for my $individual ( keys %{ $panel->{individuals} } ) {
			$individuals = $individuals . "$individual|";
		}
		chop $individuals;
		print $fh2 join ( "\t", $panel_name, $panel->{projsym}, $individuals );
	}
	close $fh2;
	INFO( 'Wrote ' . scalar( keys %panels ) . ' Panels' );
}

sub write_investigation($$) {
	local $\ = "\n";    # do the magic of println

	my @output = (
		[ 'name',                   'description', 'accession' ],
		[ 'Mouse Phenome Database', 'http://www.jax.org/phenome', ]
	);

	open my $fh_out, ">:utf8", "$basedir/investigation.txt" or die "$!";
	print $fh_out join ( "\t", qw/name description accession/ );    # write headers

	# create a hash of uniqe project names
	# from measurements available in database
	my %project;
	for my $meas ( keys %measurement ) {
		$project{ $measurement{$meas}->{projsym} }++;
	}

	# load projects name from external file
	# this list was scraped from website and is not complete
	my %project_des        = load_projects();
	my $description_suffix =
	  q{Description not available for this data set downloaded from Mouse Phenome Database};

	for my $name ( keys %project ) {
		print $fh_out join ( "\t",
			$name,
			$project_des{$name} || $description_suffix,
			"http://phenome.jax.org/pub-cgi/phenome/mpdcgi?rtn=projects/details&sym=" . $name );
	}
	close $fh_out;

	INFO( 'Wrote ' . scalar( keys %project ) . ' Investigations' );
}

sub load_projects() {
	my %project_des;

	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "$basedir/orig/projects.txt" or die "$!";

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	until ( $csv->eof() ) {
		my $row = $csv->getline_hr($fh_in);
		check_parser_for_errors( \$csv, \$row );
		$project_des{ $row->{name} } = $row->{description};
	}
	close($fh_in);
	return %project_des;
}

sub write_ontology_term($$) {
	local $\ = "\n";    # do the magic of println

	# create ontology
	#	# write header
	#	open my $fh_out, ">:utf8", "$basedir/ontology.txt"
	#	  or die "ERROR: Can't open ontology.txt for write. $!";
	#	print $fh_out join ( "\t", qw/name ontologyAccession/ );
	#	print $fh_out join ( "\t", 'EFO', 'http://www.ebi.ac.uk/efo' );
	#	close $fh_out;

	# for each ontology add terms
	open my $fh_out, ">:utf8", "$basedir/ontologyterm.txt"
	  or LOGDIE "ERROR: Can't open ontologyterm.txt for write. $!";

	# header
	print $fh_out join ( "\t", qw/name termAccession/ );

	my @ontologyterm = (
		[ 'mouse strain', 'http://www.ebi.ac.uk/efo/EFO_0000607' ],
		[ 'day',          'http://www.ebi.ac.uk/efo/EFO_0001789' ]
	);

	for my $line (@ontologyterm) {
		print $fh_out join ( "\t", @$line );
	}
	close $fh_out;
}

sub substitute_illegal_characters($) {
	my $row = shift;

	# modify some values on the fly
	for my $heading ( keys %$row ) {
		if ( defined $row->{$heading}
			&& $row->{$heading} =~ /[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/ )
		{
			$warning{ "Substituting illegal character in >>> " . $& }++;
			$row->{$heading} =~ s/[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/<>/g;
		}
	}
}

sub load_animaldatapoints($$) {
	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "$basedir/orig/animaldatapoints.txt"
	  or die "ERROR: Can't load animaldatapoints.txt. $!";

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	my $c;

	my %animalid;    # stores animalids for consistency checking
	my %measid;      # stores measurements that have values for consistency checking
	my %measdesc;    # stores measurements descriptions for consistensy checking

	until ( $csv->eof() ) {
		my $row = trim_row( $csv->getline_hr($fh_in) );
		check_parser_for_errors( \$csv, \$row );

		substitute_illegal_characters($row);

		INFO $c . ' out of 548912' if ++$c % 100000 == 0;

		if ( exists( $measurement{ $row->{measnum} } ) ) {

			# TODO: WHAT ABOUT MISSING VALUES
			if ( defined( $row->{value} ) ) {

				$animalid{ uc( $row->{'animal_id'} ) }->{ $row->{'animal_id'} }++;
				$measid{ $row->{measnum} }++;
				$warning{"CAPITALISATION: $row->{'animal_id'} inconsistent in animaldatapoints.txt"}
				  ++
				  if scalar keys %{ $animalid{ uc( $row->{'animal_id'} ) } } > 1;
				my $projsym         = $measurement{ $row->{measnum} }->{projsym};
				my $individual_name =
				  uc( $row->{'strain'} . ' ' . $row->{'animal_id'} . ' ' . $projsym );

				# animal_id = {strain} + {animalid}
				# it turns out animals are numbered per strain

				$datapoint{$individual_name}->{sex} = 'male'
				  if lc( $row->{sex} ) eq 'm';
				$datapoint{$individual_name}->{sex} = 'female'
				  if lc( $row->{sex} ) eq 'f';

# NO LONGER UNDERSTAND WHAT'S BEING CHECKED HERE
#				 if (defined $datapoint{ $individual_name }->{animal_id}
#				 && $datapoint{ $individual_name }->{animal_id} ne uc($row->{strain} . "-" . $row->{animal_id} )) {
#					WARN "ERROR MULTIPLE ANIMAL IDs per ANIMAL?" . $individual_name . " "
#					. $datapoint{ $individual_name }->{animal_id} . " " . $row->{strain} . "-" . $row->{animal_id};
#				  }
# animal_id as such is not unique and we have individidual_name already
#$datapoint{ $individual_name }->{animal_id} = uc($row->{strain} . "-" . $row->{animal_id});
				$datapoint{$individual_name}->{strain} = $row->{strain};
				if ( defined $datapoint{$individual_name}->{projsym} ) {
					WARN "WARNING individual in 2 different investigations! "
					  . $individual_name
					  . " already in "
					  . $datapoint{$individual_name}->{projsym} . " and "
					  . $projsym
					  if $datapoint{$individual_name}->{projsym} ne $projsym;
				}
				$datapoint{$individual_name}->{projsym} = $projsym;
				push @{ $datapoint{$individual_name}->{measnum}->{ $row->{measnum} } },
				  $row->{value};
			}
			else {
				$warning{"EMPTY VALUE: Line $. in animaldatapoints.txt has an empty value"}++;
			}
		}
		else {
			$warning{"MISSING REFERENCE: measnum $row->{measnum} was not found in measurements.txt"}
			  ++;
		}
	}
	close($fh_in);

	# find unmatched measurements (with no values)
	for my $meas ( keys %measurement ) {
		$measdesc{ uc( $measurement{$meas}->{desc} ) }->{$meas}++;
		$warning{"MEASUREMENT MISSING: $meas $measurement{$meas}->{desc} in animaldatapoints.txt"}++
		  if !defined $measid{$meas};
	}

	# find similiar measurements by description
	for my $meas ( keys %measurement ) {
		$warning{"DUPLICATE MEASUREMENTS: $measurement{$meas}->{desc} (measnum $meas)"}++
		  if scalar keys %{ $measdesc{ uc( $measurement{$meas}->{desc} ) } } > 1;
	}
}

sub load_measurements($) {
	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "$basedir/orig/measurements.txt"
	  or die "ERROR: Can't load measurements.txt. $!";

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	my $c;

	until ( $csv->eof() ) {
		my $row = trim_row( $csv->getline_hr($fh_in) );
		check_parser_for_errors( \$csv, \$row );

		substitute_illegal_characters($row);

		# assign variables
		$measurement{ $row->{measnum} }->{projsym} = 'MPD: ' . $row->{projsym};
		$measurement{ $row->{measnum} }->{varname} =
		    $measurement{ $row->{measnum} }->{projsym} . ' - '
		  . $row->{varname};    # . ': ' .. $row->{desc};

		my $protocol_name = $row->{cat1};
		$protocol_name .= ', ' . $row->{cat2} if $row->{cat2} ne '<>';
		$protocol_name .= ', ' . $row->{cat3} if $row->{cat3} ne '<>';
		$protocol_name .= ', intervention:' . $row->{intervention}
		  if $row->{intervention} ne '<>';
		$protocol_name .= ', intparam:' . $row->{intparm} if $row->{intparm} ne '<>';
		$measurement{ $row->{measnum} }->{protocol} = $protocol_name;

		$measurement{ $row->{measnum} }->{protocolapp} = $row->{projsym} . ': ' . $protocol_name;

		$measurement{ $row->{measnum} }->{desc}  = $row->{desc};
		$measurement{ $row->{measnum} }->{units} = $row->{units};
		$measurement{ $row->{measnum} }->{cat1}  = $row->{cat1} if $row->{cat1} ne '=';
		$measurement{ $row->{measnum} }->{cat2}  = $row->{cat2} if $row->{cat2} ne '=';
		$measurement{ $row->{measnum} }->{cat3}  = $row->{cat3} if $row->{cat3} ne '=';

	}
	close($fh_in);

	remove_dup_measurements(%measurement);

}

sub trim_row ($) {
	my $row = shift;
	local $\ = "\n";    # do the magic of println

	for my $key ( keys %$row ) {
		if ( defined $row->{$key} && $row->{$key} =~ s/\s{2,}// ) {
			$warning{"TRIMMING $row->{$key}"}++;
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/^\s+// ) {
			$warning{"TRIMMING $row->{$key}"}++;
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/\s+$// ) {
			$warning{"TRIMMING $row->{$key}"}++;
		}
	}

	return $row;
}

sub remove_dup_measurements($) {

	my (%word_count);

	# find duplicates, by storing path to branch in a hash
	# if multiple paths are found, the term is duplicated in different places
	while ( my ( $key, $meas ) = each %measurement ) {
		$word_count{ $meas->{cat1} }->{ROOT}++            if defined $meas->{cat1};
		$word_count{ $meas->{cat2} }->{ $meas->{cat1} }++ if defined $meas->{cat2};
		$word_count{ $meas->{cat3} }->{ $meas->{cat1} . $meas->{cat2} }++
		  if defined $meas->{cat3};
	}

	# concatenate to previous cat
	while ( my ( $key, $meas ) = each %measurement ) {
		$meas->{units} = 'N' if $meas->{units} eq 'n';

		if ( defined $meas->{cat2} && scalar keys %{ $word_count{ $meas->{cat2} } } > 1 ) {
			my $no = scalar keys %{ $word_count{ $meas->{cat2} } };
			$warning{"DUPLICATE CATEGORY: prefixing $meas->{cat2} with $meas->{cat1} ($no) "}++;
			$meas->{cat2} = $meas->{cat1} . ' ' . $meas->{cat2};
		}
		if ( defined $meas->{cat3} && scalar keys %{ $word_count{ $meas->{cat3} } } > 1 ) {
			my $no = scalar keys %{ $word_count{ $meas->{cat3} } };
			$warning{"DUPLICATE CATEGORY: prefixing $meas->{cat3} with $meas->{cat2} ($no) "}++;
			$meas->{cat3} = $meas->{cat2} . ' ' . $meas->{cat3};
		}
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
		ERROR "WARNING: CSV parser error <$diag> on line - $bad_argument.";
	}
}

=back

=cut

=head1 AUTHOR

Tomasz Adamusiak 2009

=cut

main();
