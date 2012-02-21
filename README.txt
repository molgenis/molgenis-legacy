Welcome to MOLGENIS apps, the suite of biological research portals:

Quick start:
1. Checkout molgenis/trunk and molgenis_apps/trunk into your Eclipse workspace
2. Build the molgenis app of your choice:
   A. Right-click in the molgenis_apps project on the build script for your MOLGENIS app and choose "Run As"-> "Ant..." -> 
      select the Ant task "clean-generate-compile-test" -> click the "Run" button.
   B. If you see BUILD SUCCESSFUL in the Console view you can continue and run your app:
      Right-click in the molgenis_apps project on the build script for your app and choose "Run As"-> "Ant..." -> 
      select the Ant task "run" -> click the "Run" button.
3. In the popup 'web server': click the hyperlink shown which will open your MOLGENIS app in your favorite web browser

What happened?
- your Eclipse settings were updated for this app
- your app was generated and run via embedded server
- you opened your web browser showing results

For developers, after step 1-3:

4. In Eclipse: right-click the 'molgenis_apps' project and choose 'refresh'
   Eclipse will now discover the generated code and compile, showing compilation errors if any.
   This will also allow you to write your own plugins...

This procedure is tested on
* Windows XP, Vista, 7, Mac OS X 'snow leopard and lion', Ubuntu Linux
* Eclipse Helios and Indigo

Users not using Eclipse can do all this via commandline:
1. $> svn co http://www.molgenis.org/svn/molgenis/trunk molgenis
2. $> svn co http://www.molgenis.org/svn/molgenis_apps/trunk molgenis_apps
3. $> cd molgenis_apps
4. $> ant <appname>.build clean-generate-compile-test
5. $> ant <appname>.build run
5. Browse to http://<yourhost>:8080/<appname>/



# =====================================================
# SVN revision info:
# $Id$
# $URL$
# $LastChangedDate$ 
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
