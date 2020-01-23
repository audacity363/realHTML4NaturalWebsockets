CC=gcc
INCLUDE=-I /usr/lib/jvm/java-8-openjdk-amd64/include/ -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux \
		-I ./jni/include -I ./include -I ./rh4nbin/include -I./libs/rh4n_logging/include

CARGS1 = -g -c -fPIC $(INCLUDE)
CARGS2 = 
CARGS_SO = -c -g -fPIC $(INCLUDE)

LIBOUTPUT = ./bin/libs/

LOGGING_SRC = ./libs/rh4n_logging/src
LOGGING_BIN = ./bin/rh4n_logging
LOGGING_OBJS = rh4n_logging.c
LOGGING_LIB = librh4nlogging.a

logging: logging_clean logging_pre $(LOGGING_OBJS)
	@printf "Creating $(LIBOUTPUT)/$(LOGGING_LIB)\n"
	@$(AR) -cru $(LIBOUTPUT)/$(LOGGING_LIB) $(LOGGING_BIN)/*.o
	@printf "Done compiling and linking logging\n"

$(LOGGING_OBJS):
	@printf "CC $(LOGGING_SRC)/$*.c => $(LOGGING_BIN)/$*.o\n"
	@$(CC) $(CARGS1) -o $(LOGGING_BIN)/$*.o $(LOGGING_SRC)/$*.c

logging_pre:
	@printf "Creating logging output folder\n"
	@mkdir -p $(LOGGING_BIN)
	@mkdir -p $(LIBOUTPUT)

logging_clean:
	@printf "Cleaning logging library\n"
	@rm -f $(LIBOUTPUT)/$(LOGGING_BIN)
	@printf "Cleaning logging objects\n"
	@rm -f $(LOGGING_BIN)/*.o

all: jni_target rh4nbin_target
	@echo "Done"

JNI_OBJS=rh4n_jni_createUDS_Server.o rh4n_jni_acceptClient.o rh4n_jni_startNatural.o \
		 rh4n_jni_childProcess.o rh4n_jni_utils.o rh4n_jni_sendMessageToNatural.o \
		 rh4n_jni_clientInformations.o

jni_target: clean $(JNI_OBJS)
	@printf "Linking ./librealHTMLconnector.so\n"
	@$(CC) -shared ./bin/jni/*.o -o ./librealHTMLconnector.so

$(JNI_OBJS):
	@printf "./jni/src/$*.c => ./bin/jni/$*.o\n"
	@$(CC) -c -g -fpic ./jni/src/$*.c -o ./bin/jni/$*.o $(INCLUDE)

BIN_OBJS=rh4n_bin_main.o rh4n_bin_utils.o rh4n_bin_ChildConfiguration.o

rh4nbin_target: $(BIN_OBJS)
	@printf "Linking ./realHTML4Natural\n"
	@$(CC) ./bin/rh4nbin/*.o -o ./realHTML4Natural

$(BIN_OBJS):
	@printf "./rh4nbin/src/$*.c => ./bin/rh4nbin/$*.o\n"
	@$(CC) -c -g ./rh4nbin/src/$*.c -o ./bin/rh4nbin/$*.o $(INCLUDE)

clean:
	@find ./bin -name "*.o" -type f -exec rm {} \;
	@rm -f ./librealHTMLconnector.so
