Besides all the things concerning:
- Eclipse
- Subclipse
- Freemarker
- Tomcat
- MySQL
- etc, -> see molgenis.org, installation guide

Most recent XGAP specific things to do are:

- Download molgenis/3.3, rev. 3121, from www.molgenis.org/svn
- Download xgap_1_3_distro, rev. HEAD, from www.xgap.org/svn

- Generate
- Run as webapp
- Go to 'System', 'Settings' and configure data storage directory
- Run handwritten/java/regressiontest/cluster/TestCluster.java to get some example data plus cluster metadata


_____
DUTCH, TEMPORARY

- molgenis/3.3 vanaf www.molgenis.org/svn mbv Subclipse, rev. 3121 met daarbij nog rev. 3191 en 3383. (switch met History naar 3121, die Sync, en update behalve commits met nummer 3130)
- xgap_1_3_distro vanaf www.xgap.org/svn
- runnende mysql
- ??
- geen runnende tomcat: die start je vanuit eclipse mbv. xgap_1_3_distro, Run As, Run on Server
- MolgenisGenerate, UpdateDatabase, refresh, clean, etc.....
- ??
- Als dat lukt moet je een systeemdirectory instellen in de System -> Settings plugin
- Daarna kan je QuickStart.java runnen om het systeem te vullen met wat example data