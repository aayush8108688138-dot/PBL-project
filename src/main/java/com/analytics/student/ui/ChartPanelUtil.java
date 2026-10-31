package com.analytics.student.ui;

import com.analytics.student.model.GroupComparison;
import com.analytics.student.model.PerformanceCategory;
import com.analytics.student.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * EduTrack 2026 — Dark-themed custom chart rendering engine.
 */
public class ChartPanelUtil {

    // ─── Light palette for charts ───────────────────────────────────────────────
    private static final Color DOT_COLOR      = UITheme.PRIMARY_HOVER;
    private static final Color DOT_STROKE     = UITheme.PRIMARY_DEEP;
    private static final Color TREND_COLOR    = UITheme.AMBER_ACCENT;
    private static final Color GRID_COLOR     = UITheme.BORDER_COLOR;
    private static final Color AXIS_COLOR     = UITheme.BORDER_STRONG;

    private static final Color HIGH_BAR  = UITheme.SUCCESS_ACCENT;
    private static final Color MED_BAR   = UITheme.PRIMARY_ACCENT;
    private static final Color LOW_BAR   = UITheme.DANGER_ACCENT;

    private static final Color HIGH_TRACK = UITheme.SUCCESS_LIGHT;
    private static final Color MED_TRACK  = UITheme.PRIMARY_LIGHT;
    private static final Color LOW_TRACK  = UITheme.DANGER_LIGHT;

