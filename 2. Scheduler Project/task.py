
class Task:
    def __init__(self, name, type, executionTime):
        self.name = name
        self.type = type
        self.onCPUTime = 0
        self.executionTime = executionTime
        self.setPriority()

    def setPriority(self):
        if self.type == 'X':
            self.priority = 3
        elif self.type == 'Y':
            self.priority = 2
        elif self.type == 'Z':
            self.priority = 1

    def setState(self, state):
        self.state = state

    def getName(self):
        return self.name

    def getType(self):
        return self.type

    def getPriority(self):
        return self.priority

    def getExecutionTime(self):
        return self.executionTime

    def getState(self):
        return self.state

    def getOnCPUTime(self):
        return self.onCPUTime