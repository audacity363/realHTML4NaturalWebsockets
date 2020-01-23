#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <jni.h>

#include "rh4n_ws.h"
#include "rh4n_jni.h"

pid_t rh4n_jni_startNaturalProcess(JNIEnv *env, const char *naturalbinpath, const char *socketpath, int mode) {
    char execPath[5000]; //Max ext4 length;
    char errorstr[5100];
    pid_t natpid = 0;
    char modestr[2] = {0x00, 0x00};

    sprintf(execPath, "%s/%s", naturalbinpath, RH4N_BIN_NAME);

    printf("startNaturalProcess - starting [%s]\n", execPath); fflush(stdout);

    //Check if the file exists and we can execute it
    if(access(execPath, F_OK|X_OK) < 0) {
        sprintf(errorstr, "%s - %s", execPath, strerror(errno));
        rh4n_jni_utils_throwJNIException(env, errno, errorstr);
        printf("startNaturalProcess - error while check file - %s\n", strerror(errno)); fflush(stdout);
        return(-1);
    }

    modestr[0] = 0x30 + mode;

    if((natpid = fork()) == 0) {
        execl(execPath, RH4N_BIN_NAME, socketpath, modestr, NULL);
        printf("startNaturalProcess - could not start natural - %s\n", strerror(errno)); 
        exit(errno);
    }

    return(natpid);
}

JNIEXPORT jint JNICALL Java_realHTML_jni_JNI_jni_1startNaturalWS
  (JNIEnv *env, jobject this, jstring jnaturalbinpath, jstring jsocketpath) {
    const char *naturalbinpath = NULL, *socketpath = NULL;

    if((naturalbinpath = (*env)->GetStringUTFChars(env, jnaturalbinpath, NULL)) == NULL) {
        return(-1);
    }
    printf("startNaturalWS - nat bin path [%s]\n", naturalbinpath); fflush(stdout);

    if((socketpath = (*env)->GetStringUTFChars(env, jsocketpath, NULL)) == NULL) {
        return(-1);
    }
    printf("startNaturalWS - socket path [%s]\n", socketpath); fflush(stdout);

    return(rh4n_jni_startNaturalProcess(env, naturalbinpath, socketpath, 0));
}

