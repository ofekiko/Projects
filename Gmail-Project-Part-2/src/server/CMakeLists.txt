cmake_minimum_required(VERSION 3.10)
project(ServerProject)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# Grab all .cpp source files under server/src recursively
file(GLOB_RECURSE SOURCES "src/*.cpp")

# Define the executable with all collected source files
add_executable(server ${SOURCES})

# Include the header directories
target_include_directories(server PRIVATE ${CMAKE_SOURCE_DIR}/server/src)
