cmake_minimum_required(VERSION 3.10)
project(BloomFilterTests)

set(CMAKE_CXX_STANDARD 20)

include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/release-1.12.1.zip
)
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

enable_testing()

# file(GLOB CURRENT_TESTS_FILES "tests/*.cpp")
add_executable(runTests 
  DeleteTests.cpp
  PrintTests.cpp
  CheckTests.cpp
  ../src/server/src/App.cpp
  ../src/server/src/AddUrlCommand.cpp
  ../src/server/src/DeleteUrlCommand.cpp
  ../src/server/src/CheckUrlCommand.cpp
  ../src/server/src/FileFunctions.cpp
  ../src/server/src/BloomFilter.cpp
  ../src/server/src/InputValidity.cpp
)

# add_executable(runTests ${CURRENT_TESTS_FILES})

add_library(testforadd ../src/server/src/BloomFilter.cpp ../src/server/src/FileFunctions.cpp )
target_include_directories(testforadd PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/src)
target_link_libraries(runTests
  gtest_main testforadd
)