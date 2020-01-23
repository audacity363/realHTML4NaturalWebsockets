#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <jni.h>

#include "rh4n_ws.h"
#include "rh4n_jni.h"

void rh4n_jni_handleChildInformations(int clientFD, JNIEnv *env, jobject msg) {
    RH4nChildInformations_t infos; memset(&infos, 0x00, sizeof(infos));
    const struct RH4nReadOutChildInformations lookup[] = {
        {"library", infos.library, sizeof(infos.library)},
        {"program", infos.program, sizeof(infos.program)},
        {"parms", &infos.parms, -1},
        {"srcPath", &infos.srcPath, -1},
        {"loglevel", infos.loglevel, sizeof(infos.loglevel)},
        {"logPath", &infos.logpath, -1}
    };
    int lookupLength = sizeof(lookup)/sizeof(struct RH4nReadOutChildInformations);

    rh4n_jni_readChildInformations(env, msg, lookup, lookupLength);
    if((*env)->ExceptionCheck(env)) {
        if(infos.parms) free(infos.parms);
        if(infos.srcPath) free(infos.srcPath);
        if(infos.logpath) free(infos.logpath);
        return;
    }

    printf("Lib: [%s]/Prog: [%s]/Parms: [%s]/srcPath: [%s]\n", infos.library, infos.program, infos.parms, infos.srcPath);
    printf("Log-Level: [%s]/Path: [%s]\n", infos.loglevel, infos.logpath);

    rh4n_jni_sendChildInformations(clientFD, lookup, lookupLength, env);
    if((*env)->ExceptionCheck(env)) {
        if(infos.parms) free(infos.parms);
        if(infos.srcPath) free(infos.srcPath);
        if(infos.logpath) free(infos.logpath);
        return;
    }

    fflush(stdout);
}

void rh4n_jni_readChildInformations(JNIEnv *env, jobject msg, 
  const struct RH4nReadOutChildInformations *lookup, int lookupLength) {
    jclass cChildInformations = NULL;
    jfieldID jfield = NULL;
    jobject targetobj = NULL;
    const char *cvalue = NULL;
    char errorstr[1024];
    int i = 0;

    if((cChildInformations = (*env)->FindClass(env, "realHTML/jni/natural/ChildInformations")) == NULL) {
        return;
    }

    for(; i < lookupLength; i++) {
        if((jfield = (*env)->GetFieldID(env, cChildInformations, lookup[i].name, "Ljava/lang/String;")) == NULL) {
            return;
        }

        if((targetobj = (*env)->GetObjectField(env, msg, jfield)) == NULL) {
            sprintf(errorstr, "Field \"%s\" is NULL", lookup[i].name);
            rh4n_jni_utils_throwJNIException(env, -1, errorstr);
            return;
        }

        if((cvalue = (*env)->GetStringUTFChars(env, (jstring)targetobj, NULL)) == NULL || (*env)->ExceptionCheck(env)) {
            return;
        }

        if(lookup[i].maxsize == -1) {
            if((*((void**)lookup[i].target) = malloc(sizeof(char)*(strlen(cvalue)+1))) == NULL) {
                rh4n_jni_utils_throwJNIException(env, errno, strerror(errno));
                return;
            }

            memset(*((void**)lookup[i].target), 0x00, sizeof(char)*(strlen(cvalue)+1));
            strcpy(*((void**)lookup[i].target), cvalue);
        } else {
            if(strlen(cvalue) > lookup[i].maxsize-1) {
                sprintf(errorstr, "Field \"%s\" is too long [%d/%d]", 
                        lookup[i].name, (int)strlen(cvalue), lookup[i].maxsize-1);
                rh4n_jni_utils_throwJNIException(env, -1, errorstr);
                return;
            }

            strncpy(lookup[i].target, cvalue, lookup[i].maxsize-1);
        }

        (*env)->ReleaseStringUTFChars(env, (jstring)targetobj, cvalue);
    }
    return;
} 

void rh4n_jni_sendChildInformations(int clientFD, const struct RH4nReadOutChildInformations *lookup, 
  int lookupLength, JNIEnv *env) {
    int i = 0, byte_send = 0, length = 0;
    void *target = NULL;

    printf("Write to client [%d]\n", clientFD); fflush(stdout);

    for(; i < lookupLength; i++) {
        if(lookup[i].maxsize == -1) {
            target = *((void**)lookup[i].target);
            length = strlen((char*)target);
            if(write(clientFD, &length, sizeof(length)) < 0) {
                rh4n_jni_utils_throwJNIException(env, errno, strerror(errno));
                return;
            }
        } else {
            length = lookup[i].maxsize;
            target = lookup[i].target;
        }

        if(write(clientFD, target, length) < 0) {
            rh4n_jni_utils_throwJNIException(env, errno, strerror(errno));
            return;
        }
    }
}
