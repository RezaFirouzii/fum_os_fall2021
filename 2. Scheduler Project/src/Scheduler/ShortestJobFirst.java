package Scheduler;
import Process.*;

public class ShortestJobFirst implements Scheduler {

    @Override
    public void setPriority(Job task) {
        task.setPriority(task.getTaskDuration());
    }

    @Override
    public void setOnProcessorTime(Job task) {
        task.setTimePassedOnProcessor(task.getTaskDuration() - task.getExecutedTime());
    }
}
