package com.studentproductivity.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * UI Utilities for consistent typography, modern colors, and styled card components.
 */
public class UiUtils {

    // Color Palette: Professional Academic Theme with Navy Accents
    public static final Color COLOR_PRIMARY = new Color(30, 58, 138);       // Navy #1E3A8A
    public static final Color COLOR_PRIMARY_LIGHT = new Color(59, 130, 246); // Blue #3B82F6
    public static final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color COLOR_BG = new Color(248, 250, 252);          // Light grey-blue #F8FAFC
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    public static final Color COLOR_BORDER = new Color(226, 232, 240);      // Slate 200
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);      // Emerald 500
    public static final Color COLOR_WARNING = new Color(245, 158, 11);      // Amber 500
    public static final Color COLOR_DANGER = new Color(239, 68, 68);        // Red 500

    // Typography
    public static final Font FONT_HEADER_1 = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_2 = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_HEADER_3 = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 28);

    /**
     * Creates a styled card panel with rounded border appearance.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Creates a primary filled button with navy blue styling.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARY);
            }
        });
        return btn;
    }

    /**
     * Creates a secondary outline button.
     */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    /**
     * Creates an informative metric indicator card.
     */
    public static JPanel createMetricCard(String title, String value, String subtext, Color accent) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(5, 5));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_SMALL);
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(FONT_METRIC);
        valLbl.setForeground(accent != null ? accent : COLOR_TEXT_MAIN);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(COLOR_TEXT_MUTED);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Creates a status badge label.
     */
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setFont(FONT_SMALL);
        badge.setForeground(fg);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return badge;
    }
}
