package os;

import java.util.LinkedList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		Scanner scanner = new Scanner(System.in);
		String again = "";

		do {
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

			Thread1 thread1 = new Thread1(jobQueue);
			thread1.start();
			thread1.join();

			int totalProcesses = jobQueue.size();

			if (totalProcesses == 0) {
				System.out.println("Error: job.txt is empty or not found.");
				System.out.print("\nDo you want to run another algorithm? (yes/no): ");
				again = scanner.nextLine().trim();
				continue;
			}

			MemoryManager memoryManager = new MemoryManager();
			Thread2 thread2 = new Thread2(jobQueue, readyQueue, memoryManager, totalProcesses);
			thread2.start();

			Thread.sleep(50);

			switch (choice) {
			case 1: {
				SJFScheduler sjf = new SJFScheduler(readyQueue, totalProcesses, memoryManager);
				sjf.run();
				thread2.join();
				Output.printOutput(sjf.getGanttChart(), sjf.getFinishedProcesses(), "SJF");
				break;
			}
			case 2: {
				RoundRobinScheduler rr = new RoundRobinScheduler(readyQueue, totalProcesses, memoryManager);
				rr.run();
				thread2.join();
				Output.printOutput(rr.getGanttChart(), rr.getFinishedProcesses(), "Round Robin");
				break;
			}
			case 3: {
				PriorityScheduler ps = new PriorityScheduler(readyQueue, totalProcesses, memoryManager);
				ps.run();
				thread2.join();
				Output.printOutput(ps.getGanttChart(), ps.getFinishedProcesses(), "Priority");
				break;
			}
			}

			System.out.print("\nDo you want to run another algorithm? (yes/no): ");
			again = scanner.nextLine().trim();

		} while (again.equalsIgnoreCase("yes"));

		scanner.close();
		System.out.println("Program ended.");
	}
}