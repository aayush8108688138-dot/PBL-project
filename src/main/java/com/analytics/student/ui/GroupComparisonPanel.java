package com.analytics.student.ui;

import com.analytics.student.model.GroupComparison;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * EduTrack 2026 — Premium Group Comparison panel.
 */
public class GroupComparisonPanel extends JPanel {

    private final StudentService studentService;
    private DefaultTableModel tableModel;
    private JTable table;

    public GroupComparisonPanel(StudentService studentService) {
        this.studentService = studentService;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        initComponents();
    }

    private void initComponents() {
        // ── Hero Header ────────────────────────────────────────────────────────
        JPanel hero = buildHeroHeader();
        add(hero, BorderLayout.NORTH);

        // ── Content ────────────────────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Info pill row
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        infoRow.setOpaque(false);

        infoRow.add(makePill("▲ High ≥ 75%",   UITheme.SUCCESS_ACCENT,  UITheme.BADGE_HIGH_BG));
        infoRow.add(makePill("◆ Medium 50–75%", UITheme.PRIMARY_HOVER,   UITheme.BADGE_MED_BG));
        infoRow.add(makePill("▼ Low < 50%",     UITheme.DANGER_ACCENT,   UITheme.BADGE_LOW_BG));

        contentPanel.add(infoRow, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.SUCCESS_ACCENT);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel tableTitle = new JLabel("Aggregate Lifestyle Metrics per Group");
        tableTitle.setFont(UITheme.FONT_SUBHEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {
                "Performance group", "Students", "Avg marks",
                "Avg study (h)", "Avg sleep (h)", "Avg attendance %",
                "Avg assign %", "Avg screen (h)", "Avg exercise (h)"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        tableScroll.setBackground(UITheme.BG_SURFACE);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel makePill(String text, Color fg, Color bg) {
        JLabel pill = new JLabel("  " + text + "  ");
        pill.setFont(UITheme.FONT_SMALL);
        pill.setForeground(fg);
        pill.setBackground(bg);
        pill.setOpaque(true);
        pill.setBorder(new EmptyBorder(4, 8, 4, 8));
        return pill;
    }

    public void refreshData() {
        List<GroupComparison> comparisons = studentService.getGroupComparisons();
        tableModel.setRowCount(0);
        for (GroupComparison gc : comparisons) {
            tableModel.addRow(new Object[]{
                    gc.getCategory().getDisplayName(),
                    gc.getStudentCount(),
                    gc.getAvgCurrentMarks(),
                    gc.getAvgStudyHours(),
                    gc.getAvgSleepHours(),
                    gc.getAvgAttendance(),
                    gc.getAvgAssignmentCompletion(),
                    gc.getAvgScreenTime(),
                    gc.getAvgExercise()
            });
        }
    }

    private JPanel buildHeroHeader() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(5, 45, 30), getWidth(), 0, new Color(10, 70, 50));
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

        JLabel badge = new JLabel("  GROUPS  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UITheme.SUCCESS_ACCENT);
        badge.setBackground(new Color(52, 211, 153, 30));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Performance Group Comparison");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Rule-based classification · Lifestyle aggregates by tier");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(110, 231, 183)); // emerald-300
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
