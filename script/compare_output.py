#!/usr/bin/python

import csv

def compare_out(pout, mout):
  with open(pout, 'r') as pfd, open(mout, 'r') as mfd:
    for line1 in pfd:
      for line2 in mfd:
        array1 = line1.split(',')
        array2 = line2.split(',')
        if(len(array1)!= len(array2)):
          print line1, "\n", line2, "-----------------"
        else:
          for i in range(len(array1)):
            if array1[i] != array2[i]:
              print line1, "\n", line2, "-----------------"
  # with open(pout, 'r') as pfd, open(mout, 'r') as mfd:
  #   pfile = csv.reader(pfd)
  #   mfile = csv.reader(mfd)
  #   plen = sum(1 for row in pfile)
  #   mlen = sum(1 for row in mfile)
  #   print plen, mlen
  #   for prow,mrow in zip(pfile, mfile):
  #     print 1
  #     for i in range(len(mrow)):
  #       if mrow[i] != prow[i]:
  #         raise Exception("following rows didn't match-\n"+mrow+"\n"+prows)

compare_out("out/q1_out_base.csv", "out/q1_out_hash.csv")