package Process;

import Resource.ResourceHandler;

import java.util.*;

public class WaitingQueue implements Comparator<Job> {
    private ReadyQueue readyQueue;
    private List<Job> waitingQueue;
    private ResourceHandler resourceManager;

    public WaitingQueue(List<Job> waitingQueue, ResourceHandler resourceManager, ReadyQueue readyQueue) {
        this.waitingQueue = waitingQueue;
        this.resourceManager = resourceManager;
        this.readyQueue = readyQueue;
    }



    public void addToWaitingQueue(Job task) {
        synchronized (this) {
            waitingQueue.add(task);
            waitingQueue.sort(this);
        }
    }

    public void checkIfTaskIsStarving(List<Processor> processors) {
        synchronized (this) {
            Set<String> contextSwitchedProcessors = new HashSet<>();
            Set<Job> removedTasksFromWaiting = new HashSet<>();
            for (Job task : waitingQueue) {
                if (task.getWaitedTime() >= task.getTaskDuration() * 2) {
                    for (Processor processor : processors) {
                        if (processor.getTask() != null && !contextSwitchedProcessors.contains(processor.toString())) {
                            Job taskToBeRemovedFromProcessor = processor.getTask();
                            if (processor.getTask().getResources().containsAll(task.getResources())) {
                                System.out.println("Starvation: " + task.getName() + ", Replace: " + taskToBeRemovedFromProcessor.getName());
                                processor.setTask(task);
                                readyQueue.addTaskToReadyQueueFromWaitingQueue(taskToBeRemovedFromProcessor);
                                contextSwitchedProcessors.add(processor.toString());
                                removedTasksFromWaiting.add(task);
                                task.setWaitedTime(0);
                                break;
                            } else if (resourceManager.hasReleasedResource(processor.getTask().getResources(), task.getResources())) {
                                System.out.println("Starvation: " + task.getName() + ", Replace: " + taskToBeRemovedFromProcessor.getName());
                                processor.setTask(task);
                                resourceManager.releaseResource(taskToBeRemovedFromProcessor.getResources());
                                resourceManager.getResource(task.getResources());
                                readyQueue.addTaskToReadyQueueFromWaitingQueue(taskToBeRemovedFromProcessor);
                                contextSwitchedProcessors.add(processor.toString());
                                removedTasksFromWaiting.add(task);
                                task.setWaitedTime(0);
                            }
                        }
                    }

                }
            }

            for (Job task : removedTasksFromWaiting) {
                waitingQueue.remove(task);
            }
            waitingQueue.sort(this);

        }
    }

    public void returnTaskToReadyQueue() {
        synchronized (this) {
            List<Job> removedTasks = new ArrayList<>();
            for (Job task : waitingQueue) {
                task.setWaitedTime(task.getWaitedTime() + 1);
                if (resourceManager.hasResource(task.getResources())) {
                    readyQueue.addTaskToReadyQueueFromWaitingQueue(task);
//                    waitingQueue.remove(task);
                    removedTasks.add(task);
//                    Collections.sort(waitingQueue);
                }
            }

            while (!removedTasks.isEmpty()) {
                waitingQueue.remove(removedTasks.remove(0));
            }
            waitingQueue.sort(this);
        }
    }

    public String toString() {
        synchronized (this) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("waiting queue : [");
            for (Job task : waitingQueue) {
                stringBuilder.append(task.getName())
                        .append(" ");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    @Override
    public int compare(Job o1, Job o2) {
        double o1WaitingRate = o1.getWaitedTime() / (o1.getTaskDuration() * 1.0);
        double o2WaitingRate = o2.getWaitedTime() / (o2.getTaskDuration() * 1.0);

        if (o1WaitingRate > o2WaitingRate) {
            return -1;
        } else if (o1WaitingRate == o2WaitingRate) {
            return 0;
        }
        return 1;
    }
}
