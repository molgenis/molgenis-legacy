use strict;
use warnings;

#use modules::IO;

my $IDs = "";
my $new_genotypes = "";
my @files;
my $file;
my $individuals;

&main();

sub main
{
	# per regel eerste 3 stukjes weg
	# eerste stukje houden
	# laatste stuk opslitsen met tabs
	for (my $indiv = 1; $indiv <= 1667; $indiv++)
	{
		$individuals .= "\t" . "I" . $indiv;
	}
	for (my $i = 1; $i < 2; $i++)
	{
		$file =  "genotypes_" . $i;
		&splitEnZo();
	}
}

sub readFileArray
{
	my $path = shift;
	open(INFO, $path);
	my @data = <INFO>;
	close(INFO);
	return @data;
}

sub streamingReader
{	
	# leest bestand regel voor regel door
	my $path = shift;
	my $data = '';
	
	open (FILE, '<' . $path) or die "IO: Bestand $data kan niet worden ingelezen:\n- $path -\n";
	while (not eof(FILE))
	{
		$data = <FILE>;
		&splitEnZo($data);
		print "splitenzo\n";
	}
	close (FILE);
}

sub splitEnZo
{
	my $count = 0;
	
	&addToFile($file . "_annotation.txt", "name\tchr\tbpstart" . "\n");
	&addToFile($file . "_matrix.txt", $individuals . "\n");
	
	my @temp = &readFileArray($file . ".txt");

	foreach my $regel (@temp)
	{
		$count++;
		if ($regel =~ m/(.+?)\t(.+?)\t(.+?)\t(.+)/g)
		{
			$IDs .= $1 . "\t" . $2 . "\t" . $3 . "\n";
			$new_genotypes .= $1;
			my @splitter = split('', $4);
			foreach my $val (@splitter)
			{
				$new_genotypes .= "\t" . $val;
			}		
			$new_genotypes .= "\n";
		}
		
		#batch
		if ($count % 5000 == 0)
		{
			print "5000 verder\n";
			
			&addToFile($file . "_annotation.txt", $IDs);
			&addToFile($file . "_matrix.txt", $new_genotypes);
			
			$IDs = "";
			$new_genotypes = "";
		}		
	}
	
	#restje
	&addToFile($file . "_annotation.txt", $IDs);
	&addToFile($file . "_matrix.txt", $new_genotypes);
}

sub addToFile
{
	my $path = shift;
	my $data = shift;
	
	#print "printing to $path...\n";
	
	open (FILE, '>>' . $path);
	print FILE $data;
	close (FILE);
}