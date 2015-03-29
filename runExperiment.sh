source commons.sh
export TS=`date +%Y%m%d%H%M%S`
mkdir $LOG_DIR/$TS
bash runMaster.sh
sleep 1
bash runWorkers.sh
sleep 1
tail $LOG_DIR/$TS/Worker4.out |grep DONE 
while [ $? -eq 1 ]
do
sleep 10
tail $LOG_DIR/$TS/Worker4.out |grep DONE            
done

#ps -ef |grep seep |cut -d ' ' -f4 |xargs kill
pkill java
 
