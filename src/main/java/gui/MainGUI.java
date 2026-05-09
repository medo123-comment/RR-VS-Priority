package gui;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import metrics.MetricsCalculator;
import model.Process;
import scheduler.*;
 
public class MainGUI extends JFrame {
    private JTextField idF, arrF, burF, priF, qF;
    private JRadioButton priBtn, rrBtn;
    private JTable table;
    private DefaultTableModel model;
    private GanttChartPanel ganttPanel;
    private JLabel statusLabel;
    private List<Process> masterList = new ArrayList<>();
 
    private JScrollPane tableScroll;
    private JPanel comparisonPanel;
 
    private DefaultTableModel comparisonModel;
    private JTable tablePriComp, tableRRComp;
    private DefaultTableModel modelPriComp, modelRRComp;
    private GanttChartPanel ganttPriComparison, ganttRRComparison;
 
    public MainGUI() {
        setTitle("OS Scheduling Simulator - Protected Tables Version");
        setSize(1300, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
 
        // --- 1. Top Input Panel ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        top.add(new JLabel("ID:")); idF = new JTextField(3); top.add(idF);
        top.add(new JLabel("Arrival (AT):")); arrF = new JTextField(3); top.add(arrF);
        top.add(new JLabel("Burst (BT):")); burF = new JTextField(3); top.add(burF);
        top.add(new JLabel("Priority (Pri):")); priF = new JTextField(3); top.add(priF);
 
        JButton addBtn = new JButton("Add Process");
        addBtn.addActionListener(e -> addProcessAction());
        top.add(addBtn);
 
        priBtn = new JRadioButton("Priority", true);
        rrBtn = new JRadioButton("Round Robin (RR)");
        ButtonGroup g = new ButtonGroup(); g.add(priBtn); g.add(rrBtn);
        top.add(priBtn); top.add(rrBtn);
        top.add(new JLabel("Quantum:")); qF = new JTextField("4", 3); top.add(qF);
 
        JButton runBtn = new JButton("Run Single");
        runBtn.addActionListener(e -> runSim());
        top.add(runBtn);
 
        JButton compareBtn = new JButton("Compare Both");
        compareBtn.setBackground(new Color(200, 230, 201));
        compareBtn.addActionListener(e -> showComparisonInSameWindow());
        top.add(compareBtn);
 
        JButton clearBtn = new JButton("Clear All");
        clearBtn.addActionListener(e -> clearEverything());
        top.add(clearBtn);
 
        // --- 2. Scenario Buttons Panel ---
        JPanel scenarioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        scenarioPanel.setBorder(BorderFactory.createTitledBorder("Test Scenarios"));
 
        JButton btnA = new JButton("Scenario A: Basic Mixed");
        btnA.addActionListener(e -> loadScenarioA());
        scenarioPanel.add(btnA);
 
        JButton btnB = new JButton("Scenario B: Urgency");
        btnB.addActionListener(e -> loadScenarioB());
        scenarioPanel.add(btnB);
 
        JButton btnC = new JButton("Scenario C: Fairness");
        btnC.addActionListener(e -> loadScenarioC());
        scenarioPanel.add(btnC);
 
        JButton btnD = new JButton("Scenario D: Starvation");
        btnD.addActionListener(e -> loadScenarioD());
        scenarioPanel.add(btnD);
 
        scenarioPanel.add(new JSeparator(SwingConstants.VERTICAL));
 
        JButton btnE1 = new JButton("E1: Negative Priority Error");
        btnE1.addActionListener(e -> triggerScenarioE1());
        scenarioPanel.add(btnE1);
 
        JButton btnE2 = new JButton("E2: Invalid BT Error");
        btnE2.addActionListener(e -> triggerScenarioE2());
        scenarioPanel.add(btnE2);
 
        // North panel combines input + scenarios
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(top);
        northPanel.add(scenarioPanel);
 
        // --- 3. Central Content Area ---
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
 
        String[] columnNames = {"ID", "AT", "BT", "Pri", "CT", "TAT", "WT", "RT"};
 
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(1250, 200));
        mainContainer.add(tableScroll);
 
        ganttPanel = new GanttChartPanel();
        ganttPanel.setPreferredSize(new Dimension(1250, 150));
        mainContainer.add(ganttPanel);
 
        // --- 4. Comparison Panel ---
        comparisonPanel = new JPanel();
        comparisonPanel.setLayout(new BoxLayout(comparisonPanel, BoxLayout.Y_AXIS));
        comparisonPanel.setVisible(false);
        comparisonPanel.setBorder(BorderFactory.createTitledBorder("Algorithms Comparison Analysis"));
 
        String[] compCols = {"Metric", "Priority", "Round Robin (RR)", "Winner"};
        comparisonModel = new DefaultTableModel(compCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable summaryTable = new JTable(comparisonModel);
        summaryTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        summaryScroll.setPreferredSize(new Dimension(1250, 120));
        comparisonPanel.add(summaryScroll);
 
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
 
        modelPriComp = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablePriComp = new JTable(modelPriComp);
        tablePriComp.getTableHeader().setReorderingAllowed(false);
        JPanel pTable1 = new JPanel(new BorderLayout());
        pTable1.setBorder(BorderFactory.createTitledBorder("Priority Full Results"));
        pTable1.add(new JScrollPane(tablePriComp), BorderLayout.CENTER);
 
        modelRRComp = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableRRComp = new JTable(modelRRComp);
        tableRRComp.getTableHeader().setReorderingAllowed(false);
        JPanel pTable2 = new JPanel(new BorderLayout());
        pTable2.setBorder(BorderFactory.createTitledBorder("Round Robin Full Results"));
        pTable2.add(new JScrollPane(tableRRComp), BorderLayout.CENTER);
 
        tablesPanel.add(pTable1); tablesPanel.add(pTable2);
        tablesPanel.setPreferredSize(new Dimension(1250, 250));
        comparisonPanel.add(tablesPanel);
 
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        ganttPriComparison = new GanttChartPanel();
        ganttRRComparison = new GanttChartPanel();
 
        JPanel pChart1 = new JPanel(new BorderLayout());
        pChart1.setBorder(BorderFactory.createTitledBorder("Priority Gantt"));
        pChart1.add(ganttPriComparison, BorderLayout.CENTER);
 
        JPanel pChart2 = new JPanel(new BorderLayout());
        pChart2.setBorder(BorderFactory.createTitledBorder("Round Robin Gantt"));
        pChart2.add(ganttRRComparison, BorderLayout.CENTER);
 
        chartsPanel.add(pChart1); chartsPanel.add(pChart2);
        chartsPanel.setPreferredSize(new Dimension(1250, 200));
        comparisonPanel.add(chartsPanel);
 
        mainContainer.add(comparisonPanel);
 
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainContainer), BorderLayout.CENTER);
 
