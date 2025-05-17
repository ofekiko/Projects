#ifndef APP_H
#define APP_H

#include <map>
#include "ICommand.h"

using namespace std;

// Class responsible for executing the program.
class App {
private:
    map<string, ICommand*> commands;
    int acceptClient(int sockServer);
    
    public:
    void handleClient(BloomFilter BF, int client_sock);
    App(map<string, ICommand*> commands);
    void run(int sockServer, string definitionInput);
    int initSocketServer(int portID);
    string createDefinitionBF(int argc, char* argv[]);
    void runOnce(int sockServer, const std::string& definitionInput);
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