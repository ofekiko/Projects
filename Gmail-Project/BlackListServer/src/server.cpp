#include "App.h"
#include "AddUrlCommand.h"
#include "CheckUrlCommand.h"
#include "DeleteUrlCommand.h"

using namespace std;

// Server entry point.
int main(int argc, char* argv[]) {
    map<string, ICommand*> commands;

    ICommand* addUrlCommand = new AddUrlCommand();
    commands["POST"] = addUrlCommand;

    ICommand* checkUrlCommand = new CheckUrlCommand();
    commands["GET"] = checkUrlCommand;

    ICommand* deleteUrlCommand = new DeleteUrlCommand();
    commands["DELETE"] = deleteUrlCommand;   

    App app(commands);

    // Checks if the main input has at least two arguments.
    if (!IsServCommValid(argc)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }  

    // Convert the server port input to an integer.
    int portID = stoi(argv[1]);

    // Checks if the given port is valid.
    if (!IsPortValid(portID)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }  

    // Create the socket server ID.
    int sockServer = app.initSocketServer(portID);

    // Create the Bloom Filter input string.
    string definitionInput = app.createDefinitionBF(argc, argv);

    // Checks if the initial input used to define the Bloom Filter is valid.
    if(!IsFirstInputValid(definitionInput)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }

    // Run the server program.
    app.run(sockServer, definitionInput);

    // Clean up dynamically allocated command objects.
    app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
}