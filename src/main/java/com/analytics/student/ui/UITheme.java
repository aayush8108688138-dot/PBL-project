package com.analytics.student.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * EduTrack 2026 — Premium Modern Design System.
 * Deep indigo sidebar (#0F172A), vibrant electric-blue accent (#6366F1),
 * gradient headers, glassmorphism-style cards, animated nav, badge pills.
 */
public class UITheme {

    // ─── Core Backgrounds ─────────────────────────────────────────────────────
    public static final Color BG_MAIN       = new Color(249, 250, 251);   // #F9FAFB
    public static final Color BG_SURFACE    = new Color(255, 255, 255);   // #FFFFFF
    public static final Color BG_ELEVATED   = new Color(243, 244, 246);   // #F3F4F6
    public static final Color CARD_BG       = new Color(255, 255, 255);   // #FFFFFF
    public static final Color SIDEBAR_BG    = new Color(249, 250, 251);   // #F9FAFB

    // ─── Borders ──────────────────────────────────────────────────────────────
    public static final Color BORDER_COLOR  = new Color(229, 231, 235);   // #E5E7EB
    public static final Color BORDER_STRONG = new Color(209, 213, 219);   // #D1D5DB

    // ─── Subtle Accent (Sage Green / Warm Tones) ──────────────────────────────
    public static final Color PRIMARY_ACCENT  = new Color(85, 139, 110);  // Sage green
    public static final Color PRIMARY_INDIGO  = PRIMARY_ACCENT; // Kept for compat
    public static final Color PRIMARY_HOVER   = new Color(110, 160, 130);
    public static final Color PRIMARY_LIGHT   = new Color(85, 139, 110, 25);
    public static final Color PRIMARY_GLOW    = new Color(85, 139, 110, 40);
    public static final Color PRIMARY_DEEP    = new Color(60, 110, 85);

    // ─── Danger / Alert Tint ─────────────────────────────────────────────────
    public static final Color DANGER_ACCENT = new Color(225, 112, 112); // Soft Red
    public static final Color DANGER_LIGHT  = new Color(225, 112, 112, 30);
    public static final Color DANGER_BORDER = new Color(225, 112, 112, 60);
    public static final Color DANGER_DEEP   = new Color(195, 80, 80);

    // ─── Success / Emerald ───────────────────────────────────────────────────
    public static final Color SUCCESS_ACCENT = new Color( 72, 180, 130); // Soft Emerald
    public static final Color SUCCESS_LIGHT  = new Color( 72, 180, 130, 30);
    public static final Color SUCCESS_BORDER = new Color( 72, 180, 130, 60);

    // ─── Amber / Warning ─────────────────────────────────────────────────────
    public static final Color AMBER_ACCENT  = new Color(230, 170, 70); // Soft Amber
    public static final Color AMBER_LIGHT   = new Color(230, 170, 70, 30);
    public static final Color AMBER_BORDER  = new Color(230, 170, 70, 60);

    // ─── Cyan Highlight ──────────────────────────────────────────────────────
    public static final Color CYAN_ACCENT   = new Color( 90, 190, 200); // Soft Cyan

    // ─── Named aliases (for back-compat) ─────────────────────────────────────
    public static final Color ACCENT_CYAN    = CYAN_ACCENT;
    public static final Color ACCENT_BLUE    = PRIMARY_ACCENT;
    public static final Color ACCENT_EMERALD = SUCCESS_ACCENT;
    public static final Color ACCENT_GREEN   = SUCCESS_ACCENT;
    public static final Color ACCENT_AMBER   = AMBER_ACCENT;
    public static final Color ACCENT_ROSE    = DANGER_ACCENT;

    // ─── Typography Colors ────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(31, 41, 55);    // #1F2937 charcoal
    public static final Color TEXT_SECONDARY = new Color(75, 85, 99);    // #4B5563 mid gray
    public static final Color TEXT_MUTED     = new Color(107, 114, 128); // #6B7280 light gray
    public static final Color TEXT_DARK      = TEXT_PRIMARY;

    // ─── Badge / Category Pills ───────────────────────────────────────────────
    public static final Color BADGE_HIGH_BG  = new Color( 72, 180, 130, 30);
    public static final Color BADGE_HIGH_FG  = new Color( 50, 140, 90);
    public static final Color BADGE_MED_BG   = new Color( 85, 139, 110, 30);
    public static final Color BADGE_MED_FG   = new Color( 60, 110, 85);
    public static final Color BADGE_LOW_BG   = new Color(225, 112, 112, 30);
    public static final Color BADGE_LOW_FG   = new Color(195, 80, 80);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_APP_TITLE  = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER     = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBHEADER  = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY       = new Font("Segoe UI Semibold", Font.PLAIN, 16);
    public static final Font FONT_BODY_BOLD  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD       = FONT_BODY_BOLD;
    public static final Font FONT_SMALL      = new Font("Segoe UI Semibold", Font.PLAIN, 14);
    public static final Font FONT_STAT_NUM   = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_TITLE      = FONT_HEADER;
    public static final Font FONT_LARGE_VAL  = FONT_STAT_NUM;
    public static final Font FONT_NAV        = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_CAPTION    = new Font("Segoe UI", Font.BOLD, 12);

    // ─── Nav icon map (emoji) ─────────────────────────────────────────────────
    private static final java.util.Map<String, String> NAV_ICONS = new java.util.LinkedHashMap<>();
    static {
        NAV_ICONS.put("Dashboard",    "⚡");
        NAV_ICONS.put("Import data",  "📂");
        NAV_ICONS.put("Correlation",  "📈");
        NAV_ICONS.put("Groups",       "👥");
        NAV_ICONS.put("Anomalies",    "🔍");
        NAV_ICONS.put("Reports",      "📋");
        NAV_ICONS.put("AI Chat",      "🤖");
    }

    // =========================================================================
    // FlatLaf Setup (Dark Mode + Custom Overrides)
    // =========================================================================
    public static void setupFlatLaf() {
        try {
            // Configure FlatLaf global UI defaults
            UIManager.put("defaultFont", FONT_BODY);
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("Table.rowHeight", 36);

            com.formdev.flatlaf.FlatLightLaf.setup();

            // Override key colors for light theme matching our palette
            UIManager.put("Panel.background", BG_MAIN);
            UIManager.put("TextArea.background", BG_SURFACE);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.background", BG_ELEVATED);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.caretForeground", PRIMARY_ACCENT);
            UIManager.put("ComboBox.background", BG_ELEVATED);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
            UIManager.put("ComboBox.selectionBackground", PRIMARY_ACCENT);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("Button.background", BG_ELEVATED);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Table.background", BG_SURFACE);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.selectionBackground", PRIMARY_GLOW);
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("TableHeader.background", BG_MAIN);
            UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
            UIManager.put("ScrollPane.background", BG_SURFACE);
            UIManager.put("Slider.trackColor", BG_ELEVATED);
            UIManager.put("Slider.thumbColor", PRIMARY_ACCENT);
            UIManager.put("SplitPane.background", BG_MAIN);
            UIManager.put("SplitPaneDivider.background", BORDER_COLOR);

        } catch (Exception e) {
            try {
                // Fallback to any available nimbus or metal
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }

    // =========================================================================
    // Stat Card Factory
    // =========================================================================
    /**
     * Creates a premium dark stat card with a vibrant accent color strip, label, and big number.
     */
    public static JPanel createStatCard(String labelText, String initialVal, Color accentColor, boolean isDangerTint) {
        Color cardBg   = isDangerTint ? new Color(239, 68, 68, 18) : CARD_BG;
        Color borderC  = isDangerTint ? new Color(239, 68, 68, 60) : BORDER_COLOR;
        Color textC    = isDangerTint ? DANGER_ACCENT : accentColor;

        JPanel card = new GlassCard(10, cardBg, borderC, isDangerTint ? DANGER_DEEP : accentColor);
        card.setLayout(new BorderLayout(4, 8));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel label = new JLabel(labelText.toUpperCase());
        label.setFont(FONT_CAPTION);
        label.setForeground(isDangerTint ? DANGER_ACCENT : TEXT_MUTED);
        label.putClientProperty("isTracking", true);

        JLabel valLbl = new JLabel(initialVal);
        valLbl.setName("statValueLabel");
        valLbl.setFont(FONT_STAT_NUM);
        valLbl.setForeground(textC);

        JLabel iconLbl = new JLabel(isDangerTint ? "⚠" : "●");
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        iconLbl.setForeground(textC);
        iconLbl.setBorder(new EmptyBorder(0, 0, 0, 4));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(label, BorderLayout.CENTER);
        topRow.add(iconLbl, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);

        // Subtle pulse hover
        card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                ((GlassCard) card).setHovered(true);
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                ((GlassCard) card).setHovered(false);
                card.repaint();
            }
        });

        return card;
    }

    public static JPanel createStatCard(String labelText, String initialVal, boolean isDangerTint) {
        return createStatCard(labelText, initialVal,
                isDangerTint ? DANGER_ACCENT : PRIMARY_ACCENT, isDangerTint);
    }

    public static JPanel createMetricCard(String title, String initialVal, Color accentColor, String badgeText) {
        return createStatCard(title, initialVal, accentColor, false);
    }

    public static void updateStatCardValue(JPanel card, String newValue) {
        for (Component c : card.getComponents()) {
            if ("statValueLabel".equals(c.getName()) && c instanceof JLabel lbl) {
                lbl.setText(newValue);
                return;
            }
        }
        // If nested (in topRow or elsewhere)
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel inner) {
                for (Component ic : inner.getComponents()) {
                    if ("statValueLabel".equals(ic.getName()) && ic instanceof JLabel lbl) {
                        lbl.setText(newValue);
                        return;
                    }
                }
            }
        }
    }

    // =========================================================================
    // Button Styling
    // =========================================================================
    public static void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new FlatBorder(8, BORDER_COLOR),
                new EmptyBorder(7, 16, 7, 16)
        ));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BG_ELEVATED);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new FlatBorder(8, BORDER_STRONG),
                        new EmptyBorder(7, 16, 7, 16)
                ));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new FlatBorder(8, BORDER_COLOR),
                        new EmptyBorder(7, 16, 7, 16)
                ));
            }
        });
    }

    public static void styleButton(JButton button, boolean isPrimary) {
        if (isPrimary) {
            button.setFont(FONT_BODY_BOLD);
            button.setBackground(PRIMARY_ACCENT);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new FlatBorder(8, PRIMARY_DEEP),
                    new EmptyBorder(7, 18, 7, 18)
            ));
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(PRIMARY_HOVER);
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(PRIMARY_ACCENT);
                }
            });
        } else {
            styleButton(button, BG_ELEVATED, TEXT_PRIMARY);
        }
    }

    // =========================================================================
    // Nav Button (Sidebar)
    // =========================================================================
    public static void styleNavButton(JButton button, boolean isActive) {
        String label = button.getText();
        // Fallback safely if it already contains an icon or prefix
        if (label.contains("  ")) {
            label = label.substring(label.indexOf("  ") + 2);
        }
        
        button.putClientProperty("nav.active", isActive);
        button.setText(label);
        button.setIcon(NavIconFactory.getIcon(label));
        button.setIconTextGap(12);

        button.setFont(FONT_NAV);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(175, 42));
        button.setMaximumSize(new Dimension(184, 42));
        button.setOpaque(true);

        // Remove all existing mouse listeners first
        for (var ml : button.getMouseListeners()) {
            if (ml instanceof MouseAdapter) button.removeMouseListener(ml);
        }

        if (isActive) {
            button.setBackground(new Color(85, 139, 110, 35));
            button.setForeground(PRIMARY_ACCENT);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LeftAccentBorder(3, PRIMARY_ACCENT),
                    new EmptyBorder(8, 16, 8, 10)
            ));
        } else {
            button.setBackground(SIDEBAR_BG);
            button.setForeground(TEXT_MUTED);
            button.setBorder(new EmptyBorder(8, 16, 8, 10));
            
            button.addMouseListener(new MouseAdapter() {
                private Timer timer;
                private int currentPadding = 15;
                private final int TARGET_PADDING = 22;
                private final int BASE_PADDING = 15;

                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(230, 235, 240)); // Slightly darker hover
                    button.setForeground(TEXT_PRIMARY);
                    
                    if (timer != null) timer.stop();
                    timer = new Timer(12, ae -> {
                        currentPadding += 1;
                        if (currentPadding >= TARGET_PADDING) {
                            currentPadding = TARGET_PADDING;
                            timer.stop();
                        }
                        button.setBorder(new EmptyBorder(8, currentPadding, 8, 10));
                    });
                    timer.start();
                }
                
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SIDEBAR_BG);
                    button.setForeground(TEXT_MUTED);

                    if (timer != null) timer.stop();
                    timer = new Timer(12, ae -> {
                        currentPadding -= 1;
                        if (currentPadding <= BASE_PADDING) {
                            currentPadding = BASE_PADDING;
                            timer.stop();
                        }
                        button.setBorder(new EmptyBorder(8, currentPadding, 8, 10));
                    });
                    timer.start();
                }
            });
        }
    }

    // =========================================================================
    // Table Styling
    // =========================================================================
    public static void styleTable(JTable table) {
        table.setRowHeight(44);
        table.setFont(FONT_BODY);
        table.setBackground(BG_SURFACE);
        table.setForeground(TEXT_PRIMARY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(BG_MAIN);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        tbl, val, isSelected, hasFocus, row, col);
                c.setBorder(new EmptyBorder(0, 14, 0, 14));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_SURFACE : BG_MAIN);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(TEXT_PRIMARY);
                }

                if (val != null) {
                    String sv = val.toString();
                    if ("HIGH".equalsIgnoreCase(sv) || "High Performer".equalsIgnoreCase(sv)) {
                        c.setOpaque(true);
                        c.setBackground(BADGE_HIGH_BG);
                        c.setForeground(BADGE_HIGH_FG);
                        c.setHorizontalAlignment(CENTER);
                        c.setText("▲ High");
                    } else if ("MEDIUM".equalsIgnoreCase(sv) || "Medium Performer".equalsIgnoreCase(sv)) {
                        c.setOpaque(true);
                        c.setBackground(BADGE_MED_BG);
                        c.setForeground(BADGE_MED_FG);
                        c.setHorizontalAlignment(CENTER);
                        c.setText("◆ Medium");
                    } else if ("LOW".equalsIgnoreCase(sv) || "Low Performer".equalsIgnoreCase(sv)) {
                        c.setOpaque(true);
                        c.setBackground(BADGE_LOW_BG);
                        c.setForeground(BADGE_LOW_FG);
                        c.setHorizontalAlignment(CENTER);
                        c.setText("▼ Low");
                    }
                }
                return c;
            }
        });
    }

    // =========================================================================
    // Inner Panel / Border Classes
    // =========================================================================

    /**
     * Premium glass-effect dark card with optional top accent color strip.
     */
    public static class GlassCard extends JPanel {
        private final int   radius;
        private final Color bgColor;
        private final Color borderColor;
        private final Color accentColor;
        private boolean hovered = false;

        public GlassCard(int radius, Color bgColor, Color borderColor, Color accentColor) {
            this.radius      = radius;
            this.bgColor     = bgColor;
            this.borderColor = borderColor;
            this.accentColor = accentColor;
            setOpaque(false);
        }

        public void setHovered(boolean h) { this.hovered = h; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background fill
            Color fill = hovered ? new Color(
                    Math.min(bgColor.getRed() + 15, 255),
                    Math.min(bgColor.getGreen() + 15, 255),
                    Math.min(bgColor.getBlue() + 20, 255),
                    bgColor.getAlpha() == 0 ? 0 : Math.min(bgColor.getAlpha() + 20, 255)
            ) : bgColor;

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // Subtle top accent strip (3px)
            if (accentColor != null) {
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 180));
                g2.fillRoundRect(0, 0, w - 1, 3, radius, radius);
                g2.fillRect(0, 3, w - 1, 1); // flatten bottom of strip
            }

            // Border
            Color bord = hovered ? BORDER_STRONG : borderColor;
            g2.setColor(bord);
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

            g2.dispose();
        }
    }

    /** Back-compat alias */
    public static class FlatCardPanel extends GlassCard {
        public FlatCardPanel(int radius, Color bgColor, Color borderColor) {
            super(radius, bgColor, borderColor, null);
        }
    }

    public static class RoundedPanel extends GlassCard {
        public RoundedPanel(int radius, Color bgColor) {
            super(radius, bgColor, BORDER_COLOR, null);
        }
    }

    /**
     * Rounded border outline.
     */
    public static class FlatBorder extends AbstractBorder {
        private final int   radius;
        private final Color color;

        public FlatBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 6, 4, 6);
        }
    }

    public static class RoundedBorder extends FlatBorder {
        public RoundedBorder(int radius, Color color) {
            super(radius, color);
        }
    }

    /**
     * Left-side accent bar border (used for active nav items).
     */
    public static class LeftAccentBorder extends AbstractBorder {
        private final int   barWidth;
        private final Color barColor;

        public LeftAccentBorder(int barWidth, Color barColor) {
            this.barWidth = barWidth;
            this.barColor = barColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(barColor);
            g2.fillRoundRect(x, y + 6, barWidth, height - 12, barWidth, barWidth);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 15, 8, 10);
        }
    }

    // =========================================================================
    // Gradient Header Panel
    // =========================================================================
    public static class GradientHeader extends JPanel {
        private final Color colorStart;
        private final Color colorEnd;

        public GradientHeader(Color start, Color end) {
            this.colorStart = start;
            this.colorEnd   = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, colorStart,
                    getWidth(), 0, colorEnd
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
