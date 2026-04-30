import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
 
public class RoundRobinScheduler {
 
    private static final int TIME_QUANTUM = 5; // q = 5 ms
 
    private LinkedList<PCB> readyQueue;
    private int totalProcesses;
    private List<int[]> ganttChart;
    private List<PCB> finishedProcesses;
 
    public RoundRobinScheduler(LinkedList<PCB> readyQueue, int totalProcesses) {
        this.readyQueue        = readyQueue;
        this.totalProcesses    = totalProcesses;
        this.ganttChart        = new ArrayList<int[]>();
        this.finishedProcesses = new ArrayList<PCB>();
    }
 
    public void run() {
        int currentTime    = 0;
        PCB runningProcess = null;
        int ganttStart     = 0;
        int burstAtStart   = 0;
        int quantumUsed    = 0; // ms used from the current time quantum
 
        System.out.println("\n[Round Robin Scheduler] Starting... (q=" + TIME_QUANTUM + " ms)");
 
        while (finishedProcesses.size() < totalProcesses) {
 
            if (runningProcess == null) {
                synchronized (readyQueue) {
                    runningProcess = findNextReady();
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
                    quantumUsed  = 0;
                    System.out.println("[t=" + currentTime + "] Dispatched P" + runningProcess.getId()
                            + " | remaining burst=" + burstAtStart + " ms");
                }
            }
 
            if (runningProcess != null) {
 
                synchronized (readyQueue) {
                    for (int i = 0; i < readyQueue.size(); i++) {
                        PCB p = readyQueue.get(i);
                        if (p.getState() == PCB.State.READY) {
                            p.setWaitingTime(p.getWaitingTime() + 1);
                        }
                    }
                }
 
                runningProcess.decreaseRemainingBurst();
                quantumUsed++;
 
                boolean completed      = runningProcess.isCompleted();
                boolean quantumExpired = (quantumUsed >= TIME_QUANTUM);
 
                if (completed) {
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
 
                } else if (quantumExpired) {
                    // time quantum elapsed, process is preempted and added to the end of the ready queue
                    int endTime = currentTime + 1;
                    ganttChart.add(new int[]{runningProcess.getId(), ganttStart, endTime, burstAtStart, runningProcess.getRemainingBurst()});
 
                    System.out.println("[t=" + endTime + "] P" + runningProcess.getId()
                            + " PREEMPTED | remaining burst=" + runningProcess.getRemainingBurst() + " ms");
 
                    synchronized (readyQueue) {
                        runningProcess.setState(PCB.State.READY);
                        readyQueue.remove(runningProcess);
                        readyQueue.addLast(runningProcess); // move to the back of the ready queue
                    }
 
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
                System.out.println("[Round Robin Scheduler] Interrupted.");
            }
        }
 
        System.out.println("[Round Robin Scheduler] All processes completed.");
    }
 
    private PCB findNextReady() {
        for (int i = 0; i < readyQueue.size(); i++) {
            PCB p = readyQueue.get(i);
            if (p.getState() == PCB.State.READY) {
                return p;
            }
        }
        return null;
    }
 
    // Getters for Output.java
    public List<int[]> getGanttChart() {
        return ganttChart;
    }
 
    public List<PCB> getFinishedProcesses() {
        return finishedProcesses;
    }
}
