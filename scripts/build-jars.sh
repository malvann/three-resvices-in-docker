#!/bin/bash
for service in restaurant delivery kitchen; do
  echo "====$service===="
  ./gradlew :$service:clean :$service:build
  mv -v $service/build/libs/* "$service/build/libs/$service.jar"
done