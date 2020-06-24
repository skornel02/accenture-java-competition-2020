#!/bin/bash

function checkCluster() {
  curl -I http://localhost:8081 | head -n 1

}
if ! mvn -B package -Dmaven.test.skip=true --file pom.xml; then
  exit 1
fi

if ! docker-compose up -d --build backend.kibe frontend.kibe selenium-hub firefox kafka zookeeper; then
  docker-compose down
  exit 1
fi

sleep 5

while [[ ! "$(checkCluster)" =~ "200" ]]; do
  echo "Cluster not ready. Sleeping."
  sleep 5
done

mvn test -pl :end-to-end -am -Dtest=* -DfailIfNoTests=false
result=$?

docker-compose logs backend.kibe
docker-compose logs frontend.kibe

docker-compose down

exit $result;
