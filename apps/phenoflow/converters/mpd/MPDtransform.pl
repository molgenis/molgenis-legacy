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

sub main() {

	# Print usage
	usage();

	# load data into respective hashes
	load_measurements();
	load_animaldatapoints();

	# write data to molgenis import format
	write_investigation();
	write_individual_panel();

	#write_ontology_term();

	#write_observablefeature();
	#write_observedvalue();
	#write_protocol();

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

	# load protocols into respective hashes
	my $prot;
	my $feat;
	my $protocol_projsym;

	while ( my ( $mesnum, $meas ) = each(%measurement) ) {
		{
			no warnings 'uninitialized';

			# create a tree
			$prot->{ $meas->{projsym} . '-' . $meas->{cat1} }
			  ->{ $meas->{projsym} . '-' . $meas->{cat2} }
			  ->{ $meas->{projsym} . '-' . $meas->{cat3} } = undef;

			# keep the values
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat1} }->{projsym} =
			  $meas->{projsym}
			  if defined $meas->{cat1};
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat1} }->{prot_name} =
			  $meas->{cat1}
			  if defined $meas->{cat1};
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat2} }->{projsym} =
			  $meas->{projsym}
			  if defined $meas->{cat2};
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat2} }->{prot_name} =
			  $meas->{cat2}
			  if defined $meas->{cat2};
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat3} }->{projsym} =
			  $meas->{projsym}
			  if defined $meas->{cat3};
			$protocol_projsym->{ $meas->{projsym} . '-' . $meas->{cat3} }->{prot_name} =
			  $meas->{cat3}
			  if defined $meas->{cat3};

			# trim empty branches
			if ( !defined $meas->{cat3} ) {
				delete $prot->{ $meas->{projsym} . '-' . $meas->{cat1} }
				  ->{ $meas->{projsym} . '-' . $meas->{cat2} }
				  ->{ $meas->{projsym} . '-' . $meas->{cat3} };
			}
			if ( !defined $meas->{cat2} ) {
				delete $prot->{ $meas->{projsym} . '-' . $meas->{cat1} }
				  ->{ $meas->{projsym} . '-' . $meas->{cat2} };
			}

			# add observablefeatures
			if ( defined $meas->{cat3} ) {
				$feat->{ $meas->{cat3} }->{ $meas->{desc} }++;
			}
			elsif ( defined $meas->{cat2} ) {
				$feat->{ $meas->{cat2} }->{ $meas->{desc} }++;
			}
			else {
				$feat->{ $meas->{cat1} }->{ $meas->{desc} }++;
			}
		}
	}

	#print Dumper($prot);
	#print Dumper($protocol_projsym);

	# write protocols
	open my $fh1, ">:utf8", "$basedir/protocol.txt"                    or die "$!";
	open my $fh2, ">:utf8", "$basedir/protocol_protocolComponents.txt" or die "$!";
	open my $fh3, ">:utf8", "$basedir/protocol_observableFeatures.txt" or die "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/name investigation_name protocolComponents_name/
	);
	print $fh2 join (
		"\t",
		qw/protocol_name protocol_self_name/
	);
	print $fh3 join (
		"\t",
		qw/protocol_name observableFeature_name/
	);

	# walk the tree write protocol and protocolcomponents
	while ( my ( $name1, $prot2 ) = each(%$prot) ) {
		my @comps2;
		while ( my ( $name2, $prot3 ) = each(%$prot2) ) {
			push @comps2, $name2;
			my @comps3;
			for my $name3 ( keys %$prot3 ) {
				push @comps3, $name3;
				print $fh1 join ( "\t",
					$protocol_projsym->{$name3}->{prot_name},
					$protocol_projsym->{$name3}->{projsym} );
				print $fh2 join ( "\t",
					$protocol_projsym->{$name2}->{prot_name},
					$protocol_projsym->{$name3}->{prot_name} );

				#print 'name3 ' . $name3 . ' ' . Dumper($protocol_projsym->{$name3});
			}
			print $fh1 join ( "\t",
				$protocol_projsym->{$name2}->{prot_name},
				$protocol_projsym->{$name2}->{projsym} );
			print $fh2 join ( "\t",
				$protocol_projsym->{$name1}->{prot_name},
				$protocol_projsym->{$name2}->{prot_name} );

			#print 'name2 '. $name2 . ' '. Dumper($protocol_projsym->{$name2});
		}
		print $fh1
		  join ( "\t", $protocol_projsym->{$name1}->{prot_name},
			$protocol_projsym->{$name1}->{projsym} );
	}

	# walk the tree write protocol_observablefeatures
	while ( my ( $protocolName, $feature ) = each(%$feat) ) {
		for my $featureName ( keys %$feature ) {
			print $fh3 join ( "\t", $protocolName, $featureName );
		}
	}
	close $fh1;
	close $fh2;
	close $fh3;
}

sub write_observedvalue($$) {
	local $\ = "\n";    # do the magic of println

	open my $fh1, ">:utf8", "$basedir/observedvalue.txt" or die "$!";

	# write headers
	print $fh1 join (
		"\t",
		qw/measnum observationTarget_name observationTarget_investigation_name observableFeature_name observableFeature_investigation_name investigation_name value/
	);
	my $observationTarget_name;
	my $observableFeature_name;
	my $investigation_name;

	while ( my ( $id, $datapoint ) = each(%datapoint) ) {
		$observationTarget_name = $datapoint->{animal_id};
		for my $measnum ( keys %{ $datapoint->{measnum} } ) {
			$observableFeature_name = $measurement{$measnum}->{varname};
			$investigation_name     = $measurement{$measnum}->{projsym};
			for my $value ( @{ $datapoint->{measnum}->{$measnum} } ) {
				print $fh1 join ( "\t",
					$measnum, $observationTarget_name, $investigation_name, $observableFeature_name,
					$investigation_name, $investigation_name, $value );
			}
		}
	}
	close $fh1;

}

