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

inputs "${arrayfile}"
inputs ""
outputs ""

##Set R library path
R_LIBS=${gatkrlib}
export R_LIBS

##Extract header and individual from GenomeStudio Final_Report
head -10 ${arrayfile} > ${finalreport}
awk '$2 == "${arrayid}" {$2 = "${externalSampleID}"; print}' ${arrayfile} >> ${finalreport}

##Push sample belonging to family "1" into list.txt
echo '1 ${externalSampleID}' > ${familylist}

##Create .fam, .lgen and .map file from sample_report.txt
sed -e '1,10d' ${finalreport} | awk '{print "1",$2,"0","0","0","1"}' | uniq > ${arrayfamfile}
sed -e '1,10d' ${finalreport} | awk '{print "1",$2,$1,$3,$4}' | awk -f ${tooldir}/scripts/RecodeFRToZero.awk > ${arraylgenfile}
sed -e '1,10d' ${finalreport} | awk '{print $6,$1,"0",$7}' OFS="\t" | sort -k1n -k4n | uniq > ${arraytmpmapfile}
grep -P '^[123456789]' ${arraytmpmapfile} | sort -k1n -k4n > ${arraymapfile}
grep -P '^[X]\s' ${arraytmpmapfile} | sort -k4n >> ${arraymapfile}
grep -P '^[Y]\s' ${arraytmpmapfile} | sort -k4n >> ${arraymapfile}

##Create .bed and other files (keep sample from sample_list.txt).
${plink-1.07-x86_64} \
--lfile ${fileWithIndexID} \
--recode \
--out ${fileWithIndexID} \
--keep ${familylist}

##Create genotype VCF for sample
${plink-1.08} \
--recode-vcf \
--ped ${arraypedfile} \
--map ${arraymapfile} \
--out ${fileWithIndexID}

##Rename plink.vcf to sample.vcf
mv ${arrayvcffile} ${genvcffile}

##Remove family ID from sample in header genotype VCF
perl -pi -e 's/1_${externalSampleID}/${externalSampleID}/' ${genvcffile}


#################################
############ CONTINUE FROM HERE!#
#################################


##Create binary ped (.bed) and make tab-delimited .fasta file for all genotypes
sed -e 's/chr//' ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.vcf | awk '{OFS="\t"; if (!/^#/){print $1,$2-1,$2}}' \
> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.bed

${tooldir}/BEDTools-Version-2.11.2/bin/fastaFromBed \
-fi ${resdir}/b36/hs_ref_b36.fasta \
-bed ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.bed \
-fo ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.tabdelim.fasta -tab

##Align vcf to reference AND DO NOT FLIP STRANDS!!! (genotype data is already in forward-forward format) If flipping is needed use "-f" command before sample.genotype_array.vcf
perl ${tooldir}/scripts/align-vcf-to-ref.pl \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.vcf \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.tabdelim.fasta \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.vcf \
> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.out

##Lift over sample.genotype_array.aligned_to_ref.vcf from build 36 to build 37
perl ${tooldir}/GATK-1.0.5069/Sting/perl/liftOverVCF.pl \
-vcf ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.vcf \
-gatk ${tooldir}/GATK-1.0.5069/Sting \
-chain ${resdir}/b36/chainfiles/b36ToHg19.broad.over.chain \
-newRef ${resdir}/hg19/indices/human_g1k_v37 \
-oldRef ${resdir}/b36/hs_ref_b36 \
-tmp ${tempdir} \
-out ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.vcf

##Some GATK versions sort header alphabetically, which results in wrong individual genotypes. So cut header from "original" sample.genotype_array.vcf and replace in sample.genotype_array.aligned_to_ref.lifted_over.out
head -3 ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.vcf > ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.header.txt

sed '1,4d' ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.vcf \
> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.headerless.vcf

cat ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.header.txt \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.headerless.vcf \
> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.updated_header.vcf

##Create interval_list of CHIP SNPs to call SNPs in sequence data on
perl ${tooldir}/scripts/iChip_pos_to_interval_list.pl \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.updated_header.vcf \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.updated_header.interval_list

###THESE STEPS USE NEWER VERSION OF GATK THAN OTHER STEPS IN ANALYSIS PIPELINE!!!
##Call SNPs on all positions known to be on array and output VCF (including hom ref calls)
java -Xmx4g -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
-l INFO \
-T UnifiedGenotyper \
-R ${resdir}/${genome}/indices/human_g1k_v37.fa \
-I ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe13.recalibrate.ftl.${index_file}.recal.sorted.bam \
-o ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.all_sites.vcf \
-stand_call_conf 30.0 \
-stand_emit_conf 10.0 \
-out_mode EMIT_ALL_SITES \
-L ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.updated_header.interval_list

##Change FILTER column from GATK "called SNPs". All SNPs having Q20 & DP10 change to "PASS", all other SNPs are "filtered" (not used in concordance check)
perl ${tooldir}/scripts/change_vcf_filter.pl \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.all_sites.vcf \
${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.concordance_check.q20_dp10.vcf 10 20

##Calculate condordance between genotype SNPs and GATK "called SNPs"
java -Xmx2g -Djava.io.tmpdir=${tempdir} -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
-T VariantEval \
-eval:eval,VCF ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.concordance_check.q20_dp10.vcf \
-comp:comp_immuno,VCF ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.genotype_array.aligned_to_ref.lifted_over.updated_header.vcf \
-o ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.q20_dp10_concordance.eval \
-R ${resdir}/${genome}/indices/human_g1k_v37.fa \
-D:dbSNP,VCF /target/gpfs2/gcc/home/fvandijk/VQSR_test/dbsnp_132.b37.excluding_sites_after_129.vcf \
-EV GenotypeConcordance

##Create concordance output file with header
echo 'name,step,#SNPs\t%dbSNP,Ti/Tv_known,Ti/Tv_Novel,%All_comp_het_called_het,%Known_comp_het_called_het,%Non-Ref_Sensitivity,%Non-Ref_discrepancy,%Overall_concordance\n' \
> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.calls_vs_array_concordance.txt


##Retrieve name,step,#SNPs,%dbSNP,Ti/Tv known,Ti/Tv Novel,Non-Ref Sensitivity,Non-Ref discrepancy,Overall concordance from sample.q20_dp10_concordance.eval
##Don't forget to add .libPaths("/target/gpfs2/gcc/tools/GATK-1.3-24-gc8b1c92/public/R") to your ~/.Rprofile
Rscript ${tooldir}/scripts/extract_info_GATK_variantEval_V3.R \
--in ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.q20_dp10_concordance.eval \
--step q20_dp10_concordance \
--name ${sample} \
--comp comp_immuno \
--header >> ${outputdir}/${sample}/${sample}.${flowcell}_${lane}.HSpe26.concordance_check.calls_vs_array_concordance.txt

<@end />