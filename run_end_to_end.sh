#!/bin/bash

function checkCluster() {
  curl -I http://frontend.kibe:8080 2>/dev/null | head -n 1 | sed "s/^[^ ]* //"

}
if ! mvn -B package -Dmaven.test.skip=true --file pom.xml; then
  exit 1
fi

if ! docker-compose up -d --build backend.kibe frontend.kibe selenium-hub chrome firefox kafka zookeeper; then
  docker-compose down
  exit 1
fi

while [ "$(checkCluster)" -ne 200 ]; do
  echo "Cluster not ready. Sleeping."
  sleep 1
done

mvn test -pl :end-to-end -am -Dtest=* -DfailIfNoTests=false
result=$?

docker-compose down

exit $result;