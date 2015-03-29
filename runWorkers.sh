#!/bin/bash
source commons.sh
java $DEBSVM_ARGS -Doperator.batchLimit=1 -Dlog-file-name=$LOG_DIR/$TS/Worker1.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3501 > $LOG_DIR/$TS/Worker1.out 2>&1 &
#java $DEBSVM_ARGS -Doperator.batchLimit=1 -Dlog-file-name=$LOG_DIR/$TS/Worker1.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3501 > /dev/null 2>&1 &
sleep 1
java $DEBSVM_ARGS -Doperator.batchLimit=1 -Dlog-file-name=$LOG_DIR/$TS/Worker2.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3502 > $LOG_DIR/$TS/Worker2.out 2>&1 &
sleep 1
java $DEBSVM_ARGS -Doperator.batchLimit=1 -Dlog-file-name=$LOG_DIR/$TS/Worker3.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3503 > $LOG_DIR/$TS/Worker3.out 2>&1 &
sleep 1
java $DEBSVM_ARGS -Dlog-file-name=$LOG_DIR/$TS/Worker4.log -Xmx4g -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3504 > $LOG_DIR/$TS/Worker4.out 2>&1 &
#sleep 1
#java -Dlog-file-name=$LOG_DIR/$TS/Worker5.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3505 > $LOG_DIR/$TS/Worker5.out 2>&1 &

