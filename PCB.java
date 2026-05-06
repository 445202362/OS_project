package os;

public class PCB {

	public enum State {
		NEW, READY, RUNNING, WAITING, TERMINATED
	}

	private int id; // Process ID
	private State state; // Process state
	private int burstTime; // Total CPU time
	private int priority; // Priority number
	private int requiredMemory; // Required memory
	private int waitingTime; // Total waiting time
	private int turnaroundTime; // Total turnaround time
	private int remainingBurst; // Remaining CPU time
	private int startTime; // When the process first got the CPU
	private int terminationTime; // When the process finished
	private int arrivalOrder; // Position in input file
	private int timeInReadyQueue; // Time spent waiting
	private boolean isStarved; // Starvation flag

	public PCB(int id, int burstTime, int priority, int requiredMemory, int arrivalOrder) {
		this.id = id;
		this.state = State.NEW;
		this.burstTime = burstTime;
		this.priority = priority;
		this.requiredMemory = requiredMemory;
		this.arrivalOrder = arrivalOrder;
		this.remainingBurst = burstTime;
		this.waitingTime = 0;
		this.turnaroundTime = 0;
		this.startTime = -1;
		this.terminationTime = -1;
		this.timeInReadyQueue = 0;
		this.isStarved = false;
	}

	// Getters and Setters

	public int getId() {
		return id;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getBurstTime() {
		return burstTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRequiredMemory() {
		return requiredMemory;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public int getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public int getRemainingBurst() {
		return remainingBurst;
	}

	public void setRemainingBurst(int remainingBurst) {
		this.remainingBurst = remainingBurst;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getTerminationTime() {
		return terminationTime;
	}

	public void setTerminationTime(int terminationTime) {
		this.terminationTime = terminationTime;
	}

	public int getArrivalOrder() {
		return arrivalOrder;
	}

	public int getTimeInReadyQueue() {
		return timeInReadyQueue;
	}

	public void setTimeInReadyQueue(int timeInReadyQueue) {
		this.timeInReadyQueue = timeInReadyQueue;
	}

	public boolean isStarved() {
		return isStarved;
	}

	public void setStarved(boolean isStarved) {
		this.isStarved = isStarved;
	}

	// Helper methods

	public boolean isCompleted() {
		return remainingBurst == 0;
	}

	public void decreaseRemainingBurst() {
		if (remainingBurst > 0) {
			remainingBurst--;
		}
	}

	public void applyAging() {
		if (priority > 1) {
			priority--;
		}
	}
}