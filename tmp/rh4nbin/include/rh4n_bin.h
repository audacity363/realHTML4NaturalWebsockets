#ifndef RH4NBIN
#define RH4NBIN

int rh4n_bin_ws_loadSettings(char *socketpath, RH4nChildInformations_t *infos);

//Utils
int rh4n_bin_createUDSClient(char *socketpath);
int rh4n_bin_waitForData(int client);

#endif
