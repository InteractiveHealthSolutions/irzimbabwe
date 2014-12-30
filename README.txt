=== IRZimbabwe ===
Contributors: IHS
Software Type: Free, Open-source
Requires: Microsoft Windows XP or higher, Oracle Java Runtime Environment (JRE) v6.0 or higher
License: GPLv3

== Description ==
Interactive Reminders for Zimbabwe is a web-based project for tablet computers in Zimbabwe. The project focuses Tuberculosis and Male Circumcision patients to improve the follow-up rate after treatment by sending scheduled SMS alerts.

== Installation ==
1. Install Oracle Java v6.0.x or higher 
2. Install tomcat v6.0.x
3. Install MySQL Server v5.0 or higher

== Configuration - irzimbabweweb ==
This is a GWT-based web application that provides web forms for IRZ project
1. Copy the "war" directory from tbreach in tomcat installation directory
2. Rename the directory from "war" to "irzimbabweweb"
3. In MySQL Server, create two new databases "irzimbabwe" and "irzimbabwe_rpt"
4. In the tbreach directory, which you created in tomcat, edit WEB-INF/classes/hibernate.cfg.xml file:
    - change username to the database username (using root is NOT recommended)
    - change the password to the database password (the user must have all privileges on tbreach, tbreach_rpt databases)
    - Save and exit
5. Start the tomcat service
6. Launch your browser and enter the web app: http://localhost:8080/irzimbabweweb (localhost is your deployment server and 8080 is the defult port)
7. Login using: Username=admin and Password=jingle94
