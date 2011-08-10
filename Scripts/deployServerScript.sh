rvm use 1.9.2
s3cmd put /home/reuben/Sportaneous/Sportaneous-API/sportaneous-api-server/target/sportaneous-api-server-1.0-SNAPSHOT.war s3://elasticbeanstalk-us-east-1-773777220056
elastic-beanstalk-create-application-version -a "SportaneousQAAPI" -l $1 -s elasticbeanstalk-us-east-1-773777220056/sportaneous-api-server-1.0-SNAPSHOT.war
elastic-beanstalk-update-environment -e "sportaneousQAAPI" -l $1
