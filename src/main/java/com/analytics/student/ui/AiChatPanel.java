package com.analytics.student.ui;

import com.analytics.student.model.Student;
import com.analytics.student.service.AiChatService;
import com.analytics.student.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AiChatPanel extends JPanel {

    private final StudentService studentService;
    private final AiChatService aiService;
    
    private final CardLayout cardLayout;
    private final JPanel container;
    
    // Chat Components
    private JPanel chatHistoryPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    public AiChatPanel(StudentService studentService, AiChatService aiService) {
        this.studentService = studentService;
        this.aiService = aiService;
        this.aiService.setStudentService(studentService);
        
        this.cardLayout = new CardLayout();
        this.container = new JPanel(cardLayout);
        this.container.setOpaque(false);
        
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 30, 20, 30));
        
        initSetupView();
        initChatView();
        
        add(container, BorderLayout.CENTER);
    }
    
    private void initSetupView() {
        JPanel setupPanel = new JPanel(new GridBagLayout());
        setupPanel.setOpaque(false);
        
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        JLabel title = new JLabel("Welcome to EduTrack AI");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("<html><center>Chat with your student data using <b>Sarvam AI</b><br>or use the built-in <b>Local Smart Assistant</b> without an API key.</center></html>");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField keyField = new JTextField(aiService.getApiKey() != null ? aiService.getApiKey() : "", 25);
        keyField.setMaximumSize(new Dimension(320, 40));
        keyField.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        keyField.setToolTipText("Sarvam AI API Key");
        
        JButton connectBtn = new JButton("🚀 Connect with Sarvam AI");
        UITheme.styleButton(connectBtn, true);
        connectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        connectBtn.addActionListener(e -> {
            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your API Key or click 'Use Local Smart AI' below.", "API Key Needed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            aiService.setApiKey(key);
            initializeAiContext();
            cardLayout.show(container, "CHAT");
        });

        JLabel orLabel = new JLabel("─── OR ───");
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        orLabel.setForeground(UITheme.TEXT_MUTED);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton localBtn = new JButton("🧠 Use Local Smart AI (Instant / No Key)");
        UITheme.styleButton(localBtn, false);
        localBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        localBtn.addActionListener(e -> {
            aiService.setOfflineMode(true);
            initializeAiContext();
            cardLayout.show(container, "CHAT");
        });
        
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(keyField);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(connectBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(orLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(localBtn);
        
        setupPanel.add(card);
        container.add(setupPanel, "SETUP");
    }
    
    private void initChatView() {
        JPanel chatPanel = new JPanel(new BorderLayout(0, 15));
        chatPanel.setOpaque(false);
        
        // Header with switch button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("EduTrack AI Assistant");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(title, BorderLayout.WEST);

        JButton changeKeyBtn = new JButton("⚙️ Switch Mode / Change Key");
        UITheme.styleButton(changeKeyBtn, false);
        changeKeyBtn.addActionListener(e -> cardLayout.show(container, "SETUP"));
        headerPanel.add(changeKeyBtn, BorderLayout.EAST);

        chatPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Chat History Area
        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(UITheme.BG_SURFACE);
        chatHistoryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(chatHistoryPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Input Area
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setOpaque(false);
        
        inputField = new JTextField();
        inputField.setFont(UITheme.FONT_BODY);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        
        sendButton = new JButton("Send");
        UITheme.styleButton(sendButton, true);
        
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        sendButton.addActionListener(e -> sendMessage());
        
        inputArea.add(inputField, BorderLayout.CENTER);
        inputArea.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(inputArea, BorderLayout.SOUTH);
        
        container.add(chatPanel, "CHAT");
    }
    
    private void initializeAiContext() {
        List<Student> students = studentService.getAllStudents();
        StringBuilder summary = new StringBuilder();
        summary.append("Total Students: ").append(students.size()).append("\n");
        
        long highCount = students.stream().filter(s -> s.getPerformanceCategory() != null && s.getPerformanceCategory().name().equals("HIGH")).count();
        long lowCount = students.stream().filter(s -> s.getPerformanceCategory() != null && s.getPerformanceCategory().name().equals("LOW")).count();
        summary.append("High Performers: ").append(highCount).append("\n");
        summary.append("Low Performers: ").append(lowCount).append("\n");
        
        if (!students.isEmpty()) {
            double avgMarks = students.stream().mapToDouble(Student::getCurrentMarks).average().orElse(0.0);
            summary.append("Average Marks: ").append(String.format("%.1f", avgMarks)).append("\n");
        }
        
        aiService.setContext(summary.toString());
        
        chatHistoryPanel.removeAll();
        if (aiService.isOfflineMode()) {
            appendMessage("🧠 Local AI", "Welcome to EduTrack Local Smart Assistant! I am running directly on your computer with " + students.size() + " students loaded.\n\nTry asking me:\n• \"Give me an overview of class performance\"\n• \"Who are the top performers?\"\n• \"Which students need academic support?\"\n• \"What is the correlation between study hours and marks?\"\n• Or search for any student by name or ID!", true);
        } else {
            appendMessage("🚀 Sarvam AI", "Hello! I am connected via Sarvam AI Cloud. You have " + students.size() + " students loaded. Ask me anything about their academic performance and lifestyle habits!", true);
        }
    }
    
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        
        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        
        appendMessage("👤 You", text, false);
        
        // Add a temporary loading message
        JLabel loadingLabel = new JLabel("🤖 Assistant is thinking...");
        loadingLabel.setFont(UITheme.FONT_BODY);
        loadingLabel.setForeground(UITheme.TEXT_MUTED);
        loadingLabel.setBorder(new EmptyBorder(5, 5, 15, 5));
        chatHistoryPanel.add(loadingLabel);
        scrollToBottom();
        
        // Process in background thread to keep UI responsive
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return aiService.sendMessage(text);
            }

            @Override
            protected void done() {
                chatHistoryPanel.remove(loadingLabel);
                try {
                    String response = get();
                    appendMessage(aiService.isOfflineMode() ? "🧠 Local AI" : "🚀 Sarvam AI", response, true);
                } catch (Exception e) {
                    appendMessage("⚠️ System", "Error: " + e.getMessage() + "\n\n💡 Tip: You can click the '⚙️ Switch Mode / Change Key' button above and choose '🧠 Use Local Smart AI' to chat immediately without needing an API key!", true);
                } finally {
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    inputField.requestFocus();
                    scrollToBottom();
                }
            }
        };
        worker.execute();
    }
    
    private void appendMessage(String sender, String text, boolean isAi) {
        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setOpaque(false);
        msgPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel senderLabel = new JLabel(sender);
        senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        senderLabel.setForeground(isAi ? UITheme.PRIMARY_ACCENT : UITheme.TEXT_PRIMARY);
        senderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // For wrapping text
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFont(UITheme.FONT_BODY);
        textArea.setForeground(UITheme.TEXT_SECONDARY);
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        textArea.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        msgPanel.add(senderLabel);
        msgPanel.add(textArea);
        
        chatHistoryPanel.add(msgPanel);
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(UITheme.BORDER_COLOR);
        chatHistoryPanel.add(sep);
        
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
