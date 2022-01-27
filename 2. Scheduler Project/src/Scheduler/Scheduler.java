package Scheduler;
import Process.*;

public interface Scheduler {
    void setPriority(Job task);
    void setOnProcessorTime(Job task);
}
