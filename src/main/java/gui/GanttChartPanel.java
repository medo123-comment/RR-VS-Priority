package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GanttChartPanel extends JPanel {

    private List<String> labels;
    private List<Integer> times;
    private final Map<String, Color> colors = new HashMap<>();

    public GanttChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 150)); 
        setFixedColors();
    }

    private void setFixedColors() {
        colors.put("P1", new Color(220, 230, 240)); 
        colors.put("P2", new Color(230, 240, 220)); 
        colors.put("P3", new Color(240, 220, 230)); 
        colors.put("P4", new Color(250, 245, 210)); 
        colors.put("P5", new Color(230, 230, 230)); 
    }

    public void setData(List<String> labels, List<Integer> times) {
        this.labels = labels;
        this.times = times;
        repaint(); 
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (labels == null || times == null || labels.isEmpty()) return;

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int xStart = 50, yStart = 40, boxHeight = 50;
    int totalTime = times.get(times.size() - 1);
    double scale = (double) (getWidth() - 100) / Math.max(1, totalTime);

    int currentX = xStart;
    for (int i = 0; i < labels.size(); i++) {
        int width = (int) ((times.get(i+1) - times.get(i)) * scale);
        
        
        g2.setColor(new Color(235, 240, 245)); 
        g2.fillRect(currentX, yStart, width, boxHeight);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(currentX, yStart, width, boxHeight);

        
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(labels.get(i), currentX + (width/2) - 10, yStart + (boxHeight/2) + 5);

        
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(String.valueOf(times.get(i)), currentX - 5, yStart + boxHeight + 20);

        currentX += width;
    }
    
    g2.drawString(String.valueOf(totalTime), currentX - 5, yStart + boxHeight + 20);
}
}