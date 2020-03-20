# pingtest

## Introduction

This C++ program was intended to have a basic understanding of the ping time between 2 hosts, or on localhost, so as to have a gross estimate of them and work from there.

## Build & Run

### Build

This is a CMake project. CMake 3.10 or higher is required

```
mkdir build
cd build
cmake ..
cmake --build .
```

### Run

Usage : 

```pingtest [target ip]```

Root priviledge may be required on certain Unix OS. 

## Initial results :

```
Mean time over 1021 iterations : 127747 ns = 127.747 us = 0.127747 ms = 0.000127747 s
Mean distance over 1021 iterations : 19.1488 km

Max time over 1021 iterations : 276235 ns = 276.235 us = 0.276235 ms = 0.000276235 s
Max distance over 1021 iterations :  41.4066 km

Min time over 1021 iterations : 13439 ns = 13.439 us = 0.013439 ms = 1.3439e-05 s
Min distance over 1021 iterations : 2.01446 km

Equivalent distance between max and min : 39.3921 km
```
