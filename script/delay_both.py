#!/usr/bin/python

import csv

# Script to calculate average delay
FILE = "temp.csv"

rows = {}
delay = {}

with open(FILE, 'r') as q1fd:
  q1file = csv.reader(q1fd)
  for row in q1file:
    if 'Q' in row[0]:
      if row[0] not in rows:
        rows[row[0]] = 0
        delay[row[0]] = 0
      rows[row[0]] = rows[row[0]] + 1
      delay[row[0]] = delay[row[0]] + int(row[-1])

total_rows = 0
total_delay = 0;
print rows, delay
for key in rows.iterkeys():
  total_rows = total_rows + rows[key]
  total_delay = total_delay + delay[key]
avg_delay = total_delay/total_rows
print "average delay = {}".format(avg_delay)

for key in rows.iterkeys():
  print "{} delay = {}".format(key, delay[key]/rows[key])
