cd ../Sportaneous-API;
mvn clean install;
scp -i ~/.ssh/sportaneousdev.pem sportaneous-api-server/target/sportaneous-api-server-1.0-SNAPSHOT.war root@api.sportaneous.com:~/.;

cd ../Sportaneous-Web-App;
mvn clean install;
scp -i ~/.ssh/sportaneousdev.pem target/sportaneous-web-1.0-SNAPSHOT.war root@api.sportaneous.com:~/.;

cd ..;

ssh -i ~/.ssh/sportaneousdev.pem root@api.sportaneous.com '/sbin/service jetty6 stop';
ssh -i ~/.ssh/sportaneousdev.pem root@api.sportaneous.com 'cp -f ~/sportaneous-api-server-1.0-SNAPSHOT.war /usr/share/jetty6/webapps/ROOT.war';
ssh -i ~/.ssh/sportaneousdev.pem root@api.sportaneous.com 'cp -f ~/sportaneous-web-1.0-SNAPSHOT.war /usr/share/jetty6/webapps-web/ROOT.war';
ssh -i ~/.ssh/sportaneousdev.pem root@api.sportaneous.com '/sbin/service jetty6 start';
