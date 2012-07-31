

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

#INPUTS imputedoutputbeagle #<-- Add gprobsinput later
#OUTPUTS bgltotrityperoutput
#EXES plink
#TARGETS project,chr

inputs "${imputedoutputbeagle}"
alloutputsexist "${bgltotrityperoutput}" #<-- Check all beagle outputs, where does the inserted chrnum come from??



#Convert beagle output to TriTyper format using hacked converter from Harm-Jan (This should be fixed by TriTyper team/developers)
java -jar -Xmx4g ${convertbgltotrityperjar} \
/target/gpfs2/gcc/home/pdeelen/beagleImputed/imputed.chr20.chr20.bgl.gprobs.gz \ #<-- Change this when known where chrnum is coming from
${bgltotrityperoutput}