import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        String again;

        do {
            // New shared queues for each run
            LinkedList<PCB> jobQueue = new LinkedList<>();
            LinkedList<PCB> readyQueue = new LinkedList<>();

            int choice = 0;

            System.out.println("\nSelect a scheduling algorithm:");
            System.out.println("1. Shortest Job First (SJF)");
            System.out.println("2. Round Robin (q = 5 ms)");
            System.out.println("3. Priority Scheduling (Non-Preemptive)");
            System.out.print("Enter choice (1-3): ");

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

            // Thread 1: reads job.txt and adds PCBs to job queue
            Thread1 thread1 = new Thread1(jobQueue);
            thread1.start();
            thread1.join();

            int totalProcesses = jobQueue.size();

            if (totalProcesses == 0) {
                System.out.println("Error: job.txt is empty or not found.");
                return;
            }

            // Thread 2: loads jobs from job queue to ready queue
            Thread2 thread2 = new Thread2(jobQueue, readyQueue);
            thread2.start();

            // Main thread: runs selected scheduler
            switch (choice) {
                case 1:
                    SJFScheduler sjf = new SJFScheduler(readyQueue, totalProcesses);
                    sjf.run();
                    thread2.join();
                    Output.printOutput(sjf.getGanttChart(), sjf.getFinishedProcesses(), "SJF");
                    break;

                case 2:
                    RoundRobinScheduler rr = new RoundRobinScheduler(readyQueue, totalProcesses);
                    rr.run();
                    thread2.join();
                    Output.printOutput(rr.getGanttChart(), rr.getFinishedProcesses(), "Round Robin");
                    break;

                case 3:
                    PriorityScheduler ps = new PriorityScheduler(readyQueue, totalProcesses);
                    ps.run();
                    thread2.join();
                    Output.printOutput(ps.getGanttChart(), ps.getFinishedProcesses(), "Priority");
                    break;
            }

            System.out.print("\nDo you want to run another algorithm? (yes/no): ");
            again = scanner.nextLine().trim();

        } while (again.equalsIgnoreCase("yes"));

        scanner.close();
        System.out.println("Program ended.");
    }
}
