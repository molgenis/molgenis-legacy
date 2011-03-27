How to install XGAP locally:

Step 1: Install and configure required software

XGAP is based on  MOLGENIS which is known to run happily on Windows, Linux and Mac. We here assume the procedure for the windows installation; for other operating system distributions we refer to respective documentations. To get started, download and install the most recent:

    * Java 6 JDK  http://java.sun.com/javase/downloads/
      Just install the most recent standard JDK with standard options.
    * Tomcat >5 web server  http://tomcat.apache.org/
      Remember the root password you are asked
    * Mysql >5.1 database  http://dev.mysql.com/downloads/mysql/5.1.html During installation of the windows version you need to tick 'innodb'. Remember your root password.
    * Download XGAP .war and .sql files at  XGAP Install downloads, current version xgap_1_3_distro 

Step 2: Configure XGAP mysql database

Open mysql commandline client and:

    * Create a database named 'xgap_1_3_distro'`

      create database xgap_1_3_distro;

    * Allow molgenis (engine of XGAP) to read and write to this database as user 'molgenis'`

      grant all privileges on xgap_1_3_distro.* to 'molgenis'@'localhost' identified by 'molgenis'; 
      flush privileges;

    * Select the database you just created.

      use xgap_1_3_distro;

    * Copy-paste the contents of xgap_create_tables.sql in the mysql prompt to create the database. Alternatively, use command:

      source /path/to/xgap_create_tables.sql

Step 3: Run XGAP software on web server

    * If not already started (as in windows) start Tomcat using the proper startup SH or BAT script 

    * Visit your local Tomcat Manager at
      http://localhost:8080/manager/html 

    * At "WAR file to deploy", select the XGAP_1_2_distro.war file and click "Deploy" 

You can now use your XGAP at  http://localhost:8080/xgap_1_3_distro/molgenis.do


Development installation of XGAP:

XGAP builds on the MOLGENIS toolbox for XgapCustomization. There are three steps involved in the development installation of XGAP:

    * Download XGAP from the repository. Currently we use version 1.2
    * Download  MOLGENIS from a repository. Currently we use version 3.3
    * Check buildpath and running of the generator 

We will now go into detail for each of these steps.
Download XGAP source code from subversion

For this tutorial it is assumed you already installed Java, MySQL and Tomcat as described in XgapInstall or  MolgenisInstall.

1) Get an IDE such as Eclipse (see:  http://www.eclipse.org) and install by extracting the archive.

For this tutorial, we assume you use Eclipse and start out with a clean installation and a new workspace.

2) Install Subclipse (see:  http://subclipse.tigris.org):

    * Go to Help -> Install New SoftwareÉ
    * Paste the URL  http://subclipse.tigris.org/update_1.6.x or equivalent, tick 'Subclipse', click Next 

    * Click Next again, 'I agree' (if you do), Finish.
    * Restart Eclipse 'Yes'. 

3) Switch to SVN perpective:

    * Go to Window -> Open Perspective -> OtherÉ
    * Select 'SVN Repository Exploring' 

4) Add the XGAP repository:

    * Click right-mouse button in the SVN window 

    * Enter this URL: http://www.xgap.org/svn and click Finish. 

5) Download the XGAP distro:

    * Expand the repository. Right-click on 'xgap_1_3_distro' and select Checkout. 

    * Leave the settings and click Finish. 

Download MOLGENIS

1) Stay in SVN perspective or repeat Step 3 of 'Download XGAP'.

2) Add the XGAP repository:

    * Click right-mouse button in the SVN window
    * Enter this URL:  http://gbic.target.rug.nl/svn/molgenis and click Finish. 

3) Download the MOLGENIS toolbox:

    * Expand the repository. Expand the folder 'molgenis', right-click on '3.3' and select Checkout. 

Running XGAP

The distro doesn't contain generated code. So we will first generate this code. Then we will update the database and run XGAP. See  http://www.molgenis.org/wiki/MolgenisGeneratorBasics for general details on this procedure.

1) Switch back to Java EE perspective. (either use the toolbar or Window -> Open Perspective)

2) Check the xgap_1_3_distro build path. The XGAP distro project has a number of pre-set dependancies. The 'Java EE module dependancies' should include the 'molgenis' project as well as the libraries contained within.

3) The other libraries needed are as such:

4) When this is set up correctly, you can run the MolgenisGenerate.java to generate the code for the XGAP system. You can find the generator in handwritten/java:

5) Refresh eclipse to pick up the generated code. To this end rightclick on the xgap_1_3_distro and click 'Refresh'. The code is now compiled.

With all the code compiled we can now update the database and run XGAP. We assume you have created a xgap_1_3_distro database as described in XgapInstall and have given MOLGENIS permission to access it.

6) To update the database structure, run MolgenisUpdateDatabase.java. This updates the database structure

7) Run XGAP by right-clicking the xgap_1_3_distro project and choosing 'Run -> Run on server'. Notice: on the first run this will in a dialog asking you to define a new server. Choose Tomcat of your version and choose [next] to point it to the right directory. 

This readme was based on: http://www.xgap.org/wiki/XgapInstall and http://www.xgap.org/wiki/XgapDevelopment