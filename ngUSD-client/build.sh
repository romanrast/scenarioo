#!/bin/sh
BASEDIR=$(dirname $0)
cd $BASEDIR

if [ "$#" -gt 0 ]; then
    echo "Set path for grunt as configured: $1"
    export PATH=$PATH:$1
fi

echo $PATH

# TODO remove --force, as soon as all problems are resolved
grunt build --force