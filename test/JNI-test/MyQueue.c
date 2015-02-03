#include "MyQueue.h"
#include <jni.h>
#include <stdio.h>
#include <string.h>

#include <mqueue.h>
#include <fcntl.h>

#define MQ_NAME "/mqconsumer"
#define MQ_MESSAGE_MAX_LENGTH 20

mqd_t mqd;

JNIEXPORT void JNICALL Java_MyQueue_createQueue(JNIEnv *env, jobject callingObject) {
    struct mq_attr attr;
    attr.mq_maxmsg = 10;
    attr.mq_msgsize = MQ_MESSAGE_MAX_LENGTH;
    mqd = mq_open(MQ_NAME, O_RDWR | O_CREAT, 0664, &attr);
    if( mqd == (mqd_t)-1 ) perror("Message queue open failed");
    printf("%s\n", "Queue created ...");
}

JNIEXPORT void JNICALL Java_MyQueue_sendMessage(JNIEnv *env, jobject callingObject, jstring msg) {
    const char *str;
    str = (*env)->GetStringUTFChars(env, msg, 0);
    char c[MQ_MESSAGE_MAX_LENGTH];
    strcpy(c,str);
    int ret = mq_send(mqd, c, MQ_MESSAGE_MAX_LENGTH, 0);
    //printf("%s\n", "Sent..");
    if(ret) perror("Message queue send failed");
    /*
    ret = mq_close(mqd);
    if(ret) perror(" Message queue close (send) failed");
    printf("%s\n", "Closed..");
    */
}

JNIEXPORT jstring JNICALL Java_MyQueue_receiveMessage(JNIEnv *env, jobject callingObject) {
    //char msg_buff[MQ_MESSAGE_MAX_LENGTH];
    char c[MQ_MESSAGE_MAX_LENGTH];
    int msg_len = mq_receive(mqd, c, MQ_MESSAGE_MAX_LENGTH, 0);
    if(msg_len<0) perror("Message queue receive failed");
    //c[1]='\0';
    //printf("%s\n" , c);
    /*
    int ret = mq_close(mqd);
    if(ret) perror(" Message queue close (receive) failed");
    ret = mq_unlink(MQ_NAME);
    if(ret) perror(" Message queue unlink failed");
    */
    return (*env)->NewStringUTF(env, c);
}