/*
 *    DALAS Check for update package 
 *        for Android 
 *        compile with arm-gnueabi
 *          checks update and downloads it 
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 10. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
 */
#include <iostream>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <vector>
#include <sstream>
#include <unistd.h>
#include <netdb.h>
#include <cstring>
#define FGG ""

#define IP "192.168.0.18"
#define PORT "19072"

using namespace std;

struct packet{ 
  string mode;
  string ver;
  string passwd;
};

string checkForUpdates(string current){

  string resp = "";
  int sock;
  struct addrinfo *res,*ptr,hints;
  memset(&hints,0,sizeof(hints));
  hints.ai_family = AF_INET;
  hints.ai_socktype= SOCK_STREAM;
  hints.ai_protocol = IPPROTO_TCP;
  
  getaddrinfo(IP,PORT,&hints,&res);
  for(ptr=res;ptr!=NULL;ptr=ptr->ai_next){
      if((sock = socket(ptr->ai_family,ptr->ai_socktype,ptr->ai_protocol)) == -1){
        cout<<"sock err"<<endl;
        exit(1);
     }
      if(connect(sock,ptr->ai_addr,(int)ptr->ai_addrlen) == -1){
          cout<<"conn error"<<endl;
          exit(1);
      }
  }
  freeaddrinfo(res);
  struct packet pack;
  pack.mode="CHECK";
  pack.ver=current;
  pack.passwd="dalasv13v035";
  string comb = pack.mode+":"+pack.ver+":"+pack.passwd;
  send(sock,comb.c_str(),comb.length(),0);
  char buffer[2048];
  memset(&buffer,0,sizeof(buffer));
  if( recv(sock,buffer,sizeof(buffer),0) ==-1){
    cout<<"recv error"<<endl;
  }
  resp = buffer;
  
  return resp;
}


int main(int argc,char *argv[]){

  if(argc <2){
      cout<<"\n==PalOS software updater==\nRun with -c to check for updates (provide the current version)"<<endl;
  }
  string arg = argv[1];
  if(arg =="-c"){
    string currver= argv[2];
  
    string resp = checkForUpdates(currver);
    resp = resp.substr(0,resp.find("\n"));
    if(resp != currver){
        cout<<"Software is out of date."<<endl;
        //you have to update
    }
    else{
      cout<<"Newest software version is already installed."<<endl;

    }

  }
  
}
