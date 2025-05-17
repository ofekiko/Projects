#include "App.h"

using namespace std;

// Initialize the App object with the relevant commands.
App::App(map<string, ICommand*> commands) : commands(commands) {}

int App::initSocketServer(int portID) {
    const int server_port = portID;
    int sockServer = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(server_port);
    bind(sockServer, (struct sockaddr *) &sin, sizeof(sin));
    listen(sockServer, 1); 
    return sockServer;
}

string App::createDefinitionBF(int argc, char* argv[]) {
    string definitionInput;
    for (int i = 2; i < argc; i++) {
        definitionInput += argv[i];
        if (i < argc) {
            definitionInput += " ";
        }
    }
    return definitionInput;
}


void App::deleteCommands(ICommand* add, ICommand* check, ICommand* del) {
    delete add;
    delete check;
    delete del;    
}

int App::acceptClient(int sockServer) {
    struct sockaddr_in client_sin;
    unsigned int addr_len = sizeof(client_sin);
    int client_sock = accept(sockServer, (struct sockaddr *) &client_sin,  &addr_len); 
    return client_sock;
}

void App::handleClient(BloomFilter BF, int client_sock){
    string taskAndUrl;
    string inValidInput = "400 Bad Request";

    while (true) {

        char clientMessage[4096];
        int expected_data_len = sizeof(clientMessage);
        int read_bytes = recv(client_sock, clientMessage, expected_data_len, 0);
        if (read_bytes == 0) {
            break;
        }
 
        string taskAndUrl(clientMessage, read_bytes);    
        
        // Check if the input is a valid command and a valid URL.
        if (!IsCommandValid(taskAndUrl)) {
            send(client_sock, inValidInput.c_str(), inValidInput.size(), 0);
        }   
        
        // If the input is a valid command and a valid URL.
        else {
            // Split the input into two parts: the command and the URL.
            istringstream inputSplit = istringstream(taskAndUrl);
            string task;
            string url;
            inputSplit >> task;
            inputSplit >> url;

            // Execute the command with the BloomFilter and the URL.
            commands[task]->execute(client_sock, BF, url);
        }
    }

    close(client_sock);
}

void App::run(int sockServer, string definitionInput) {
    BloomFilter BF(definitionInput);
    InitFromFile(BF);

    while (true) {
        int client_sock = acceptClient(sockServer);
        handleClient(BF, client_sock);
    }
}

void App::runOnce(int sockServer, const std::string& definitionInput) {
    BloomFilter BF(definitionInput);
    InitFromFile(BF);

    int client_sock = acceptClient(sockServer);
    handleClient(BF, client_sock);
}
