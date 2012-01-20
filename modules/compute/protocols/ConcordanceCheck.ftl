#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=09:59:00 mem=4

inputs "${arrayfilelocation}"
inputs "${sortedrecalbam}"
alloutputsexist \
"${finalreport}" \
"${familylist}" \
"${fileWithIndexID}.concordance.fam" \
"${fileWithIndexID}.concordance.lgen" \
"${arraytmpmap}" \
"${arraymapfile}" \
"${fileWithIndexID}.ped" \
"${fileWithIndexID}.genotypeArray.vcf" \
"${fileWithIndexID}.genotypeArray.bed" \
"${fileWithIndexID}.genotypeArray.fasta" \
"${fileWithIndexID}.genotypeArray.aligned_to_ref.vcf.out" \
"${fileWithIndexID}.genotypeArray.aligned_to_ref.vcf" \
"${fileWithIndexID}.genotypeArray.aligned_to_ref.lifted_over.vcf" \
"${fileWithIndexID}.genotypeArray.header.txt" \
"${fileWithIndexID}.genotypeArray.headerless.vcf" \
"${fileWithIndexID}.genotypeArray.updated.header.vcf" \
"${fileWithIndexID}.concordance.allSites.vcf" \
"${fileWithIndexID}.genotypeArray.updated.header.interval_list" \
"${fileWithIndexID}.concordance.q20.dp10.vcf" \
"${fileWithIndexID}.concordance.q20.dp10.eval" \
"${concordancefile}"

##Set R library path
R_LIBS=${tooldir}/GATK-1.3-24-gc8b1c92/gsalib
export R_LIBS

##Extract header and individual from GenomeStudio Final_Report
head -10 ${arrayfilelocation} > ${finalreport}
awk '$2 == "${arrayID}" {$2 = "${externalSampleID}"; print}' ${arrayfilelocation} >> ${finalreport}

##Push sample belonging to family "1" into list.txt
echo '1 ${externalSampleID}' > ${familylist}

##Create .fam, .lgen and .map file from sample_report.txt
sed -e '1,10d' ${finalreport} | awk '{print "1",$2,"0","0","0","1"}' | uniq > ${fileWithIndexID}.concordance.fam
sed -e '1,10d' ${finalreport} | awk '{print "1",$2,$1,$3,$4}' | awk -f ${tooldir}/scripts/RecodeFRToZero.awk > ${fileWithIndexID}.concordance.lgen
sed -e '1,10d' ${finalreport} | awk '{print $6,$1,"0",$7}' OFS="\t" | sort -k1n -k4n | uniq > ${arraytmpmap}
grep -P '^[123456789]' ${arraytmpmap} | sort -k1n -k4n > ${arraymapfile}
grep -P '^[X]\s' ${arraytmpmap} | sort -k4n >> ${arraymapfile}
grep -P '^[Y]\s' ${arraytmpmap} | sort -k4n >> ${arraymapfile}

#?# MD vraagt: wat doen --lfile en --out, en horen die gelijk te zijn?
##Create .bed and other files (keep sample from sample_list.txt).
${tooldir}/plink-1.07-x86_64/plink-1.07-x86_64/plink \
--lfile ${fileWithIndexID}.concordance \
--recode \
--out ${fileWithIndexID}.concordance \
--keep ${familylist}

##Create genotype VCF for sample
${tooldir}/plink-1.08/plink108 \
--recode-vcf \
--ped ${fileWithIndexID}.concordance.ped \
--map ${arraymapfile} \
--out ${fileWithIndexID}.concordance

##Rename plink.vcf to sample.vcf
mv ${fileWithIndexID}.concordance.vcf ${fileWithIndexID}.genotypeArray.vcf

##Remove family ID from sample in header genotype VCF
perl -pi -e 's/1_${externalSampleID}/${externalSampleID}/' ${fileWithIndexID}.genotypeArray.vcf

##Create binary ped (.bed) and make tab-delimited .fasta file for all genotypes
sed -e 's/chr//' ${fileWithIndexID}.genotypeArray.vcf | awk '{OFS="\t"; if (!/^#/){print $1,$2-1,$2}}' \
> ${fileWithIndexID}.genotypeArray.bed

${tooldir}/BEDTools-Version-2.11.2/bin/fastaFromBed \
-fi ${resdir}/b36/hs_ref_b36.fasta \
-bed ${fileWithIndexID}.genotypeArray.bed \
-fo ${fileWithIndexID}.genotypeArray.fasta -tab

