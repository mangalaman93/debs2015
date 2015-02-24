#!/usr/bin/python

import csv
import os
import subprocess
import sys

# integration test files
Q1FILES = {"test/Query1/inputData.csv":"test/Query1/outputData.csv"}
Q2FILES = {"test/Query2/inputData.csv":"test/Query2/outputData.csv",
           "test/Query2/raw_input_1.csv":"test/Query2/raw_output_1.csv",
           "test/Query2/raw_input_2.csv":"test/Query2/raw_output_2.csv"}
CMD = "java -jar debs2015.jar"

# execute system command and return output
def system_cmd(cmd, message):
  p = subprocess.Popen(cmd, shell=True)
  # if only the command executed successfully
  if p.wait() == 0:
    return True
  else:
    raise Exception(message)

# compares program out with manually calculated output
def compare_out(pout, mout):
  with open(pout, 'r') as pfd, open(mout, 'r') as mfd:
    pfile = csv.reader(pfd)
    mfile = csv.reader(mfd)
    plen = sum(1 for row in pfile)
    mlen = sum(1 for row in mfile)
    if plen != mlen:
      raise Exception("different number of rows!")
    for prow,mrow in zip(pfile, mfile):
      for i in range(len(mrow)):
        if mrow[i] != prow[i]:
          raise Exception("following rows didn't match-\n"+mrow+"\n"+prows)

# run all unit tests
print "running unit tests..."
sys.stdout.flush()
system_cmd("make test", "Error: unit test(s) unsuccessful!")

# run all q1 integration tests
for inp,out in Q1FILES.items():
  print "executing code with input file: "+inp+"..."
  sys.stdout.flush()
  system_cmd(CMD+" "+inp, "unable to run code with input file: "+inp)
  print "comparing output: "+out+" ..."
  sys.stdout.flush()
  compare_out("out/q1_out.csv", out)
  print "success \m/\n"
  sys.stdout.flush()

# run all q2 integration test
for inp,out in Q2FILES.items():
  print "executing code with input file: "+inp+"..."
  sys.stdout.flush()
  system_cmd(CMD+" "+inp, "unable to run code with input file: "+inp)
  print "comparing output: "+out+" ..."
  sys.stdout.flush()
  compare_out("out/q2_out.csv", out)
  print "success \m/\n"
  sys.stdout.flush()

# cleaning
system_cmd("make clean", "Error in make clean!")
system_cmd("rm -f test/q1_out.csv test/q2_out.csv",
           "unable to delete output files")
