#
# =====================================================
# $Id: ConcordanceCheck.ftl 12159 2012-06-13 10:56:41Z freerkvandijk $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/ConcordanceCheck.ftl $
# $LastChangedDate: 2012-06-13 12:56:41 +0200 (Wed, 13 Jun 2012) $
# $LastChangedRevision: 12159 $
# $LastChangedBy: freerkvandijk $
# =====================================================
#

#MOLGENIS walltime=09:59:00 mem=4
#FOREACH externalSampleID

inputs "${mergedbam}"
alloutputsexist \
"${finalreport}" \
"${familylist}" \
"${sample}.concordance.fam" \
"${sample}.concordance.lgen" \
"${arraytmpmap}" \
"${arraymapfile}" \
"${sample}.ped" \
"${sample}.genotypeArray.vcf" \
"${sample}.genotypeArray.bed" \
"${sample}.genotypeArray.fasta" \
"${sample}.genotypeArray.aligned_to_ref.vcf.out" \
"${sample}.genotypeArray.aligned_to_ref.vcf" \
"${sample}.genotypeArray.aligned_to_ref.lifted_over.vcf" \
"${sample}.genotypeArray.header.txt" \
"${sample}.genotypeArray.headerless.vcf" \
"${sample}.genotypeArray.updated.header.vcf" \
"${sample}.concordance.allSites.vcf" \
"${sample}.genotypeArray.updated.header.interval_list" \
"${sample}.concordance.q20.dp10.vcf" \
"${sample}.concordance.q20.dp10.eval" \
"${sampleconcordancefile}"

if test ! -e ${finalreport};
then
	echo "name, step, nSNPs, PercDbSNP, Ti/Tv_known, Ti/Tv_Novel, All_comp_het_called_het, Known_comp_het_called_het, Non-Ref_Sensitivity, Non-Ref_discrepancy, Overall_concordance" > ${sampleconcordancefile}
	echo "[1] NA,NA,NA,NA,NA,NA,NA,NA,NA,NA,NA" >> ${sampleconcordancefile} 
