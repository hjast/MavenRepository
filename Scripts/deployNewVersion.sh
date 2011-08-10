if test $# = 1
then
	cd /home/reuben/Sportaneous/Sportaneous-Web-App-XSBT
	mvn package
        cd /home/reuben/Sportaneous/Scripts
        sh ./deployAppScript.sh $1
        cd /home/reuben/Sportaneous/Sportaneous-API/sportaneous-api-server
	mvn package
        cd /home/reuben/Sportaneous/Scripts
	sh ./deployServerScript.sh $1  
else
	echo "Usage - $0   nameNewVersion"
	echo "        Where nameNewVersion is the new versionName of the app to push"
fi
