#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H

#include <iostream>
#include <sstream>
#include <vector>
#include <string>
#include <functional>

using namespace std;

class BloomFilter {
    private:
        friend class HashFunctionTest_RepeatHash_IsDeterministic_Test;
        friend class HashFunctionTest_RepeatHash_ChangesWithRepetitionCount_Test;
        friend class HashFunctionTest_RepeatHash_ChangesWithInput_Test;
        friend class HashFunctionTest_RepeatHash_CorrectRange_Test;
        int sizeOfArr;
        int* ptrToArr;
        vector<int> hashFunctions;

        // The function takes a number n and a string, applies the std::hash function to the string n times, and returns the last result modulo sizeOfArr.
        int repeatHashOnString(int n, const string& input);

        // This function finds the indices in the hash table that represent the given URL, based on the hash function values provided by the user.
        vector<int> hashResultsVector (const string& url);

    public:
        // Create the Bloom Filter by the input of the user.
        BloomFilter(const string& input);

        // Returns the size of hash table.
        int getSize() const;  

        // Add an URL to the blacklist. 
        void addUrl(const string& url); 
        
        // Check if an URL in the blacklist. 
        string checkUrl(const string& url);
};

#endif