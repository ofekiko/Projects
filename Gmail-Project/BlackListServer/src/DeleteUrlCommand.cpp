#include <iostream>
#include "DeleteUrlCommand.h"

using namespace std;

// Class responsible for the command that deletes a specific URL from the blacklist.
void DeleteUrlCommand::execute(int client_sock, BloomFilter& BF, const string& url) {
    // Set the output messages that will be sent to the client after executing the command.
    goodOutput = "204 No Content";
    badOutput = "404 Not Found";

    // Delete the URL from the file "Blacklist.txt".
    bool isDelete = DeleteURLFromFile(url);

    // Send the relevant output to the client.
    if (isDelete) {
        send(client_sock, goodOutput.c_str(), goodOutput.size(), 0);
    }
    else {
        send(client_sock, badOutput.c_str(), badOutput.size(), 0);
    }

}