    // =========================================================================
    // Scatter Plot
    // =========================================================================
    public static JPanel createScatterChartPanel(
            List<Student> students,
            String varXLabel, ToDoubleFunction<Student> extX,
            String varYLabel, ToDoubleFunction<Student> extY,
            String captionText) {

        JPanel chartComponent = new JPanel() {
            class HitDot {
                int x, y, radius;
                Student student;
                HitDot(int x, int y, int radius, Student student) {
                    this.x = x; this.y = y; this.radius = radius; this.student = student;
                }
            }
            java.util.List<HitDot> hitDots = new java.util.ArrayList<>();

            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                for (HitDot d : hitDots) {
                    if (Math.hypot(d.x - e.getX(), d.y - e.getY()) <= d.radius) {
                        return "<html><b style='color:#38BDF8;'>" + d.student.getName() + "</b> (" + d.student.getStudentId() + ")<br/>" + 
                               varXLabel + ": " + String.format("%.1f", extX.applyAsDouble(d.student)) + "<br/>" +
                               varYLabel + ": " + String.format("%.1f", extY.applyAsDouble(d.student)) + "</html>";
                    }
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

                hitDots.clear();

                int w = getWidth();
                int h = getHeight();
                int pl = 42, pb = 30, pt = 14, pr = 18;

                // ── Background ────────────────────────────────────────────────
                g2.setColor(UITheme.CARD_BG);
                g2.fillRect(0, 0, w, h);

                if (students == null || students.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    String msg = "No data to plot — import a CSV first.";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                    g2.dispose();
                    return;
                }

                double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
                double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
                double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
                int n = students.size();

                for (Student s : students) {
                    double x = extX.applyAsDouble(s);
                    double y = extY.applyAsDouble(s);
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    sumX += x; sumY += y; sumXY += x * y; sumXX += x * x;
                }

                if (maxX == minX) maxX += 1.0;
                if (maxY == minY) maxY += 1.0;
                double padX = (maxX - minX) * 0.08;
                double padY = (maxY - minY) * 0.10;
                minX -= padX; maxX += padX;
                minY -= padY; maxY += padY;

                int plotX = pl, plotY = pt;
                int plotW = w - pl - pr;
                int plotH = h - pt - pb;

                // ── Grid lines ────────────────────────────────────────────────
                g2.setColor(GRID_COLOR);
                g2.setStroke(new BasicStroke(0.7f));
                int numGrid = 4;
                for (int i = 1; i < numGrid; i++) {
                    int gy = plotY + (plotH * i / numGrid);
                    g2.drawLine(plotX, gy, plotX + plotW, gy);
                    int gx = plotX + (plotW * i / numGrid);
                    g2.drawLine(gx, plotY, gx, plotY + plotH);
                }

                // ── Axes ──────────────────────────────────────────────────────
                g2.setColor(AXIS_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(plotX, plotY, plotX, plotY + plotH);
                g2.drawLine(plotX, plotY + plotH, plotX + plotW, plotY + plotH);

                // Axis labels
                g2.setFont(UITheme.FONT_CAPTION);
                g2.setColor(UITheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(varXLabel, plotX + (plotW - fm.stringWidth(varXLabel)) / 2, h - 4);

                // ── Trend Line ────────────────────────────────────────────────
                if (n >= 2) {
                    double slope     = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
                    double intercept = (sumY - slope * sumX) / n;
                    double ty1 = slope * minX + intercept;
                    double ty2 = slope * maxX + intercept;
                    int tx1 = plotX;
                    int tx2 = plotX + plotW;
                    int pty1 = (int) (plotY + plotH - ((ty1 - minY) / (maxY - minY)) * plotH);
                    int pty2 = (int) (plotY + plotH - ((ty2 - minY) / (maxY - minY)) * plotH);

                    g2.setColor(TREND_COLOR);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            10f, new float[]{5f, 4f}, 0f));
                    g2.draw(new Line2D.Double(tx1, pty1, tx2, pty2));
                }

                // ── Data Dots ─────────────────────────────────────────────────
                g2.setStroke(new BasicStroke(1.2f));
                for (Student s : students) {
                    double x = extX.applyAsDouble(s);
                    double y = extY.applyAsDouble(s);
                    int px = (int) (plotX + ((x - minX) / (maxX - minX)) * plotW);
                    int py = (int) (plotY + plotH - ((y - minY) / (maxY - minY)) * plotH);
                    
                    hitDots.add(new HitDot(px, py, 10, s)); // Track dot for tooltips with a generous radius

                    // Outer glow
                    g2.setColor(new Color(DOT_COLOR.getRed(), DOT_COLOR.getGreen(), DOT_COLOR.getBlue(), 50));
                    g2.fill(new Ellipse2D.Double(px - 5, py - 5, 10, 10));
                    // Inner dot
                    g2.setColor(DOT_COLOR);
                    g2.fill(new Ellipse2D.Double(px - 3, py - 3, 6, 6));
                    g2.setColor(DOT_STROKE);
                    g2.draw(new Ellipse2D.Double(px - 3, py - 3, 6, 6));
                }
                g2.dispose();
            }
        };

        ToolTipManager.sharedInstance().registerComponent(chartComponent);
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(10000);

        chartComponent.setOpaque(true);
        chartComponent.setBackground(UITheme.CARD_BG);

        JPanel container = new JPanel(new BorderLayout(4, 6));
        container.setOpaque(false);
        container.add(chartComponent, BorderLayout.CENTER);

        if (captionText != null && !captionText.isEmpty()) {
            JLabel caption = new JLabel("ⓘ  " + captionText);
            caption.setFont(UITheme.FONT_CAPTION);
            caption.setForeground(UITheme.TEXT_MUTED);
            container.add(caption, BorderLayout.SOUTH);
        }

        return container;
    }

