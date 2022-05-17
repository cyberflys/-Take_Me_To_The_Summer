cmake_minimum_required(VERSION 3.4.1)

add_library(
        lowlevel
        SHARED
        lowlevel.cpp)
find_library(
        lowlevel
        log)
target_link_libraries(
        lowlevel
        ${log-lib})