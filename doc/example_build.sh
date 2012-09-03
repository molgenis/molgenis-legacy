#name of the directory and the main file in that directory
PREFIX=example

#build directory
mkdir ../dist/doc
OUTDIR="../dist/doc/$PREFIX"
mkdir $OUTDIR
cp -R $PREFIX/* $OUTDIR/

#change to outdir
cd $OUTDIR

#html
pandoc -s -S --toc -c pandoc.css $PREFIX.md -o $PREFIX.html

#pdf
pandoc -s -S --toc -c pandoc.css $PREFIX.md -o $PREFIX.pdf

#texi
pandoc $PREFIX.md -s -o $PREFIX.texi
makeinfo $PREFIX.texi --html -o web
texi2pdf $PREFIX.texi 