    // =========================================================================
    // Horizontal Bar Chart (Performance Groups)
    // =========================================================================
    public static JPanel createHorizontalBarChartPanel(List<GroupComparison> groupComparisons) {
        JPanel chartComponent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(UITheme.CARD_BG);
                g2.fillRect(0, 0, w, h);

                if (groupComparisons == null || groupComparisons.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    String msg = "No data — import CSV first.";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                    g2.dispose();
                    return;
                }

                int total = 0, high = 0, med = 0, low = 0;
                for (GroupComparison gc : groupComparisons) {
                    total += gc.getStudentCount();
                    if (gc.getCategory() == PerformanceCategory.HIGH)   high = gc.getStudentCount();
                    if (gc.getCategory() == PerformanceCategory.MEDIUM) med  = gc.getStudentCount();
                    if (gc.getCategory() == PerformanceCategory.LOW)    low  = gc.getStudentCount();
                }
                if (total == 0) total = 1;

                int rowH     = 52;
                int startY   = (h - rowH * 3) / 2;
                int labelW   = 62;
                int trackX   = labelW + 8;
                int trackW   = w - trackX - 44;
                int barH     = 10;

                String[][] rows = {
                    {"▲  High",   String.valueOf(high), String.valueOf(high)},
                    {"◆  Medium", String.valueOf(med),  String.valueOf(med)},
                    {"▼  Low",    String.valueOf(low),  String.valueOf(low)}
                };
                Color[] barColors   = { HIGH_BAR,   MED_BAR,   LOW_BAR  };
                Color[] trackColors = { HIGH_TRACK,  MED_TRACK,  LOW_TRACK };
                Color[] textColors  = {
                    new Color( 52, 211, 153),
                    new Color(129, 140, 248),
                    new Color(248, 113, 113)
                };
                int[] counts = { high, med, low };

                g2.setFont(UITheme.FONT_BODY_BOLD);
                for (int i = 0; i < 3; i++) {
                    int y = startY + i * rowH;

                    // Label
                    g2.setColor(textColors[i]);
                    g2.drawString(rows[i][0], 2, y + 14);

                    // Count badge on far right
                    String countStr = rows[i][1];
                    g2.setFont(UITheme.FONT_BODY_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    int badgeW = fm.stringWidth(countStr) + 14;
                    int badgeX = w - badgeW - 2;
                    int badgeY = y + 2;
                    g2.setColor(new Color(barColors[i].getRed(), barColors[i].getGreen(), barColors[i].getBlue(), 25));
                    g2.fillRoundRect(badgeX, badgeY, badgeW, 18, 9, 9);
                    g2.setColor(barColors[i]);
                    g2.drawString(countStr, badgeX + 7, badgeY + 13);

                    // Track background
                    int trackY = y + 22;
                    g2.setColor(trackColors[i]);
                    g2.fill(new RoundRectangle2D.Double(trackX, trackY, trackW, barH, barH, barH));

                    // Filled bar
                    int filledW = (int) (((double) counts[i] / total) * trackW);
                    if (filledW > 0) {
                        GradientPaint gp = new GradientPaint(
                                trackX, 0, barColors[i],
                                trackX + filledW, 0, new Color(
                                        Math.min(barColors[i].getRed() + 40, 255),
                                        Math.min(barColors[i].getGreen() + 40, 255),
                                        Math.min(barColors[i].getBlue() + 40, 255))
                        );
                        g2.setPaint(gp);
                        g2.fill(new RoundRectangle2D.Double(trackX, trackY, Math.max(filledW, barH), barH, barH, barH));
                    }

                    // Percentage label
                    g2.setFont(UITheme.FONT_CAPTION);
                    g2.setColor(UITheme.TEXT_MUTED);
                    int pct = (int) Math.round(100.0 * counts[i] / total);
                    g2.drawString(pct + "%", trackX, trackY + barH + 14);

                    g2.setFont(UITheme.FONT_BODY_BOLD);
                }
                g2.dispose();
            }
        };

