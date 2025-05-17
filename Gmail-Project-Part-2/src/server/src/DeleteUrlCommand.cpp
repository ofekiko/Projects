#include <iostream>
#include "DeleteUrlCommand.h"

using namespace std;

// Adds a URL to the blacklist.
void DeleteUrlCommand::execute(int client_sock, BloomFilter& BF, const string& url) {
    // Set the field output that will sent to the client after the command.
    goodOutput = "204 No Content";
    badOutput = "404 Not Found";
    // Write the URL to the file "Blacklist.txt".
    bool isDelete = DeleteURLFromFile(url);
    // Send the output to the client;
    if (isDelete) {
        send(client_sock, goodOutput.c_str(), goodOutput.size(), 0);
    }
    else {
        send(client_sock, badOutput.c_str(), badOutput.size(), 0);
    }

}