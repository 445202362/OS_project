import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler {

    private LinkedList<PCB> readyQueue;
    private int totalProcesses;
    private List<int[]> ganttChart;
    private List<PCB> finishedProcesses;

    public PriorityScheduler(LinkedList<PCB> readyQueue, int totalProcesses) {
        this.readyQueue        = readyQueue;
        this.totalProcesses    = totalProcesses;
        this.ganttChart        = new ArrayList<int[]>();
        this.finishedProcesses = new ArrayList<PCB>();
    }

    public void run() {
        int currentTime    = 0;
        int lastAgingTime  = 0;
        PCB runningProcess = null;
        int ganttStart     = 0;
        int burstAtStart   = 0;

        System.out.println("\n[Priority Scheduler] Starting...");

        while (finishedProcesses.size() < totalProcesses) {

            // Apply aging every 4 ms
            if (currentTime > 0 && (currentTime - lastAgingTime) >= 4) {
                checkStarvationAndAge(currentTime);
                lastAgingTime = currentTime;
            }

            // Selection + setState are inside one synchronized block so Thread2
            // cannot insert a higher-priority process between the two steps
            if (runningProcess == null) {
                synchronized (readyQueue) {
                    runningProcess = findHighestPriority();
                    if (runningProcess != null) {
                        runningProcess.setState(PCB.State.RUNNING);
                    }
                }
                if (runningProcess != null) {
                    if (runningProcess.getStartTime() == -1) {
                        runningProcess.setStartTime(currentTime);
                    }
                    ganttStart   = currentTime;
                    burstAtStart = runningProcess.getRemainingBurst();
                    System.out.println("[t=" + currentTime + "] Dispatched P" + runningProcess.getId()
                            + " | priority=" + runningProcess.getPriority()
                            + " | remaining burst=" + burstAtStart + " ms");
                }
            }

            if (runningProcess != null) {
                synchronized (readyQueue) {
                    for (int i = 0; i < readyQueue.size(); i++) {
                        PCB p = readyQueue.get(i);
                        if (p.getState() == PCB.State.READY) {
                            p.setWaitingTime(p.getWaitingTime() + 1);
                            p.setTimeInReadyQueue(p.getTimeInReadyQueue() + 1);
                        }
                    }
                }

                runningProcess.decreaseRemainingBurst();

                if (runningProcess.isCompleted()) {
                    int endTime = currentTime + 1;
                    runningProcess.setState(PCB.State.TERMINATED);
                    runningProcess.setTerminationTime(endTime);
                    runningProcess.setTurnaroundTime(endTime);

                    ganttChart.add(new int[]{runningProcess.getId(), ganttStart, endTime, burstAtStart, 0});
                    finishedProcesses.add(runningProcess);

                    System.out.println("[t=" + endTime + "] P" + runningProcess.getId() + " TERMINATED"
                            + " | turnaround=" + runningProcess.getTurnaroundTime()
                            + " ms | waiting=" + runningProcess.getWaitingTime() + " ms");

                    runningProcess = null;
                }

            } else {
                // CPU idle — merge consecutive idle slots
                if (!ganttChart.isEmpty() && ganttChart.get(ganttChart.size() - 1)[0] == -1) {
                    ganttChart.get(ganttChart.size() - 1)[2] = currentTime + 1;
                } else {
                    ganttChart.add(new int[]{-1, currentTime, currentTime + 1, 0, 0});
                }
            }

            currentTime++;

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("[Priority Scheduler] Interrupted.");
            }
        }

        System.out.println("[Priority Scheduler] All processes completed.");
    }

    // Called only from within a synchronized(readyQueue) block
    private PCB findHighestPriority() {
        PCB best = null;

        for (int i = 0; i < readyQueue.size(); i++) {
            PCB p = readyQueue.get(i);

            if (p.getState() != PCB.State.READY) {
                continue;
            }

            if (best == null) {
                best = p;
            } else if (p.getPriority() < best.getPriority()) {
                best = p;
            } else if (p.getPriority() == best.getPriority()) {
                if (p.getArrivalOrder() < best.getArrivalOrder()) {
                    best = p;
                }
            }
        }

        return best;
    }

    private void checkStarvationAndAge(int currentTime) {
        synchronized (readyQueue) {
            int readyCount = 0;
            for (int i = 0; i < readyQueue.size(); i++) {
                if (readyQueue.get(i).getState() == PCB.State.READY) {
                    readyCount++;
                }
            }

            int starvationThreshold = readyCount * 5;

            for (int i = 0; i < readyQueue.size(); i++) {
                PCB p = readyQueue.get(i);

                if (p.getState() != PCB.State.READY) {
                    continue;
                }

                if (p.getTimeInReadyQueue() > starvationThreshold) {
                    if (!p.isStarved()) {
                        p.setStarved(true);
                        System.out.println("[t=" + currentTime + "] STARVATION detected: P" + p.getId()
                                + " | waited=" + p.getTimeInReadyQueue()
                                + " ms | threshold=" + starvationThreshold + " ms");
                    }
                    int oldPriority = p.getPriority();
                    p.applyAging();
                    if (p.getPriority() < oldPriority) {
                        System.out.println("[t=" + currentTime + "] Aging applied to P" + p.getId()
                                + " | new priority=" + p.getPriority());
                    }
                }
            }
        }
    }

    // Getters for Output.java
    public List<int[]> getGanttChart() {
        return ganttChart;
    }

    public List<PCB> getFinishedProcesses() {
        return finishedProcesses;
    }
}
