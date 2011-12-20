#job_1
job_1=$(qsub -N martijnDemult0_demultiplex  martijnDemult0_demultiplex.sh)
echo $job_1
sleep 8
#job_2
job_2=$(qsub -N martijnDemult1_demultiplex  martijnDemult1_demultiplex.sh)
echo $job_2
sleep 8
#job_3
job_3=$(qsub -N martijnDemult2_demultiplex  martijnDemult2_demultiplex.sh)
echo $job_3
sleep 8
#job_4
job_4=$(qsub -N martijnDemult3_demultiplex  martijnDemult3_demultiplex.sh)
echo $job_4
sleep 8
#job_5
job_5=$(qsub -N martijnDemult4_demultiplex  martijnDemult4_demultiplex.sh)
echo $job_5
sleep 8
#job_6
job_6=$(qsub -N martijnDemult5_demultiplex  martijnDemult5_demultiplex.sh)
echo $job_6
sleep 8
#job_7
job_7=$(qsub -N martijnDemult6_demultiplex  martijnDemult6_demultiplex.sh)
echo $job_7
sleep 8
