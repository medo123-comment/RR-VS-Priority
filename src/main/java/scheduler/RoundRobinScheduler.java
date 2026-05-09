package scheduler;

import java.util.*;
import model.Process;
import model.GanttData;

public class RoundRobinScheduler {
    public static GanttData gantt;

    public static void schedule(List<Process> processes, int quantum) {
        gantt = new GanttData();
        gantt.times.add(0);
        
        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        int arrivedCount = 0;
        int completedCount = 0;
        int n = processes.size();

        
        for(Process p : processes) {
            p.remainingTime = p.burstTime;
            p.startTime = -1;
        }

       
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completedCount < n) {
           
            while (arrivedCount < n && processes.get(arrivedCount).arrivalTime <= time) {
                queue.add(processes.get(arrivedCount));
                arrivedCount++;
            }

            if (queue.isEmpty()) {
                time++;
                continue;
            }

            Process current = queue.poll();

          
            if (current.startTime == -1) {
                current.startTime = time;
            }

            gantt.labels.add("P" + current.id);
            
            int execTime = Math.min(quantum, current.remainingTime);
            time += execTime;
            current.remainingTime -= execTime;
            
            gantt.times.add(time);

            
            while (arrivedCount < n && processes.get(arrivedCount).arrivalTime <= time) {
                queue.add(processes.get(arrivedCount));
                arrivedCount++;
            }

            if (current.remainingTime > 0) {
                queue.add(current);
            } else {
                current.completionTime = time;
                completedCount++;
            }
        }
    }
}
