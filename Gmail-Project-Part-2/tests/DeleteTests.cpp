#include <gtest/gtest.h>
#include "../src/server/src/BloomFilter.h"
#include "../src/server/src/FileFunctions.h"

TEST(DeleteUrlTest, deleteUrl_false) {
    const std::string input = "8 2 1";
    BloomFilter BF(input);
    InitFromFile(BF);
    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");
    DeleteURLFromFile("www.gmail.com");

    EXPECT_FALSE(CheckURLInFile("www.gmail.com"));
}


TEST(DeleteUrlTest, delete2Url_false) {
    const std::string input = "8 2 1";
    BloomFilter BF(input);
    InitFromFile(BF);
    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");
    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");
    DeleteURLFromFile("www.gmail.com");

    EXPECT_FALSE(CheckURLInFile("www.gmail.com"));
}

TEST(DeleteUrlTest, deleteEmptyUrl_doesNothing) {
    const std::string input = "8 2 1";
    BloomFilter BF(input);
    InitFromFile(BF);

    DeleteURLFromFile(""); 
    SUCCEED(); 
}


TEST(DeleteUrlTest, deleteOneFromMultipleUrls) {
    const std::string input = "8 2 1";
    BloomFilter BF(input);
    InitFromFile(BF);

    BF.addUrl("a.com"); WriteURLtoFile("a.com");
    BF.addUrl("b.com"); WriteURLtoFile("b.com");
    BF.addUrl("c.com"); WriteURLtoFile("c.com");

    DeleteURLFromFile("b.com");

    EXPECT_TRUE(CheckURLInFile("a.com"));
    EXPECT_FALSE(CheckURLInFile("b.com"));
    EXPECT_TRUE(CheckURLInFile("c.com"));
}


TEST(DeleteUrlTest, deleteNonExistingUrl_noEffect) {
    const std::string input = "8 2 1";
    BloomFilter BF(input);
    InitFromFile(BF);

    DeleteURLFromFile("www.unknown.com");

    EXPECT_FALSE(CheckURLInFile("www.unknown.com"));
}
