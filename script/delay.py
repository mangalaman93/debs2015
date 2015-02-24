#!/usr/bin/python

import csv

# Script to calculate average delay
FILE1 = "out/q1_out.csv"
FILE2 = "out/q2_out.csv"

with open(FILE1, 'r') as q1fd:
  q1rows = 0
  q1delay = 0
  q1file = csv.reader(q1fd)
  for row in q1file:
    q1rows = q1rows + 1
    q1delay = q1delay + int(row[-1])

with open(FILE2, 'r') as q2fd:
  q2rows = 0
  q2delay = 0
  q2file = csv.reader(q2fd)
  for row in q2file:
    q2rows = q2rows + 1
    q2delay = q2delay + int(row[-1])

avg_delay = (q1delay+q2delay)/(q1rows+q2rows)
print "average delay = {}".format(avg_delay)
