#ifndef ADDURLCOMMAND_H
#define ADDURLCOMMAND_H

#include "ICommand.h"

using namespace std;

// Class responsible for the command that adds a specific URL to the blacklist.
class AddUrlCommand : public ICommand {
    public:
        void execute(int client_sock, BloomFilter& BF, const string& url);
};

#endif
