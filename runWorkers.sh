#!/bin/bash
#java -Dlog-file-name=Worker1.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3501 > Worker1.out 2>&1 &
java -Dlog-file-name=Worker1.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3501 > /dev/null 2>&1 &
sleep 1
java -Dlog-file-name=Worker2.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3502 > Worker2.out 2>&1 &
sleep 1
java -Dlog-file-name=Worker3.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3503 > Worker3.out 2>&1 &
sleep 1
java -Dlog-file-name=Worker4.log -Xmx4g -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3504 > Worker4.out 2>&1 &
#sleep 1
#java -Dlog-file-name=Worker5.log -jar lib/seep-system-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker 3505 > Worker5.out 2>&1 &

