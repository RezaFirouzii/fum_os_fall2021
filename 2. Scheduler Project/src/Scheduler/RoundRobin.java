package Scheduler;
import Process.*;

public class RoundRobin implements Scheduler {
    public static final int QUANTUM = 2;
    @Override
    public void setPriority(Job task) {
        task.setPriority(task.getArrivalTime());
    }

    @Override
    public void setOnProcessorTime(Job task) {
        if (task.getTaskDuration() - task.getExecutedTime() >= QUANTUM) {
            task.setTimePassedOnProcessor(QUANTUM);
        } else {
            task.setTimePassedOnProcessor(task.getTaskDuration() - task.getExecutedTime());
        }


    }
}
