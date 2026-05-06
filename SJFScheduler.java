package os;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class SJFScheduler {

	private LinkedList<PCB> readyQueue;
	private int totalProcesses;
	private MemoryManager memoryManager;
	private List<int[]> ganttChart;
	private List<PCB> finishedProcesses;

	public SJFScheduler(LinkedList<PCB> readyQueue, int totalProcesses, MemoryManager memoryManager) {
		this.readyQueue = readyQueue;
		this.totalProcesses = totalProcesses;
		this.memoryManager = memoryManager;
		this.ganttChart = new ArrayList<int[]>();
		this.finishedProcesses = new ArrayList<PCB>();
	}

	public void run() {
		int currentTime = 0;
		PCB runningProcess = null;
		int ganttStart = 0;
		int burstAtStart = 0;

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
					ganttStart = currentTime;
					burstAtStart = runningProcess.getRemainingBurst();
					System.out.println("[t=" + currentTime + "] Dispatched P" + runningProcess.getId() + " | burst="
							+ burstAtStart + "ms");
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
					runningProcess.setTurnaroundTime(endTime);

					synchronized (readyQueue) {
						readyQueue.remove(runningProcess);
					}

					memoryManager.deallocate(runningProcess.getId(), runningProcess.getRequiredMemory());
					memoryManager.notifyCompleted();

					ganttChart.add(new int[] { runningProcess.getId(), ganttStart, endTime, burstAtStart, 0 });
					finishedProcesses.add(runningProcess);

					System.out.println("[t=" + endTime + "] P" + runningProcess.getId() + " TERMINATED | turnaround="
							+ runningProcess.getTurnaroundTime() + "ms | waiting=" + runningProcess.getWaitingTime()
							+ "ms");

					runningProcess = null;
				}

			} else {
				if (!ganttChart.isEmpty() && ganttChart.get(ganttChart.size() - 1)[0] == -1) {
					ganttChart.get(ganttChart.size() - 1)[2] = currentTime + 1;
				} else {
					ganttChart.add(new int[] { -1, currentTime, currentTime + 1, 0, 0 });
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

	private PCB findShortestJob() {
		PCB best = null;

		for (int i = 0; i < readyQueue.size(); i++) {
			PCB p = readyQueue.get(i);

			if (p.getState() != PCB.State.READY)
				continue;

			if (best == null) {
				best = p;
			} else if (p.getBurstTime() < best.getBurstTime()) {
				best = p;
			} else if (p.getBurstTime() == best.getBurstTime()) {
				if (p.getArrivalOrder() < best.getArrivalOrder()) {
					best = p;
				}
			}
		}

		return best;
	}

	public List<int[]> getGanttChart() {
		return ganttChart;
	}

	public List<PCB> getFinishedProcesses() {
		return finishedProcesses;
	}
}