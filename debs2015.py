#!/usr/bin/python

import os
import sys
import time

DEFAULT_PATH = "/mnt/data/sorted_data.csv"
NUM_EVENTS = 173185091

# main code
os.system('make clean && make')

# deciding what data file to use
datafile = DEFAULT_PATH
if len(sys.argv) == 2:
	datafile = sys.argv[1]

# runnin the actual code

start = time.clock()
os.system("/usr/bin/time java -jar debs2015.jar {} > output.csv".format(datafile))
print "\nThroughput: {} events/sec".format((NUM_EVENTS/(time.clock() - start)))
os.system('python script/delay_whole.py')
