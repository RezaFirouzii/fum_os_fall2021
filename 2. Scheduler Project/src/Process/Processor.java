package Process;
import Resource.ResourceHandler;
import lombok.SneakyThrows;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicBoolean;
import Main.*;

public class Processor implements Runnable {
    private Job task;
    private ReentrantLock lock;
    private Condition processCondition;
    private AtomicBoolean loggerSignal;
    private ReadyQueue readyQueue;
    private WaitingQueue waitingQueue;
    private ResourceHandler resourceManager;

    public Processor(ReentrantLock lock, Condition ProcessCondition, AtomicBoolean loggerSignal, ReadyQueue readyQueue, WaitingQueue waitingQueue, ResourceHandler resourceManager) {
        this.lock = lock;
        this.processCondition = ProcessCondition;
        this.loggerSignal = loggerSignal;
        this.readyQueue = readyQueue;
        this.waitingQueue = waitingQueue;
        this.resourceManager = resourceManager;
    }

    public Job getTask() {
        return task;
    }

    public void setTask(Job task) {
        this.task = task;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!Main.isFinished.get()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (task == null) {
                task = readyQueue.getTask();
                if (task != null && !resourceManager.getResource(task.getResources())) {
                    waitingQueue.addToWaitingQueue(task);
                    task = null;
                }
            }


            loggerSignal.set(true);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            processCondition.awaitUninterruptibly();
            lock.unlock();


            if (task != null) {
                task.setExecutedTime(task.getExecutedTime() + 1);
                task.setTimePassedOnProcessor(task.getTimePassedOnProcessor() - 1);
                if (task.getTimePassedOnProcessor() == 0) {
                    resourceManager.releaseResource(task.getResources());
                    if (task.getExecutedTime() == task.getTaskDuration()) {
                        Main.finishedTasks.incrementAndGet();
                    } else {
                        readyQueue.addTaskToReadyQueue(task);
                    }

                    task = null;
                }

            }
        }
    }
}
