#ifndef RH4NWS
#define RH4NWS
typedef struct {
    int clientFD;
    char library[9];
    char program[9];
    char *parms;
    char *srcPath;
    char loglevel[10];
    char *logpath;
} RH4nChildInformations_t;

struct RH4nReadOutChildInformations {
    const char *name;
    void *target;
    int maxsize;
};
#endif
