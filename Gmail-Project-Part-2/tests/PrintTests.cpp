#include <gtest/gtest.h>
#include <sys/socket.h>
#include <unistd.h>
#include "../src/server/src/AddUrlCommand.h"
#include "../src/server/src/CheckUrlCommand.h"
#include "../src/server/src/DeleteUrlCommand.h"
#include "../src/server/src/BloomFilter.h"
#include "../src/server/src/FileFunctions.h"
#include "../src/server/src/InputValidity.h"
#include "../src/server/src/App.h"


TEST(AddUrlCommandTest, Sends201Created) {
    int sv[2];
    ASSERT_EQ(socketpair(AF_UNIX, SOCK_STREAM, 0, sv), 0) << "Failed to create socket pair";

    BloomFilter bf("1000 0.01");
    AddUrlCommand cmd;

    std::string url = "www.test.com";

    cmd.execute(sv[0], bf, url);

    char buffer[1024] = {0};
    ssize_t bytes = read(sv[1], buffer, sizeof(buffer));

    ASSERT_GT(bytes, 0);
    std::string response(buffer, bytes);

    EXPECT_EQ(response, "201 Created");

    close(sv[0]);
    close(sv[1]);
}


TEST(CheckUrlCommandTest, Sends200OkAndResult) {
    int sockpair[2];
    ASSERT_EQ(socketpair(AF_UNIX, SOCK_STREAM, 0, sockpair), 0) << "Couldn't create socket pair";

    CheckUrlCommand command;
    BloomFilter bf("1000 0.01");

    bf.addUrl("www.gmail.com");

    command.execute(sockpair[0], bf, "www.gmail.com");

    char buffer[1024] = {0};
    ssize_t bytes = read(sockpair[1], buffer, sizeof(buffer) - 1);
    std::string response(buffer, bytes);

    EXPECT_TRUE(response.find("200 Ok") != std::string::npos) << "Expected '200 Ok' but got: " << response;
    EXPECT_TRUE(response.find("true") != std::string::npos) << "Expected 'true' in response but got: " << response;

    close(sockpair[0]);
    close(sockpair[1]);
}
TEST(DeleteUrlCommandTest, Sends204IfUrlDeleted) {
    std::ofstream file("Blacklist.txt");
    file << "www.gmail.com\n";
    file.close();

    int sockpair[2];
    ASSERT_EQ(socketpair(AF_UNIX, SOCK_STREAM, 0, sockpair), 0);

    BloomFilter bf("1000 0.01"); 
    bf.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");
    DeleteUrlCommand cmd;

    cmd.execute(sockpair[0], bf, "www.gmail.com");

    char buffer[1024] = {0};
    ssize_t bytes = read(sockpair[1], buffer, sizeof(buffer) - 1);
    std::string response(buffer, bytes);

    EXPECT_EQ(response, "204 No Content");

    close(sockpair[0]);
    close(sockpair[1]);
}

TEST(DeleteUrlCommandTest, Sends404IfUrlNotFound) {
    std::ofstream file("Blacklist.txt");
    file.close();

    int sockpair[2];
    ASSERT_EQ(socketpair(AF_UNIX, SOCK_STREAM, 0, sockpair), 0);

    DeleteUrlCommand cmd;
    BloomFilter bf("1000 0.01");

    cmd.execute(sockpair[0], bf, "www.notfound.com");

    char buffer[1024] = {0};
    ssize_t bytes = read(sockpair[1], buffer, sizeof(buffer) - 1);
    std::string response(buffer, bytes);

    EXPECT_EQ(response, "404 Not Found");

    close(sockpair[0]);
    close(sockpair[1]);
}

TEST(AppTest, HandleClient_InvalidCommand_Returns400BadRequest) {
    int sockpair[2];
    ASSERT_EQ(socketpair(AF_UNIX, SOCK_STREAM, 0, sockpair), 0);

    std::map<std::string, ICommand*> commands = {
        {"POST", new AddUrlCommand()},
        {"GET", new CheckUrlCommand()},
        {"DELETE", new DeleteUrlCommand()}
    };
    App app(commands);

    BloomFilter bf("1000 0.01");

    std::thread serverThread([&]() {
        app.handleClient(bf, sockpair[0]);
    });

    std::string invalidRequest = "INVALID_CMD something\n";
    send(sockpair[1], invalidRequest.c_str(), invalidRequest.size(), 0);

    char buffer[4096] = {0};
    ssize_t bytesRead = recv(sockpair[1], buffer, sizeof(buffer) - 1, 0);
    std::string response(buffer, bytesRead);

    EXPECT_EQ(response, "400 Bad Request");

    shutdown(sockpair[1], SHUT_WR); 
    serverThread.join();
    close(sockpair[1]);

    for (auto& pair : commands)
        delete pair.second;
}

TEST(ServerSocketTest, BindsToCorrectPort) {
    int server_sock = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_GT(server_sock, 0) << "Failed to create socket";

    struct sockaddr_in server_addr = {};
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    server_addr.sin_port = htons(5555);

    ASSERT_EQ(bind(server_sock, (struct sockaddr*)&server_addr, sizeof(server_addr)), 0);

    struct sockaddr_in actual_addr = {};
    socklen_t actual_len = sizeof(actual_addr);
    ASSERT_EQ(getsockname(server_sock, (struct sockaddr*)&actual_addr, &actual_len), 0);

    int actual_port = ntohs(actual_addr.sin_port);
    ASSERT_EQ(actual_port, 5555) << "Server not bound to expected port";

    close(server_sock);
}

// בדיקת פורטים חוקיים ולא חוקיים
TEST(ValidationTest, ValidPortReturnsTrue) {
    EXPECT_TRUE(IsPortValid(1024));
    EXPECT_TRUE(IsPortValid(12345));
    EXPECT_TRUE(IsPortValid(65535));
}

TEST(ValidationTest, InvalidPortReturnsFalse) {
    EXPECT_FALSE(IsPortValid(0));        // קטן מדי
    EXPECT_FALSE(IsPortValid(80));       // קטן מ־1024
    EXPECT_FALSE(IsPortValid(1023));     // גבול תחתון לא כולל
    EXPECT_FALSE(IsPortValid(65536));    // גדול מדי
    EXPECT_FALSE(IsPortValid(-1));       // שלילי
}

// argc < 2 => false, אחרת true
TEST(ValidationTest, ValidArgcReturnsTrue) {
    EXPECT_TRUE(IsServCommValid(2));
    EXPECT_TRUE(IsServCommValid(3));
    EXPECT_TRUE(IsServCommValid(10));
}

TEST(ValidationTest, InvalidArgcReturnsFalse) {
    EXPECT_FALSE(IsServCommValid(0));
    EXPECT_FALSE(IsServCommValid(1));
}
