#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <sys/un.h>

#include <jni.h>

JNIEXPORT jint JNICALL Java_realHTML_jni_JNI_jni_createUDS_Server
  (JNIEnv *env, jobject this, jstring jpath, jobject jblocking) {
    int UDSsocket = -1, setOptsFD = 0; 
    const char* socketPath = NULL;
    struct sockaddr_un addr; memset(&addr, 0x00, sizeof(addr));
    jclass cboolean = NULL;
    jmethodID mbooleanValue = NULL;
    jboolean blocking = 0;

    if((socketPath = (*env)->GetStringUTFChars(env, jpath, NULL)) == NULL) {
        printf("createUDS - error while getting path from java\n"); fflush(stdout);
        return(-1);
    }

    printf("createUDS - Socket path: [%s]\n", socketPath); fflush(stdout);

    if((cboolean = (*env)->FindClass(env, "java/lang/Boolean")) == NULL) {
        printf("createUDS - could not find class Boolean\n"); fflush(stdout);
        return(-1);
    }

    if((mbooleanValue = (*env)->GetMethodID(env, cboolean, "booleanValue", "()Z")) == NULL) {
        printf("createUDS - could not get methodID for booleanValue\n"); fflush(stdout);
        return(-1);
    }

    blocking = (*env)->CallBooleanMethod(env, jblocking, mbooleanValue);
    printf("createUDS - blocking: [%d]\n", blocking); fflush(stdout);

    if((UDSsocket = socket(AF_LOCAL, SOCK_STREAM, 0)) < 0) {
        //TODO: Throw exception
        printf("createUDS - error while createing socket - %s\n", strerror(errno)); fflush(stdout);
        return(-1);
    }
    
    addr.sun_family = AF_LOCAL;
    strcpy(addr.sun_path, socketPath);

    if(unlink(socketPath) < 0) {
        printf("createUDS - error while unlinking [%s] - %s\n", socketPath, strerror(errno)); fflush(stdout);
        return(-1);
    }
    (*env)->ReleaseStringUTFChars(env, jpath, socketPath);

    if(bind(UDSsocket, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        //TODO: Throw exception
        printf("createUDS - error while bind socket - %s\n", strerror(errno)); fflush(stdout);
        return(-1);
    }

    if(listen(UDSsocket, 10) < 0) {
        //TODO: Throw exception
        printf("createUDS - could not listen on socket - %s\n", strerror(errno)); fflush(stdout);
        return(-1);
    }

    if(blocking == 0) {
        setOptsFD = fcntl(UDSsocket, F_GETFL);
        setOptsFD |= O_NONBLOCK;
        fcntl(UDSsocket, F_SETFL, setOptsFD);
    }

    return(UDSsocket);
}
