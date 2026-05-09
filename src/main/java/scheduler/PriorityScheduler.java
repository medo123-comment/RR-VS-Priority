package scheduler;

import java.util.*;
import model.GanttData;
import model.Process;

public class PriorityScheduler {
    public static GanttData gantt;

    public static void schedule(List<Process> processes) {
        gantt = new GanttData();
        gantt.times.add(0);
        int completed = 0;
        int time = 0;
        Process lastRunning = null;

        // إعادة ضبط القيم قبل البدء
        for(Process p : processes) {
            p.remainingTime = p.burstTime;
            p.startTime = -1; 
        }

        while (completed < processes.size()) {
            Process best = null;
            for (Process p : processes) {
                if (p.arrivalTime <= time && p.remainingTime > 0) {
                    if (best == null || p.priority < best.priority) {
                        best = p;
                    }
                }
            }

            if (best != null) {
                // تسجيل وقت الاستجابة (أول مرة يلمس فيها المعالج)
                if (best.startTime == -1) {
                    best.startTime = time;
                }

                if (lastRunning != best) {
                    if (lastRunning != null) gantt.times.add(time);
                    gantt.labels.add("P" + best.id);
                    lastRunning = best;
                }

                best.remainingTime--;
                time++;

                if (best.remainingTime == 0) {
                    best.completionTime = time;
                    completed++;
                }
            } else {
                time++;
            }
        }
        gantt.times.add(time);
    }
}
