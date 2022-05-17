/*    DALAS Low-Level C++ Module for fetching data
 *        for Android 
 *      
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 17. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
 */
#include <iostream>
#include <cstdio>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netdb.h>
#include <cstring>
#include <unistd.h>
#include <vector>
#include <sstream>
#include <jni.h>
#include <cmath>
#include <jni.h>
#define PORT "80"
#define IP "192.168.0.18"
#define CHK 19072
#define GK "dalasv13v035"

using namespace std;
extern "C" JNIEXPORT jstring JNICALL

Java_com_example_dasher_MainActivity_GetTemp(JNIEnv *env, jobject thiz,jstring nm) {
    string response="";
    string city = env->GetStringUTFChars(nm,0);
    string addr = "api.openweathermap.org";
    struct hostent *dom = gethostbyname(addr.c_str());
    string ip = inet_ntoa(*(struct in_addr *)dom->h_addr);
    struct addrinfo *ptr,*res,hints;
    memset(&hints,0,sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    int sock;
    getaddrinfo(ip.c_str(),PORT,&hints,&res);
    for(ptr=res;ptr!=NULL;ptr=ptr->ai_next){
        sock = socket(ptr->ai_family,ptr->ai_socktype,ptr->ai_protocol);
        connect(sock,ptr->ai_addr,(int)ptr->ai_addrlen);
    }
    freeaddrinfo(res);
    string get="GET /data/2.5/weather?appid=45f99edc933c0df490cbf6cbe7a4e855&q="+city+"  HTTP/1.1\r\nHost: api.openweathermap.org\r\n\r\n";
    char buffer[4096];
    send(sock,get.c_str(),get.length(),0);
    recv(sock,buffer,sizeof(buffer),0);
    string resp = buffer;
    resp = resp.substr(resp.find("\r\n\r\n")+4);
    string tem  = resp.substr(resp.find("temp")+6);
    tem = tem.substr(0,tem.find(","));
    string desc = resp.substr(resp.find("main")+7);
    desc = desc.substr(0,desc.find('"'));
    response=tem+":"+desc;
    return env->NewStringUTF(response.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_dasher_MainActivity_CheckUpdates(JNIEnv * env, jobject thiz,jstring path) {
    int resp = 0;
    string curr = env->GetStringUTFChars(path,0);
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;

    addr.sin_addr.s_addr = inet_addr(IP);
    addr.sin_port=htons(CHK);
    int sock = socket(AF_INET,SOCK_STREAM,0);
    connect(sock,(struct sockaddr*)&addr,sizeof(addr));
    char buffer[4096];
    string passwd = GK;
    string req = "CHECK:"+curr+":"+passwd;
    send(sock,req.c_str(),req.length(),0);
    memset(&buffer,0,sizeof(buffer));
    recv(sock,buffer,sizeof(buffer),0);

    string g = buffer;
    if(g.find("invalid") != string::npos){
        resp = -1;
    }
    else{
        resp=0;
    }
    close(sock);

    return resp;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_dasher_MainActivity_UpdateCity(JNIEnv *env, jobject thiz, jstring cc) {
    // TODO: implement UpdateCity()
    int resp =0;
    const char  *city = env->GetStringUTFChars(cc,0);
    FILE *a = fopen("/data/local/tmp/city.txt","w");
    if(fwrite(city,strlen(city),1,a) != 0){
        resp = 1;
    }
    else{
        resp =0;

    }
    fclose(a);
    return resp;

}
