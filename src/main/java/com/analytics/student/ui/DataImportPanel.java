package com.analytics.student.ui;

import com.analytics.student.exception.InvalidStudentDataException;
import com.analytics.student.model.ImportSummary;
import com.analytics.student.service.StudentService;
import com.analytics.student.util.SampleDataGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.InputStream;

/**
 * EduTrack 2026 — Premium CSV Data Import panel with dark theme.
 */
public class DataImportPanel extends JPanel {

    private final StudentService studentService;
    private final MainFrame mainFrame;

    private JLabel statusLabel;
    private JLabel dbStatusLabel;
    private JTable errorTable;
    private DefaultTableModel errorTableModel;
    private JTextArea logArea;

    public DataImportPanel(StudentService studentService, MainFrame mainFrame) {
        this.studentService = studentService;
        this.mainFrame = mainFrame;
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

        // Action Bar Card
        JPanel actionCard = new UITheme.GlassCard(10, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.PRIMARY_ACCENT);
        actionCard.setLayout(new BorderLayout(14, 8));
        actionCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel actionLeft = new JPanel();
        actionLeft.setLayout(new BoxLayout(actionLeft, BoxLayout.Y_AXIS));
        actionLeft.setOpaque(false);
        statusLabel = new JLabel("Select a student CSV dataset to import and validate.");
        statusLabel.setFont(UITheme.FONT_BODY);
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionLeft.add(statusLabel);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton browseBtn = new JButton("📂  Choose CSV File");
        UITheme.styleButton(browseBtn, true);

        JButton sampleBtn = new JButton("🎲  Load Sample Data");
        UITheme.styleButton(sampleBtn, false);

        JButton dbConfigBtn = new JButton("⚙  Configure MySQL");
        UITheme.styleButton(dbConfigBtn, false);

        actionButtons.add(browseBtn);
        actionButtons.add(sampleBtn);
        actionButtons.add(dbConfigBtn);

        actionCard.add(actionLeft, BorderLayout.WEST);
        actionCard.add(actionButtons, BorderLayout.EAST);
        contentPanel.add(actionCard, BorderLayout.NORTH);

        // ── Split: Log + Error Table ───────────────────────────────────────────
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplit.setResizeWeight(0.4);
        centerSplit.setBorder(null);
        centerSplit.setDividerSize(10);
        centerSplit.setBackground(UITheme.BG_MAIN);

        // Top: Log Console
        JPanel logCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.CYAN_ACCENT);
        logCard.setLayout(new BorderLayout(0, 10));
        logCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel logTitle = new JLabel("Import Log");
        logTitle.setFont(UITheme.FONT_SUBHEADER);
        logTitle.setForeground(UITheme.TEXT_PRIMARY);
        logCard.add(logTitle, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.BOLD, 14));
        logArea.setBackground(UITheme.BG_MAIN);
        logArea.setForeground(UITheme.TEXT_PRIMARY); // dark charcoal text
        logArea.setCaretColor(UITheme.PRIMARY_ACCENT);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        logScroll.setBackground(UITheme.BG_MAIN);
        logCard.add(logScroll, BorderLayout.CENTER);

        // Bottom: Validation Errors Table
        JPanel tableCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.DANGER_ACCENT);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel tableTitle = new JLabel("Validation Results — Rejected Rows");
        tableTitle.setFont(UITheme.FONT_SUBHEADER);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Line", "Exception type", "Validation failure reason"};
        errorTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        errorTableModel.addRow(new Object[]{"—", "Awaiting Dataset", "Click '🎲 Load Sample Data' or '📂 Choose CSV File' above to run validation."});
        errorTable = new JTable(errorTableModel);
        UITheme.styleTable(errorTable);
        if (errorTable.getColumnModel().getColumnCount() >= 3) {
            errorTable.getColumnModel().getColumn(0).setPreferredWidth(70);
            errorTable.getColumnModel().getColumn(0).setMaxWidth(90);
            errorTable.getColumnModel().getColumn(1).setPreferredWidth(190);
            errorTable.getColumnModel().getColumn(1).setMaxWidth(230);
        }

        JScrollPane errorScroll = new JScrollPane(errorTable);
        errorScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        errorScroll.setBackground(UITheme.BG_SURFACE);
        tableCard.add(errorScroll, BorderLayout.CENTER);

        centerSplit.setTopComponent(logCard);
        centerSplit.setBottomComponent(tableCard);
        contentPanel.add(centerSplit, BorderLayout.CENTER);

        // Footer DB status
        JPanel footerCard = new UITheme.GlassCard(10, UITheme.CARD_BG, UITheme.BORDER_COLOR, null);
        footerCard.setLayout(new BorderLayout());
        footerCard.setBorder(new EmptyBorder(10, 14, 10, 14));
        dbStatusLabel = new JLabel("Database status: Checking...");
        dbStatusLabel.setFont(UITheme.FONT_SMALL);
        dbStatusLabel.setForeground(UITheme.TEXT_SECONDARY);
        footerCard.add(dbStatusLabel, BorderLayout.WEST);
        contentPanel.add(footerCard, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        updateDbStatus();

        browseBtn.addActionListener(e -> chooseAndImportCsv());
        sampleBtn.addActionListener(e -> loadSampleDataset());
        dbConfigBtn.addActionListener(e -> showDbConfigDialog());
    }

    public void updateDbStatus() {
        boolean connected = studentService.isDatabaseConnected();
        if (connected) {
            dbStatusLabel.setText("● Database: Connected to MySQL (student_analytics)");
            dbStatusLabel.setForeground(UITheme.SUCCESS_ACCENT);
        } else {
            dbStatusLabel.setText("○ Database: Offline — running in-memory mode");
            dbStatusLabel.setForeground(UITheme.TEXT_MUTED);
        }
    }

    private void chooseAndImportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Student Analytics CSV File");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            processFile(chooser.getSelectedFile());
        }
    }

    private void loadSampleDataset() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("sample_students.csv");
            if (is != null) {
                ImportSummary summary = studentService.importFromStream(is);
                displaySummaryResults(summary, "sample_students.csv");
            } else {
                File tempSample = new File("sample_students.csv");
                SampleDataGenerator.generateSampleCsv(tempSample, 30, true);
                processFile(tempSample);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sample dataset: " + ex.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processFile(File file) {
        try {
            ImportSummary summary = studentService.importAndPersistCsv(file);
            displaySummaryResults(summary, file.getName());
        } catch (Exception ex) {
            logArea.append("Import error: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                    "Import failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySummaryResults(ImportSummary summary, String sourceName) {
        errorTableModel.setRowCount(0);
        logArea.setText("");

        logArea.append("════════════════════════════════════════════\n");
        logArea.append("  Import summary: " + sourceName + "\n");
        logArea.append("════════════════════════════════════════════\n");
        logArea.append(String.format("  Total rows evaluated : %d\n", summary.getTotalRowsProcessed()));
        logArea.append(String.format("  Successfully parsed  : %d\n", summary.getSuccessCount()));
        logArea.append(String.format("  Validation errors    : %d\n", summary.getErrorCount()));
        logArea.append("────────────────────────────────────────────\n");

        if (summary.getErrorCount() == 0) {
            logArea.append("  ✓ All evaluated rows passed validation with 0 errors.\n");
            errorTableModel.addRow(new Object[]{
                    "—",
                    "None (100% Valid)",
                    "All " + summary.getSuccessCount() + " student records passed schema, column, and range validation."
            });
        } else {
            for (InvalidStudentDataException ex : summary.getErrors()) {
                String exType = ex.getClass().getSimpleName();
                errorTableModel.addRow(new Object[]{
                        ex.getLineNumber() > 0 ? ex.getLineNumber() : "N/A",
                        exType,
                        ex.getMessage()
                });
                logArea.append(String.format("  Line %-4d  [%s]  %s\n",
                        ex.getLineNumber(), exType, ex.getMessage()));
            }
        }

        statusLabel.setText(String.format("✓ Processed %d records (%d valid, %d rejected).",
                summary.getTotalRowsProcessed(), summary.getSuccessCount(), summary.getErrorCount()));
        statusLabel.setForeground(summary.getErrorCount() == 0 ? UITheme.SUCCESS_ACCENT : UITheme.AMBER_ACCENT);

        updateDbStatus();
        mainFrame.refreshAllPanels();
    }

    private void showDbConfigDialog() {
        DatabaseConfigDialog dialog = new DatabaseConfigDialog(mainFrame);
        dialog.setVisible(true);
        updateDbStatus();
    }

    private JPanel buildHeroHeader() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 30, 60), getWidth(), 0, new Color(20, 50, 90));
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

        JLabel badge = new JLabel("  IMPORT  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UITheme.CYAN_ACCENT);
        badge.setBackground(new Color(34, 211, 238, 25));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Import Data");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("CSV import · Validation · MySQL persistence");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(103, 232, 249)); // cyan-300
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
