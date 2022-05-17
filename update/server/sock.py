"""
*    DALAS Server for updating
 *        for Android 
 *        
 *          checks update and downloads it 
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 10. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
"""

from socket import *
import os,io,sys
import threading 

def data(conn,addr):
    PASS= ""
    dat = conn.recv(2048)
    print(dat)
    if(b"." in dat):
        req = dat.decode()
        req = req.split(":")
        if(req[0] == "CHECK"):
            curr= req[1]
            sign = req[2]
            if(sign == PASS):
                #send back if there is the new version
                f = open("dalas.ver","r")
                newest = f.read()
                conn.send(newest.encode())
            else:
                conn.send(b"invalid request")
        else:
            conn.send(b"invalid request")


print("PalOS Server-Side update checker")
print("============== Receiving requests ===============")
s = socket(AF_INET,SOCK_STREAM)
s.bind(('',19072))

s.setsockopt(SOL_SOCKET,SO_REUSEADDR,0)
s.listen(23555)

while True:
    conn,addr = s.accept()
    da = threading.Thread(target=data,args=(conn,addr,))
    da.start()
