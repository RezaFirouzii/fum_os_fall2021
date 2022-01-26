
from task import Task

class Scheduler:
    def __init__(self, file):
        self.file = file
        self.tasks = {}
        self.readyQueue = []
        self.numberOfTotalTasks = 0
        self.readFile()

        # call a process scheduling algorithm to fill the queue
        

    def readFile(self):
        try:
            with open(self.file, 'r') as f:
                self.numberOfTotalTasks = int(f.readline())
                for _ in range(self.numberOfTotalTasks):
                    line = f.readline()
                    line = line.split(' ')
                    task = Task(line[0], line[1], int(line[2]))
                    self.tasks[line[0]] = task
        except FileNotFoundError:
            print("File is not found")
        except IOError:
            print("IO Exception")

    def getReadyQueue(self):
        return self.readyQueue



