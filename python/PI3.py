#!/usr/env/bin python

import time
import RPi.GPIO as GPIO
import socket
import sys

ena = 0
dva = 0
tri = 0
stiri = 0
pet = 0
sest = 0

GPIO.setmode(GPIO.BCM)
GPIO.setup(9,GPIO.IN)
GPIO.setup(10,GPIO.IN)
GPIO.setup(4,GPIO.IN)
GPIO.setup(23,GPIO.IN)
GPIO.setup(22,GPIO.IN)
GPIO.setup(17,GPIO.IN)


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

s.send("pi3\n")
time.sleep(1)
while True:
	if GPIO.input(9) != ena:
		ena = GPIO.input(9)
		print "ena"
		if (ena == 1):
			s.send("full 1 true\n")
		else:
			s.send("full 1 false\n")

	if GPIO.input(10) != dva:
		print "dva"
                dva = GPIO.input(10)
                if (dva == 1):
                        s.send("full 2 true\n")
                else:
                        s.send("full 2 false\n")

	if GPIO.input(4) != tri:
                print "tri"
		tri = GPIO.input(4)
                if (tri == 1):
                        s.send("full 3 true\n")
                else:
                        s.send("full 3 false\n")

	if GPIO.input(23) != stiri:
                print "vier"
		stiri = GPIO.input(23)
                if (stiri == 1):
                        s.send("full 4 true\n")
                else:
                        s.send("full 4 false\n")

	if GPIO.input(22) != pet:
		print "pet"
                pet = GPIO.input(22)
                if (pet == 1):
                        s.send("full 5 true\n")
                else:
                        s.send("full 5 false\n")

	if GPIO.input(17) != sest:
		print "69"
                sest = GPIO.input(17)
                if (sest == 1):
                        s.send("full 6 true\n")
                else:
                        s.send("full 6 false\n")
	time.sleep(0.5)

s.close()
