#!/usr/env/bin python
import RPi.GPIO as GPIO
import time
import socket
import sys
import threading

GPIO.setmode(GPIO.BCM)

GPIO.setup(17,GPIO.OUT)#not-vn 1,2
GPIO.setup(4,GPIO.OUT)
GPIO.setup(27,GPIO.OUT)#lev-odesno 4,5       so obratno napisani(npr. levo-desno=desno-levo)
GPIO.setup(22,GPIO.OUT)
GPIO.setup(2,GPIO.OUT)#gor-dol 7,8
GPIO.setup(3,GPIO.OUT)
GPIO.setup(24,GPIO.IN) #3 levo
GPIO.setup(25,GPIO.IN) #6 desno
GPIO.setup(14,GPIO.IN) #9 gor
GPIO.setup(23,GPIO.IN) #10 dol
GPIO.setup(18,GPIO.IN) #11 vn
GPIO.setup(15,GPIO.IN) #12 not
GPIO.setup(7,GPIO.IN)
GPIO.setup(8,GPIO.IN)

GPIO.output(17,0)
GPIO.output(4,0)
GPIO.output(27,0)
GPIO.output(22,0)
GPIO.output(2,0)
GPIO.output(3,0)

def resetPosition():
	#to reset position and beyond
	GPIO.output(4, 1)
	while GPIO.input(15) == 0:
		time.sleep(0.01)
	GPIO.output(4, 0)
	GPIO.output(22, 1)
	while GPIO.input(24) == 0:
		time.sleep(0.01)
	GPIO.output(22, 0)
	GPIO.output(3, 1)
	while GPIO.input(14) == 0:
		time.sleep(0.01)
	GPIO.output(3, 0)

def senderThread():
	global s
	while True:
		if (GPIO.input(24) == 1):
			s.send("xPos 0\n")
			time.sleep(1)
		elif(GPIO.input(25) == 1):
			s.send("xPos 1\n")
			time.sleep(1)
		elif(GPIO.input(7)):
			s.send("xPos 2\n")
			time.sleep(1)
		elif(GPIO.input(8)):
			s.send("xPos 3\n")
			time.sleep(1)
		if (GPIO.input(14)):
			s.send("yPos 1\n")
			time.sleep(1)
		elif (GPIO.input(23)):
			s.send("yPos 2\n")
			time.sleep(1)

def stopThread():
	global stop
	while True:
		if (stop == True):
			GPIO.output(17,0)
			GPIO.output(4,0)
			GPIO.output(27,0)
			GPIO.output(22,0)
			GPIO.output(2,0)
			GPIO.output(3,0)
			stop = False
		
		

HOST = '192.168.1.102'    # The remote host
PORT = 3333              # The same port as used by the server
s = None
for res in socket.getaddrinfo(HOST, PORT, socket.AF_UNSPEC, socket.SOCK_STREAM):
    af, socktype, proto, canonname, sa = res
    try:
        s = socket.socket(af, socktype, proto)
    except socket.error as msg:
        s = None
        continue
    try:
        s.connect(sa)
    except socket.error as msg:
        s.close()
        s = None
        continue
    break
if s is None:
    print 'could not open socket'
    sys.exit(1)

stop = False
cellId = 0
s.send("pi1\n")
time.sleep(1)
resetPosition()
t = threading.Thread(target=senderThread)
t.daemon = True
t.start()
t = threading.Thread(target=stopThread)
t.daemon = True
t.start()

while True:
	cmd = repr(s.recv(1024))
	if ("put" in cmd):
		cmd = cmd.replace("'", "")
		cellId = int(cmd.split()[1])
		print cmd

		GPIO.output(17, 1)
		while GPIO.input(18) == 0:
			time.sleep(0.01)
		GPIO.output(17, 0)
		GPIO.output(2, 1)
		time.sleep(2.3)
		GPIO.output(2, 0)
		GPIO.output(17,0)#not
		GPIO.output(4,1)
		while (GPIO.input(15) == 0):
			time.sleep(0.01)
		GPIO.output(4, 0)
		#tu je roka notr
		GPIO.output(27, 1)
		print "pele se"
		if (cellId == 1 or cellId == 4):
			while (GPIO.input(25) == 0):
				time.sleep(0.01)
		elif (cellId == 2 or cellId == 5):
			while (GPIO.input(7) == 0):
				time.sleep(0.01)
		elif (cellId == 3 or cellId == 6):
			while (GPIO.input(8) == 0):
				time.sleep(0.01)
		GPIO.output(27, 0)
		#tu je roka poslihtana po x 
		if (cellId == 1 or cellId == 2 or cellId == 3):
			GPIO.output(2, 1)
			print "gor gre"
			while (GPIO.input(23) == 0):
				time.sleep(0.01)
			time.sleep(1.1)
			GPIO.output(2, 0)
		GPIO.output(3, 1)
		if (cellId == 6 or cellId == 4 or cellId == 5):
			while (GPIO.input(14) == 0):
				time.sleep(0.01)
		GPIO.output(3, 0)
		#iztegnimo roko
		GPIO.output(17,1)
		while (GPIO.input(18) == 0):
			time.sleep(0.01)
		GPIO.output(17, 0)
		GPIO.output(3, 1)
		if (cellId == 1 or cellId == 2 or cellId == 3):
			while (GPIO.input(23) == 1):
				time.sleep(0.01)
		GPIO.output(3, 0)
		resetPosition()	
	elif ("get" in cmd):
		cmd = cmd.replace("'", "")
		cellId = int(cmd.split()[1])
		print cmd

		#let's go!
		GPIO.output(27, 1)
		if (cellId == 1 or cellId == 4):
			while (GPIO.input(25) == 0):
				time.sleep(0.01)
		elif (cellId == 2 or cellId == 5):
			while (GPIO.input(7) == 0):
				time.sleep(0.01)
		elif (cellId == 3 or cellId == 6):
			while (GPIO.input(8) == 0):
				time.sleep(0.01)
		GPIO.output(27, 0)
		if (cellId == 1 or cellId == 2 or cellId == 3):
			GPIO.output(2, 1)
			while (GPIO.input(23) == 0):
				time.sleep(0.01)
			GPIO.output(2, 0)
		GPIO.output(17, 1)
		while GPIO.input(18) == 0:
			time.sleep(0.01)
		GPIO.output(17, 0)
		#mal gor
		GPIO.output(2, 1)
		time.sleep(1.2)
		GPIO.output(2, 0)
		#reset
		GPIO.output(4, 1)
		time.sleep(0.8)
		resetPosition()
		GPIO.output(17, 1)
		while GPIO.input(18) == 0:
			time.sleep(0.01)
		GPIO.output(17, 0)
		GPIO.output(4,1)
		while (GPIO.input(15) == 0):
			time.sleep(0.01)
		GPIO.output(4, 0)
	elif ("stop" in cmd):
		stop = True
	elif ("reset" in cmd):
		resetPosition()
	else:
		print ("Unknown command: " + cmd)
		s.close()
		sys.exit(1)

s.close()
#4,17 not-vn vn:17 not:4
#27,22 levo-desno desno:27 levo:22
#2,3 gor-dol gor:2 dol:3
