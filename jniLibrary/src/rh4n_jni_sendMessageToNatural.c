#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <jni.h>

#include "rh4n_ws.h"
#include "rh4n_jni.h"

#define JAVAMESSAGETYPE_JSON 0
#define JAVAMESSAGETYPE_TEXT 1
#define JAVAMESSAGETYPE_CHILDINFORMATIONS 2

JNIEXPORT void JNICALL Java_realHTML_jni_JNI_jni_1sendMessageToNatural
  (JNIEnv *env, jobject this, jint clientFD, jobject omessage) {
    jclass cMessageType = NULL, cMessage = NULL;
    jfieldID fType = NULL, fActualMessage = NULL;
    jmethodID mordinal = NULL;
    jobject omessageType = NULL, oactualMessage = NULL;

    int messageType = 0;

    if((cMessage = (*env)->FindClass(env, "realHTML/jni/natural/Message")) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not find class realHTML/jni/natural/Message");
        return;
    }

    if((cMessageType = (*env)->FindClass(env, "realHTML/jni/natural/MessageType")) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not find class realHTML/jni/natural/MessageType");
        return;
    }

    if((fType = (*env)->GetFieldID(env, cMessage, "type", "LrealHTML/jni/natural/MessageType;")) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not find field \"type\" in Message");
        return;
    }

    if((fActualMessage = (*env)->GetFieldID(env, cMessage, "msg", "Ljava/lang/Object;")) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not find field \"msg\" in Message");
        return;
    }

    if((mordinal = (*env)->GetMethodID(env, cMessageType, "ordinal", "()I")) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not find method \"ordinal\" in MessageType");
        return;
    }

    if((omessageType = (*env)->GetObjectField(env, omessage, fType)) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not get field \"type\" from Object");
        return;
    }

    if((oactualMessage = (*env)->GetObjectField(env, omessage, fActualMessage)) == NULL) {
        rh4n_jni_utils_throwJNIException(env, -1, "Could not get field \"msg\" from Object");
        return;
    }

    messageType = (*env)->CallIntMethod(env, omessageType, mordinal);
    if((*env)->ExceptionCheck(env)) {
        return;
    }

    printf("Found message type: [%d]\n", messageType); fflush(stdout);
    switch(messageType) {
        case JAVAMESSAGETYPE_CHILDINFORMATIONS:
            rh4n_jni_handleChildInformations(clientFD, env, oactualMessage);
            break;
    }
}



