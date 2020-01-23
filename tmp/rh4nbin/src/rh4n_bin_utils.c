#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>

#include "rh4n_ws.h"
#include "rh4n_bin.h"

int rh4n_bin_createUDSClient(char *socketpath) {
    int clientFD = 0;
    struct stat filestatus;
    struct sockaddr_un address;

    if(access(socketpath, R_OK | W_OK) < 0) {
        fprintf(stderr, "Could not access file [%s] - %s\n", socketpath, strerror(errno));
        return(-1);
    }

    if(stat(socketpath, &filestatus) < 0) {
        fprintf(stderr, "Could not get stats from file [%s] - %s\n", socketpath, strerror(errno));
        return(-1);
    }

    if(S_ISSOCK(filestatus.st_mode) == 0) {
        fprintf(stderr, "File %s is not a socket\n", strerror(errno));
        return(-1);
    }

    if((clientFD = socket(PF_LOCAL, SOCK_STREAM, 0)) < 0) {
        fprintf(stderr, "Could not create socket - %s\n", strerror(errno));
        return(-1);
    }

    address.sun_family = AF_LOCAL;
    strcpy(address.sun_path, socketpath);

    if(connect(clientFD, (struct sockaddr*)&address, sizeof(address)) < 0) {
        fprintf(stderr, "Could not connect to server - %s\n", strerror(errno));
        return(-1);
    }

    return(clientFD);
}

int rh4n_bin_waitForData(int client) {
    int selectret = 0;
    fd_set rfds;
    struct timeval tv;
    tv.tv_sec = 5;

    FD_ZERO(&rfds);
    FD_SET(client, &rfds);

    if((selectret = select(client+1, &rfds, NULL, NULL, &tv)) < 0) {
        return(-1);
    }

    if(selectret == 0) {
        return(1);
    }

    if(FD_ISSET(client, &rfds)) {
        return(0);
    }

    return(-2);
}
