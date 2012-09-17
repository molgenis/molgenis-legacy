

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

#INPUTS plinkdatatransposed
#OUTPUTS beaglefile
#EXES plink
#TARGETS project,chr

#inputs "${plinkdatatransposed}.tped"
#alloutputsexist "${plinkdatatransposed}.frq"
#alloutputsexist "${plinkdatatransposed}.log"
#alloutputsexist "${markers}"


#Create first part of marker file for beagle
${plink} \
--tfile ${plinkdatatransposed} \
--freq \
--out ${plinkdatatransposed} \
--noweb

gawk '$1!="CHR" {print $2,$3,$4}' ${plinkdatatransposed}.frq \
> ${markerspt1}


#Create second part of markers
${plink} \
--tfile ${plinkdatatransposed} \
--recode \
--out ${plinkdata} \
--noweb

gawk '{print $2,$4,$1":"$4}' ${plinkdata}.map \
> ${markerspt2}


#Join both markerfiles merging via IDs
join -1 1 -2 1 \
${markerspt1} \
${markerspt2} \
| gawk '{print $5,$4,$2,$3}' \
> ${markers} #<- define this later when method is finished (include step from perl scripts)

#####ADD rest of template when steps are exactly known######

#vcftools -> --keep-filtered PASS

#--non-ref-af <float>
#--max-non-ref-af <float>



# Read GoNL vcf, extract lines starting with chrnumber, check alt allele for . If alt allele doesn't contain a . print marker file
#grep -P '^20.+' gonl.chr20.release3.vcf | awk '{ if($5!=".") {print $1":"$2,$2,$4,$5}}'