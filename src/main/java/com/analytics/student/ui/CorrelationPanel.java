package com.analytics.student.ui;

import com.analytics.student.model.CorrelationResult;
import com.analytics.student.model.Student;
import com.analytics.student.service.AnalyticsEngine;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * EduTrack 2026 — Correlation analysis panel with dark premium design.
 */
public class CorrelationPanel extends JPanel {

    private final StudentService studentService;

    private JComboBox<String> varXCombo;
    private JComboBox<String> varYCombo;
    private JLabel rValLabel;
    private JLabel strengthLabel;
    private JPanel scatterChartContainer;

    public CorrelationPanel(StudentService studentService) {
        this.studentService = studentService;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        initComponents();
    }

    private void initComponents() {
        // ── Hero Header ────────────────────────────────────────────────────────
        JPanel heroPanel = buildHeroHeader(
                "CORRELATION",
                "Correlation Analysis",
                "Calculate Pearson r · Scatter plot · Non-causation caveat",
                new Color(20, 10, 60), new Color(45, 30, 100)
        );
        add(heroPanel, BorderLayout.NORTH);

        // ── Content Area ───────────────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Variable selectors
        JPanel controlCard = new UITheme.GlassCard(10, UITheme.CARD_BG, UITheme.BORDER_COLOR, null);
        controlCard.setLayout(new BorderLayout(12, 8));
        controlCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel pickLabel = new JLabel("Select Variables:");
        pickLabel.setFont(UITheme.FONT_SUBHEADER);
        pickLabel.setForeground(UITheme.TEXT_PRIMARY);

        String[] metrics = {
                AnalyticsEngine.METRIC_STUDY_HOURS,
                AnalyticsEngine.METRIC_SLEEP_HOURS,
                AnalyticsEngine.METRIC_ATTENDANCE,
                AnalyticsEngine.METRIC_ASSIGNMENT,
                AnalyticsEngine.METRIC_SCREEN_TIME,
                AnalyticsEngine.METRIC_EXERCISE,
                AnalyticsEngine.METRIC_PREV_MARKS,
                AnalyticsEngine.METRIC_CURR_MARKS
        };

        JPanel pickersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pickersRow.setOpaque(false);

        JLabel lblX = new JLabel("X-axis:");
        lblX.setFont(UITheme.FONT_BODY_BOLD);
        lblX.setForeground(UITheme.TEXT_SECONDARY);
        varXCombo = new JComboBox<>(metrics);
        varXCombo.setFont(UITheme.FONT_BODY);
        varXCombo.setSelectedItem(AnalyticsEngine.METRIC_STUDY_HOURS);

        JLabel lblY = new JLabel("Y-axis:");
        lblY.setFont(UITheme.FONT_BODY_BOLD);
        lblY.setForeground(UITheme.TEXT_SECONDARY);
        varYCombo = new JComboBox<>(metrics);
        varYCombo.setFont(UITheme.FONT_BODY);
        varYCombo.setSelectedItem(AnalyticsEngine.METRIC_CURR_MARKS);

        pickersRow.add(lblX);
        pickersRow.add(varXCombo);
        pickersRow.add(Box.createHorizontalStrut(8));
        pickersRow.add(lblY);
        pickersRow.add(varYCombo);

        controlCard.add(pickLabel, BorderLayout.WEST);
        controlCard.add(pickersRow, BorderLayout.EAST);
        contentPanel.add(controlCard, BorderLayout.NORTH);

        // ── Center Split: Chart + Result Summary ───────────────────────────────
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setResizeWeight(0.65);
        centerSplit.setBorder(null);
        centerSplit.setDividerSize(10);
        centerSplit.setBackground(UITheme.BG_MAIN);

        // Left: Scatter Plot Card
        JPanel chartCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.PRIMARY_ACCENT);
        chartCard.setLayout(new BorderLayout(0, 10));
        chartCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel chartTitle = new JLabel("Scatter plot & linear trend line");
        chartTitle.setFont(UITheme.FONT_SUBHEADER);
        chartTitle.setForeground(UITheme.TEXT_PRIMARY);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        scatterChartContainer = new JPanel(new BorderLayout());
        scatterChartContainer.setOpaque(false);
        chartCard.add(scatterChartContainer, BorderLayout.CENTER);

