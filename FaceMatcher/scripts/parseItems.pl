#!/usr/bin/perl -w

use strict;
use JSON;
use Data::Dumper;
use IO::File;
use constant DEBUG=>0;

my @files = ();
while (my $in = shift @ARGV) {
 push (@files, $in);
}

print STDERR scalar(@files)." Files received\n" if DEBUG;

my $results = {people=>[]};
my %seen = ();
map{&add_to_results($_)} @files;

#print Dumper($results) if DEBUG;
print STDERR scalar(@{$results->{people}})." items retrieved\n";
my $json = JSON->new();
$json = encode_json $results;

print $json."\n";


#==================================================
# Add elements to hash
#==================================================

sub add_to_results {
 
  my $json_text; 
  my $in = shift @_;
  my $fh = new IO::File("<$in") or die "Couldn't read from [$in]";
  while(<$fh>) {
   chomp;
  $json_text.=$_;
 }
 $fh->close();

 my $blah = decode_json $json_text;

 foreach my $topkey(keys %{$blah}) {
  foreach my $person(@{$blah->{$topkey}}) {
   #print Dumper($person) if DEBUG;
    my $name = $person->{Staff_x0020_Name};
    my $image= $person->{OICR_x0020_Photo};
    if ($name && $image) {
     $image =~s/\-/_/g;
     $image =~s!.*/!!;
     $image =~tr/A-Z/a-z/;
     $image =~s/jpg/png/;
     $name =~s/&#39;/'/g;
     if (!$seen{$person->{ID}}++) {
       push(@{$results->{people}}, {name=>$name, png=>$image});
     }
    } 
  }
  print STDERR "Processed ".scalar(@{$blah->{$topkey}})." entries\n" if DEBUG;
 }
}
