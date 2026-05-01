import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Shared queues
        LinkedList<PCB> jobQueue = new LinkedList<>();
        LinkedList<PCB> readyQueue = new LinkedList<>();

        // User choice
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        System.out.println("Select a scheduling algorithm:");
        System.out.println("1. Shortest Job First (SJF)");
        System.out.println("2. Round Robin (q = 5 ms)");
        System.out.println("3. Priority Scheduling (Non-Preemptive)");
        System.out.print("Enter choice (1-3): ");

        // Validate input
        while (choice < 1 || choice > 3) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());

                if (choice < 1 || choice > 3) {
                    System.out.print("Invalid. Enter 1, 2, or 3: ");
                }

            } catch (NumberFormatException e) {
                System.out.print("Invalid. Enter 1, 2, or 3: ");
            }
        }

        scanner.close();

        // Read jobs from job.txt
        Thread1 thread1 = new Thread1(jobQueue);
        thread1.start();

        if (!waitForThread(thread1, "Thread 1")) {
            return;
        }

        // Total processes
        int totalProcesses = jobQueue.size();

        // Empty file check
        if (totalProcesses == 0) {
            System.out.println("Error: job.txt is empty or not found.");
            return;
        }

        // Load jobs to ready queue
        Thread2 thread2 = new Thread2(jobQueue, readyQueue);
        thread2.start();

        // Run selected scheduler
        switch (choice) {

            case 1: {
                // SJF
                SJFScheduler sjf = new SJFScheduler(readyQueue, totalProcesses);
                sjf.run();

                waitForThread(thread2, "Thread 2");

                Output.printOutput(sjf.getGanttChart(),sjf.getFinishedProcesses(),"SJF");
                break;
            }

            case 2: {
                // Round Robin
                RoundRobinScheduler rr = new RoundRobinScheduler(readyQueue, totalProcesses);
                rr.run();

                waitForThread(thread2, "Thread 2");

                Output.printOutput(rr.getGanttChart(),rr.getFinishedProcesses(),"Round Robin");
                break;
            }

            case 3: {
                // Priority
                PriorityScheduler ps = new PriorityScheduler(readyQueue, totalProcesses);
                ps.run();

                waitForThread(thread2, "Thread 2");

                Output.printOutput(ps.getGanttChart(),ps.getFinishedProcesses(),"Priority");
                break;
            }
        }
    }

    // Wait for thread completion
    private static boolean waitForThread(Thread thread, String threadName) {
        try {
            thread.join();
            return true;
        } catch (InterruptedException e) {
            System.out.println("Main Thread interrupted while waiting for " + threadName + ".");
            return false;
        }
    }
}
