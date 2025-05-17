#include <vector>
#include <string>
#include <iostream>
#include <sstream>
#include <regex>
#include "InputValidity.h"


using namespace std;

bool IsFirstInputValid(const string& input){
    stringstream ss(input);
    int number;
    int counter = 0;

    while (ss >> number) {
        counter++;
        if (number <= 0) {
            return false; // if negative or 0 return false
        ss >> ws; // removes spaces
        }
    }
    if (counter <= 1){
        return false;
    }
    // checks if something was left
    if (ss.fail() && !ss.eof()) {
        return false; // found illegal chars
    }

    return true; // everything went through so return true
}


bool IsCommandValid(const string& input) {
    // split the input into a stream by spaces.
    istringstream inputSplit = istringstream(input);

    string command;
    string url;

    // find the first and the second strings of inputSplit that supposed to be 1 or 2 and address of url.
    if (!(inputSplit >> command) || !(inputSplit >> url)) {
        return false;
    }

    string extraString;
    
    // check if the user's input contain more than 2 strings witch is invalid input.
    if (inputSplit >> extraString) {
        return false;
    }

    // check if the first string of the input that represents the command is valid.
    if (command !="POST" && command !="DELETE" && command !="GET") {
        return false;
    }

    // check if the second string of the input that represents the url address is valid.
    regex urlCheck(R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)");
    return regex_match(url, urlCheck);
}


bool IsPortValid(int portID) {
    if (portID < 1024 || portID > 65535) {
        return false;
    }    

    else {
        return true;
    }
}


bool IsServCommValid(int argc) {
    if (argc < 2) {
        return false;
    }    

    else {
        return true;
    }
}