sub write_observablefeature() {
	local $\ = "\n";    # do the magic of println
	my %unit;           # stores unique units
	open my $fh1, ">:utf8", "$basedir/observablefeature.txt" or die "$!";

	# write headers
	print $fh1 join ( "\t", qw/name investigation_name description unit_term/ );

	# write the fixed 'sex' and 'species' features which are standard
	# TODO

	# write the other features
	while ( my ( $id, $meas ) = each(%measurement) ) {
		print $fh1 join ( "\t", $meas->{varname}, $meas->{projsym}, $meas->{desc}, $meas->{units} );
		$unit{ $meas->{units} }->{term} = $meas->{units};
	}
	close $fh1;

	# add units as ontology terms to file
	open my $fh2, ">>:utf8", "$basedir/ontologyterm.txt" or die "$!";

	for my $key ( keys %unit ) {

		# headers: name,termLabel,termAccession,termSource_name
		# data: mouse strain,mouse strain,http://www.ebi.ac.uk/efo/EFO_0000607,EFO
		print $fh2 join ( "\t", $unit{$key}->{term}, $unit{$key}->{term}, q{} );
	}
	close $fh2;
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
}

sub write_investigation($$) {
	local $\ = "\n";        # do the magic of println

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
	# write header
	open my $fh_out, ">:utf8", "$basedir/ontology.txt"
	  or die "ERROR: Can't open ontology.txt for write. $!";
	print $fh_out join ( "\t", qw/name ontologyAccession/ );
	print $fh_out join ( "\t", 'EFO', 'http://www.ebi.ac.uk/efo' );
	close $fh_out;

	# for each ontology add terms
	open $fh_out, ">:utf8", "$basedir/ontologyterm.txt"
	  or die "ERROR: Can't open ontologyterm.txt for write. $!";

	# header
	print $fh_out join ( "\t", qw/term termAccession ontology_name/ );

	my @ontologyterm = (
		[ 'mouse strain', 'http://www.ebi.ac.uk/efo/EFO_0000607', 'EFO' ],
		[ 'day',          'http://www.ebi.ac.uk/efo/EFO_0001789', 'EFO' ]
	);

	for my $line (@ontologyterm) {
		print $fh_out join ( "\t", @$line );
	}
	close $fh_out;
}

sub load_animaldatapoints($$) {
	my $csv = make_csv_parser();
	open my $fh_in, "<:utf8", "$basedir/orig/animaldatapoints.txt"
	  or die "ERROR: Can't load animaldatapoints.txt. $!";

	# set column names to headers
	$csv->column_names( $csv->getline($fh_in) );

	my $c;

	my %warning;     # stores measnum that warning was already printed for
	my %animalid;    # stores animalids for consistency checking
	my %measid;      # stores measurements that have values for consistency checking
	my %measdesc;    # stores measurements descriptions for consistensy checking

	until ( $csv->eof() ) {
		my $row = trim_row( $csv->getline_hr($fh_in) );
		check_parser_for_errors( \$csv, \$row );

		# modify some values on the fly
		for my $heading ( keys %$row ) {
			if ( defined $row->{$heading}
				&& $row->{$heading} =~ /[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/ )
			{
				$warning{ "Substituting illegal character in >>> " . $& }++;
				$row->{$heading} =~ s/[^<>\/a-zA-Z0-9_\s\-:\.(),;\+\*]/ /g;
			}
		}

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

	# print accumulated warnings
	for my $msg ( sort( keys %warning ) ) {
		WARN $msg;
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

		# assign variables
		$measurement{ $row->{measnum} }->{varname} = $row->{varname};
		$measurement{ $row->{measnum} }->{desc}    = $row->{desc};
		$measurement{ $row->{measnum} }->{units}   = $row->{units};
		$measurement{ $row->{measnum} }->{projsym} = 'MPD: ' . $row->{projsym};
		$measurement{ $row->{measnum} }->{cat1}    = $row->{cat1} if $row->{cat1} ne '=';
		$measurement{ $row->{measnum} }->{cat2}    = $row->{cat2} if $row->{cat2} ne '=';
		$measurement{ $row->{measnum} }->{cat3}    = $row->{cat3} if $row->{cat3} ne '=';

	}
	close($fh_in);

	remove_dup_measurements(%measurement);

}

sub trim_row ($) {
	my $row = shift;
	local $\ = "\n";    # do the magic of println

	for my $key ( keys %$row ) {
		if ( defined $row->{$key} && $row->{$key} =~ s/\s{2,}// ) {
			WARN "TRIMMING $row->{$key}";
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/^\s+// ) {
			WARN "TRIMMING $row->{$key}";
		}
		if ( defined $row->{$key} && $row->{$key} =~ s/\s+$// ) {
			WARN "TRIMMING $row->{$key}";
		}
	}

	return $row;
}

sub remove_dup_measurements($) {

	my ( %word_count, %warning );

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

	# print accumulated warnings
	for my $msg ( sort( keys %warning ) ) {
		WARN $msg;
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
