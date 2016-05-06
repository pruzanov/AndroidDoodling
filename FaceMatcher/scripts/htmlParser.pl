#!/usr/bin/perl -w

#
# Simple html parser for FaceMatcher
# will read from the list of files and parse them, optionally checking if we have existing image
# for an entry
#

use strict;
use Data::Dumper;
use Getopt::Long;
use File::Basename;
use IO::File;
use JSON;
use constant DEBUG=>0;


my $USAGE = "htmlParser.pl --list [list of html files] --img-dir [Optional, dir with existing images]";
my($list,$imgdir);
my $result = GetOptions("list=s"         => \$list,
                        "img-dir=s"      => \$imgdir);

unless($list) { die $USAGE; }
open(LIST,"<$list") or die "Couldn't read from the list [$list]";
my @files = map{chomp; $_;} (<LIST>);
close LIST;

while (my $in = shift @ARGV) {
 push (@files, $in);
}

print STDERR scalar(@files)." Files received\n" if DEBUG;

my $results = {people=>[]};
my %seen = ();
map{&add_to_results($_)} @files;

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

  while($html_text=~/Items\d*_files\/(.\w+?.jpg)".+?="ms-cellstyle ms-vb2\">(.+?)<\/td/igc) {
       my $image = basename($1);
       my $name  = $2;
       ($image,$name) = ($1,$2) if $name=~m!/(\S+?.jpg)\"\s+alt=\"(.+?)\"!i;
       if ($image =~/^blank\./){next;}
       $image =~s/\-/_/g;
       $image =~tr/A-Z/a-z/;
       $image =~s/jpg/png/;
       $name =~s/&#39;/'/g;
       # Skip Names without image (optional)
       if ($imgdir) {
         my $img_path = join("/",($imgdir,$image));
         unless (-e $img_path) { print STDERR "Skipping $name\t(no image available)\n";next; }
       }
       if (!$seen{$name}++) {
        print STDERR "Name: $name\n" if DEBUG;
        push(@{$results->{people}}, {name=>$name, png=>$image});
        $count++;
       }
  }
  
  print STDERR "Processed $count entries\n" if DEBUG;
}

