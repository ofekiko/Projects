#include <string>
#include <iostream>
#include <fstream>
#include "FileFunctions.h"

using namespace std;

// Function to write an URL into the file.
void WriteURLtoFile(const string& URL) {
    string blacklistFile = "../data/Blacklist.txt";
    ofstream outputFile(blacklistFile, ios::app); // Open the file for writing

    // Check if the file was opened successfully
    if (outputFile.is_open()) {
        outputFile << URL << endl; // Write the URL to the file
        outputFile.close();
    } else {
        // Print an error message if the file could not be opened
        cout << "Unable to open file for writing: " << blacklistFile << endl;
    }
}

// Function to check if an URL exists in the file.
bool CheckURLInFile(const string& URL) {
    string blacklistFile = "../data/Blacklist.txt";
    ifstream inputFile(blacklistFile); // Open the file for reading
    string line;

    // Check if the file was opened successfully
    if (inputFile.is_open()) {
        // Read the file line by line until the end of the file
        while (getline(inputFile, line)) {
            // Compare the current line with the URL.
            if (line == URL) {
                inputFile.close();
                return true;       // Return true if the URL is found in the file.
            }
        }
        inputFile.close();
        return false;      // Return false if the URL was not found in the file.
    } else {
        // Print an error message if the file could not be opened
        cout << "Unable to open file for reading: " << blacklistFile << endl;
        return false;
    }
}

// Function to initialize a Bloom Filter with URLs read from file
void InitFromFile(BloomFilter BF) {
    string blacklistFile = "../data/Blacklist.txt";
    ifstream inputFile(blacklistFile); // Open the file
    string line;

    // Check if the file was opened successfully
    if (inputFile.is_open()) {
        // Read the file line by line until the end of the file
        while (getline(inputFile, line)) {
            BF.addUrl(line); // Add the URL read from the file to the Bloom Filter array.
        }
        inputFile.close();
    } else {
        // Print an error message if the file could not be opened for reading.
        cout << "Unable to open file for reading: " << blacklistFile << endl;
    }
}
// Function to delete an URL and all duplicates of it from the file.
bool DeleteURLFromFile(const string& URL) {
    string blacklistFile = "../data/Blacklist.txt";
    ifstream inputFile(blacklistFile); // Open the file for reading
    vector<string> lines; // Vector for all the lines that are not the URL
    string line;
    bool found = false; // Boolean variable to check if an occurrence of the URL was found.

    // Check if the file was opened successfully
    if (inputFile.is_open()) {
        // Read the file line by line and store only non-matching lines
        while (getline(inputFile, line)) {
            // If the line is not the URL push it to the vector
            if (line != URL) {
                lines.push_back(line);
            }
            // Otherwise ignore the line and change the boolean variable to true
            else {
                found = true;
            }
        }
        // Close the file
        inputFile.close();
         // Open the file for writing
        ofstream outputFile(blacklistFile, ios::trunc);
        // Check if the file was opened successfully
        if (outputFile.is_open()) {
            // Write back only the lines that didn't match
            for (const string& line : lines) {
                outputFile << line << endl;
            }
            outputFile.close();
        } else {
            cout << "Unable to open file for writing: " << blacklistFile << endl;
        }
    } else {
        cout << "Unable to open file for reading: " << blacklistFile << endl;
    }
    return found;
}