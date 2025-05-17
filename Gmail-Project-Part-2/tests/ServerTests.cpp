#include <gtest/gtest.h>
#include "../src/server/src/BloomFilter.h"
#include "../src/server/src/FileFunctions.h"
#include "../src/server/src/App.h"                
#include "../src/server/src/ICommand.h"           
#include "../src/server/src/AddUrlCommand.h"       
#include "../src/server/src/CheckUrlCommand.h"     
#include "../src/server/src/DeleteUrlCommand.h" 
#include <sys/time.h> 

std::string sendRequestToServer(const std::string& request) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) return "ERROR: socket";

    struct timeval timeout;
    timeout.tv_sec = 3;
    timeout.tv_usec = 0;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout, sizeof(timeout));

    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(5555);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    if (connect(sock, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
        close(sock);
        return "ERROR: connect";
    }

    send(sock, request.c_str(), request.size(), 0);

    char buffer[4096] = {0};
    int bytes = recv(sock, buffer, sizeof(buffer) - 1, 0);

    close(sock);

    if (bytes <= 0) {
        return "ERROR: recv failed or timed out";
    }

    return std::string(buffer, bytes); 
}

TEST(ServerTest, PostUrlReturns201Created) {
    std::string res = sendRequestToServer("POST http://example.com\n");
    EXPECT_NE(res.find("201 Created"), std::string::npos)
        << "Expected '201 Created' but got: " << res;
}

TEST(ServerTest, PostUrlReturns201Created2) {
    std::string res = sendRequestToServer("POST http://example.com\n");
    std::cout << "Server response: [" << res << "]" << std::endl;
    EXPECT_NE(res.find("201 Created"), std::string::npos)
        << "Expected '201 Created' but got: [" << res << "]";
}

void runServer() {
    map<string, ICommand*> commands;

    ICommand* addUrlCommand = new AddUrlCommand();
    commands["POST"] = addUrlCommand;
    
    ICommand* checkUrlCommand = new CheckUrlCommand();
    commands["GET"] = checkUrlCommand;

    ICommand* deleteUrlCommand = new DeleteUrlCommand();
    commands["DELETE"] = deleteUrlCommand;    
    
    App app(commands);
    int sockServer = app.initSocketServer();

    const char* args[] = {"server", "1000", "8", "Blacklist.txt"};
    string definitionInput = app.createDefinitionBF(4, const_cast<char**>(args));
    
    if(!IsFirstInputValid(definitionInput)) {
        delete addUrlCommand;
        delete checkUrlCommand;
        delete deleteUrlCommand;
        return;
    }
    
    app.runOnce(sockServer, definitionInput);
    
    delete addUrlCommand;
    delete checkUrlCommand;
    delete deleteUrlCommand;
}

extern int main(int argc, char* argv[]); 
int main(int argc, char** argv) {
    pid_t pid = fork();
    if (pid == 0) {
        runServer(); 
        return 0;
    }

    std::this_thread::sleep_for(std::chrono::seconds(1)); 

    ::testing::InitGoogleTest(&argc, argv);
    int result = RUN_ALL_TESTS();

    kill(pid, SIGTERM);          
    waitpid(pid, nullptr, 0);    
    return result;
}
