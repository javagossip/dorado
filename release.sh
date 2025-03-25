#!/bin/bash

echo "prepare dorado release..."
./mvnw release:prepare -Prelease
echo "perform dorado release..."
./mvnw release:perform -Prelease


