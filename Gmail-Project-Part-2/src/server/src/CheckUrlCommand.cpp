#include "CheckUrlCommand.h"

using namespace std;


// Check if a URL in the blacklist.
void CheckUrlCommand::execute(int client_sock, BloomFilter& BF, const string& url) {
    // Set the field output that will sent to the client after the command.
    goodOutput = "200 Ok\n\n";

    // Check if the URL in the Black List.
    goodOutput += BF.checkUrl(url);

    // Send the output to the client;
    send(client_sock, goodOutput.c_str(), goodOutput.size(), 0);
}