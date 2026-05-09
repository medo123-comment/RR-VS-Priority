package metrics;

import java.util.List;
import model.Process;

public class MetricCalculator {
    public static double avgWaiting;
    public static double avgTurnaround;
    public static double avgResponse;

    public static void calculate(List<Process> processes) {
        if (processes.isEmpty()) return;

        double totalWT = 0, totalTAT = 0, totalRT = 0;

        for (Process p : processes) {
            // 1. TAT = CT - AT
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            
            // 2. WT = TAT - BT
            p.waitingTime = p.turnaroundTime - p.burstTime;
            
            // 3. RT = StartTime - AT
            p.responseTime = p.startTime - p.arrivalTime;

            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;
            totalRT += p.responseTime;
        }

        int n = processes.size();
        avgTurnaround = totalTAT / n;
        avgWaiting = totalWT / n;
        avgResponse = totalRT / n;
    }
}
