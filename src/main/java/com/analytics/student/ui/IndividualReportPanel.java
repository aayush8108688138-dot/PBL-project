package com.analytics.student.ui;

import com.analytics.student.model.Student;
import com.analytics.student.service.ClassLevelReportGenerator;
import com.analytics.student.service.IndividualStudentReportGenerator;
import com.analytics.student.service.ReportGenerator;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * EduTrack 2026 — Premium per-student and class-level report panel.
 */
public class IndividualReportPanel extends JPanel {

    private final StudentService studentService;

    private JComboBox<String> studentSelector;
    private JTextArea reportTextArea;

    public IndividualReportPanel(StudentService studentService) {
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

        // Controls Card
        JPanel controlCard = new UITheme.GlassCard(10, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.AMBER_ACCENT);
        controlCard.setLayout(new BorderLayout(14, 8));
        controlCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel controlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlLeft.setOpaque(false);
        JLabel selLbl = new JLabel("Student:");
        selLbl.setFont(UITheme.FONT_BODY_BOLD);
        selLbl.setForeground(UITheme.TEXT_SECONDARY);
        studentSelector = new JComboBox<>();
        studentSelector.setFont(UITheme.FONT_BODY);
        studentSelector.setPreferredSize(new Dimension(260, 34));
        controlLeft.add(selLbl);
        controlLeft.add(studentSelector);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton exportIndivBtn = new JButton("📄  Export Student Report");
        UITheme.styleButton(exportIndivBtn, true);

        JButton exportClassBtn = new JButton("📊  Export Class Summary");
        UITheme.styleButton(exportClassBtn, false);

        actionButtons.add(exportIndivBtn);
        actionButtons.add(exportClassBtn);

        controlCard.add(controlLeft, BorderLayout.WEST);
        controlCard.add(actionButtons, BorderLayout.EAST);
        contentPanel.add(controlCard, BorderLayout.NORTH);

        // Report Preview Card
        JPanel reportCard = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.AMBER_ACCENT);
        reportCard.setLayout(new BorderLayout(0, 10));
        reportCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel reportTitle = new JLabel("Report Preview");
        reportTitle.setFont(UITheme.FONT_SUBHEADER);
        reportTitle.setForeground(UITheme.TEXT_PRIMARY);
        reportCard.add(reportTitle, BorderLayout.NORTH);

        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Consolas", Font.BOLD, 15));
        reportTextArea.setBackground(UITheme.BG_MAIN);
        reportTextArea.setForeground(UITheme.TEXT_PRIMARY); // dark charcoal text
        reportTextArea.setCaretColor(UITheme.PRIMARY_ACCENT);
        reportTextArea.setMargin(new Insets(10, 14, 10, 14));

        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.setBackground(UITheme.BG_MAIN);
        reportCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(reportCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        studentSelector.addActionListener(e -> generateStudentReportPreview());
        exportIndivBtn.addActionListener(e -> exportIndividualReport());
        exportClassBtn.addActionListener(e -> exportClassReport());
    }

    public void refreshData() {
        studentSelector.removeAllItems();
        List<Student> students = studentService.getAllStudents();
        for (Student s : students) {
            studentSelector.addItem(String.format("%s — %s", s.getStudentId(), s.getName()));
        }
        if (studentSelector.getItemCount() > 0) {
            studentSelector.setSelectedIndex(0);
            generateStudentReportPreview();
        } else {
            reportTextArea.setText("No student records available.\nImport a CSV dataset to generate reports.");
        }
    }

    private void generateStudentReportPreview() {
        String sel = (String) studentSelector.getSelectedItem();
        if (sel == null) return;
        String studentId = sel.split(" — ")[0].trim();
        Student s = studentService.getStudentById(studentId);
        if (s != null) {
            ReportGenerator gen = new IndividualStudentReportGenerator(studentService.getAllStudents(), s);
            reportTextArea.setText(gen.generateReport());
            reportTextArea.setCaretPosition(0);
        }
    }

    private void exportIndividualReport() {
        String sel = (String) studentSelector.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "No student selected. Please import data first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String studentId = sel.split(" — ")[0].trim();
        Student s = studentService.getStudentById(studentId);
        if (s == null) return;

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Student Report");
        fc.setSelectedFile(new File(String.format("Report_%s_%s.txt",
                s.getStudentId(), s.getName().replaceAll("\\s+", "_"))));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportGenerator gen = new IndividualStudentReportGenerator(studentService.getAllStudents(), s);
                gen.exportToFile(fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Report exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportClassReport() {
        if (studentService.getAllStudents().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available to export. Please import data first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Class Summary Report");
        fc.setSelectedFile(new File("Class_Level_Analytics_Report.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportGenerator gen = new ClassLevelReportGenerator(studentService.getAllStudents());
                gen.exportToFile(fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Class summary report exported.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel buildHeroHeader() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 35, 5), getWidth(), 0, new Color(80, 55, 10));
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

        JLabel badge = new JLabel("  REPORTS  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UITheme.AMBER_ACCENT);
        badge.setBackground(new Color(251, 191, 36, 25));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Student Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Per-student diagnostics · Class-level analytics export");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(new Color(253, 230, 138)); // amber-200
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
