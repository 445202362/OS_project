import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Thread2 extends Thread {

    private final LinkedList<PCB> jobQueue;
    private final LinkedList<PCB> readyQueue;
    private final Thread1 thread1;

    private static final int TOTAL_MEMORY = 2048;
    private int usedMemory = 0;
    private final Set<Integer> freedProcesses = new HashSet<>();

    public Thread2(LinkedList<PCB> jobQueue, LinkedList<PCB> readyQueue, Thread1 thread1) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.thread1 = thread1;
    }

    @Override
    public void run() {
        System.out.println("Thread 2: Started.");

        while (true) {

            PCB nextJob = null;

            synchronized (jobQueue) {
                if (!jobQueue.isEmpty()) {
                    nextJob = jobQueue.getFirst();
                }
            }

            if (nextJob != null) {
                int required = nextJob.getRequiredMemory();

                if (usedMemory + required <= TOTAL_MEMORY) {

                    synchronized (jobQueue) {
                        if (!jobQueue.isEmpty() && jobQueue.getFirst() == nextJob) {
                            jobQueue.removeFirst();
                        } else {
                            continue;
                        }
                    }

                    usedMemory += required;
                    nextJob.setState(PCB.State.READY);

                    synchronized (readyQueue) {
                        readyQueue.addLast(nextJob);
                        System.out.println("Thread 2: Process " + nextJob.getId()
                                + " admitted. Memory used: " + usedMemory + "/" + TOTAL_MEMORY + " MB");
                    }

                } else {
                    System.out.println("Thread 2: Not enough memory for Process "
                            + nextJob.getId() + ". Waiting...");
                }
            }

            synchronized (readyQueue) {
                for (PCB p : readyQueue) {
                    if (p.getState() == PCB.State.TERMINATED && !freedProcesses.contains(p.getId())) {
                        usedMemory -= p.getRequiredMemory();
                        freedProcesses.add(p.getId());

                        System.out.println("Thread 2: Memory freed from Process " + p.getId()
                                + ". Memory used: " + usedMemory + "/" + TOTAL_MEMORY + " MB");
                    }
                }
            }

            boolean jobQueueEmpty;
            synchronized (jobQueue) {
                jobQueueEmpty = jobQueue.isEmpty();
            }

            boolean readyQueueHasProcesses;
            boolean allReadyProcessesCompleted;

            synchronized (readyQueue) {
                readyQueueHasProcesses = !readyQueue.isEmpty();
                allReadyProcessesCompleted =
                        readyQueue.stream().allMatch(p -> p.getState() == PCB.State.TERMINATED);
            }

            boolean allCompleted =
                    thread1.isFinishedReading()
                    && jobQueueEmpty
                    && readyQueueHasProcesses
                    && allReadyProcessesCompleted;

            if (allCompleted) {
                System.out.println("Thread 2: All processes completed. Terminating.");
                break;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Thread 2: Interrupted.");
                break;
            }
        }
    }

    public int getUsedMemory() {
        return usedMemory;
    }
}
