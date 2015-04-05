#!/usr/bin/python

import csv

# Script to calculate average delay
FILE = "temp.csv"

rows = {'Q1':0, 'Q2':0}
delay = {'Q1':0, 'Q2':0}

with open(FILE, 'r') as qfd:
  qfile = csv.reader(qfd)
  for row in qfile:
    if '.' in row[3]:
      # => Q1
      rows['Q1'] = rows['Q1'] + 1
      delay['Q1'] = delay['Q1'] + int(row[-1])
    else:
      # => Q2
      rows['Q2'] = rows['Q2'] + 1
      delay['Q2'] = delay['Q2'] + int(row[-1])

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
