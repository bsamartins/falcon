#!/usr/bin/env bash
set -e

cd document-service
./gradlew build
cd ..

cd gateway
./gradlew build