import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;

public class Thread1 extends Thread {

    // Shared queue and file name
    private List<PCB> jobQueue;
    private final String FILE_NAME = "job.txt";

    // Counts how many processes were successfully read
    private int totalProcesses = 0;

    // Tells Thread2 that Thread1 has finished reading
    private boolean finishedReading = false;

    // Constructor to receive the queue from Main
    public Thread1(List<PCB> jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        int order = 0; // To keep arrival order

        try {
            File file = new File(FILE_NAME);
            Scanner reader = new Scanner(file);

            System.out.println("Thread 1: Reading processes from " + FILE_NAME);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split line by : and ; 
                // Format: ID:Burst:Priority;Memory
                String[] parts = line.split("[:;]");

                int id = Integer.parseInt(parts[0].trim());
                int burst = Integer.parseInt(parts[1].trim());
                int priority = Integer.parseInt(parts[2].trim());
                int memory = Integer.parseInt(parts[3].trim());

                // Create PCB object
                PCB process = new PCB(id, burst, priority, memory, order++);

                // Add to job queue safely
                synchronized (jobQueue) {
                    jobQueue.add(process);
                    totalProcesses++;
                }
            }

            reader.close();
            System.out.println("Thread 1: Loading complete. Terminating...");

        } catch (FileNotFoundException e) {
            System.err.println("Thread 1 Error: job.txt not found!");

        } catch (Exception e) {
            System.err.println("Thread 1 Error: " + e.getMessage());

        } finally {
            // Very important: Thread2 must know Thread1 is done
            finishedReading = true;
        }
    }

    public int getTotalProcesses() {
        return totalProcesses;
    }

    public boolean isFinishedReading() {
        return finishedReading;
    }
}
