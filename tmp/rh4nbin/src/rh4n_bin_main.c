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

#define RH4NBINMODE_WS 0
#define RH4NBINMODE_STANDALONE 1

int main(int argc, char *argv[]) {
    FILE *newstdout = fopen("/tmp/rh4nstdout", "w");
    FILE *newstderr = fopen("/tmp/rh4nstderr", "w");
    dup2(fileno(newstdout), STDOUT_FILENO);
    dup2(fileno(newstderr), STDERR_FILENO);
    fclose(newstdout);
    fclose(newstderr);

    if(argc == 2 && strcmp(argv[1], "--help") == 0) {
        //TODO: Print help
        printf("This is the help output\n");
        return(0);
    } else if(argc != 3) {
        printf("Usage: %s <socketfile|tmpfile> <mode>\n", argv[0]);
        return(-1);
    } 
    int mode = 0;
    RH4nChildInformations_t infos; memset(&infos, 0x00, sizeof(infos));

    mode = atoi(argv[2]);
    switch(mode) {
        case RH4NBINMODE_WS:
            rh4n_bin_ws_loadSettings(argv[1], &infos);
            printf("main - Lib: [%s]/Prog: [%s]/Parms: [%s]/srcPath: [%s]\n", infos.library, infos.program, infos.parms, infos.srcPath);
            printf("main - Log-Level: [%s]/Path: [%s]\n", infos.loglevel, infos.logpath);
            sleep(5);
            break;
        case RH4NBINMODE_STANDALONE:
            break;
        default:
            fprintf(stderr, "Unkown mode [%d]\n", mode);
            return(0);
    }
    fflush(stdout); fflush(stderr);
}
