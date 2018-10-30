#!/bin/bash

echo "prepare dorado release..."
mvn release:prepare -Prelease
echo "perform dorado release..."
mvn release:perform -Prelease


