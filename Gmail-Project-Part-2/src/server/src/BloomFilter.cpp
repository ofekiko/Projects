#include "BloomFilter.h"
#include "FileFunctions.h"
#include <sstream>
#include <functional>

using namespace std;

// Constructor of the bloom filter.
BloomFilter::BloomFilter(const string& input) {
    stringstream ss(input);
    int number;

    // Create a stringstream to split the user input into numbers, where each number (after the first) represents a hash function.
    while (ss >> number) {
        hashFunctions.push_back(number);
    }

    // The first number represents the size of the hash table.
    sizeOfArr = hashFunctions.front(); 

    // Remove the first number to leave only the values representing hash functions.       
    hashFunctions.erase(hashFunctions.begin());  

    // Initiliaze the array (hash table) with zeros.
    ptrToArr = new int[sizeOfArr]();             
}

// The function takes a number n and a string, applies the std::hash function to the string n times, and returns the last result modulo sizeOfArr.
int BloomFilter::repeatHashOnString(int n, const string& input) {
    hash<string> hasher;
    string current = input;
    size_t hashValue = 0;

    // Exectute the hash function n times on the string input.
    for (int i = 0; i < n; ++i) {
        hashValue = hasher(current);
        current = to_string(hashValue);
    }

    return static_cast<int>(hashValue % sizeOfArr);
}

// This function finds the indices in the hash table that represent the given URL, based on the hash function values provided by the user.
vector<int> BloomFilter::hashResultsVector(const string& url){
    // Create a vector of integers that will contain the indices in the hash table the URL maps to.
    vector <int> indexOfUrl;

    for (int number : hashFunctions) {
        // Running over the values from hashFunctions and return the index of the hash table that needs to be on.
        int result = repeatHashOnString(number, url);

        // Insert the reutrneing value into the vector.
        indexOfUrl.push_back(result);
    }
    return indexOfUrl;
}

// Returns the size of the hash table, as defined by the user.
int BloomFilter::getSize() const {
    return sizeOfArr;
}

// This function receives a URL and adds it to the blacklist.
void BloomFilter::addUrl(const string& url) {

    // Find the indices that represent the URL in the hash table.
    vector <int> indexOfUrl = hashResultsVector(url);

    // Iterate over the indices and set the corresponding positions in the hash table to 1.
    for (int index : indexOfUrl) {
        ptrToArr[index] = 1;
    }
}

// This function receives a URL and checks whether it is in the blacklist.
string BloomFilter::checkUrl(const string& url) {
    // Find the indices that represent the URL in the hash table.
    vector <int> indexOfUrl = hashResultsVector(url);
    string output;

    // Check if all the corresponding bits in the hash table are set.
    for (int index : indexOfUrl) {

        // If even one bit is not set, the URL is definitely not in the blacklist.
        if (ptrToArr[index] == 0) {
            output = "false";
            return output;
        }
    }

    // If all bits are set, the URL might be in the blacklist (could be a false positive).
    output = "true ";

    // Check the actual blacklist file to confirm.
    if (CheckURLInFile(url)) {
        output += "true";
    }

    // If the URL is not in the file, it's a false positive.
    else {
        output += "false";
    }

    return output;


}

