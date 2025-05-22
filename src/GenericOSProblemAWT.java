import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.Timer; // Use Swing timer for smooth updates

// Generic Process class
class Process<T> {
    private T processId;
    private int burstTime;
    private int arrivalTime;
    private int waitingTime;
    private int turnaroundTime;

    public Process(T processId, int burstTime, int arrivalTime) {
        this.processId = processId;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
    }

    public T getProcessId() { return processId; }
    public int getBurstTime() { return burstTime; }
    public int getArrivalTime() { return arrivalTime; }
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
}

// Scheduler class
class Scheduler<T> {
    private List<Process<T>> processList = new ArrayList<>();

    public void addProcess(Process<T> process) { processList.add(process); }

    public void calculateTimes() {
        int currentTime = 0;
        for (Process<T> p : processList) {
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }
            p.setWaitingTime(currentTime - p.getArrivalTime());
            currentTime += p.getBurstTime();
            p.setTurnaroundTime(currentTime - p.getArrivalTime());
        }
    }

    public String generateGanttChart() {
        StringBuilder sb = new StringBuilder("\n🔹 GANTT CHART 🔹\n| ");
        int time = 0;

        for (Process<T> p : processList) sb.append(p.getProcessId()).append(" | ");
        sb.append("\n0");

        for (Process<T> p : processList) {
            time += p.getBurstTime();
            sb.append("   ").append(time);
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public String generateTableAndAverages() {
        StringBuilder sb = new StringBuilder("🔸 Process Table:\n");
        sb.append(String.format("%-10s%-12s%-14s%-16s%-14s\n", "Process", "BurstTime", "ArrivalTime", "WaitingTime", "TurnaroundTime"));
        sb.append("------------------------------------------------------------\n");

        double totalWT = 0, totalTAT = 0;
        for (Process<T> p : processList) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
            sb.append(String.format("%-10s%-12d%-14d%-16d%-14d\n",
                    p.getProcessId(), p.getBurstTime(), p.getArrivalTime(), p.getWaitingTime(), p.getTurnaroundTime()));
        }

        sb.append("\nAverage Waiting Time: ").append(String.format("%.2f", totalWT / processList.size()));
        sb.append("\nAverage Turnaround Time: ").append(String.format("%.2f", totalTAT / processList.size()));

        return sb.toString();
    }
}

// Main GUI class (using AWT)
public class GenericOSProblemAWT extends Frame {
    private Scheduler<String> scheduler = new Scheduler<>();
    private TextField idField = new TextField(10);
    private TextField burstField = new TextField(5);
    private TextField arrivalField = new TextField(5);
    private TextArea outputArea = new TextArea(15, 40);
    private Label clockLabel = new Label();  // Date & Time Label

    private Font font = new Font("Arial", Font.PLAIN, 18);

    public GenericOSProblemAWT() {
        setTitle("FCFS Process Scheduler - Java AWT");
        setLayout(new FlowLayout());
        setSize(600, 500);

        // Input Panel
        Panel inputPanel = new Panel();
        inputPanel.add(new Label("Process ID:"));
        inputPanel.add(idField);
        inputPanel.add(new Label("Burst Time:"));
        inputPanel.add(burstField);
        inputPanel.add(new Label("Arrival Time:"));
        inputPanel.add(arrivalField);

        Button addButton = new Button("Add Process");
        Button startButton = new Button("Start Scheduling");

        inputPanel.add(addButton);
        inputPanel.add(startButton);

        add(inputPanel);

        // Set the font
        idField.setFont(font);
        burstField.setFont(font);
        arrivalField.setFont(font);
        addButton.setFont(font);
        startButton.setFont(font);
        outputArea.setFont(font);
        clockLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Output area
        add(outputArea);

        // Date & Time label
        add(clockLabel);
        startClock();  // start updating time

        // Add process button action
        addButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String burstStr = burstField.getText().trim();
            String arrivalStr = arrivalField.getText().trim();

            if (id.isEmpty() || burstStr.isEmpty() || arrivalStr.isEmpty()) {
                showMessage("Please enter Process ID, Burst Time, and Arrival Time.");
                return;
            }

            try {
                int burstTime = Integer.parseInt(burstStr);
                int arrivalTime = Integer.parseInt(arrivalStr);
                scheduler.addProcess(new Process<>(id, burstTime, arrivalTime));
                outputArea.append("Added: Process ID = " + id + ", Burst Time = " + burstTime + ", Arrival Time = " + arrivalTime + "\n");
                idField.setText("");
                burstField.setText("");
                arrivalField.setText("");
            } catch (NumberFormatException ex) {
                showMessage("Burst Time and Arrival Time must be numbers!");
            }
        });

        // Start scheduling button action
        startButton.addActionListener(e -> {
            scheduler.calculateTimes();
            String result = scheduler.generateGanttChart() + scheduler.generateTableAndAverages();
            outputArea.append("\n" + result + "\n------------------------------------\n");
        });

        setVisible(true);
    }

    // Function to start and update clock every second
    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            clockLabel.setText("Current Time: " + dateTime);
        });
        timer.start();
    }

    private void showMessage(String message) {
        Dialog dialog = new Dialog(this, "Error", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(400, 200);
        dialog.add(new Label(message));
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.setVisible(false));
        dialog.add(okButton);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new GenericOSProblemAWT();
    }
}