else
	##Set R library path
	export PATH=${R_HOME}/bin:<#noparse>${PATH}</#noparse>
	export R_LIBS=${R_LIBS}
	
	##Push sample belonging to family "1" into list.txt
	echo '1 ${externalSampleID}' > ${familylist}
	
	##Create .fam, .lgen and .map file from sample_report.txt
	sed -e '1,10d' ${finalreport} | awk '{print "1",$2,"0","0","0","1"}' | uniq > ${sample}.concordance.fam
	sed -e '1,10d' ${finalreport} | awk '{print "1",$2,$1,$3,$4}' | awk -f ${tooldir}/scripts/RecodeFRToZero.awk > ${sample}.concordance.lgen
	sed -e '1,10d' ${finalreport} | awk '{print $6,$1,"0",$7}' OFS="\t" | sort -k1n -k4n | uniq > ${arraytmpmap}
	grep -P '^[123456789]' ${arraytmpmap} | sort -k1n -k4n > ${arraymapfile}
	grep -P '^[X]\s' ${arraytmpmap} | sort -k4n >> ${arraymapfile}
	grep -P '^[Y]\s' ${arraytmpmap} | sort -k4n >> ${arraymapfile}
	
	#?# MD vraagt: wat doen --lfile en --out, en horen die gelijk te zijn?
	##Create .bed and other files (keep sample from sample_list.txt).
	${tooldir}/plink-1.07-x86_64/plink-1.07-x86_64/plink \
	--lfile ${sample}.concordance \
	--recode \
	--out ${sample}.concordance \
	--keep ${familylist}
	
	##Create genotype VCF for sample
	${tooldir}/plink-1.08/plink108 \
	--recode-vcf \
	--ped ${sample}.concordance.ped \
	--map ${arraymapfile} \
	--out ${sample}.concordance
	
	##Rename plink.vcf to sample.vcf
	mv ${sample}.concordance.vcf ${sample}.genotypeArray.vcf
	
	##Replace chr23 and 24 with X and Y
    perl -pi -e 's/^23/X/' ${sample}.genotypeArray.vcf
    perl -pi -e 's/^24/Y/' ${sample}.genotypeArray.vcf
	
	##Remove family ID from sample in header genotype VCF
	perl -pi -e 's/1_${externalSampleID}/${externalSampleID}/' ${sample}.genotypeArray.vcf
	
	##Create binary ped (.bed) and make tab-delimited .fasta file for all genotypes
	sed -e 's/chr//' ${sample}.genotypeArray.vcf | awk '{OFS="\t"; if (!/^#/){print $1,$2-1,$2}}' \
	> ${sample}.genotypeArray.bed
	
	${tooldir}/BEDTools-Version-2.11.2/bin/fastaFromBed \
	-fi ${resdir}/b36/hs_ref_b36.fasta \
	-bed ${sample}.genotypeArray.bed \
	-fo ${sample}.genotypeArray.fasta -tab
	
	###############################
	#Check build of arraydata by taking rs10001565 and checking the position on chr1
	position=`awk '$3 == "rs10001565" {print $2}' ${sample}.genotypeArray.vcf`
	
	if [ ! -z $position ] && [ $position == 15331671 ]
	then # File is on build36
	
		##Align vcf to reference AND DO NOT FLIP STRANDS!!! (genotype data is already in forward-forward format) If flipping is needed use "-f" command before sample.genotype_array.vcf
		perl ${tooldir}/scripts/align-vcf-to-ref.pl \
		${sample}.genotypeArray.vcf \
		${sample}.genotypeArray.fasta \
		${sample}.genotypeArray.aligned_to_ref.vcf \
		> ${sample}.genotypeArray.aligned_to_ref.vcf.out
	
		##Lift over sample.genotype_array.aligned_to_ref.vcf from build 36 to build 37
		perl ${tooldir}/GATK-1.0.5069/Sting/perl/liftOverVCF.pl \
		-vcf ${sample}.genotypeArray.aligned_to_ref.vcf \
		-gatk ${tooldir}/GATK-1.0.5069/Sting \
		-chain ${resdir}/b36/chainfiles/b36ToHg19.broad.over.chain \
		-newRef ${resdir}/hg19/indices/human_g1k_v37 \
		-oldRef ${resdir}/b36/hs_ref_b36 \
		-tmp ${tempdir} \
		-out ${sample}.genotypeArray.aligned_to_ref.lifted_over.vcf
	
		##Some GATK versions sort header alphabetically, which results in wrong individual genotypes. So cut header from "original" sample.genotype_array.vcf and replace in sample.genotype_array.aligned_to_ref.lifted_over.out
		head -3 ${sample}.genotypeArray.vcf > ${sample}.genotypeArray.header.txt
	
		sed '1,4d' ${sample}.genotypeArray.aligned_to_ref.lifted_over.vcf \
		> ${sample}.genotypeArray.headerless.vcf
	
		cat ${sample}.genotypeArray.header.txt \
		${sample}.genotypeArray.headerless.vcf \
		> ${sample}.genotypeArray.updated.header.vcf
	
		##Create interval_list of CHIP SNPs to call SNPs in sequence data on
		perl ${tooldir}/scripts/iChip_pos_to_interval_list.pl \
		${sample}.genotypeArray.updated.header.vcf \
		${sample}.genotypeArray.updated.header.interval_list
	
		###THESE STEPS USE NEWER VERSION OF GATK THAN OTHER STEPS IN ANALYSIS PIPELINE!!!
		##Call SNPs on all positions known to be on array and output VCF (including hom ref calls)
		java -Xmx4g -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
		-l INFO \
		-T UnifiedGenotyper \
		-R ${indexfile} \
		-I ${mergedbam} \
		-o ${sample}.concordance.allSites.vcf \
		-stand_call_conf 30.0 \
		-stand_emit_conf 10.0 \
		-out_mode EMIT_ALL_SITES \
		-L ${sample}.genotypeArray.updated.header.interval_list
	
		##Change FILTER column from GATK "called SNPs". All SNPs having Q20 & DP10 change to "PASS", all other SNPs are "filtered" (not used in concordance check)
		perl ${tooldir}/scripts/change_vcf_filter.pl \
		${sample}.concordance.allSites.vcf \
		${sample}.concordance.q20.dp10.vcf 10 20
	
		##Calculate condordance between genotype SNPs and GATK "called SNPs"
		java -Xmx2g -Djava.io.tmpdir=${tempdir} -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
		-T VariantEval \
		-eval:eval,VCF ${sample}.concordance.q20.dp10.vcf \
		-comp:comp_immuno,VCF ${sample}.genotypeArray.updated.header.vcf \
		-o ${sample}.concordance.q20.dp10.eval \
		-R ${indexfile} \
		-D:dbSNP,VCF ${dbsnpexsitesafter129vcf} \
		-EV GenotypeConcordance
	
		##Create concordance output file with header
		echo 'name, step, nSNPs, PercDbSNP, Ti/Tv_known, Ti/Tv_Novel, All_comp_het_called_het, Known_comp_het_called_het, Non-Ref_Sensitivity, Non-Ref_discrepancy, Overall_concordance' \
		> ${sampleconcordancefile}
	
		##Retrieve name,step,#SNPs,%dbSNP,Ti/Tv known,Ti/Tv Novel,Non-Ref Sensitivity,Non-Ref discrepancy,Overall concordance from sample.q20_dp10_concordance.eval
		##Don't forget to add .libPaths("/target/gpfs2/gcc/tools/GATK-1.3-24-gc8b1c92/public/R") to your ~/.Rprofile
		Rscript ${tooldir}/scripts/extract_info_GATK_variantEval_V3.R \
		--in ${sample}.concordance.q20.dp10.eval \
		--step q20_dp10_concordance \
		--name ${externalSampleID} \
		--comp comp_immuno \
		--header >> ${sampleconcordancefile}
	
	else
		###################################
		#Arrayfile is on build 37 (position 15722573)
		
		##Align vcf to reference AND DO NOT FLIP STRANDS!!! (genotype data is already in forward-forward format) If flipping is needed use "-f" command before sample.genotype_array.vcf
		perl ${tooldir}/scripts/align-vcf-to-ref.pl \
		${sample}.genotypeArray.vcf \
		${sample}.genotypeArray.fasta \
		${sample}.genotypeArray.aligned_to_ref.vcf \
		> ${sample}.genotypeArray.aligned_to_ref.vcf.out
	
		##Some GATK versions sort header alphabetically, which results in wrong individual genotypes. So cut header from "original" sample.genotype_array.vcf and replace in sample.genotype_array.aligned_to_ref.lifted_over.out
		head -3 ${sample}.genotypeArray.vcf > ${sample}.genotypeArray.header.txt
	
		sed '1,3d' ${sample}.genotypeArray.aligned_to_ref.vcf \
		> ${sample}.genotypeArray.headerless.vcf
	
		cat ${sample}.genotypeArray.header.txt \
		${sample}.genotypeArray.headerless.vcf \
		> ${sample}.genotypeArray.updated.header.vcf
	
		##Create interval_list of CHIP SNPs to call SNPs in sequence data on
		perl ${tooldir}/scripts/iChip_pos_to_interval_list.pl \
		${sample}.genotypeArray.updated.header.vcf \
		${sample}.genotypeArray.updated.header.interval_list
	
		###THESE STEPS USE NEWER VERSION OF GATK THAN OTHER STEPS IN ANALYSIS PIPELINE!!!
		##Call SNPs on all positions known to be on array and output VCF (including hom ref calls)
		java -Xmx4g -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
		-l INFO \
		-T UnifiedGenotyper \
		-R ${indexfile} \
		-I ${mergedbam} \
		-o ${sample}.concordance.allSites.vcf \
		-stand_call_conf 30.0 \
		-stand_emit_conf 10.0 \
		-out_mode EMIT_ALL_SITES \
		-L ${sample}.genotypeArray.updated.header.interval_list
	
		##Change FILTER column from GATK "called SNPs". All SNPs having Q20 & DP10 change to "PASS", all other SNPs are "filtered" (not used in concordance check)
		perl ${tooldir}/scripts/change_vcf_filter.pl \
		${sample}.concordance.allSites.vcf \
		${sample}.concordance.q20.dp10.vcf 10 20
	
		##Calculate condordance between genotype SNPs and GATK "called SNPs"
		java -Xmx2g -Djava.io.tmpdir=${tempdir} -jar ${tooldir}/GATK-1.2-1-g33967a4/dist/GenomeAnalysisTK.jar \
		-T VariantEval \
		-eval:eval,VCF ${sample}.concordance.q20.dp10.vcf \
		-comp:comp_immuno,VCF ${sample}.genotypeArray.updated.header.vcf \
		-o ${sample}.concordance.q20.dp10.eval \
		-R ${indexfile} \
		-D:dbSNP,VCF ${dbsnpexsitesafter129vcf} \
		-EV GenotypeConcordance
	
		##Create concordance output file with header
		echo 'name, step, nSNPs, PercDbSNP, Ti/Tv_known, Ti/Tv_Novel, All_comp_het_called_het, Known_comp_het_called_het, Non-Ref_Sensitivity, Non-Ref_discrepancy, Overall_concordance' \
		> ${sampleconcordancefile}
	
		##Retrieve name,step,#SNPs,%dbSNP,Ti/Tv known,Ti/Tv Novel,Non-Ref Sensitivity,Non-Ref discrepancy,Overall concordance from sample.q20_dp10_concordance.eval
		##Don't forget to add .libPaths("/target/gpfs2/gcc/tools/GATK-1.3-24-gc8b1c92/public/R") to your ~/.Rprofile
		Rscript ${tooldir}/scripts/extract_info_GATK_variantEval_V3.R \
		--in ${sample}.concordance.q20.dp10.eval \
		--step q20_dp10_concordance \
		--name ${externalSampleID} \
		--comp comp_immuno \
		--header >> ${sampleconcordancefile}	
	
	
	fi
fi