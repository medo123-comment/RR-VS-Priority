package model;

public class Process {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int remainingTime;
    public int priority;
    public int completionTime;
    public int waitingTime;
    public int turnaroundTime;
    public int responseTime;
    public int startTime = -1;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }
}
