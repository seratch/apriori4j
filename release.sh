#!/bin/bash

sbt apriori4j/publishSigned \
    "project apriori4s" \
    +publishSigned \
    sonatypeRelease

