# Juice Bottler Lab

## What is this?

With this first lab in our OS course, we are tasked with designing a Juice Bottling Plant to help us better understand
multithreading by way of Data Parallelization with Plant objects, and Task Parallelization with Worker threads.
Essentially, the Plant sets up the factory, the Workers perform the tasks in parallel, and the Orange objects
are the data being processed. (More in depth explanation down below)

## Requirements:

### Apache Ant:

Apache Ant is a build tool to quickly run java files from the command line.
Instructions for install found at: https://ant.apache.org/manual/install.html  
To ensure you have Ant installed and properly running use the following command  
to check your installation version: `ant -v` or `ant --version`

## How to Run:

- Download the files
- Navigate to the directory you have the files, and run
  the following Ant build command `ant`
- After it has built, run the following to begin the program `ant run`

# Explanation of the lab

## Technical Overview

The Plant class acts as the central control for this orange juice factory.
It executes the entire process, from receiving oranges to bottling the juice.
It uses multithreading to achieve both data and task parallelism.
Data parallelism is achieved by having multiple Plant instances running concurrently,
each processing a separate set of oranges and workers. Task parallelism is achieved within each
Plant by creating multiple Worker threads, each responsible for a specific stage of
the process (fetching, peeling, squeezing, bottling). These workers operate concurrently
on different oranges, thus speeding up the overall time processing. The Orange class represents
the oranges themselves as they move through different States (Fetched, Peeled, etc.) when
they are processed by the workers. The Plant class manages the flow of oranges between
workers using concurrent queues, which are thread-safe data structures that allow the
workers to communicate and pass oranges along to the next processing stage.

## Data-Parallelization with Plants:

Each Plant instance manages its own set of queues and workers.
By ensuring each Plant gets its own separate queues, we maintain data-parallelism.
The static utility methods (e.g., `sendOranges()` and `getOranges()`) operate on
the queues passed into them. They don't store or manage any global states,
so each Plant's work remains independent of one another.

## Task-Parallelization with Workers:

Workers continue to execute their processing tasks concurrently.
When they call the static methods from Plant, they're simply using thread-safe
operations (e.g., using `ConcurrentLinkedQueue.poll()` and `.add()`) to fetch and
send work. This keeps the task-parallel structure intact.

## Thread Safety:

As long as the static methods operate on the thread-safe data structures, the
operations remain safe even when called by multiple threads concurrently.

# Sources:

- ChatGPT / GoogleGemini:
    - Used for helping me formulate better comments, though most of them way too wordy. Did help me improve my Javadoc
      game though
- I have used others' work as both inspiration and guidance in the process of making my own. They include:
    - Rakaiah Grende (https://github.com/rgrende/CS-410_JuiceBottlerLab1/tree/main)
    - Hank Rugg (https://github.com/hankrugg/JuiceBottler)
    - Dustin Gardner (https://github.com/dustgard/CS-410_JuiceBottler_Lab)
    - and Aidan Scott (https://github.com/cwdatlas/OrangePlant)
