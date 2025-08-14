#ifndef INPUTVALIDITY_H
#define INPUTVALIDITY_H

#include <string>
#include <iostream>

using namespace std;

// Checks if the initial input used to define the Bloom Filter is valid.
bool IsFirstInputValid(const string& input);

// Checks if the input representing a command and a URL is valid.
bool IsCommandValid(const string& input);

// Checks if the given port is valid.
bool IsPortValid(int portID);

// Checks if the main input has at least two arguments.
bool IsServCommValid (int argc);

#endif 

