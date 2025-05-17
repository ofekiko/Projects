#include "AddUrlCommand.h"

using namespace std;

// Adds a URL to the blacklist.
void AddUrlCommand::execute(int client_sock, BloomFilter& BF, const string& url) {
    // Set the field output that will sent to the client after the command.
    goodOutput = "201 Created";

    // Add the URL to the blacklist.
    BF.addUrl(url);

    // Write the URL to the file "Blacklist.txt".
    WriteURLtoFile(url);

    // Send the output to the client;
    send(client_sock, goodOutput.c_str(), goodOutput.size(), 0);
}