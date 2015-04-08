## VVS
### Deployment

#### What you need
Tomcat (tested with Tomcat 7)
MYSQL (5.6.5 or higher)

#### Setup
Install Tomcat and MYSQL
Import vvs.sql in MYSQL
If your MYSQL username/password combination is NOT root/root -> change it in /de/dhbw/vvs/database/DatabaseConnection.java
Generate VVS.war file
Deploy VVS.war and birt-viewer.war to Tomcat
