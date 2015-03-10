#!/usr/bin/python

import csv

def compare_out(pout, mout):
  with open(pout, 'r') as pfd, open(mout, 'r') as mfd:
    pfile = csv.reader(pfd)
    mfile = csv.reader(mfd)
    plen = sum(1 for row in pfile)
    mlen = sum(1 for row in mfile)
    print plen, mlen
    if plen != mlen:
      raise Exception("different number of rows!")
    for prow,mrow in zip(pfile, mfile):
      for i in range(len(mrow)):
        if mrow[i] != prow[i]:
          raise Exception("following rows didn't match-\n"+mrow+"\n"+prows)

compare_out("out/q1_out_base.csv", "out/q1_out_hash.csv")