##Align vcf to reference AND DO NOT FLIP STRANDS!!! (genotype data is already in forward-forward format) If flipping is needed use "-f" command before sample.genotype_array.vcf
perl ${tooldir}/scripts/align-vcf-to-ref.pl \
${fileWithIndexID}.genotypeArray.vcf \
${fileWithIndexID}.genotypeArray.fasta \
${fileWithIndexID}.genotypeArray.aligned_to_ref.vcf \
> ${fileWithIndexID}.genotypeArray.aligned_to_ref.vcf.out

##Lift over sample.genotype_array.aligned_to_ref.vcf from build 36 to build 37
perl ${tooldir}/GATK-1.0.5069/Sting/perl/liftOverVCF.pl \
-vcf ${fileWithIndexID}.genotypeArray.aligned_to_ref.vcf \
-gatk ${tooldir}/GATK-1.0.5069/Sting \
-chain ${resdir}/b36/chainfiles/b36ToHg19.broad.over.chain \
-newRef ${resdir}/hg19/indices/human_g1k_v37 \
-oldRef ${resdir}/b36/hs_ref_b36 \
-tmp ${tempdir} \
-out ${fileWithIndexID}.genotypeArray.aligned_to_ref.lifted_over.vcf

##Some GATK versions sort header alphabetically, which results in wrong individual genotypes. So cut header from "original" sample.genotype_array.vcf and replace in sample.genotype_array.aligned_to_ref.lifted_over.out
head -3 ${fileWithIndexID}.genotypeArray.vcf > ${fileWithIndexID}.genotypeArray.header.txt

sed '1,4d' ${fileWithIndexID}.genotypeArray.aligned_to_ref.lifted_over.vcf \
> ${fileWithIndexID}.genotypeArray.headerless.vcf

cat ${fileWithIndexID}.genotypeArray.header.txt \
${fileWithIndexID}.genotypeArray.headerless.vcf \
> ${fileWithIndexID}.genotypeArray.updated.header.vcf

##Create interval_list of CHIP SNPs to call SNPs in sequence data on
perl ${tooldir}/scripts/iChip_pos_to_interval_list.pl \
${fileWithIndexID}.genotypeArray.updated.header.vcf \
${fileWithIndexID}.genotypeArray.updated.header.interval_list

###THESE STEPS USE NEWER VERSION OF GATK THAN OTHER STEPS IN ANALYSIS PIPELINE!!!
##Call SNPs on all positions known to be on array and output VCF (including hom ref calls)
java -Xmx4g -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
-l INFO \
-T UnifiedGenotyper \
-R ${indexfile} \
-I ${sortedrecalbam} \
-o ${fileWithIndexID}.concordance.allSites.vcf \
-stand_call_conf 30.0 \
-stand_emit_conf 10.0 \
-out_mode EMIT_ALL_SITES \
-L ${fileWithIndexID}.genotypeArray.updated.header.interval_list

##Change FILTER column from GATK "called SNPs". All SNPs having Q20 & DP10 change to "PASS", all other SNPs are "filtered" (not used in concordance check)
perl ${tooldir}/scripts/change_vcf_filter.pl \
${fileWithIndexID}.concordance.allSites.vcf \
${fileWithIndexID}.concordance.q20.dp10.vcf 10 20

##Calculate condordance between genotype SNPs and GATK "called SNPs"
java -Xmx2g -Djava.io.tmpdir=${tempdir} -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
-T VariantEval \
-eval:eval,VCF ${fileWithIndexID}.concordance.q20.dp10.vcf \
-comp:comp_immuno,VCF ${fileWithIndexID}.genotypeArray.updated.header.vcf \
-o ${fileWithIndexID}.concordance.q20.dp10.eval \
-R ${resdir}/${genome}/indices/human_g1k_v37.fa \
-D:dbSNP,VCF ${resdir}/${genome}/dbsnp/dbsnp_132.b37.excluding_sites_after_129.vcf \
-EV GenotypeConcordance

##Create concordance output file with header
echo 'name, step, nSNPs, PercDbSNP, Ti/Tv_known, Ti/Tv_Novel, All_comp_het_called_het, Known_comp_het_called_het, Non-Ref_Sensitivity, Non-Ref_discrepancy, Overall_concordance' \
> ${concordancefile}

##Retrieve name,step,#SNPs,%dbSNP,Ti/Tv known,Ti/Tv Novel,Non-Ref Sensitivity,Non-Ref discrepancy,Overall concordance from sample.q20_dp10_concordance.eval
##Don't forget to add .libPaths("/target/gpfs2/gcc/tools/GATK-1.3-24-gc8b1c92/public/R") to your ~/.Rprofile
Rscript ${tooldir}/scripts/extract_info_GATK_variantEval_V3.R \
--in ${fileWithIndexID}.concordance.q20.dp10.eval \
--step q20_dp10_concordance \
--name ${externalSampleID} \
--comp comp_immuno \
--header >> ${concordancefile}
<@end />