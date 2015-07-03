#!/usr/env/bin python 

import time
import RPi.GPIO as GPIO
import socket
import sys

Prosto = 0
GPIO.setmode(GPIO.BCM)
GPIO.setup(22,GPIO.OUT)#a
GPIO.setup(27,GPIO.OUT)#b
GPIO.setup(17,GPIO.OUT)#c
GPIO.setup(4,GPIO.OUT)#d
GPIO.setup(23,GPIO.OUT)#e
GPIO.setup(18,GPIO.OUT)#f
GPIO.setup(14,GPIO.OUT)#g
GPIO.setup(15,GPIO.OUT)#?
HOST = '192.168.1.106'    # The remote host
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

s.send("pi2\n")
time.sleep(1)
while True:
	input = repr(s.recv(1024))
	if ("free" in input):
		input = input.replace("'", "")
		print input
		Prosto = int(input.split()[1])		
	if Prosto == 0:
		GPIO.output(22,1)#e
		GPIO.output(27,1)#d
		GPIO.output(17,1)#c
		GPIO.output(23,1)#b
		GPIO.output(18,1)#a
		GPIO.output(15,1)#f
		GPIO.output(14,0)#g

	if Prosto == 1:
		GPIO.output(22,0)#e
                GPIO.output(27,0)#d
                GPIO.output(17,1)#c
                GPIO.output(23,1)#b
                GPIO.output(18,0)#a
                GPIO.output(15,0)#f
                GPIO.output(14,0)#g
	
	if Prosto == 2:
		GPIO.output(22,1)#e
                GPIO.output(27,1)#d
                GPIO.output(17,0)#c
                GPIO.output(23,1)#b
                GPIO.output(18,1)#a
                GPIO.output(15,0)#f
                GPIO.output(14,1)#g

	if Prosto == 3:
                GPIO.output(22,0)#e
                GPIO.output(27,1)#d
                GPIO.output(17,1)#c
                GPIO.output(23,1)#b
                GPIO.output(18,1)#a
                GPIO.output(15,0)#f
                GPIO.output(14,1)#g
	
	if Prosto == 4:
                GPIO.output(22,0)#e
                GPIO.output(27,0)#d
                GPIO.output(17,1)#c
                GPIO.output(23,1)#b
                GPIO.output(18,0)#a
                GPIO.output(15,1)#f
                GPIO.output(14,1)#g

	if Prosto == 5:
                GPIO.output(22,0)#e
                GPIO.output(27,1)#d
                GPIO.output(17,1)#c
                GPIO.output(23,0)#b
                GPIO.output(18,1)#a
                GPIO.output(15,1)#f
                GPIO.output(14,1)#g

	if Prosto == 6:
                GPIO.output(22,1)#e
                GPIO.output(27,1)#d
                GPIO.output(17,1)#c
                GPIO.output(23,0)#b
                GPIO.output(18,1)#a
                GPIO.output(15,1)#f
                GPIO.output(14,1)#g


s.close()



