#!/bin/bash

df -h

function checkCluster() {
  curl -I http://localhost:8081 2>/dev/null | head -n 1

}
if ! mvn -B package -Dmaven.test.skip=true --file pom.xml; then
  exit 1
fi

if ! docker-compose up -d --build backend.kibe frontend.kibe selenium-hub firefox kafka zookeeper; then
  docker-compose down
  exit 1
fi

sleep 10

while [[ ! "$(checkCluster)" =~ "200" ]]; do
  checkCluster
  echo "Cluster not ready. Sleeping."
  sleep 1
  checkCluster
  docker-compose logs backend.kibe
  docker-compose logs frontend.kibe
  sleep 10
  checkCluster
  docker-compose logs backend.kibe
  docker-compose logs frontend.kibe
  df -h
  exit 1
done

mvn test -pl :end-to-end -am -Dtest=* -DfailIfNoTests=false
result=$?

checkCluster
docker-compose logs backend.kibe
docker-compose logs frontend.kibe
df -h

docker-compose down

exit $result;
