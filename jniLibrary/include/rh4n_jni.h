#ifndef RH4NJNI
#define RH4NJNI

#include "rh4n_ws.h"

#define RH4N_BIN_NAME "realHTML4Natural"



//Client Informations
void rh4n_jni_handleChildInformations(int clientFD, JNIEnv *env, jobject msg);
void rh4n_jni_readChildInformations(JNIEnv *env, jobject msg, const struct RH4nReadOutChildInformations *lookup, int lookupLength);
void rh4n_jni_sendChildInformations(int clientFD, const struct RH4nReadOutChildInformations *lookup, int lookupLength, JNIEnv *env);

//utils
void rh4n_jni_utils_throwJNIException(JNIEnv *env, int errorno, const char *errorstr);

#endif
