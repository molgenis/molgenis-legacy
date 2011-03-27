README HOW TO:

Deploy under a specific name and database, ie. 'brassica' instead of 'xgap_1_3_distro':

#####################
### PROJECT NAME  ###
#####################

Either: change 'molgenis name' in xgap_project_url_name:
--> molgenis name="brassica"
and run MolgenisGenerator.java

or:

change 'getMolgenisVariantID()' in MolgenisServlet:
--> return "brassica";

WARNING: be aware that this solution will be overwritten in the next MolgenisGenerate

-!-!-!-!-!-!-!-!-!-!-!-!-
-!- IMPORTANT NOTICE  -!-
-!-!-!-!-!-!-!-!-!-!-!-!-

In XGAP 1.4, data sources can be stored in file backends. The MOLGENIS name is used to seperate projects within the file structure.
Therefore, THIS MUST BE UNIQUE. As a rule of thumb, it should be equal to the deployed URL name. (therefore: project_url_name XML)
For example: deploy name 'brassica', therefore project name 'brassica', data will now be stored as:

So, storage consists of:
1. User entered directory, usable when verified by the system settings plugin
2. projectname (a.k.a. URL name, deploy name)
3. binaryfiles OR plainfiles (OR something else in the future, eg. excelfiles)
4. Escaped 'Investigation' name + underscore
5. Escaped 'Data' name + extension

Example:
1. /data/xgap
2. /brassica
3. /binaryfiles
4. /brassica_nutrigenomics_
5. lcms_lodscores.bin

For added convience, it is advisable to use the deploy name ALSO as database name.


#####################
### DATABASE NAME ###
#####################

Either change db name in 'xgap.properties':
--> db_uri= jdbc:mysql://localhost/brassica?innodb_autoinc_lock_mode=2
and run MolgenisGenerator.java

or:

change 'url' in META-INF/context.xml:
-->url="jdbc:mysql://localhost/brassica?innodb_autoinc_lock_mode=2"

WARNING: be aware that this solution will be overwritten in the next MolgenisGenerate


#####################
###  DEPLOY NAME  ###
#####################

Now deploy as xgap4brassica.war so the URL will be:
http://myserver.somewhere.com:8080/brassica


#####################
###   SECURITY    ###
#####################

Add or enable to WEB-INF/web.xml:
the properties <security-constraint>, <login-config>, <security-role>


