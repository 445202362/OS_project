import java.util.*;

public class Output {

    /**
     * Method to display all simulation results for the chosen algorithm.
     */
    public static void display(List<PCB> finishedProcesses, String algorithmName) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SIMULATION RESULTS: " + algorithmName.toUpperCase());
        System.out.println("=".repeat(60));

        // 1. Gantt Chart: Execution order and timing
        printGanttChart(finishedProcesses);

        // 2. Process Table: Full details for each PCB
        printProcessTable(finishedProcesses);

        // 3. Performance Metrics: Averages
        printPerformanceMetrics(finishedProcesses);

        // 4. Starvation Status: Only for Priority Scheduling
        if (algorithmName.equalsIgnoreCase("Priority")) {
            printStarvationStatus(finishedProcesses);
        }
    }

    private static void printGanttChart(List<PCB> processes) {
        System.out.println("\n[1] GANTT CHART");
        System.out.println("--------------------------------------------------");
        System.out.print("|");
        for (PCB p : processes) {
            // Shows Process ID and the time interval (Start-End)
            System.out.print(" P" + p.getId() + " (" + p.getStartTime() + "-" + p.getTerminationTime() + ") |");
        }
        System.out.println("\n--------------------------------------------------");
    }

    private static void printProcessTable(List<PCB> processes) {
        System.out.println("\n[2] PROCESS TABLE");
        System.out.printf("%-5s | %-7s | %-7s | %-7s | %-7s | %-10s\n", 
                          "ID", "Burst", "Start", "End", "Wait", "Turnaround");
        System.out.println("-".repeat(55));

        for (PCB p : processes) {
            // Calculating Turnaround = Termination - Arrival(0)
            // Calculating Waiting = Start - Arrival(0)
            System.out.printf("%-5d | %-7d | %-7d | %-7d | %-7d | %-10d\n", 
                              p.getId(), p.getBurstTime(), p.getStartTime(), 
                              p.getTerminationTime(), p.getWaitingTime(), p.getTurnaroundTime());
        }
    }

    private static void printPerformanceMetrics(List<PCB> processes) {
        double totalWait = 0;
        double totalTurnaround = 0;

        for (PCB p : processes) {
            totalWait += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
        }

        // Calculating Averages
        double avgWait = totalWait / processes.size();
        double avgTurnaround = totalTurnaround / processes.size();

        System.out.println("\n[3] PERFORMANCE METRICS");
        System.out.printf("> Average Waiting Time: %.2f ms\n", avgWait);
        System.out.printf("> Average Turnaround Time: %.2f ms\n", avgTurnaround);
    }

    private static void printStarvationStatus(List<PCB> processes) {
        System.out.println("\n[4] STARVATION REPORT (Priority Only)");
        boolean found = false;
        for (PCB p : processes) {
            // Using the flag from your PCB class
            if (p.isStarved()) {
                System.out.println("! Process " + p.getId() + " suffered from starvation.");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No processes suffered from starvation.");
        }
    }
}
