# Juice Bottler Lab

## What is this?
With this first lab in our OS course, we are tasked with designing a Juice Bottling Plant to help us better understand
multithreading by way of Data Parallelization with Plant objects, and Task Parallelization with Worker threads.  

Inside each Plant, there are workers each of which are assigned one of five jobs:
- Fetcher
- Peeler
- Squeezer
- Bottler
- Processor  

Once the program is run, and the Plant begins, each of the workers are given a 
title based on their job, spawning in an  orange, beginning the juicing process.  

## Data-Parallelization with Plants:
Each Plant instance manages its own set of queues and workers. 
By  ensuring each Plant gets its own separate queues, we maintain data-parallelism. 
The static utility methods (e.g., sendOranges() and getOranges())operate on 
the queues passed into them. They don't store or manage any global states,
so each Plant's work remains independent of one another.

## Task-Parallelization with Workers:
Workers continue to execute their processing tasks concurrently. 
When they call the static methods from Plant, they're simply using thread-safe 
operations (e.g., using ConcurrentLinkedQueue.poll() and .add()) to fetch and 
send work. This keeps the task-parallel structure intact.

## Thread Safety:
As long as the static methods operate on thread-safe data structures 
(like ConcurrentLinkedQueue), the operations remain safe even when called 
by multiple threads concurrently.


## Requirements:
### Apache Ant:
Apache Ant is a build tool to quickly run java files from the command line.
Instructions for install found at: https://ant.apache.org/manual/install.html  
To ensure you have Ant installed and properly running use the following command  
to check your installation version: `ant -v` or `ant --version`


## How to Run:
- Download the files
- Navigate to the directory you have the files, and run
the following Ant build command `ant build`
- After it has built, run the following to begin the program `ant`