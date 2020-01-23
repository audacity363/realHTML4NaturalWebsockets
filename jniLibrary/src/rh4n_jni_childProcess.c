#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <jni.h>

#include "rh4n_jni.h"

JNIEXPORT jobject JNICALL Java_realHTML_jni_JNI_jni_1getChildProcessStatus
  (JNIEnv *env, jobject this, jint pid) {
    int status = 0, pid_status = 0, exit_code = 0;
    jboolean exited = 0;
    jstring jreason = NULL;

    jclass cChildProcess = NULL;
    jmethodID mChildProcessConstructor = NULL;
    jobject returnobj = NULL;

    if((pid_status = waitpid(pid, &status, WNOHANG)) < 0) {
        rh4n_jni_utils_throwJNIException(env, errno, strerror(errno));
        return(NULL);
    }

    if(pid_status == pid) {
        exited = 1;
        exit_code = WEXITSTATUS(status);
        jreason = (*env)->NewStringUTF(env, strerror(exit_code));
        printf("Child %d exited with status: [%d] - %s\n", pid, exit_code, strerror(exit_code)); fflush(stdout);
    } else {
        printf("Child %d is running\n", pid); fflush(stdout);
    }

    if((cChildProcess = (*env)->FindClass(env, "realHTML/jni/ChildProcess")) == NULL) {
        printf("childStatus - could not find ChildProcess\n"); fflush(stdout);
        return(NULL);
    }

    if((mChildProcessConstructor = (*env)->GetMethodID(env, cChildProcess, "<init>", "(IZILjava/lang/String;)V")) == NULL) {
        printf("childStatus - could not find ChildProcess constructor\n"); fflush(stdout);
        return(NULL);
    }

    if((returnobj = (*env)->NewObject(env, cChildProcess, mChildProcessConstructor, 
                    pid, exited, exit_code, jreason)) == NULL) {
        printf("childStatus - could not create return object\n"); fflush(stdout);
        return(NULL);
    }

    return(returnobj);
}

JNIEXPORT void JNICALL Java_realHTML_jni_JNI_jni_1killChildProcess
  (JNIEnv *env, jobject this, jint pid, jint signal) {
    if(kill(pid, signal) < 0) {
        rh4n_jni_utils_throwJNIException(env, errno, strerror(errno));
    }
 }
