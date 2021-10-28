#!/bin/bash

# This script shows the difference between the templates and files in the given path
# Usage: ./diff.sh <target-dir>

templates=`dirname "$0"`
target="$1"

runDiff() {
  echo "diff $1 ${target}/$2"
  diff "${templates}/$1" "${target}/$2"
}

runDiff _dockerignore   .dockerignore
runDiff _editorconfig   .editorconfig
runDiff _gitignore      .gitignore
runDiff Dockerfile-mvn  Dockerfile
