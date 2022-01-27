package Main;

import Process.*;
import Resource.*;
import Scheduler.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static int CORE_NUM = 4;
    public static AtomicBoolean isFinished = new AtomicBoolean(false);
    public static AtomicInteger finishedTasks = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException, IOException {

        var reader = new BufferedReader(new FileReader("test/input2.txt"));
        var R_COUNT = reader.readLine().split(" ");

        var R1 = new Resource("R1", Integer.parseInt(R_COUNT[0]));
        var R2 = new Resource("R2", Integer.parseInt(R_COUNT[1]));
        var R3 = new Resource("R3", Integer.parseInt(R_COUNT[2]));

        var resourceHandler = new ResourceHandler();
        resourceHandler.addResource(R1);
        resourceHandler.addResource(R2);
        resourceHandler.addResource(R3);

        var xTypes = new HashSet<String>();
        xTypes.add(R1.getName());
        xTypes.add(R2.getName());

        var yTypes = new HashSet<String>();
        yTypes.add(R2.getName());
        yTypes.add(R3.getName());

        var zTypes = new HashSet<String>();
        zTypes.add(R3.getName());
        zTypes.add(R1.getName());

        var tasks = new ArrayList<Job>();
        var tasks_number = Integer.parseInt(reader.readLine());
        for (int i = 0; i < tasks_number; i++) {
            var line = reader.readLine().split(" ");
            var task = new Job(line[0], line[1], Integer.parseInt(line[2]), i);
            switch (line[1]) {
                case "X":
                    task.setResources(xTypes);
                    break;
                case "Y":
                    task.setResources(yTypes);
                    break;
                default:
                    task.setResources(zTypes);
            }
            tasks.add(task);
        }
        reader.close();


        var processors = new ArrayList<Processor>();
        // different algorithm could be applied by using different algo classes
        var scheduler = new ShortestJobFirst();
//        var scheduler = new FirstComeFirstServe();
//        var scheduler = new RoundRobin();

        var readyQueue = new ReadyQueue(tasks, scheduler);
        var waitingQueue = new WaitingQueue(new ArrayList<>(), resourceHandler, readyQueue);
        readyQueue.setWaitingQueue(waitingQueue);

        var loggerSignals = new ArrayList<AtomicBoolean>();
        var locks = new ArrayList<ReentrantLock>();
        var conditions = new ArrayList<Condition>();

        for (int i = 0; i < CORE_NUM; i++) {
            var loggerSignal = new AtomicBoolean(false);
            var lock = new ReentrantLock();
            var condition = lock.newCondition();

            processors.add(new Processor(lock, condition, loggerSignal, readyQueue, waitingQueue, resourceHandler));
            loggerSignals.add(loggerSignal);
            conditions.add(condition);
            locks.add(lock);
        }

        for (int i = 0; i < CORE_NUM; i++)
            new Thread(processors.get(i)).start();

        var time = 1;
        while (!isFinished.get()) {

            // wait for processors to finish a cycle
            while (!loggerSignals.get(0).get() || !loggerSignals.get(1).get() || !loggerSignals.get(2).get() || !loggerSignals.get(3).get())
            {}
            {}

            // printing system status
            System.out.println("Time: " + time++);
            System.out.println(resourceHandler);
            System.out.println(readyQueue);
            System.out.println(waitingQueue);
            for (int i = 0; i < processors.size(); i++)
                System.out.println("CPU" + (i+1) + ": " + (processors.get(i).getTask() == null ? "Idle" : processors.get(i).getTask().getName()));
            System.out.println("-------------------------------");

            waitingQueue.returnTaskToReadyQueue();
            waitingQueue.checkIfTaskIsStarving(processors);

            Thread.sleep(50);
            for (int i = 0; i < CORE_NUM; i++) loggerSignals.get(i).set(false);
            if (finishedTasks.get() == tasks.size()) isFinished.set(true);
            for (int i = 0; i < CORE_NUM; i++) {
                locks.get(i).lock();
                conditions.get(i).signalAll();
                locks.get(i).unlock();
            }
        }
    }
}
