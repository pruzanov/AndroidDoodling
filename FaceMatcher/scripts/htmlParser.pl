#!/usr/bin/perl -w

#
# Simple html parser for FaceMatcher
#

use strict;
use Data::Dumper;
use IO::File;
use JSON;
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
# Add elements to hash (different implementation)
#==================================================

sub add_to_results {
 
  my $html_text; 
  my $count=0;
  my $in = shift @_;
  my $fh = new IO::File("<$in") or die "Couldn't read from [$in]";
  while(<$fh>) {
   chomp;
   if (/Items\d*_files/) {
       $html_text.=$_;
   }
 }
 $fh->close();

  while($html_text=~/Items\d*_files\/(.\w+?.jpg)".+?="ms-cellstyle ms-vb2\">(.+?)<\/td/gc) {
       my $image = $1;
       my $name  = $2;
       if ($image =~/^blank\./){next;}
       $image =~s/\-/_/g;
       $image =~s!.*/!!;
       $image =~tr/A-Z/a-z/;
       $image =~s/jpg/png/;
       $name =~s/&#39;/'/g;
       if (!$seen{$name}++) {
        push(@{$results->{people}}, {name=>$name, png=>$image});
        $count++;
       }
  }
  
  print STDERR "Processed $count entries\n" if DEBUG;
}

