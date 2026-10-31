package com.analytics.student.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * High-DPI Vector Icon Factory for EduTrack Navigation System.
 * Renders anti-aliased modern SVG-quality icons that dynamically match theme states.
 */
public class NavIconFactory {

    public static Icon getIcon(String navKey) {
        return new NavVectorIcon(navKey);
    }

    private static class NavVectorIcon implements Icon {
        private final String key;
        private final int size = 18;

        public NavVectorIcon(String key) {
            this.key = key;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Determine dynamic state color
            Color iconColor = UITheme.TEXT_MUTED;
            if (c instanceof AbstractButton btn) {
                boolean active = Boolean.TRUE.equals(btn.getClientProperty("nav.active"));
                boolean hover = btn.getModel().isRollover();

                if (active) {
                    iconColor = UITheme.PRIMARY_ACCENT;
                } else if (hover) {
                    iconColor = UITheme.TEXT_PRIMARY;
                } else {
                    iconColor = UITheme.TEXT_MUTED;
                }
            }

            g2.setColor(iconColor);

            switch (key) {
                case "Dashboard" -> drawDashboard(g2, x, y);
                case "Import data" -> drawImportData(g2, x, y);
                case "Correlation" -> drawCorrelation(g2, x, y);
                case "Groups" -> drawGroups(g2, x, y);
                case "Anomalies" -> drawAnomalies(g2, x, y);
                case "Reports" -> drawReports(g2, x, y);
                case "AI Chat" -> drawAiChat(g2, x, y);
                default -> drawDefault(g2, x, y);
            }

            g2.dispose();
        }

        private void drawDashboard(Graphics2D g2, int x, int y) {
            // Bento Grid Cards
            g2.fill(new RoundRectangle2D.Double(x + 1, y + 1, 6.5, 6.5, 2, 2));
            g2.fill(new RoundRectangle2D.Double(x + 9.5, y + 1, 6.5, 4.5, 2, 2));
            g2.fill(new RoundRectangle2D.Double(x + 1, y + 9.5, 6.5, 6.5, 2, 2));
            g2.fill(new RoundRectangle2D.Double(x + 9.5, y + 7.5, 6.5, 8.5, 2, 2));
        }

        private void drawImportData(Graphics2D g2, int x, int y) {
            // Folder with Upload Arrow
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D folder = new Path2D.Double();
            folder.moveTo(x + 2, y + 5);
            folder.lineTo(x + 6, y + 5);
            folder.lineTo(x + 8, y + 7);
            folder.lineTo(x + 15, y + 7);
            folder.lineTo(x + 15, y + 15);
            folder.lineTo(x + 2, y + 15);
            folder.closePath();
            g2.draw(folder);

            // Center Upward Arrow
            g2.drawLine(x + 8, y + 13, x + 8, y + 9);
            g2.drawLine(x + 6, y + 11, x + 8, y + 9);
            g2.drawLine(x + 10, y + 11, x + 8, y + 9);
        }

        private void drawCorrelation(Graphics2D g2, int x, int y) {
            // Coordinate Trend Chart
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Axes
            g2.drawLine(x + 2, y + 2, x + 2, y + 15);
            g2.drawLine(x + 2, y + 15, x + 16, y + 15);

            // Trend Path
            Path2D path = new Path2D.Double();
            path.moveTo(x + 4, y + 12);
            path.lineTo(x + 8, y + 9);
            path.lineTo(x + 11, y + 10);
            path.lineTo(x + 15, y + 4);
            g2.draw(path);

            // Data Points
            g2.fill(new Ellipse2D.Double(x + 3, y + 11, 2.5, 2.5));
            g2.fill(new Ellipse2D.Double(x + 7, y + 8, 2.5, 2.5));
            g2.fill(new Ellipse2D.Double(x + 10, y + 9, 2.5, 2.5));
            g2.fill(new Ellipse2D.Double(x + 14, y + 3, 2.5, 2.5));
        }

        private void drawGroups(Graphics2D g2, int x, int y) {
            // Community / Dual User Silhouette
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Primary user
            g2.draw(new Ellipse2D.Double(x + 3.5, y + 2.5, 5, 5));
            Path2D body1 = new Path2D.Double();
            body1.moveTo(x + 1.5, y + 15);
            body1.curveTo(x + 1.5, y + 10.5, x + 10.5, y + 10.5, x + 10.5, y + 15);
            g2.draw(body1);

            // Secondary user
            g2.draw(new Ellipse2D.Double(x + 10.5, y + 4, 4, 4));
            Path2D body2 = new Path2D.Double();
            body2.moveTo(x + 11, y + 12.5);
            body2.curveTo(x + 12, y + 11.5, x + 15.5, y + 11.5, x + 16, y + 15);
            g2.draw(body2);
        }

        private void drawAnomalies(Graphics2D g2, int x, int y) {
            // Magnifying Glass with Focal Outlier Marker
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Double(x + 2, y + 2, 10, 10));
            g2.drawLine(x + 10, y + 10, x + 15, y + 15);

            // Center Outlier Dot
            g2.fill(new Ellipse2D.Double(x + 5.5, y + 5.5, 3, 3));
        }

        private void drawReports(Graphics2D g2, int x, int y) {
            // Document with Text Lines
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Double(x + 3, y + 1.5, 11, 15, 2, 2));

            // Content lines
            g2.drawLine(x + 6, y + 5, x + 11, y + 5);
            g2.drawLine(x + 6, y + 8, x + 11, y + 8);
            g2.drawLine(x + 6, y + 11, x + 9, y + 11);
        }

        private void drawAiChat(Graphics2D g2, int x, int y) {
            // Modern 4-point AI Star / Sparkle
            double cx = x + 8.5, cy = y + 9.5, r = 6.5;
            Path2D star = new Path2D.Double();
            star.moveTo(cx, cy - r);
            star.quadTo(cx, cy, cx + r, cy);
            star.quadTo(cx, cy, cx, cy + r);
            star.quadTo(cx, cy, cx - r, cy);
            star.quadTo(cx, cy, cx, cy - r);
            star.closePath();
            g2.fill(star);

            // Secondary minor sparkle
            double cx2 = x + 14, cy2 = y + 4, r2 = 3.0;
            Path2D star2 = new Path2D.Double();
            star2.moveTo(cx2, cy2 - r2);
            star2.quadTo(cx2, cy2, cx2 + r2, cy2);
            star2.quadTo(cx2, cy2, cx2, cy2 + r2);
            star2.quadTo(cx2, cy2, cx2 - r2, cy2);
            star2.quadTo(cx2, cy2, cx2, cy2 - r2);
            star2.closePath();
            g2.fill(star2);
        }

        private void drawDefault(Graphics2D g2, int x, int y) {
            g2.fill(new Ellipse2D.Double(x + 6, y + 6, 6, 6));
        }
    }
}
