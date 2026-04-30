import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
 
public class SJFScheduler {
 
    private LinkedList<PCB> readyQueue;
    private int totalProcesses;
    private List<int[]> ganttChart;
    private List<PCB> finishedProcesses;
 
    public SJFScheduler(LinkedList<PCB> readyQueue, int totalProcesses) {
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
 
        System.out.println("\n[SJF Scheduler] Starting...");
 
        while (finishedProcesses.size() < totalProcesses) {
 
            if (runningProcess == null) {
                synchronized (readyQueue) {
                    runningProcess = findShortestJob();
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
                            + " | burst=" + burstAtStart + " ms");
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
 
                if (runningProcess.isCompleted()) {
                    int endTime = currentTime + 1;
                    runningProcess.setState(PCB.State.TERMINATED);
                    runningProcess.setTerminationTime(endTime);
                    runningProcess.setTurnaroundTime(endTime); // all processes arrive at time 0
 
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
                System.out.println("[SJF Scheduler] Interrupted.");
            }
        }
 
        System.out.println("[SJF Scheduler] All processes completed.");
    }
 
    // SJF is optimal, gives minimum average waiting time for a given set of processes
    private PCB findShortestJob() {
        PCB best = null;
 
        for (int i = 0; i < readyQueue.size(); i++) {
            PCB p = readyQueue.get(i);
 
            if (p.getState() != PCB.State.READY) {
                continue;
            }
 
            if (best == null) {
                best = p;
            } else if (p.getBurstTime() < best.getBurstTime()) {
                best = p;
            } else if (p.getBurstTime() == best.getBurstTime()) {
                if (p.getArrivalOrder() < best.getArrivalOrder()) { // equal CPU bursts, scheduled by arrival order
                    best = p;
                }
            }
        }
 
        return best;
    }
 
    // Getters for Output.java
    public List<int[]> getGanttChart() {
        return ganttChart;
    }
 
    public List<PCB> getFinishedProcesses() {
        return finishedProcesses;
    }
}
