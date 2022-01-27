package Scheduler;
import Process.*;

public class FirstComeFirstServe implements Scheduler {
    @Override
    public void setPriority(Job task) {
        task.setPriority(task.getArrivalTime());
    }

    @Override
    public void setOnProcessorTime(Job task) {
        task.setTimePassedOnProcessor(task.getTaskDuration() - task.getExecutedTime());
    }
}
