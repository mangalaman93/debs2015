#!/usr/bin/python

from collections import deque
from datetime import datetime
from time import time

def geo(lat, lon):
	distX = ((lat+74.913585)*84.38*1000) + 0.5*500;
	x = distX / 500;

	distY = ((41.474937-lon)*110.54*1000) + 0.5*500;
	y = distY / 500;
	return [int(x), int(y)]

def max_route_frequency(file, fromX, fromY, toX, toY):
	queue = deque([])
	count = 0
	with open(file) as infile:
		for line in infile:
			params = line.split(',')
			fromA = geo(float(params[6]), float(params[7]))
			if(fromA[0] == fromX):
				if(fromA[1] == fromY):
					toA = geo(float(params[8]), float(params[9]))
					if(toA[0] == toX):
						if(toA[1] == toY):
							dttime = datetime.strptime(params[3], "%Y-%m-%d %H:%M:%S")
							dt = (dttime - datetime(1970,1,1)).total_seconds()
							flag = 0
							if(count > 0):
								e = queue.popleft()
								count = count - 1
								flag = 1
								while(dt - 1800 >= e):
									if(count > 0):
										e = queue.popleft()
										count = count - 1
										flag = 1
									else:
										break
							if(flag == 1):
								queue.appendleft(e)
								count = count + 1
							queue.append(dt)
							count = count + 1
							print count
	print "max_route_frequency ", count

max_route_frequency("out/sorted_data_full.csv", 160, 159, 156, 160)
