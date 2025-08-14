#ifndef APP_H
#define APP_H

#include <map>
#include "ICommand.h"


using namespace std;

// Class responsible for executing the program.
class App {
private:
    map<string, ICommand*> commands;
    mutex bloomFilterMutex;

    // Accepts a client connection using the given server socket ID.
    int acceptClient(int sockServer);
    
    public:
    // Handles client commands using the given client socket and Bloom Filter.
    void handleClient(BloomFilter& BF, int client_sock);

    // Constructor for the App class.
    App(map<string, ICommand*> commands);

    // Executes the main server operation.
    void run(int sockServer, string definitionInput);

    // Creates and initializes the socket ID for the server.
    int initSocketServer(int portID);

    // Converts the main input into a string format suitable for the Bloom Filter definition.
    string createDefinitionBF(int argc, char* argv[]);

    // Runs the server for a single command; mainly used for testing.
    void runOnce(int sockServer, const std::string& definitionInput);

    // Deletes the dynamically allocated commands to prevent memory leaks.
    void deleteCommands(ICommand* add, ICommand* check, ICommand* del);
};

#endif








// #ifndef APP_H
// #define APP_H

// #include <map>
// #include "ICommand.h"
// #include "InputValidity.h"
// #include "BloomFilter.h"
// #include "FileFunctions.h"

// using namespace std;

// // Class responsible for executing the program.
// class App {
// private:
//     map<string, ICommand*> commands;
    
// public:
//     App(map<string, ICommand*> commands);
//     void run();
// };

// #endif