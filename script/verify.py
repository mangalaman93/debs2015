f = open('tmp', 'r')

prev_x = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
prev_y = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
prev_taxi = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
prev_pft = [-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0]
prev_ptb = [-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0]

line_no = 0

for line in f :
	line_no += 1
	s = line.split(",")
	if s[0]=="Q2" :
		for i in range(0,11) :
			if(i==10) :
				print line_no
				exit(0)
			if(prev_x[i]==-1 and s[3+i*4]=="NULL") :
				print line_no
				exit(0)
			if(prev_x[i]==-1 and s[3+i*4]!="NULL") :
				break
			if(prev_x[i]!=-1 and s[3+i*4]=="NULL") :
				break
			if(prev_x[i]!=int(s[3+i*4].split(".")[0])) :
				break
			if(prev_y[i]!=int(s[3+i*4].split(".")[1])) :
				break
			if(prev_taxi[i]!=int(s[4+i*4])) :
				break
			if(prev_pft[i]!=float(s[5+i*4])) :
				break
			if(prev_ptb[i]!=float(s[6+i*4])) :
				break

		for i in range(0,10) :
			if(s[3+i*4]=="NULL") :
				prev_x[i] = -1
				break
			prev_x[i] = int(s[3+i*4].split(".")[0])
			prev_y[i] = int(s[3+i*4].split(".")[1])
			prev_taxi[i] = int(s[4+i*4])
			prev_pft[i] = float(s[5+i*4])
			prev_ptb[i] = float(s[6+i*4])