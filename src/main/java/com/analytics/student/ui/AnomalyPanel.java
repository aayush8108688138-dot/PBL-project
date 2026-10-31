package com.analytics.student.ui;

import com.analytics.student.model.AnomalyResult;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * EduTrack 2026 — Premium Anomaly Detection panel.
 */
public class AnomalyPanel extends JPanel {

    private final StudentService studentService;

    private JSlider thresholdSlider;
    private JLabel thresholdValLabel;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public AnomalyPanel(StudentService studentService) {
        this.studentService = studentService;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        initComponents();
    }

    private void initComponents() {
        // ── Hero Header ────────────────────────────────────────────────────────
        JPanel heroPanel = buildHeroHeader();
        add(heroPanel, BorderLayout.NORTH);

        // ── Content ────────────────────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Threshold Control Card
        JPanel controlCard = new UITheme.GlassCard(10, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.DANGER_ACCENT);
        controlCard.setLayout(new BorderLayout(14, 8));
        controlCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel controlLeft = new JPanel();
        controlLeft.setLayout(new BoxLayout(controlLeft, BoxLayout.Y_AXIS));
        controlLeft.setOpaque(false);
        JLabel ctrlTitle = new JLabel("Sensitivity Threshold");
        ctrlTitle.setFont(UITheme.FONT_SUBHEADER);
        ctrlTitle.setForeground(UITheme.TEXT_PRIMARY);
        ctrlTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ctrlSub = new JLabel("Flags students whose z-score exceeds the selected standard deviation cutoff");
        ctrlSub.setFont(UITheme.FONT_SMALL);
        ctrlSub.setForeground(UITheme.TEXT_MUTED);
        ctrlSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlLeft.add(ctrlTitle);
        controlLeft.add(Box.createRigidArea(new Dimension(0, 3)));
        controlLeft.add(ctrlSub);

        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sliderPanel.setOpaque(false);
        JLabel sensLbl = new JLabel("|z| ≥");
        sensLbl.setFont(UITheme.FONT_BODY_BOLD);
        sensLbl.setForeground(UITheme.TEXT_SECONDARY);

        thresholdSlider = new JSlider(15, 35, 20); // 1.5 to 3.5 StdDev (x10)
        thresholdSlider.setOpaque(false);
        thresholdSlider.setMajorTickSpacing(5);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPreferredSize(new Dimension(180, 36));
        thresholdSlider.setBackground(UITheme.BG_MAIN);

        thresholdValLabel = new JLabel("2.0 σ", JLabel.LEFT);
        thresholdValLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        thresholdValLabel.setForeground(UITheme.DANGER_ACCENT);
        thresholdValLabel.setPreferredSize(new Dimension(56, 24));

        sliderPanel.add(sensLbl);
        sliderPanel.add(thresholdSlider);
        sliderPanel.add(thresholdValLabel);

        controlCard.add(controlLeft, BorderLayout.WEST);
        controlCard.add(sliderPanel, BorderLayout.EAST);
        contentPanel.add(controlCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, null);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel tableTitle = new JLabel("Flagged Students");
        tableTitle.setFont(UITheme.FONT_SUBHEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        countLabel = new JLabel("0 anomalies");
        countLabel.setFont(UITheme.FONT_SMALL);
        countLabel.setForeground(UITheme.DANGER_ACCENT);
        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(countLabel, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Name", "Group", "Max z-score", "Anomalous metrics & deviation details"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(600);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.setBackground(UITheme.BG_SURFACE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        thresholdSlider.addChangeListener(e -> {
            double thr = thresholdSlider.getValue() / 10.0;
            thresholdValLabel.setText(String.format("%.1f σ", thr));
            refreshData();
        });
    }

    public void refreshData() {
        double threshold = thresholdSlider.getValue() / 10.0;
        List<AnomalyResult> anomalies = studentService.detectAnomalies(threshold);

        tableModel.setRowCount(0);
        for (AnomalyResult ar : anomalies) {
            tableModel.addRow(new Object[]{
                    ar.getStudent().getStudentId(),
                    ar.getStudent().getName(),
                    ar.getStudent().getPerformanceCategory().getDisplayName(),
                    String.format("%.2f", ar.getMaxZScore()),
                    ar.getExplanation()
            });
        }
        countLabel.setText(anomalies.size() + " anomaly" + (anomalies.size() != 1 ? "ies" : "") + " flagged");
    }

    private JPanel buildHeroHeader() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(60, 10, 10), getWidth(), 0, new Color(100, 20, 20));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,4));
                for (int x = 0; x < getWidth(); x += 24)
                    for (int y = 0; y < getHeight(); y += 24)
                        g2.fillOval(x, y, 2, 2);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel badge = new JLabel("  ANOMALIES  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UITheme.DANGER_ACCENT);
        badge.setBackground(new Color(239, 68, 68, 30));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Anomaly Detection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Z-score outlier detection · Adjustable sensitivity");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(252, 165, 165)); // red-300
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(badge);
        box.add(Box.createRigidArea(new Dimension(0, 8)));
        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 3)));
        box.add(sub);
        hero.add(box, BorderLayout.WEST);
        return hero;
    }
}
