#!/bin/bash

# This script copies the templates to the given path
# Usage: ./apply.sh <target-dir>
# CAVE: This overwrites existing files without prompting!

templates=`dirname "$0"`
target="$1"

copyFile() {
  cp -p "${templates}/$1" "${target}/$2"
}

copyFile _dockerignore   .dockerignore
copyFile _editorconfig   .editorconfig
copyFile _gitignore      .gitignore
copyFile Dockerfile-mvn  Dockerfile
