#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>

#include "rh4n_ws.h"
#include "rh4n_bin.h"

int rh4n_bin_ws_loadSettings(char *socketpath, RH4nChildInformations_t *infos) {
    int i = 0, bytes_read = 0, timeout = 0, length = 0;
    void *target = NULL;
    const struct RH4nReadOutChildInformations lookup[6] = {
        {NULL, infos->library, sizeof(infos->library)},
        {NULL, infos->program, sizeof(infos->program)},
        {NULL, &infos->parms, -1},
        {NULL, &infos->srcPath, -1},
        {NULL, infos->loglevel, sizeof(infos->loglevel)},
        {NULL, &infos->logpath, -1}
    };

    memset(infos, 0x00, sizeof(RH4nChildInformations_t));

    if((infos->clientFD = rh4n_bin_createUDSClient(socketpath)) < 0) {
        return(-1);
    }

    for(; i < sizeof(lookup)/sizeof(struct RH4nReadOutChildInformations); i++) {
        if(lookup[i].maxsize == -1) {
            switch(rh4n_bin_waitForData(infos->clientFD)) {
                case -1:
                    return(-1);
                case -2:
                    fprintf(stderr, "ws_loadSettings - Unkown socket has reacted\n");
                    return(-1);
                case 1:
                    fprintf(stderr, "ws_loadSettings - No data from socket - exiting\n");
                    return(1);
            }

            if((bytes_read = read(infos->clientFD, &length, sizeof(length))) < 0) {
                return(-1);
            } else if(bytes_read == 0) {
                fprintf(stderr, "ws_loadSettings - Server disconnected\n");
                return(-2);
            }

            if((*((void**)lookup[i].target) = malloc(sizeof(char)*(length+1))) == NULL) {
                return(-1);
            }
            target = *((void**)lookup[i].target); 
            memset(target, 0x00, sizeof(char)*(length+1));
        } else {
            length = lookup[i].maxsize;
            target = lookup[i].target;
        }

        switch(rh4n_bin_waitForData(infos->clientFD)) {
            case -1:
                return(-1);
            case -2:
                fprintf(stderr, "ws_loadSettings - Unkown socket has reacted\n");
                return(-1);
            case 1:
                fprintf(stderr, "ws_loadSettings - No data from socket - exiting\n");
                return(1);
        }

        if((bytes_read = read(infos->clientFD, target, length)) < 0) {
            fprintf(stderr, "ws_loadSettings - Errir whiel read() - %s\n", strerror(errno));
            return(-1);
        } else if(bytes_read == 0) {
            fprintf(stderr, "ws_loadSettings - Server disconnected\n");
            return(-2);
        }
    }
}

