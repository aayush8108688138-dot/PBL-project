package com.analytics.student.ui;

import com.analytics.student.db.ConnectionManager;
import java.sql.Connection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Flat Dialog allowing user to configure MySQL connection credentials (host, port, user, password) dynamically.
 */
public class DatabaseConfigDialog extends JDialog {

    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public DatabaseConfigDialog(Frame owner) {
        super(owner, "Configure MySQL database connection", true);
        setSize(540, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new UITheme.FlatCardPanel(10, UITheme.CARD_BG, UITheme.BORDER_COLOR);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titleLbl = new JLabel("MySQL database settings");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        mainPanel.add(titleLbl, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUrl = new JLabel("JDBC URL:");
        lblUrl.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(lblUrl, gbc);

        urlField = new JTextField(ConnectionManager.getUrl(), 30);
        urlField.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(urlField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(lblUser, gbc);

        userField = new JTextField(ConnectionManager.getUser(), 20);
        userField.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        formPanel.add(lblPass, gbc);

        passField = new JPasswordField(ConnectionManager.getPassword(), 20);
        passField.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(passField, gbc);

        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_MAIN);

        JButton testBtn = new JButton("Test connection");
        UITheme.styleButton(testBtn, false);

        JButton saveBtn = new JButton("Save & apply");
        UITheme.styleButton(saveBtn, true);

        JButton cancelBtn = new JButton("Cancel");
        UITheme.styleButton(cancelBtn, false);

        btnPanel.add(testBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);

        testBtn.addActionListener(e -> testConn());
        saveBtn.addActionListener(e -> saveConn());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void testConn() {
        ConnectionManager.updateCredentials(urlField.getText().trim(), userField.getText().trim(), new String(passField.getPassword()));
        boolean ok = false;
        try (Connection conn = ConnectionManager.getConnection()) {
            ok = conn != null && !conn.isClosed();
        } catch (Exception ignored) {}
        
        if (ok) {
            statusLabel.setText("Connection successful!");
            statusLabel.setForeground(UITheme.PRIMARY_ACCENT);
        } else {
            statusLabel.setText("Connection failed. Operating in in-memory mode.");
            statusLabel.setForeground(UITheme.DANGER_ACCENT);
        }
    }

    private void saveConn() {
        testConn();
        dispose();
    }
}
