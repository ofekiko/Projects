#ifndef CHECKURLCOMMAND_H
#define CHECKURLCOMMAND_H

#include "ICommand.h"

using namespace std;

// Class responsible for the command that checks if a specific URL is in the blacklist.
class CheckUrlCommand : public ICommand {
    public:
        void execute(int client_sock, BloomFilter& BF, const string& url);
};

#endif
