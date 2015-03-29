#!/bin/bash
source commons.sh
ant
sh masterInput.sh | java $DEBSVM_ARGS  -Dlog-file-name=$LOG_DIR/$TS/Base.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Master dist/debsgc15.jar Base &
#java $DEBSVM_ARGS -Dlog-file-name=$LOG_DIR/$TS/Base.log -jar lib/seep-system-0.0.1-bufferon.jar Master dist/debsgc15.jar Base


