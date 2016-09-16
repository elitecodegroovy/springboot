keytool -genkey -alias tomcat -keyalg RSA -keystore src/main/resources/keystore.jks

Enter keystore password: tomcat
Re-enter new password: tomcat
What is your first and last name?
[Unknown]: apress media
What is the name of your organizational unit?
[Unknown]: publishing
What is the name of your organization?
[Unknown]: apress
What is the name of your City or Locality?
[Unknown]: ny
What is the name of your State or Province?
[Unknown]: ny
What is the two-letter country code for this unit?
[Unknown]: us
Is CN=apress media, OU=publishing, O=apress, L=ny, ST=ny, C=us correct?
[no]: yes
Enter key password for <tomcat>
(RETURN if same as keystore password):

Testing SSL
All the other files remain the same, so it’s time to test the new SSL part. Remember that you need to have
your MySQL server up and running. Then, you can run your application as usual:<br>
$ ./mvnw spring-boot:run