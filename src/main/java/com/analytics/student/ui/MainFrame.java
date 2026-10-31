package com.analytics.student.ui;

import com.analytics.student.service.StudentService;
import com.analytics.student.service.StudentServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EduTrack 2026 — Premium dark-themed main application window.
 * 180px deep-indigo sidebar, animated nav, CardLayout content area.
 */
public class MainFrame extends JFrame {

    private final StudentService studentService;

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String activeCardName = "Dashboard";

    private DataImportPanel importPanel;
    private DashboardPanel dashboardPanel;
    private GroupComparisonPanel groupPanel;
    private CorrelationPanel correlationPanel;
    private AnomalyPanel anomalyPanel;
    private IndividualReportPanel reportPanel;

    private CardLayout rootCardLayout;
    private JPanel rootPanel;
    private JPanel appPanel;

    public MainFrame() {
        super("EduTrack — Student Analytics");
        this.studentService = new StudentServiceImpl();

        UITheme.setupFlatLaf();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        rootCardLayout = new CardLayout();
        rootPanel = new JPanel(rootCardLayout);

        // 1. Auth Panel
        AuthPanel authPanel = new AuthPanel(this);
        rootPanel.add(authPanel, "AUTH");

        // 2. App Panel (Sidebar + Main Content)
        appPanel = new JPanel(new BorderLayout());

        // ── Sidebar ────────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        appPanel.add(sidebar, BorderLayout.WEST);

        // ── Main CardLayout Panel ──────────────────────────────────────────────
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(UITheme.BG_MAIN);

        dashboardPanel = new DashboardPanel(studentService, this);
        importPanel = new DataImportPanel(studentService, this);
        correlationPanel = new CorrelationPanel(studentService);
        groupPanel = new GroupComparisonPanel(studentService);
        anomalyPanel = new AnomalyPanel(studentService);
        reportPanel = new IndividualReportPanel(studentService);
        
        // AI Chat integration
        com.analytics.student.service.AiChatService aiChatService = new com.analytics.student.service.AiChatService();
        AiChatPanel chatPanel = new AiChatPanel(studentService, aiChatService);

        mainCardPanel.add(dashboardPanel, "Dashboard");
        mainCardPanel.add(importPanel, "Import data");
        mainCardPanel.add(correlationPanel, "Correlation");
        mainCardPanel.add(groupPanel, "Groups");
        mainCardPanel.add(anomalyPanel, "Anomalies");
        mainCardPanel.add(reportPanel, "Reports");
        mainCardPanel.add(chatPanel, "AI Chat");

        appPanel.add(mainCardPanel, BorderLayout.CENTER);
        
        rootPanel.add(appPanel, "APP");

        setContentPane(rootPanel);
        rootCardLayout.show(rootPanel, "AUTH");

        refreshAllPanels();
    }

    public void onLoginSuccess() {
        rootCardLayout.show(rootPanel, "APP");
    }

    // ── Sidebar Builder ────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Subtle right-edge gradient
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        getWidth() - 40, 0, new Color(UITheme.PRIMARY_ACCENT.getRed(), UITheme.PRIMARY_ACCENT.getGreen(), UITheme.PRIMARY_ACCENT.getBlue(), 0),
                        getWidth(), 0, new Color(UITheme.PRIMARY_ACCENT.getRed(), UITheme.PRIMARY_ACCENT.getGreen(), UITheme.PRIMARY_ACCENT.getBlue(), 12));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        // ── Brand Header ───────────────────────────────────────────────────────
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(20, 16, 16, 16));

        // Logo badge
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.PRIMARY_ACCENT,
                        getWidth(), getHeight(), UITheme.PRIMARY_DEEP);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String t = "E";
                g2.drawString(t,
                        (getWidth() - fm.stringWidth(t)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logoBadge.setPreferredSize(new Dimension(36, 36));
        logoBadge.setMaximumSize(new Dimension(36, 36));
        logoBadge.setOpaque(false);
        logoBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("EduTrack");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(UITheme.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagLine = new JLabel("Analytics Platform");
        tagLine.setFont(UITheme.FONT_CAPTION);
        tagLine.setForeground(UITheme.TEXT_MUTED);
        tagLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(logoBadge);
        brand.add(Box.createRigidArea(new Dimension(0, 10)));
        brand.add(appName);
        brand.add(Box.createRigidArea(new Dimension(0, 2)));
        brand.add(tagLine);

        // Divider under brand
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        sep.setBackground(UITheme.BORDER_COLOR);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(brand, BorderLayout.CENTER);
        topSection.add(sep, BorderLayout.SOUTH);

        sidebar.add(topSection, BorderLayout.NORTH);

        // ── Nav Section Label ──────────────────────────────────────────────────
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        navContainer.setBorder(new EmptyBorder(12, 8, 10, 8));

        JLabel navSection = new JLabel("  NAVIGATION");
        navSection.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navSection.setForeground(UITheme.TEXT_MUTED);
        navSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        navSection.setBorder(new EmptyBorder(4, 8, 8, 8));
        navContainer.add(navSection);

        String[] navKeys = { "Dashboard", "Import data", "Correlation", "Groups", "Anomalies", "Reports", "AI Chat" };

        for (String key : navKeys) {
            JButton btn = new JButton(key);
            UITheme.styleNavButton(btn, key.equals(activeCardName));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(184, 42));

            btn.addActionListener(e -> switchCard(key));

            navButtons.put(key, btn);
            navContainer.add(btn);
            navContainer.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        sidebar.add(navContainer, BorderLayout.CENTER);

        // ── Sidebar Footer ─────────────────────────────────────────────────────
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 14, 16, 14));

        JSeparator footSep = new JSeparator();
        footSep.setForeground(UITheme.BORDER_COLOR);

        JLabel semLbl = new JLabel("Semester 3 · OOP Java PBL");
        semLbl.setFont(UITheme.FONT_CAPTION);
        semLbl.setForeground(UITheme.TEXT_MUTED);
        semLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teamLbl = new JLabel("Team of 4-5 Students");
        teamLbl.setFont(UITheme.FONT_CAPTION);
        teamLbl.setForeground(new Color(UITheme.TEXT_MUTED.getRed(),
                UITheme.TEXT_MUTED.getGreen(), UITheme.TEXT_MUTED.getBlue(), 140));
        teamLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        footer.add(footSep);
        footer.add(Box.createRigidArea(new Dimension(0, 10)));
        footer.add(semLbl);
        footer.add(Box.createRigidArea(new Dimension(0, 3)));
        footer.add(teamLbl);

        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────
    public void switchCard(String cardName) {
        activeCardName = cardName;
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            String key = entry.getKey();
            JButton btn = entry.getValue();
            // Reset text to plain key so styleNavButton adds the icon fresh
            btn.setText(key);
            UITheme.styleNavButton(btn, key.equals(activeCardName));
        }
        cardLayout.show(mainCardPanel, cardName);
        refreshActivePanel(cardName);
    }

    public void refreshAllPanels() {
        dashboardPanel.refreshData();
        importPanel.updateDbStatus();
        groupPanel.refreshData();
        correlationPanel.refreshData();
        anomalyPanel.refreshData();
        reportPanel.refreshData();
    }

    private void refreshActivePanel(String cardName) {
        switch (cardName) {
            case "Dashboard" -> dashboardPanel.refreshData();
            case "Import data" -> importPanel.updateDbStatus();
            case "Correlation" -> correlationPanel.refreshData();
            case "Groups" -> groupPanel.refreshData();
            case "Anomalies" -> anomalyPanel.refreshData();
            case "Reports" -> reportPanel.refreshData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