        statusLabel = new JLabel("Avg WT: 0.00 | Avg TAT: 0.00 | Avg RT: 0.00", SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(1000, 45));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(230, 230, 230));
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        add(statusLabel, BorderLayout.SOUTH);
 
        setLocationRelativeTo(null);
    }
 
    // ===================== SCENARIO LOADERS =====================
 
    /** Scenario A: Basic mixed workload - normal processes */
    private void loadScenarioA() {
        clearEverything();
        qF.setText("4");
        int[][] data = {
            // id, at, bt, pri
            {1, 0, 10, 1},
            {2, 1, 2, 1},
            {3, 1, 2, 1}
        };
        loadProcesses(data);
        JOptionPane.showMessageDialog(this,
            "Scenario A loaded: Basiاc Mixed Workload\n3 processes with varied AT, BT, and Priority.",
            "Scenario A", JOptionPane.INFORMATION_MESSAGE);
    }
 
    /** Scenario B: Urgency case - one process has clearly higher priority */
    private void loadScenarioB() {
        clearEverything();
        qF.setText("4");
        int[][] data = {
            {1, 0, 10, 3},
            {2, 1, 2, 1},
            {3, 1, 2, 3}
        };
        loadProcesses(data);
        JOptionPane.showMessageDialog(this,
            "Scenario B loaded: Urgency Case\nProcess 2 has highest priority (1) — watch how Priority favors it.",
            "Scenario B", JOptionPane.INFORMATION_MESSAGE);
    }
 
    /** Scenario C: Fairness case - similar burst times, check RR balance */
    private void loadScenarioC() {
        clearEverything();
        qF.setText("4");
        int[][] data = {
            {1, 0, 6, 1},
            {2, 0, 6,  1},   
            {3, 0, 6,  1}
        };
        loadProcesses(data);
        JOptionPane.showMessageDialog(this,
            "Scenario C loaded: Fairness Case\n3 identical processes — RR should distribute time equally.",
            "Scenario C", JOptionPane.INFORMATION_MESSAGE);
    }
 
    /** Scenario D: Starvation - low-priority processes may wait very long */
    private void loadScenarioD() {
        clearEverything();
        qF.setText("4");
        int[][] data = {
            {1, 0, 10, 8},
            {2, 1, 3, 1},
            {3, 2, 3, 1},
            {4, 3, 3, 1}
        };
        loadProcesses(data);
        JOptionPane.showMessageDialog(this,
            "Scenario D loaded: Possible Starvation Case\nProcesses 1 low priority (9) — they may wait very long in Priority mode.",
            "Scenario D", JOptionPane.INFORMATION_MESSAGE);
    }
 
    /** Scenario E1: Invalid input — negative priority */
    private void triggerScenarioE1() {
        idF.setText("10");
        arrF.setText("2");
        burF.setText("5");
        priF.setText("-3");
        JOptionPane.showMessageDialog(this,
            "- Priority cannot be negative",
            "Input Error", JOptionPane.ERROR_MESSAGE);
    }
 
    /** Scenario E2: Invalid input — BT out of range */
    private void triggerScenarioE2() {
        idF.setText("11");
        arrF.setText("1");
        burF.setText("600");
        priF.setText("2");
        JOptionPane.showMessageDialog(this,
            "- BT must be 1-500",
            "Input Error", JOptionPane.ERROR_MESSAGE);
    }
 
    /** Helper: load array of {id, at, bt, pri} into masterList and table */
    private void loadProcesses(int[][] data) {
        for (int[] d : data) {
            masterList.add(new Process(d[0], d[1], d[2], d[3]));
            model.addRow(new Object[]{d[0], d[1], d[2], d[3], "", "", "", ""});
        }
        tableScroll.setVisible(true);
        ganttPanel.setVisible(true);
        revalidate(); repaint();
    }
 
    // ===================== EXISTING METHODS =====================
 
    private void addProcessAction() {
        StringBuilder errorLog = new StringBuilder();
        if (idF.getText().trim().isEmpty()) errorLog.append("- Missing ID\n");
        if (arrF.getText().trim().isEmpty()) errorLog.append("- Missing Arrival Time (AT)\n");
        if (burF.getText().trim().isEmpty()) errorLog.append("- Missing Burst Time (BT)\n");
 
        int id = -1, at = -1, bt = -1, pri = 0;
        try {
            if (!idF.getText().trim().isEmpty()) {
                id = Integer.parseInt(idF.getText().trim());
                if (id < 0) errorLog.append("- ID cannot be negative\n");
                for (Process p : masterList) if (p.id == id) errorLog.append("- ID already exists\n");
            }
            if (!arrF.getText().trim().isEmpty()) {
                at = Integer.parseInt(arrF.getText().trim());
                if (at < 0 || at > 2000) errorLog.append("- AT must be 0-2000\n");
            }
            if (!burF.getText().trim().isEmpty()) {
                bt = Integer.parseInt(burF.getText().trim());
                if (bt <= 0 || bt > 500) errorLog.append("- BT must be 1-500\n");
            }
            if (!priF.getText().trim().isEmpty()) {
                pri = Integer.parseInt(priF.getText().trim());
                if (pri < 0) errorLog.append("- Priority cannot be negative\n");
            }
        } catch (NumberFormatException e) { errorLog.append("- Inputs must be integers\n"); }
 
        if (errorLog.length() > 0) {
            JOptionPane.showMessageDialog(this, errorLog.toString(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } else {
            model.addRow(new Object[]{id, at, bt, pri, "", "", "", ""});
            masterList.add(new Process(id, at, bt, pri));
            idF.setText(""); arrF.setText(""); burF.setText(""); priF.setText("");
        }
    }
 
    private int validateQuantum() {
        try {
            String qStr = qF.getText().trim();
            if (qStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Quantum is empty!"); return -1; }
            int q = Integer.parseInt(qStr);
            if (q <= 0) { JOptionPane.showMessageDialog(this, "Quantum must be > 0!"); return -1; }
            return q;
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid Quantum!"); return -1; }
    }
 
    private void runSim() {
        if (masterList.isEmpty()) return;
        tableScroll.setVisible(true);
        ganttPanel.setVisible(true);
        comparisonPanel.setVisible(false);
 
        List<Process> exec = getCopyOfMaster();
        if (priBtn.isSelected()) {
            PriorityScheduler.schedule(exec);
            ganttPanel.setData(new ArrayList<>(PriorityScheduler.gantt.labels), new ArrayList<>(PriorityScheduler.gantt.times));
        } else {
            int q = validateQuantum();
            if (q == -1) return;
            RoundRobinScheduler.schedule(exec, q);
            ganttPanel.setData(new ArrayList<>(RoundRobinScheduler.gantt.labels), new ArrayList<>(RoundRobinScheduler.gantt.times));
        }
        MetricsCalculator.calculate(exec);
        updateTable(model, exec);
        statusLabel.setText(String.format("Avg WT: %.2f | Avg TAT: %.2f | Avg RT: %.2f",
                MetricsCalculator.avgWaiting, MetricsCalculator.avgTurnaround, MetricsCalculator.avgResponse));
        revalidate(); repaint();
    }
 
    private void showComparisonInSameWindow() {
        if (masterList.isEmpty()) return;
        int q = validateQuantum();
        if (q == -1) return;
 
        tableScroll.setVisible(false);
        ganttPanel.setVisible(false);
 
        List<Process> listPri = getCopyOfMaster();
        PriorityScheduler.schedule(listPri);
        MetricsCalculator.calculate(listPri);
        updateTable(modelPriComp, listPri);
        double wP = MetricsCalculator.avgWaiting, tP = MetricsCalculator.avgTurnaround, rP = MetricsCalculator.avgResponse;
        ganttPriComparison.setData(new ArrayList<>(PriorityScheduler.gantt.labels), new ArrayList<>(PriorityScheduler.gantt.times));
 
        List<Process> listRR = getCopyOfMaster();
        RoundRobinScheduler.schedule(listRR, q);
        MetricsCalculator.calculate(listRR);
        updateTable(modelRRComp, listRR);
        double wR = MetricsCalculator.avgWaiting, tR = MetricsCalculator.avgTurnaround, rR = MetricsCalculator.avgResponse;
        ganttRRComparison.setData(new ArrayList<>(RoundRobinScheduler.gantt.labels), new ArrayList<>(RoundRobinScheduler.gantt.times));
 
        comparisonModel.setRowCount(0);
        comparisonModel.addRow(new Object[]{"Avg Waiting Time (WT)", String.format("%.2f", wP), String.format("%.2f", wR), (wP <= wR ? "Priority" : "RR")});
        comparisonModel.addRow(new Object[]{"Avg Turnaround (TAT)", String.format("%.2f", tP), String.format("%.2f", tR), (tP <= tR ? "Priority" : "RR")});
        comparisonModel.addRow(new Object[]{"Avg Response Time (RT)", String.format("%.2f", rP), String.format("%.2f", rR), (rP <= rR ? "Priority" : "RR")});
 
        comparisonPanel.setVisible(true);
        revalidate(); repaint();
    }
 
    private void updateTable(DefaultTableModel m, List<Process> list) {
        m.setRowCount(0);
        for (Process p : list) {
            m.addRow(new Object[]{p.id, p.arrivalTime, p.burstTime, p.priority, p.completionTime, p.turnaroundTime, p.waitingTime, p.responseTime});
        }
    }
 
    private List<Process> getCopyOfMaster() {
        List<Process> copy = new ArrayList<>();
        for (Process p : masterList) copy.add(new Process(p.id, p.arrivalTime, p.burstTime, p.priority));
        return copy;
    }
 
    private void clearEverything() {
        masterList.clear();
        model.setRowCount(0);
        comparisonModel.setRowCount(0);
        modelPriComp.setRowCount(0);
        modelRRComp.setRowCount(0);
        tableScroll.setVisible(true);
        ganttPanel.setVisible(true);
        ganttPanel.setData(new ArrayList<>(), new ArrayList<>(List.of(0)));
        comparisonPanel.setVisible(false);
        statusLabel.setText("Avg WT: 0.00 | Avg TAT: 0.00 | Avg RT: 0.00");
        revalidate(); repaint();
    }
 
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}