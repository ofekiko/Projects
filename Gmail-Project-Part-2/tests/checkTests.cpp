#include <gtest/gtest.h>
#include "../src/server/src/BloomFilter.h"
#include "../src/server/src/FileFunctions.h"
#include <string>
#include <sstream>

// check that the check function return true when a url had been added to the bloomfilter
TEST(CheckUrlTest, CheckUrl_ReturnsTrue) {
    const std::string input = "16 8 1 2";
    BloomFilter BF(input);

    std::string url = "www.gmail.com";
    BF.addUrl(url);
    WriteURLtoFile(url);  

    std::string result = BF.checkUrl(url);

    EXPECT_EQ(result, "true true") << "Expected 'true true' but got: " << result;
}
// check that the check function return true when a url had been added to the bloomfilter
TEST(CheckUrlTest, CheckUrl_IsSavedAfterRerun) {
    const std::string input = "16 8 1 2";
    BloomFilter BF(input);
    InitFromFile(BF);

    std::string url = "www.gmail.com";
    std::string result = BF.checkUrl(url);

    EXPECT_EQ(result, "true true");
}

// check that the check function returns false when a url hadn't been added to the bloomfilter
TEST(CheckUrlTest, CheckUrl_ReturnsFalse) {
    const std::string input = "46 8 1 2";
    BloomFilter BF(input);

    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");

    std::string url = "www.walla.com";
    std::string result = BF.checkUrl(url);

    EXPECT_EQ(result, "false");
}

// check that the check function may return a false positive
TEST(CheckUrlTest, CheckUrl_MayBeFalsePositive) {
    const std::string input = "2 1";
    BloomFilter BF(input);

    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");

    std::string output = BF.checkUrl("www.notInTheBloomFilter.com");

    EXPECT_TRUE(output == "false" || output == "true false")
        << "Unexpected result: " << output;
}

// check that the check function returns false for a similar but different URL
TEST(CheckUrlTest, CheckUrl_SimilarButDifferentReturnsFalse) {
    const std::string input = "16 8 1 2";
    BloomFilter BF(input);

    BF.addUrl("www.gmail.com");
    WriteURLtoFile("www.gmail.com");

    std::string url = "www.gmaill.com";
    std::string result = BF.checkUrl(url);

    EXPECT_EQ(result, "false");
}

// check that the check function returns true for a very long URL that had been added to the bloomfilter
TEST(CheckUrlTest, CheckUrl_LongStringStabilityTest) {
    const std::string input = "128 4";
    BloomFilter BF(input);

    std::string longStr(1000, 'a'); 
    BF.addUrl(longStr);
    WriteURLtoFile(longStr);

    std::string result = BF.checkUrl(longStr);

    EXPECT_EQ(result, "true true");
}
