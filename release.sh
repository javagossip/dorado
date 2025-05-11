#!/bin/bash

echo "prepare dorado release..."
./mvnw release:prepare -Prelease -B
echo "perform dorado release..."
./mvnw release:perform -Prelease -B


