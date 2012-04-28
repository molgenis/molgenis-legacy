Welcome to MOLGENIS apps, the suite of biological research portals:

####################################################################
# Building a MOLGENIS app without Eclipse 
####################################################################

Users not using Eclipse can build and start an app on the commandline like this:
1. $> svn co http://www.molgenis.org/svn/molgenis/trunk molgenis
2. $> svn co http://www.molgenis.org/svn/molgenis_apps/trunk molgenis_apps
3. $> cd molgenis_apps
4. $> ant <appname>.build clean-generate-compile-test
5. $> ant <appname>.build run
5. Browse to http://<yourhost>:8080/<appname>/

####################################################################
# Building a MOLGENIS app with Eclipse: Quick start 
####################################################################

This quick start is for those who already have Eclipse + a database back end installed and are familiar with the Eclipse IDE.

If you don't have Eclipse installed, please visit the wiki for instructions: http://www.molgenis.org/
If you don't have a database management system (DBMS) installed, please visit the wiki for instructions: http://www.molgenis.org/.

1. Checkout molgenis/trunk and molgenis_apps/trunk into your Eclipse workspace:
   
2  Create a new database for your app in your DBMS and grant all privileges to that database to a new user for your MOLGENIS app.
   Update the molgenis_apps/apps/<appname>/org/molgenis/<appname>/<appname>.properties file for the connection / authentication details of your new database. 
3. Build the molgenis app of your choice:
   A. Right-click in the molgenis_apps project on the build script for your MOLGENIS app and choose "Run As"-> "Ant..." -> 
      select the Ant task "clean-generate-compile-test" -> click the "Run" button.
   B. If you see BUILD SUCCESSFUL in the Console view you can continue and run your app:
      Right-click in the molgenis_apps project on the build script for your app and choose "Run As"-> "Ant..." -> 
      select the Ant task "run" -> click the "Run" button.
4. Click the hyperlink shown in the popup 'web server', which will open your MOLGENIS app in your favorite web browser.

What happened?
- your Eclipse settings were updated for this app
- your app was generated and run via an embedded web server
- you opened your web browser showing results

For developers, after step 1-4:

5. In Eclipse: right-click the 'molgenis_apps' project and choose 'refresh'
   Eclipse will now discover the generated code and compile, showing compilation errors if any.
   This will also allow you to write your own plugins...

This procedure is tested on
* Windows XP, Vista, 7, Mac OS X 'snow leopard and lion', Ubuntu Linux
* Eclipse Helios and Indigo

# =====================================================
# SVN revision info:
# $Id$
# $URL$
# $LastChangedDate$ 
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
