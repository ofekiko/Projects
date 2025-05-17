#include "App.h"
#include "AddUrlCommand.h"
#include "CheckUrlCommand.h"
#include "DeleteUrlCommand.h"

using namespace std;

int main(int argc, char* argv[]) {
    map<string, ICommand*> commands;

    ICommand* addUrlCommand = new AddUrlCommand();
    commands["POST"] = addUrlCommand;

    ICommand* checkUrlCommand = new CheckUrlCommand();
    commands["GET"] = checkUrlCommand;

    ICommand* deleteUrlCommand = new DeleteUrlCommand();
    commands["DELETE"] = deleteUrlCommand;   

    App app(commands);

    if (!IsServCommValid(argc)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }  

    int portID = stoi(argv[1]);

    if (!IsPortValid(portID)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }  

    int sockServer = app.initSocketServer(portID);

    string definitionInput = app.createDefinitionBF(argc, argv);

    if(!IsFirstInputValid(definitionInput)) {
        app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
        return 1;
    }

    app.run(sockServer, definitionInput);

    app.deleteCommands(addUrlCommand, checkUrlCommand, deleteUrlCommand);
}