#include <string>
#include <iostream>
#include <fstream>
#include "BloomFilter.h"

using namespace std;

void WriteURLtoFile(const string& URL);
bool CheckURLInFile (const string& URL);
void InitFromFile (BloomFilter BF);
bool DeleteURLFromFile (const string& URL);