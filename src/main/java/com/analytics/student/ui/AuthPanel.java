package com.analytics.student.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthPanel extends JPanel {

    private MainFrame mainFrame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public AuthPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        // Center container to hold the login/signup card
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(UITheme.BG_MAIN);

        cardLayout = new CardLayout();
        cardPanel = new UITheme.GlassCard(12, UITheme.CARD_BG, UITheme.BORDER_COLOR, UITheme.PRIMARY_ACCENT);
        cardPanel.setLayout(cardLayout);
        cardPanel.setPreferredSize(new Dimension(400, 500));
        cardPanel.setBorder(new EmptyBorder(32, 32, 32, 32));

        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createSignUpPanel(), "SIGNUP");

        centerContainer.add(cardPanel);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to continue to EduTrack");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        JTextField emailField = createTextField("Email or Phone number");
        JPasswordField passwordField = createPasswordField("Password");

        panel.add(createLabeledField("Email / Phone", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createLabeledField("Password", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton loginBtn = createPrimaryButton("Login");
        loginBtn.addActionListener(e -> {
            String loginId = emailField.getText();
            String pwd = new String(passwordField.getPassword());
            if (loginId.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Email/Phone and Password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // For now, simulate successful login
            mainFrame.onLoginSuccess();
        });
        panel.add(loginBtn);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton switchToSignUpBtn = createLinkButton("Don't have an account? Sign up");
        switchToSignUpBtn.addActionListener(e -> cardLayout.show(cardPanel, "SIGNUP"));
        panel.add(switchToSignUpBtn);

        return panel;
    }

    private JPanel createSignUpPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join EduTrack today");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JTextField emailField = createTextField("Email Address");
        JTextField phoneField = createTextField("Phone Number");
        JPasswordField passwordField = createPasswordField("Password");

        panel.add(createLabeledField("Email", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createLabeledField("Phone", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createLabeledField("Password", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton signUpBtn = createPrimaryButton("Create Account");
        signUpBtn.addActionListener(e -> {
            String email = emailField.getText();
            String phone = phoneField.getText();
            String pwd = new String(passwordField.getPassword());
            if (email.isEmpty() && phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide an email or phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(cardPanel, "LOGIN");
        });
        panel.add(signUpBtn);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton switchToLoginBtn = createLinkButton("Already have an account? Login");
        switchToLoginBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        panel.add(switchToLoginBtn);

        return panel;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(320, 60));
        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(320, 36));
        field.setFont(UITheme.FONT_BODY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(320, 36));
        field.setFont(UITheme.FONT_BODY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setBackground(UITheme.PRIMARY_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 40));
        btn.setPreferredSize(new Dimension(320, 40));
        return btn;
    }

    private JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.PRIMARY_ACCENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}
