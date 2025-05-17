#ifndef ICOMMAND_H
#define ICOMMAND_H

#include <string>
#include <iostream>
#include "BloomFilter.h"
#include "FileFunctions.h"
#include "InputValidity.h"
#include <sys/socket.h> 
#include <netinet/in.h>  
#include <arpa/inet.h> 
#include <unistd.h> 
#include <cstring>

// Abstract base class for all commands used by the Bloom Filter.
class ICommand {
protected:
    string goodOutput;
    string badOutput;

public:
    // Executes the command using the given Bloom Filter and URL.
    virtual void execute(int client_sock, BloomFilter& BF, const string& url) = 0;
    virtual ~ICommand() {}

protected:
    ICommand(){}
};

#endif
