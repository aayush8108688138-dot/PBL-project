package com.analytics.student.ui;

import com.analytics.student.model.GroupComparison;
import com.analytics.student.model.Student;
import com.analytics.student.service.AnalyticsEngine;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * EduTrack 2026 — Premium Dashboard panel.
 * Gradient hero header, 4-column vibrant stat cards, dual-chart row.
 */
public class DashboardPanel extends JPanel {

    private final StudentService studentService;
    private final MainFrame mainFrame;

    private JLabel subtitleLabel;
    private JPanel avgStudyCard;
    private JPanel avgAttendanceCard;
    private JPanel avgMarksCard;
    private JPanel anomalyCountCard;
    private JPanel chartsContainer;

    public DashboardPanel(StudentService studentService, MainFrame mainFrame) {
        this.studentService = studentService;
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        initComponents();
    }

    private void initComponents() {

        // ── Hero / Header Section ───────────────────────────────────────────────
        JPanel heroPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Light subtle sage/cream gradient hero
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(238, 242, 239),   
                        getWidth(), 0, new Color(225, 235, 230)  
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Subtle dot pattern overlay
                g2.setColor(new Color(0, 0, 0, 10)); // faint dark dots
                for (int x = 0; x < getWidth(); x += 24) {
                    for (int y = 0; y < getHeight(); y += 24) {
                        g2.fillOval(x, y, 2, 2);
                    }
                }
                g2.dispose();
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setBorder(new EmptyBorder(28, 28, 28, 28));

        // Left: Title + subtitle
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel badge = new JLabel("  DASHBOARD  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UITheme.PRIMARY_DEEP);
        badge.setBackground(UITheme.PRIMARY_LIGHT);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageTitle = new JLabel("Class Overview");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel = new JLabel("Semester 3  ·  0 students enrolled");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED); 
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(badge);
        titleBox.add(Box.createRigidArea(new Dimension(0, 10)));
        titleBox.add(pageTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(subtitleLabel);

        // Right: Import button
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionBox.setOpaque(false);

        JButton importBtn = new JButton("＋  Import CSV");
        importBtn.setFont(UITheme.FONT_BODY_BOLD);
        importBtn.setBackground(UITheme.PRIMARY_ACCENT);
        importBtn.setForeground(Color.WHITE);
        importBtn.setFocusPainted(false);
        importBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        importBtn.setOpaque(true);
        importBtn.setBorder(BorderFactory.createCompoundBorder(
                new UITheme.FlatBorder(8, UITheme.PRIMARY_DEEP),
                new EmptyBorder(9, 20, 9, 20)
        ));
        importBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { importBtn.setBackground(UITheme.PRIMARY_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { importBtn.setBackground(UITheme.PRIMARY_ACCENT); }
        });
        importBtn.addActionListener(e -> mainFrame.switchCard("Import data"));
        actionBox.add(importBtn);

        heroPanel.add(titleBox, BorderLayout.WEST);
        heroPanel.add(actionBox, BorderLayout.EAST);

        add(heroPanel, BorderLayout.NORTH);

        // ── Center Content (Stat Cards + Charts) ───────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Stat Card Grid — 4 columns with distinct accent colors
        JPanel statGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statGrid.setOpaque(false);

        avgStudyCard      = UITheme.createStatCard("Avg Study Hours",  "0.0", UITheme.CYAN_ACCENT, false);
        avgAttendanceCard = UITheme.createStatCard("Avg Attendance",   "0%",  UITheme.SUCCESS_ACCENT, false);
        avgMarksCard      = UITheme.createStatCard("Avg Current Marks","0.0", UITheme.PRIMARY_ACCENT, false);
        anomalyCountCard  = UITheme.createStatCard("Anomalies Flagged","0",   UITheme.DANGER_ACCENT, true);

        statGrid.add(avgStudyCard);
        statGrid.add(avgAttendanceCard);
        statGrid.add(avgMarksCard);
        statGrid.add(anomalyCountCard);

        centerPanel.add(statGrid, BorderLayout.NORTH);

        // Charts Row
        chartsContainer = new JPanel(new GridBagLayout());
        chartsContainer.setOpaque(false);
        centerPanel.add(chartsContainer, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        List<Student> students = studentService.getAllStudents();
        subtitleLabel.setText(String.format("Semester 3  ·  %d students enrolled", students.size()));

        Map<String, Double> avgs = studentService.getClassAverages();
        UITheme.updateStatCardValue(avgStudyCard,
                String.format("%.1f h", avgs.getOrDefault(AnalyticsEngine.METRIC_STUDY_HOURS, 0.0)));
        UITheme.updateStatCardValue(avgAttendanceCard,
                String.format("%.0f%%", avgs.getOrDefault(AnalyticsEngine.METRIC_ATTENDANCE, 0.0)));
        UITheme.updateStatCardValue(avgMarksCard,
                String.format("%.1f", avgs.getOrDefault(AnalyticsEngine.METRIC_CURR_MARKS, 0.0)));

        int anomalyCount = studentService.detectAnomalies(2.0).size();
        UITheme.updateStatCardValue(anomalyCountCard, String.valueOf(anomalyCount));

        // Rebuild Charts
        chartsContainer.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.insets  = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;

        // ── Left: Scatter Plot Card ────────────────────────────────────────────
        JPanel scatterCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.PRIMARY_ACCENT);
        scatterCard.setLayout(new BorderLayout(0, 10));
        scatterCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel scatterTitleRow = new JPanel(new BorderLayout());
        scatterTitleRow.setOpaque(false);
        JLabel scatterTitle = new JLabel("Study Hours  ↔  Marks");
        scatterTitle.setFont(UITheme.FONT_SUBHEADER);
        scatterTitle.setForeground(UITheme.TEXT_PRIMARY);
        JLabel scatterSub = new JLabel("Correlation scatter");
        scatterSub.setFont(UITheme.FONT_CAPTION);
        scatterSub.setForeground(UITheme.TEXT_MUTED);
        scatterTitleRow.add(scatterTitle, BorderLayout.WEST);
        scatterTitleRow.add(scatterSub, BorderLayout.EAST);
        scatterCard.add(scatterTitleRow, BorderLayout.NORTH);

        com.analytics.student.model.CorrelationResult res =
                studentService.calculateCorrelation(AnalyticsEngine.METRIC_STUDY_HOURS, AnalyticsEngine.METRIC_CURR_MARKS);
        String rCaption = String.format("r = %.2f — correlation only, not causation.", res.getPearsonR());

        JPanel scatterChart = ChartPanelUtil.createScatterChartPanel(
                students,
                AnalyticsEngine.METRIC_STUDY_HOURS, Student::getStudyHours,
                AnalyticsEngine.METRIC_CURR_MARKS, Student::getCurrentMarks,
                rCaption
        );
        scatterCard.add(scatterChart, BorderLayout.CENTER);

        gbc.gridx   = 0; gbc.weightx = 1.0;
        chartsContainer.add(scatterCard, gbc);

        // Spacer 1
        gbc.gridx = 1; gbc.weightx = 0.01;
        chartsContainer.add(Box.createRigidArea(new Dimension(14, 0)), gbc);

        // ── Middle: Doughnut Chart (Activities) ──────────────────────────────
        JPanel doughnutCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.AMBER_ACCENT);
        doughnutCard.setLayout(new BorderLayout(0, 10));
        doughnutCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel doughTitleRow = new JPanel(new BorderLayout());
        doughTitleRow.setOpaque(false);
        JLabel doughTitle = new JLabel("Time Allocation");
        doughTitle.setFont(UITheme.FONT_SUBHEADER);
        doughTitle.setForeground(UITheme.TEXT_PRIMARY);
        JLabel doughSub = new JLabel(students.isEmpty() ? "Daily breakdown" : String.format("24h Breakdown · %d students", students.size()));
        doughSub.setFont(UITheme.FONT_CAPTION);
        doughSub.setForeground(UITheme.TEXT_MUTED);
        doughTitleRow.add(doughTitle, BorderLayout.WEST);
        doughTitleRow.add(doughSub, BorderLayout.EAST);
        doughnutCard.add(doughTitleRow, BorderLayout.NORTH);

        java.util.Map<String, Double> timeSlices = new java.util.LinkedHashMap<>();
        if (!students.isEmpty()) {
            double sleep = avgs.getOrDefault(AnalyticsEngine.METRIC_SLEEP_HOURS, 0.0);
            double study = avgs.getOrDefault(AnalyticsEngine.METRIC_STUDY_HOURS, 0.0);
            double screen = avgs.getOrDefault(AnalyticsEngine.METRIC_SCREEN_TIME, 0.0);
            double exercise = avgs.getOrDefault(AnalyticsEngine.METRIC_EXERCISE, 0.0);
            double other = 24.0 - (sleep + study + screen + exercise);
            if (other < 0) other = 0;

            timeSlices.put("Sleep", sleep);
            timeSlices.put("Study", study);
            timeSlices.put("Screen", screen);
            timeSlices.put("Exercise", exercise);
            if (other > 0.05) {
                timeSlices.put("Other", other);
            }
        }

        JPanel doughnutChart = ChartPanelUtil.createDoughnutChartPanel(timeSlices);
        doughnutCard.add(doughnutChart, BorderLayout.CENTER);

        gbc.gridx = 2; gbc.weightx = 1.12;
        chartsContainer.add(doughnutCard, gbc);

        // Spacer 2
        gbc.gridx = 3; gbc.weightx = 0.01;
        chartsContainer.add(Box.createRigidArea(new Dimension(14, 0)), gbc);

        // ── Right: Performance Groups Bar ─────────────────────────────────────
        JPanel barCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.SUCCESS_ACCENT);
        barCard.setLayout(new BorderLayout(0, 10));
        barCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel barTitleRow = new JPanel(new BorderLayout());
        barTitleRow.setOpaque(false);
        JLabel barTitle = new JLabel("Performance Groups");
        barTitle.setFont(UITheme.FONT_SUBHEADER);
        barTitle.setForeground(UITheme.TEXT_PRIMARY);
        JLabel barSub = new JLabel("High · Med · Low");
        barSub.setFont(UITheme.FONT_CAPTION);
        barSub.setForeground(UITheme.TEXT_MUTED);
        barTitleRow.add(barTitle, BorderLayout.WEST);
        barTitleRow.add(barSub, BorderLayout.EAST);
        barCard.add(barTitleRow, BorderLayout.NORTH);

        List<GroupComparison> groupComparisons = studentService.getGroupComparisons();
        JPanel barChart = ChartPanelUtil.createHorizontalBarChartPanel(groupComparisons);
        barCard.add(barChart, BorderLayout.CENTER);

        gbc.gridx = 4; gbc.weightx = 1.0;
        chartsContainer.add(barCard, gbc);

        chartsContainer.revalidate();
        chartsContainer.repaint();
    }
}
