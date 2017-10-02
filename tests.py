#!/usr/bin/python
import os, subprocess
d = "./ejemplos-parser/"
files = sorted([(d+x, d+x.replace("in","out")) for x in os.listdir(d) if "in" in x and x.endswith(".txt")])

o = subprocess.check_output("make",stderr=subprocess.STDOUT,shell=True)
for f in files:
	print(f[0])
	try:
		cmd = "java Main <"+f[0]
		o = subprocess.check_output(cmd,stderr=subprocess.STDOUT,shell=True)
	except subprocess.CalledProcessError as e:
		o = e.output
	e = subprocess.check_output("cat "+f[1],stderr=subprocess.STDOUT,shell=True)
	print(o.strip())
	print(e)
	print("")