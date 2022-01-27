package Process;

import Scheduler.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ReadyQueue implements Comparator<Job> {
    private Scheduler scheduler;
    private WaitingQueue waitingQueue;
    private List<Job> readyQueue = new ArrayList<>();

    public ReadyQueue(List<Job> readyQueue, Scheduler scheduler) {

        this.scheduler = scheduler;
        for (Job task : readyQueue) {
            scheduler.setPriority(task);
            this.readyQueue.add(task);
        }
    }

    public void addTaskToReadyQueueFromWaitingQueue(Job task) {
        synchronized (this) {
            if (!readyQueue.isEmpty()) {
                task.setPriority(readyQueue.get(0).getPriority() - 1);

            }
            readyQueue.add(task);
            readyQueue.sort(this);
        }
    }

    public void addTaskToReadyQueue(Job task) {
        synchronized (this) {

            if (scheduler instanceof RoundRobin)
                task.setPriority(readyQueue.size());
            else
                scheduler.setPriority(task);

            task.setTimePassedOnProcessor(0);
            readyQueue.add(task);
            readyQueue.sort(this);
            waitingQueue.returnTaskToReadyQueue();
        }
    }

    public Job getTask() {
        synchronized (this) {
            Job task = null;
            if (!readyQueue.isEmpty()) {
                readyQueue.sort(this);
                task = readyQueue.remove(0);
                scheduler.setOnProcessorTime(task);
            }

            return task;
        }
    }

    public List<Job> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue(List<Job> readyQueue) {
        this.readyQueue = readyQueue;
    }


    @Override
    public int compare(Job o1, Job o2) {
        if (o1.getPriority() > o2.getPriority()) {
            return 1;
        } else if (o1.getPriority() == o2.getPriority()) {
            return 0;
        }
        return -1;
    }

    public String toString() {
        synchronized (this) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ready queue: [");
            for (Job task : readyQueue) {
                stringBuilder.append(task.getName())
                        .append(" ");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    public void setWaitingQueue(WaitingQueue waitingQueue) {
        this.waitingQueue = waitingQueue;
    }
}