        chartComponent.setOpaque(true);
        chartComponent.setBackground(UITheme.CARD_BG);
        return chartComponent;
    }
    // =========================================================================
    // Doughnut Chart (Time Breakdown)
    // =========================================================================
    public static JPanel createDoughnutChartPanel(java.util.Map<String, Double> slices) {
        class SliceInfo {
            final String name;
            final double value;
            final Color color1;
            final Color color2;
            final String tipNote;
            final Rectangle legendBounds = new Rectangle();

            SliceInfo(String name, double value, Color c1, Color c2, String tip) {
                this.name = name;
                this.value = value;
                this.color1 = c1;
                this.color2 = c2;
                this.tipNote = tip;
            }
        }

        final java.util.List<SliceInfo> sliceList = new java.util.ArrayList<>();
        if (slices != null) {
            Color[][] fallbackPalettes = {
                { new Color(14, 165, 233), new Color(56, 189, 248) },   // Sky Blue
                { new Color(16, 185, 129), new Color(52, 211, 153) },   // Emerald
                { new Color(139, 92, 246), new Color(167, 139, 250) },  // Violet
                { new Color(245, 158, 11), new Color(251, 191, 36) },   // Amber
                { new Color(236, 72, 153), new Color(244, 114, 182) }   // Rose/Pink
            };
            int fallbackIdx = 0;

            for (java.util.Map.Entry<String, Double> entry : slices.entrySet()) {
                String key = entry.getKey();
                double val = Math.max(0.0, entry.getValue());
                if (val <= 0.001) continue;

                Color c1, c2;
                String tip;
                switch (key.toLowerCase()) {
                    case "sleep" -> {
                        c1 = new Color(14, 165, 233); // #0EA5E9 Sky
                        c2 = new Color(56, 189, 248); // #38BDF8
                        tip = "Healthy benchmark: 7.0–9.0 hrs/day for optimal retention & focus";
                    }
                    case "study" -> {
                        c1 = new Color(16, 185, 129); // #10B981 Emerald
                        c2 = new Color(52, 211, 153); // #34D399
                        tip = "Core self-study and academic prep outside scheduled classes";
                    }
                    case "screen" -> {
                        c1 = new Color(139, 92, 246); // #8B5CF6 Violet
                        c2 = new Color(167, 139, 250); // #A78BFA
                        tip = "Digital screen exposure (entertainment, social media, devices)";
                    }
                    case "exercise" -> {
                        c1 = new Color(245, 158, 11); // #F59E0B Amber
                        c2 = new Color(251, 191, 36); // #FBBF24
                        tip = "Physical activity & sports (recommended: 1.0–2.0 hrs/day)";
                    }
                    case "other" -> {
                        c1 = new Color(236, 72, 153); // #EC4899 Rose
                        c2 = new Color(244, 114, 182); // #F472B6
                        tip = "Meals, transit, grooming, and unstructured relaxation time";
                    }
                    default -> {
                        Color[] pal = fallbackPalettes[fallbackIdx % fallbackPalettes.length];
                        fallbackIdx++;
                        c1 = pal[0];
                        c2 = pal[1];
                        tip = key + " daily time allocation";
                    }
                }
                sliceList.add(new SliceInfo(key, val, c1, c2, tip));
            }
        }

        final double totalHours = sliceList.stream().mapToDouble(s -> s.value).sum();

        JPanel chartComponent = new JPanel() {
            private int hoveredIndex = -1;

            {
                ToolTipManager.sharedInstance().registerComponent(this);
                ToolTipManager.sharedInstance().setInitialDelay(50);
                ToolTipManager.sharedInstance().setDismissDelay(8000);
                setToolTipText(""); // Activate Swing tooltip handling

                java.awt.event.MouseAdapter mouseHandler = new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int w = getWidth();
                        int h = getHeight();
                        if (sliceList.isEmpty() || totalHours <= 0.001 || w <= 0 || h <= 0) return;

                        int donutAreaW = (int) (w * 0.48);
                        int maxDiam = Math.min(donutAreaW - 16, h - 24);
                        int size = Math.max(70, Math.min(maxDiam, 210));
                        int cx = donutAreaW / 2;
                        int cy = h / 2;
                        double outerR = size / 2.0;
                        double innerR = outerR * 0.62;

                        int mx = e.getX();
                        int my = e.getY();
                        int foundIndex = -1;

                        // 1. Hit-test Donut Ring
                        double dx = mx - cx;
                        double dy = my - cy;
                        double dist = Math.hypot(dx, dy);

                        if (dist >= innerR - 2 && dist <= outerR + 10) {
                            double mouseAngle = Math.toDegrees(Math.atan2(dy, dx));
                            double clkAngle = mouseAngle + 90.0;
                            if (clkAngle < 0) clkAngle += 360.0;

                            double cum = 0.0;
                            for (int i = 0; i < sliceList.size(); i++) {
                                double extent = (sliceList.get(i).value / totalHours) * 360.0;
                                if (clkAngle >= cum && clkAngle < cum + extent) {
                                    foundIndex = i;
                                    break;
                                }
                                cum += extent;
                            }
                        }

                        // 2. Hit-test Legend Rows
                        if (foundIndex == -1) {
                            for (int i = 0; i < sliceList.size(); i++) {
                                if (sliceList.get(i).legendBounds.contains(mx, my)) {
                                    foundIndex = i;
                                    break;
                                }
                            }
                        }

                        if (foundIndex != hoveredIndex) {
                            hoveredIndex = foundIndex;
                            setCursor(hoveredIndex >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                            repaint();
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (hoveredIndex != -1) {
                            hoveredIndex = -1;
                            setCursor(Cursor.getDefaultCursor());
                            repaint();
                        }
                    }
                };

                addMouseListener(mouseHandler);
                addMouseMotionListener(mouseHandler);
            }

            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                if (hoveredIndex >= 0 && hoveredIndex < sliceList.size()) {
                    SliceInfo s = sliceList.get(hoveredIndex);
                    double pct = (s.value / totalHours) * 100.0;
                    String hexColor = String.format("#%02X%02X%02X", s.color1.getRed(), s.color1.getGreen(), s.color1.getBlue());
                    return "<html><div style='padding:6px; font-family: Segoe UI, sans-serif;'>"
                            + "<div style='font-size:13px; font-weight:bold; color:" + hexColor + ";'>● " + s.name + "</div>"
                            + "<div style='font-size:12px; font-weight:bold; color:#1F2937; margin:3px 0;'>"
                            + String.format("%.1f hrs", s.value) + " <span style='font-size:11px; font-weight:normal; color:#6B7280;'>(" + String.format("%.1f%% of day", pct) + ")</span></div>"
                            + "<div style='font-size:10px; color:#4B5563; max-width:210px; line-height:1.3;'>" + s.tipNote + "</div>"
                            + "</div></html>";
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(UITheme.CARD_BG);
                g2.fillRect(0, 0, w, h);

                if (sliceList.isEmpty() || totalHours <= 0.001) {
                    int d = Math.min(130, Math.min(w, h) - 40);
                    int ex = (w - d) / 2;
                    int ey = (h - d) / 2 - 10;
                    g2.setColor(UITheme.BORDER_COLOR);
                    g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{6f, 6f}, 0f));
                    g2.drawOval(ex, ey, d, d);

                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_SUBHEADER);
                    String msg = "No Student Data";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, ey + d / 2 + 5);

                    g2.setFont(UITheme.FONT_CAPTION);
                    String sub = "Import CSV to analyze daily lifestyle breakdown";
                    FontMetrics fmSub = g2.getFontMetrics();
                    g2.drawString(sub, (w - fmSub.stringWidth(sub)) / 2, ey + d + 22);
                    g2.dispose();
                    return;
                }

                // Partition layout: Left ~48% Donut, Right ~52% Legend
                int donutAreaW = (int) (w * 0.48);
                int maxDiam = Math.min(donutAreaW - 16, h - 24);
                int size = Math.max(70, Math.min(maxDiam, 210));
                int cx = donutAreaW / 2;
                int cy = h / 2;
                int x = cx - size / 2;
                int y = cy - size / 2;
                double outerR = size / 2.0;
                double innerR = outerR * 0.62;

                // Ambient drop shadow under donut
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillOval(x + 3, y + 6, size, size);
                g2.setColor(new Color(0, 0, 0, 4));
                g2.fillOval(x + 1, y + 3, size, size);

                // ── Render Slices ────────────────────────────────────────────────
                double currentAngle = 90.0;
                for (int i = 0; i < sliceList.size(); i++) {
                    SliceInfo s = sliceList.get(i);
                    double angle = (s.value / totalHours) * 360.0;
                    boolean isHovered = (hoveredIndex == i);

                    // Dynamic pop-out translation along radial bisector
                    double midAngle = currentAngle - angle / 2.0;
                    double rad = Math.toRadians(midAngle);
                    double popDist = isHovered ? 5.0 : 0.0;
                    double px = popDist * Math.cos(rad);
                    double py = -popDist * Math.sin(rad);

                    double sx = x + px;
                    double sy = y + py;

                    // Hover luminous glow aura behind the popped slice
                    if (isHovered) {
                        Graphics2D gGlow = (Graphics2D) g2.create();
                        gGlow.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        gGlow.setPaint(new GradientPaint((float)sx, (float)sy, s.color1, (float)(sx + size), (float)(sy + size), s.color2));
                        gGlow.setComposite(AlphaComposite.SrcOver.derive(0.35f));
                        gGlow.fill(new Arc2D.Double(sx - 4, sy - 4, size + 8, size + 8, currentAngle, -angle, Arc2D.PIE));
                        gGlow.dispose();
                    }

                    // Slice arc geometry
                    Arc2D.Double sliceArc = new Arc2D.Double(sx, sy, size, size, currentAngle, -angle, Arc2D.PIE);

                    // If another slice is hovered, slightly soften this one for contrast
                    float alpha = (hoveredIndex == -1 || isHovered) ? 1.0f : 0.50f;
                    Graphics2D gSlice = (Graphics2D) g2.create();
                    gSlice.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gSlice.setComposite(AlphaComposite.SrcOver.derive(alpha));
                    gSlice.setPaint(new GradientPaint((float)sx, (float)sy, s.color1, (float)(sx + size), (float)(sy + size), s.color2));
                    gSlice.fill(sliceArc);

                    // Separation gap stroke
                    gSlice.setColor(UITheme.CARD_BG);
                    gSlice.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    gSlice.draw(sliceArc);
                    gSlice.dispose();

                    currentAngle -= angle;
                }

                // ── Donut Inner Hole (HUD Center) ────────────────────────────────
                int innerDiam = (int) (innerR * 2.0);
                int ix = cx - (int) innerR;
                int iy = cy - (int) innerR;

                // Soft inner shadow & recessed surface
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillOval(ix - 2, iy - 1, innerDiam + 4, innerDiam + 4);
                g2.setColor(UITheme.CARD_BG);
                g2.fillOval(ix, iy, innerDiam, innerDiam);

                // Subtle inner border line
                g2.setColor(UITheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(ix, iy, innerDiam, innerDiam);

                // Center Typography (Interactive Feedback)
                if (hoveredIndex >= 0 && hoveredIndex < sliceList.size()) {
                    SliceInfo s = sliceList.get(hoveredIndex);
                    double pct = (s.value / totalHours) * 100.0;

                    // Line 1: Category Name
                    g2.setColor(s.color1);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, (int)(innerR * 0.25))));
                    FontMetrics fmLbl = g2.getFontMetrics();
                    String nameStr = s.name.toUpperCase();
                    g2.drawString(nameStr, cx - fmLbl.stringWidth(nameStr) / 2, cy - (int)(innerR * 0.26));

                    // Line 2: Hours Value
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(16, (int)(innerR * 0.44))));
                    FontMetrics fmVal = g2.getFontMetrics();
                    String valStr = String.format("%.1fh", s.value);
                    g2.drawString(valStr, cx - fmVal.stringWidth(valStr) / 2, cy + (int)(innerR * 0.18));

                    // Line 3: Percentage
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(9, (int)(innerR * 0.22))));
                    FontMetrics fmPct = g2.getFontMetrics();
                    String pctStr = String.format("%.0f%% of day", pct);
                    g2.drawString(pctStr, cx - fmPct.stringWidth(pctStr) / 2, cy + (int)(innerR * 0.50));
                } else {
                    // Default State: Total Daily Hours
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(18, (int)(innerR * 0.48))));
                    FontMetrics fmTotal = g2.getFontMetrics();
                    String totalStr = String.format("%.0fh", totalHours);
                    g2.drawString(totalStr, cx - fmTotal.stringWidth(totalStr) / 2, cy + (int)(innerR * 0.08));

                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(9, (int)(innerR * 0.22))));
                    FontMetrics fmSub = g2.getFontMetrics();
                    String subStr = "DAILY TOTAL";
                    g2.drawString(subStr, cx - fmSub.stringWidth(subStr) / 2, cy + (int)(innerR * 0.42));
                }

                // ── Right Side: Modern Interactive Legend ─────────────────────────
                int legendX = donutAreaW + 12;
                int legendW = Math.max(80, w - legendX - 10);
                int n = sliceList.size();
                int itemH = Math.min(42, Math.max(30, (h - 20) / n));
                int totalLegendH = n * itemH;
                int legendStartY = (h - totalLegendH) / 2;

                for (int i = 0; i < sliceList.size(); i++) {
                    SliceInfo s = sliceList.get(i);
                    int itemY = legendStartY + i * itemH;
                    boolean isHovered = (hoveredIndex == i);

                    // Update hit bounds for mouse events
                    s.legendBounds.setBounds(legendX - 4, itemY, legendW + 8, itemH - 4);

                    // Hover highlight row container
                    if (isHovered) {
                        g2.setColor(new Color(s.color1.getRed(), s.color1.getGreen(), s.color1.getBlue(), 18));
                        g2.fillRoundRect(legendX - 4, itemY, legendW + 8, itemH - 4, 8, 8);
                        g2.setColor(new Color(s.color1.getRed(), s.color1.getGreen(), s.color1.getBlue(), 80));
                        g2.setStroke(new BasicStroke(1.2f));
                        g2.drawRoundRect(legendX - 4, itemY, legendW + 8, itemH - 4, 8, 8);
                    }

                    int midY = itemY + (itemH - 4) / 2;

                    // Color Indicator Pill (Gradient with rounded corners)
                    int pipSize = 10;
                    int pipY = midY - pipSize / 2;
                    g2.setPaint(new GradientPaint(legendX + 2, pipY, s.color1, legendX + 2 + pipSize, pipY + pipSize, s.color2));
                    g2.fillRoundRect(legendX + 2, pipY, pipSize, pipSize, 4, 4);

                    // Category Name
                    g2.setColor(isHovered ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY);
                    g2.setFont(isHovered ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
                    g2.drawString(s.name, legendX + 18, midY + 5);

                    // Pill Badge on right: e.g. "7.8h · 32%"
                    double pct = (s.value / totalHours) * 100.0;
                    String badgeText = String.format("%.1fh · %.0f%%", s.value, pct);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fmBadge = g2.getFontMetrics();
                    int badgeTextW = fmBadge.stringWidth(badgeText);
                    int badgePadH = 8;
                    int badgeW = badgeTextW + badgePadH * 2;
                    int badgeH = 20;
                    int badgeX = legendX + legendW - badgeW;
                    int badgeY = midY - badgeH / 2;

                    // Badge background
                    Color badgeBg = isHovered
                            ? new Color(s.color1.getRed(), s.color1.getGreen(), s.color1.getBlue(), 30)
                            : new Color(243, 244, 246);
                    g2.setColor(badgeBg);
                    g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
                    g2.setColor(isHovered ? s.color1 : UITheme.BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);

                    // Badge text
                    g2.setColor(isHovered ? s.color1 : UITheme.TEXT_PRIMARY);
                    g2.drawString(badgeText, badgeX + badgePadH, badgeY + 14);
                }

                g2.dispose();
            }
        };

        chartComponent.setOpaque(true);
        chartComponent.setBackground(UITheme.CARD_BG);
        return chartComponent;
    }
}
