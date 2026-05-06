package os;

public class MemoryManager {

	private static final int TOTAL_MEMORY = 2048;
	private int usedMemory = 0;
	private int completedProcesses = 0;

	public synchronized boolean allocate(int processId, int amount) {
		if (usedMemory + amount <= TOTAL_MEMORY) {
			usedMemory += amount;
			System.out.println("Memory: Allocated " + amount + "MB to P" + processId + " | Used: " + usedMemory + "/"
					+ TOTAL_MEMORY + "MB");
			return true;
		}
		System.out.println("Memory: Not enough space for P" + processId + " (needs " + amount + "MB | available: "
				+ (TOTAL_MEMORY - usedMemory) + "MB)");
		return false;
	}

	public synchronized void deallocate(int processId, int amount) {
		usedMemory -= amount;
		System.out.println("Memory: Freed " + amount + "MB from P" + processId + " | Used: " + usedMemory + "/"
				+ TOTAL_MEMORY + "MB");
	}

	public synchronized void notifyCompleted() {
		completedProcesses++;
	}

	public synchronized int getCompleted() {
		return completedProcesses;
	}

	public synchronized int getAvailable() {
		return TOTAL_MEMORY - usedMemory;
	}
}