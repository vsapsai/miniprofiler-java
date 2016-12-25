Java version of [MiniProfiler](http://miniprofiler.com).

## Current state
[![Build Status](https://travis-ci.org/vsapsai/miniprofiler-java.svg?branch=master)](https://travis-ci.org/vsapsai/miniprofiler-java)

It is in early stage of development and doesn't have many features. Main difference from other Java implementations is that current implementation uses [MiniProfiler/ui](https://github.com/MiniProfiler/ui).

## How to develop
####Be sure to `git submodule init` and `git submodule update` after cloning to pull down the ui repo!####

During development use the following commands:

* `./gradlew :samples:general:jettyRunWar` to run the sample;
* `./gradlew :samples:general:jettyRunWarDebug` to debug the sample;
* `./gradlew test` to run tests;
* `./gradlew war` to build .war files.

## See also
In no particular order:
* https://github.com/alvins82/java-mini-profiler-core
* https://github.com/jriecken/gae-java-mini-profiler
* https://github.com/tomdcc/miniprofiler-jvm
