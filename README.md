# Juice Bottler Lab

## What is this?
With this first lab in our OS course, we are tasked with designing a Juice Bottling Plant to help us better understand
multithreading. In this plant, there are workers who can be assigned one of five jobs:
- Fetcher
- Peeler
- Squeezer
- Bottler
- Processor  

Once the program is ran, and the Juice Bottling Plant begins, the workers are given a title based on their job, and
an orange is spawned in, beginning the juicing process.



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
- After it has built, run the following to begin the program `ant run`