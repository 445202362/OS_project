package os;

import java.util.List;
import java.util.ArrayList;

public class Output {

	public static void printOutput(List<int[]> ganttChart, List<PCB> finishedProcesses, String algorithm) {
		System.out.println("\n============================================================");
		System.out.println("  " + algorithm.toUpperCase() + " SCHEDULING - RESULTS");
		System.out.println("============================================================");

		printGanttChart(ganttChart);
		printProcessTable(finishedProcesses);
		printMetrics(finishedProcesses);

		if (algorithm.equalsIgnoreCase("Priority")) {
			printStarvationReport(finishedProcesses);
		}

		System.out.println("============================================================\n");
	}

	private static void printGanttChart(List<int[]> ganttChart) {
		System.out.println("\n--- Gantt Chart ---");

		StringBuilder topBorder = new StringBuilder();
		StringBuilder labelLine = new StringBuilder();
		StringBuilder timeLine = new StringBuilder();
		StringBuilder burstLine = new StringBuilder();

		for (int i = 0; i < ganttChart.size(); i++) {
			int[] entry = ganttChart.get(i);
			int processId = entry[0];
			int burstStart = entry[3];
			int burstEnd = entry[4];

			String label;
			String burstInfo;
			if (processId == -1) {
				label = "IDLE";
				burstInfo = " -- ";
			} else {
				label = "P" + processId;
				burstInfo = burstStart + "->" + burstEnd;
			}

			int width = Math.max(label.length(), burstInfo.length()) + 2;

			topBorder.append("+").append(repeat('-', width));
			labelLine.append("|").append(center(label, width));
			burstLine.append(" ").append(center(burstInfo, width));

			String startStr = String.valueOf(entry[1]);
			timeLine.append(startStr).append(repeat(' ', width - startStr.length() + 1));
		}

		topBorder.append("+");
		labelLine.append("|");
		timeLine.append(ganttChart.get(ganttChart.size() - 1)[2]);

		System.out.println(topBorder);
		System.out.println(labelLine);
		System.out.println(topBorder);
		System.out.println(timeLine);
		System.out.println("(burst start->end: " + burstLine.toString().trim() + ")");
	}

	private static void printProcessTable(List<PCB> finishedProcesses) {
		System.out.println("\n--- Process Results Table ---");
		System.out.printf("%-6s  %-10s  %-10s  %-15s  %-10s  %-15s%n", "PID", "Burst(ms)", "Start(ms)", "Terminate(ms)",
				"Wait(ms)", "Turnaround(ms)");
		System.out.println(repeat('-', 72));

		List<PCB> sorted = sortById(finishedProcesses);

		for (int i = 0; i < sorted.size(); i++) {
			PCB p = sorted.get(i);
			System.out.printf("%-6d  %-10d  %-10d  %-15d  %-10d  %-15d%n", p.getId(), p.getBurstTime(),
					p.getStartTime(), p.getTerminationTime(), p.getWaitingTime(), p.getTurnaroundTime());
		}

		System.out.println(repeat('-', 72));
	}

	private static void printMetrics(List<PCB> finishedProcesses) {
		double totalWT = 0;
		double totalTAT = 0;

		for (int i = 0; i < finishedProcesses.size(); i++) {
			totalWT += finishedProcesses.get(i).getWaitingTime();
			totalTAT += finishedProcesses.get(i).getTurnaroundTime();
		}

		int n = finishedProcesses.size();
		System.out.println("\n--- Performance Metrics ---");
		System.out.printf("Average Waiting Time    : %.2f ms%n", totalWT / n);
		System.out.printf("Average Turnaround Time : %.2f ms%n", totalTAT / n);
	}

	private static void printStarvationReport(List<PCB> finishedProcesses) {
		System.out.println("\n--- Starvation Report ---");

		List<Integer> starvedIDs = new ArrayList<Integer>();
		for (int i = 0; i < finishedProcesses.size(); i++) {
			if (finishedProcesses.get(i).isStarved()) {
				starvedIDs.add(finishedProcesses.get(i).getId());
			}
		}

		if (starvedIDs.isEmpty()) {
			System.out.println("No processes suffered from starvation.");
		} else {
			System.out.print("Starved Process(es): ");
			for (int i = 0; i < starvedIDs.size(); i++) {
				System.out.print("P" + starvedIDs.get(i) + "  ");
			}
			System.out.println();
		}
	}

	private static List<PCB> sortById(List<PCB> list) {
		List<PCB> sorted = new ArrayList<PCB>(list);
		for (int i = 0; i < sorted.size() - 1; i++) {
			for (int j = i + 1; j < sorted.size(); j++) {
				if (sorted.get(i).getId() > sorted.get(j).getId()) {
					PCB tmp = sorted.get(i);
					sorted.set(i, sorted.get(j));
					sorted.set(j, tmp);
				}
			}
		}
		return sorted;
	}

	private static String repeat(char ch, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append(ch);
		return sb.toString();
	}

	private static String center(String text, int width) {
		if (text.length() >= width)
			return text;
		int pad = width - text.length();
		int left = pad / 2;
		int right = pad - left;
		return repeat(' ', left) + text + repeat(' ', right);
	}
}