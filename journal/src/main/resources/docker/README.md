build command：
mvn package docker:build -DpushImage

run command：
docker run -d --name myspringboot --net=host -t johnlau/journal