import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // Shared queues
        LinkedList<PCB> jobQueue   = new LinkedList<>();
        LinkedList<PCB> readyQueue = new LinkedList<>();

        // User chooses scheduling algorithm
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        System.out.println("Select a scheduling algorithm:");
        System.out.println("1. Shortest Job First (SJF)");
        System.out.println("2. Round Robin (q = 5 ms)");
        System.out.println("3. Priority Scheduling (Non-Preemptive)");
        System.out.print("Enter choice (1-3): ");
        while (choice < 1 || choice > 3) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > 3)
                    System.out.print("Invalid. Enter 1, 2, or 3: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid. Enter 1, 2, or 3: ");
            }
        }
        scanner.close();

        // Thread 1: reads job.txt, creates PCBs, adds to job queue, then terminates
        Thread1 thread1 = new Thread1(jobQueue);
        thread1.start();
        thread1.join();

        // Get total processes from job queue after Thread 1 finishes
        int totalProcesses = jobQueue.size();
        if (totalProcesses == 0) {
            System.out.println("Error: job.txt is empty or not found.");
            return;
        }

        // Thread 2: loads jobs from job queue to ready queue (memory-aware), runs concurrently with main
        Thread2 thread2 = new Thread2(jobQueue, readyQueue);
        thread2.start();

        // Main thread: runs the chosen scheduling algorithm
        switch (choice) {
            case 1: {
                SJFScheduler sjf = new SJFScheduler(readyQueue, totalProcesses);
                sjf.run();
                thread2.join();
                Output.printOutput(sjf.getGanttChart(), sjf.getFinishedProcesses(), "SJF");
                break;
            }
            case 2: {
                RoundRobinScheduler rr = new RoundRobinScheduler(readyQueue, totalProcesses);
                rr.run();
                thread2.join();
                Output.printOutput(rr.getGanttChart(), rr.getFinishedProcesses(), "Round Robin");
                break;
            }
            case 3: {
                PriorityScheduler ps = new PriorityScheduler(readyQueue, totalProcesses);
                ps.run();
                thread2.join();
                Output.printOutput(ps.getGanttChart(), ps.getFinishedProcesses(), "Priority");
                break;
            }
        }
    }
}
