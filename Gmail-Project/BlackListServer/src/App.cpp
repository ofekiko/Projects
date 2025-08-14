#include "App.h"

using namespace std;

const int BUFFER_CLIENT_MESSAGE_SIZE = 4096;
const int MAX_CONNECTIONS = 10;

// Initialize the App object with the relevant commands.
App::App(map<string, ICommand*> commands) : commands(commands) {}


int App::initSocketServer(int portID) {
    //  Create the socket ID for the server using TCP.
    const int server_port = portID;
    int sockServer = socket(AF_INET, SOCK_STREAM, 0);
    if (sockServer < 0) {
        // If socket fail close the program.
        exit(1);
    }

    // Set up the server address structure.
    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(server_port);

    // Bind the socket to the given port.
     if (::bind(sockServer, (struct sockaddr *) &sin, sizeof(sin)) < 0) {
        close(sockServer);
        // If binding fail close the program.
        exit(1); 
    }

    // Start listening for incoming connections and allow at most 10 pending connection in the queue.
    if (listen(sockServer, MAX_CONNECTIONS) < 0) {
        close(sockServer);
        // If listening fail close the program.
        exit(1);  
    }
    
    // Return the socekt server ID.
    return sockServer;
}


string App::createDefinitionBF(int argc, char* argv[]) {
    string definitionInput;
    
    // Iterate over the command-line arguments starting from index 2 and collect the relevant numbers for the Bloom Filter definition.
    for (int i = 2; i < argc; i++) {
        definitionInput += argv[i];
        // Add a space if it's not the last element.
        if (i < argc - 1) {
            definitionInput += " ";
        }
    }
    return definitionInput;
}


void App::deleteCommands(ICommand* add, ICommand* check, ICommand* del) {
    // Delete the dynamic commands to prevent memory leaks.
    delete add;
    delete check;
    delete del;    
}


int App::acceptClient(int sockServer) {
    // Accept a client connection using the given server socket ID.
    struct sockaddr_in client_sin;
    unsigned int addr_len = sizeof(client_sin);

    // Create the socket ID for the connected client.
    int client_sock = accept(sockServer, (struct sockaddr *) &client_sin,  &addr_len); 
    if (client_sock < 0) {
        // If accepting fail close the program.
        exit(1);
    }    
    return client_sock;
}


void App::handleClient(BloomFilter& BF, int client_sock){
    string taskAndUrl;

    // Define the output string to send to the client for an invalid command.
    string inValidInput = "400 Bad Request";

    // Infinite loop to handle client communication.
    while (true) {

        // Recieve a mmessage from the client.
        char clientMessage[BUFFER_CLIENT_MESSAGE_SIZE];
        int expected_data_len = sizeof(clientMessage);
        int read_bytes = recv(client_sock, clientMessage, expected_data_len, 0);

        // Stop the loop if no data is received.
        if (read_bytes == 0) {
            break;
        }
 
        // Convert the client's message into a string.
        string taskAndUrl(clientMessage, read_bytes);    
        
        // Check if the client's input is a valid command and a valid URL.
        if (!IsCommandValid(taskAndUrl)) {
            send(client_sock, inValidInput.c_str(), inValidInput.size(), 0);
        }   
        
        // If the cient's input is a valid command and a valid URL.
        else {
            // Split the input into two parts: the command and the URL.
            istringstream inputSplit = istringstream(taskAndUrl);
            string task;
            string url;
            inputSplit >> task;
            inputSplit >> url;

            // Execute the command with the BloomFilter and the URL.
            {
                // Lock the BloomFilter command in each thread to prevent them from interrupting each other.
                lock_guard<mutex> lock(bloomFilterMutex);
                commands[task]->execute(client_sock, BF, url);
            }
        }
    }

    // Close the client socket.
    close(client_sock);
}

void App::run(int sockServer, string definitionInput) {
    // Create the Bloom Filter for the server.
    BloomFilter BF(definitionInput);

    // Initialize the Bloom Filter with the existing blacklist.
    InitFromFile(BF);

    // Infinite loop to handle one client at a time.
    while (true) {
        int client_sock = acceptClient(sockServer);

        // Create a new thread for each connected client.
        thread thread(&App::handleClient, this, ref(BF), client_sock);
        thread.detach();

    }
}

void App::runOnce(int sockServer, const std::string& definitionInput) {
    // Create the Bloom Filter for the server.
    BloomFilter BF(definitionInput);

    // Initialize the Bloom Filter with the existing blacklist.
    InitFromFile(BF);

    // Handle a client.
    int client_sock = acceptClient(sockServer);
    handleClient(BF, client_sock);
}
