#!/bin/sh

mvn clean compile assembly:single

mkdir workarea
cd workarea

if ! test -f jazzer-macos.tar.gz; then
    echo "jazzer-macos.tar.gz exists."
    wget https://github.com/CodeIntelligenceTesting/jazzer/releases/download/v0.15.0/jazzer-macos.tar.gz
    tar -xvf jazzer-macos.tar.gz
fi

mkdir output_example
./jazzer --cp=../target/fuzzing-1.0-jar-with-dependencies.jar --target_class=se.omegapoint.fuzzing.StupidExample output_example
