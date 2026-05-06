package os;

import java.util.LinkedList;

public class Thread2 extends Thread {

	private final LinkedList<PCB> jobQueue;
	private final LinkedList<PCB> readyQueue;
	private final MemoryManager memoryManager;
	private final int totalProcesses;

	public Thread2(LinkedList<PCB> jobQueue, LinkedList<PCB> readyQueue, MemoryManager memoryManager,
			int totalProcesses) {
		this.jobQueue = jobQueue;
		this.readyQueue = readyQueue;
		this.memoryManager = memoryManager;
		this.totalProcesses = totalProcesses;
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
				boolean allocated = memoryManager.allocate(nextJob.getId(), nextJob.getRequiredMemory());

				if (allocated) {
					synchronized (jobQueue) {
						if (!jobQueue.isEmpty() && jobQueue.getFirst() == nextJob) {
							jobQueue.removeFirst();
						} else {
							memoryManager.deallocate(nextJob.getId(), nextJob.getRequiredMemory());
							continue;
						}
					}

					nextJob.setState(PCB.State.READY);

					synchronized (readyQueue) {
						readyQueue.addLast(nextJob);
						System.out.println("Thread 2: P" + nextJob.getId() + " admitted to ready queue.");
					}
				}
			}

			boolean jobQueueEmpty;
			synchronized (jobQueue) {
				jobQueueEmpty = jobQueue.isEmpty();
			}

			if (jobQueueEmpty && memoryManager.getCompleted() == totalProcesses) {
				System.out.println("Thread 2: All processes admitted and completed. Terminating.");
				break;
			}

			boolean shouldWait = false;

			synchronized (jobQueue) {
				if (jobQueue.isEmpty()) {
					shouldWait = true;
				} else {
					if (memoryManager.getAvailable() < jobQueue.getFirst().getRequiredMemory()) {
						shouldWait = true;
					}
				}
			}

			if (shouldWait) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Thread 2: Interrupted.");
					break;
				}
			}
		}
	}
}