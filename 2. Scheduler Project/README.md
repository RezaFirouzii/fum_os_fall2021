
# Scheduler
Design of a scheduler for a 4 processor system.
## Installation

1. Create a java project
2. Copy both src and test directories
3. Add extra library to project (lombok.jar)
4. Run Main.java located in the Main package

## Documentation
In this project we use 3 different process scheduling algorithms (SJF, RR, FCFS) in order to simulate a scheduler with limited amount of resources per each processor.

Project is consists of 4 packages which are Main, Process, Resource and Scheduler.

* The Main class is located in the Main package as it's the start of program and input address of the test is specified in the main method.
* The Process package contains 4 classes which are Job, Processor, ReadyQueue and WaitingQueue. 
* The Resource package includes 2 important classes which are Resource and ResourceHandler.
* The Scheduler package is the most important one with an interface class called Scheduler and 3 main algorithms classes implementing interface class and are all used with different calls to Job. 
## Features

- RoundRobin Algorithm
- Shortest Job First Algorithm
- First Come First Serve Algorithm
- Complete "race condition" handling using Reentrant Lock and Synchronization.
- Complete starvation handling by switching conflicting Jobs in the waiting queue.
    
## NOT supported
- HRRN Algorithm
- Multilevel Feedback Queue


  
## Author

- [Reza Firouzi](https://www.github.com/rezafirouzii)

  