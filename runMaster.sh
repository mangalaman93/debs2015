#!/bin/bash
ant
java -Dlog-file-name=Base.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Master dist/debsgc15.jar Base
#java -Dlog-file-name=Base.log -jar lib/seep-system-0.0.1-bufferon.jar Master dist/debsgc15.jar Base