        // Right: Statistics & Caveat Card
        JPanel resultCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.CYAN_ACCENT);
        resultCard.setLayout(new BorderLayout(0, 14));
        resultCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel resTitle = new JLabel("Pearson Coefficient");
        resTitle.setFont(UITheme.FONT_SUBHEADER);
        resTitle.setForeground(UITheme.TEXT_PRIMARY);
        resultCard.add(resTitle, BorderLayout.NORTH);

        JPanel resBody = new JPanel();
        resBody.setLayout(new BoxLayout(resBody, BoxLayout.Y_AXIS));
        resBody.setOpaque(false);

        rValLabel = new JLabel("r = ——");
        rValLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        rValLabel.setForeground(UITheme.PRIMARY_HOVER);
        rValLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        strengthLabel = new JLabel("Run analysis to see result");
        strengthLabel.setFont(UITheme.FONT_BODY_BOLD);
        strengthLabel.setForeground(UITheme.TEXT_SECONDARY);
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resBody.add(rValLabel);
        resBody.add(Box.createRigidArea(new Dimension(0, 8)));
        resBody.add(strengthLabel);

        // Caveat / Warning box
        JPanel cautionBox = new UITheme.GlassCard(10, new Color(239, 68, 68, 15), new Color(239, 68, 68, 50), UITheme.DANGER_ACCENT);
        cautionBox.setLayout(new BorderLayout(8, 6));
        cautionBox.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel cautionTitle = new JLabel("⚠  Scientific Caveat");
        cautionTitle.setFont(UITheme.FONT_BODY_BOLD);
        cautionTitle.setForeground(UITheme.DANGER_ACCENT);

        JTextArea cautionText = new JTextArea(
                "Correlation ≠ Causation. A strong Pearson r value indicates " +
                "statistical co-occurrence in this dataset, not a direct causal " +
                "relationship. Confounding variables (prior knowledge, teaching quality, " +
                "self-discipline) may influence both metrics independently."
        );
        cautionText.setFont(UITheme.FONT_SMALL);
        cautionText.setForeground(UITheme.TEXT_SECONDARY);
        cautionText.setWrapStyleWord(true);
        cautionText.setLineWrap(true);
        cautionText.setEditable(false);
        cautionText.setOpaque(false);

        cautionBox.add(cautionTitle, BorderLayout.NORTH);
        cautionBox.add(cautionText, BorderLayout.CENTER);

        resultCard.add(resBody,    BorderLayout.CENTER);
        resultCard.add(cautionBox, BorderLayout.SOUTH);

        centerSplit.setLeftComponent(chartCard);
        centerSplit.setRightComponent(resultCard);
        contentPanel.add(centerSplit, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        varXCombo.addActionListener(e -> updateCorrelationView());
        varYCombo.addActionListener(e -> updateCorrelationView());
    }

    public void refreshData() { updateCorrelationView(); }

    private void updateCorrelationView() {
        String varX = (String) varXCombo.getSelectedItem();
        String varY = (String) varYCombo.getSelectedItem();
        if (varX == null || varY == null) return;

        List<Student> students = studentService.getAllStudents();
        CorrelationResult res = studentService.calculateCorrelation(varX, varY);

        rValLabel.setText(String.format("r = %.4f", res.getPearsonR()));
        strengthLabel.setText("Interpretation: " + res.getStrengthInterpretation());

        // Color code by r value
        double r = Math.abs(res.getPearsonR());
        if (r >= 0.7)      rValLabel.setForeground(UITheme.SUCCESS_ACCENT);
        else if (r >= 0.4) rValLabel.setForeground(UITheme.AMBER_ACCENT);
        else               rValLabel.setForeground(UITheme.TEXT_SECONDARY);

        ToDoubleFunction<Student> extX = studentService.getAnalyticsEngine().getExtractor(varX);
        ToDoubleFunction<Student> extY = studentService.getAnalyticsEngine().getExtractor(varY);

        scatterChartContainer.removeAll();
        JPanel chart = ChartPanelUtil.createScatterChartPanel(
                students, varX, extX, varY, extY,
                "Correlation does not imply causation.");
        scatterChartContainer.add(chart, BorderLayout.CENTER);
        scatterChartContainer.revalidate();
        scatterChartContainer.repaint();
    }

    // ── Shared hero header builder ────────────────────────────────────────────
    private JPanel buildHeroHeader(String badge, String title, String subtitle,
                                    Color colorA, Color colorB) {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, colorA, getWidth(), 0, colorB);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 5));
                for (int x = 0; x < getWidth(); x += 24)
                    for (int y = 0; y < getHeight(); y += 24)
                        g2.fillOval(x, y, 2, 2);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel badgeLbl = new JLabel("  " + badge + "  ");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeLbl.setForeground(UITheme.PRIMARY_HOVER);
        badgeLbl.setBackground(new Color(99, 102, 241, 35));
        badgeLbl.setOpaque(true);
        badgeLbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        badgeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(new Color(165, 180, 252));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(badgeLbl);
        titleBox.add(Box.createRigidArea(new Dimension(0, 8)));
        titleBox.add(titleLbl);
        titleBox.add(Box.createRigidArea(new Dimension(0, 3)));
        titleBox.add(subLbl);

        hero.add(titleBox, BorderLayout.WEST);
        return hero;
    }
}
