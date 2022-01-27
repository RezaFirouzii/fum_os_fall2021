package Process;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Job {

    private String name;
    private String type;

    private int taskDuration;
    private int arrivalTime;
    private int executedTime;

    private int priority;

    private Set<String> resources = new HashSet<String>();
    // ready: 0, waiting: 1, running: 2
    private int state = 0;
    private int waitedTime = 0;
    private int timePassedOnProcessor = 0;

    public Job(String name, String type, int taskDuration, int arrivalTime, int executedTime, int priority, Set<String> resources) {
        this.name = name;
        this.type = type;
        this.taskDuration = taskDuration;
        this.arrivalTime = arrivalTime;
        this.executedTime = executedTime;
        this.priority = priority;
        this.resources = resources;
    }

    public Job(String name, String type, int taskDuration, int arrivalTime) {
        this.name = name;
        this.type = type;
        this.taskDuration = taskDuration;
        this.arrivalTime = arrivalTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(int taskDuration) {
        this.taskDuration = taskDuration;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getExecutedTime() {
        return executedTime;
    }

    public void setExecutedTime(int executedTime) {
        this.executedTime = executedTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<String> getResources() {
        return resources;
    }

    public void setResources(Set<String> resources) {
        this.resources = resources;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getWaitedTime() {
        return waitedTime;
    }

    public void setWaitedTime(int waitedTime) {
        this.waitedTime = waitedTime;
    }

    public int getTimePassedOnProcessor() {
        return timePassedOnProcessor;
    }

    public void setTimePassedOnProcessor(int timePassedOnProcessor) {
        this.timePassedOnProcessor = timePassedOnProcessor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job task = (Job) o;
        return Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
