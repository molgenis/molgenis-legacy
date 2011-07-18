

Configure the cluster:

1) add on cluster

module initadd torque
module initadd maui
module initadd R
mkdir libs

2)
logout from your cluster, log back in!

start R

install.packages("RCurl",lib="~/libs")
install.packages("qtl",lib="~/libs")
install.packages("snow",lib="~/libs")


3)

NO LONGER NEEDED
wget -r -l2 http://www.xgap.org/svn/xgap_1_4_distro/handwritten/java/plugins/cluster/R/ClusterJobs/
cd www.xgap.org/svn/xgap_1_4_distro/handwritten/java/plugins/cluster/R/
R CMD INSTALL ClusterJobs --library=~/libs 


3) make sure your firewall does not block communication
	to port where tomcat is running (8080)mo
	
	
______

R CMD BUILD <dir>