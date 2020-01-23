#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <sys/un.h>

#include <jni.h>

JNIEXPORT jint JNICALL Java_realHTML_jni_JNI_jni_1waitForClient
  (JNIEnv *env, jobject this, jstring jpath, jint serverFD) {
    int clientFD = -1;
    const char *socketPath = NULL;
    struct sockaddr_un addr; memset(&addr, 0x00, sizeof(addr));
    socklen_t addrlen; memset(&addrlen, 0x00, sizeof(addrlen));
    jclass cexception = NULL;
    jstring serrorMessage = NULL;
    jmethodID mexceptionInit = NULL;
    jobject oexception = NULL;

    if((socketPath = (*env)->GetStringUTFChars(env, jpath, NULL)) == NULL) {
        printf("waitForClient - error while getting path from java\n"); fflush(stdout);
        return(-1);
    }


    addr.sun_family = AF_LOCAL;
    strcpy(addr.sun_path, socketPath);
    (*env)->ReleaseStringUTFChars(env, jpath, socketPath);

    if((clientFD = accept(serverFD, (struct sockaddr*)&addr, &addrlen)) > -1) {
        return(clientFD);
    }

    if(errno == EWOULDBLOCK) {
        if((cexception = (*env)->FindClass(env, "realHTML/jni/exceptions/NoClientException")) == NULL) {
            printf("waitForClient - error while searching NoClientException\n"); fflush(stdout);
            return(-1);
        }

        if((mexceptionInit = (*env)->GetMethodID(env, cexception, "<init>", "()V")) == NULL) {
            printf("waitForClient - could not find init method for NoClientException\n"); fflush(stdout);
            return(-1);
        }

        if((oexception = (*env)->NewObject(env, cexception, mexceptionInit)) == NULL) {
            printf("waitForClient - could not create new NoClientException\n"); fflush(stdout);
            return(-1);
        }
        (*env)->Throw(env, (jthrowable)oexception);
        return(-1);
    }

    return(-1);
}
