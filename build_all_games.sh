#!/bin/bash

# pull new source, build and run
git restore .
git clean -df
git pull
chmod -R 700 *
./gradlew build