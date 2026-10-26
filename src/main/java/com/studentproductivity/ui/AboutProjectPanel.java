package com.studentproductivity.ui;

import com.studentproductivity.config.AppConfig;
import com.studentproductivity.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * About Project Panel (Page 6) - Formal academic documentation view showcasing student team
 * members, objectives, technology stack, dataset citations, limitations, and future work.
 */
public class AboutProjectPanel extends JPanel {

    public AboutProjectPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
    }

    private void initComponents() {
        AppConfig config = AppConfig.getInstance();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_BG);
        JLabel title = new JLabel("About Student Productivity Predictor");
        title.setFont(UiUtils.FONT_HEADER_1);
        title.setForeground(UiUtils.COLOR_PRIMARY);

        JLabel sub = new JLabel("Academic Machine Learning Project — Computer Science and Engineering (AIML)");
        sub.setFont(UiUtils.FONT_BODY);
        sub.setForeground(UiUtils.COLOR_TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Content Area (Scrollable Card)
        JPanel contentCard = UiUtils.createCardPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));

        // Team Banner Box
        JPanel teamBox = new JPanel(new GridLayout(1, 2, 20, 0));
        teamBox.setBackground(new Color(241, 245, 249));
        teamBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel s1 = new JPanel(new GridLayout(3, 1, 2, 2));
        s1.setBackground(new Color(241, 245, 249));
        JLabel s1Role = new JLabel("STUDENT DEVELOPER 1");
        s1Role.setFont(UiUtils.FONT_SMALL);
        s1Role.setForeground(UiUtils.COLOR_TEXT_MUTED);
        JLabel s1Name = new JLabel(config.getStudent1Name());
        s1Name.setFont(UiUtils.FONT_HEADER_2);
        s1Name.setForeground(UiUtils.COLOR_PRIMARY);
        JLabel s1Reg = new JLabel("Register No: " + config.getStudent1Reg());
        s1Reg.setFont(UiUtils.FONT_BODY_BOLD);
        s1.add(s1Role);
        s1.add(s1Name);
        s1.add(s1Reg);

        JPanel s2 = new JPanel(new GridLayout(3, 1, 2, 2));
        s2.setBackground(new Color(241, 245, 249));
        JLabel s2Role = new JLabel("STUDENT DEVELOPER 2");
        s2Role.setFont(UiUtils.FONT_SMALL);
        s2Role.setForeground(UiUtils.COLOR_TEXT_MUTED);
        JLabel s2Name = new JLabel(config.getStudent2Name());
        s2Name.setFont(UiUtils.FONT_HEADER_2);
        s2Name.setForeground(UiUtils.COLOR_PRIMARY);
        JLabel s2Reg = new JLabel("Register No: " + config.getStudent2Reg());
        s2Reg.setFont(UiUtils.FONT_BODY_BOLD);
        s2.add(s2Role);
        s2.add(s2Name);
        s2.add(s2Reg);

        teamBox.add(s1);
        teamBox.add(s2);
        contentCard.add(teamBox);
        contentCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sections
        addSection(contentCard, "1. Problem Statement",
                "Students commonly struggle to understand how daily behavioral choices—such as study hours, sleep " +
                "duration, screen distractions, stress, and attendance—interact to shape their academic productivity. " +
                "Without empirical feedback, students often rely on subjective impressions rather than measurable habit patterns.");

        addSection(contentCard, "2. Project Objectives",
                "• Develop an end-to-end, reproducible Machine Learning pipeline strictly in Java 17 without Python runtime dependencies.\n" +
                "• Train, compare, and validate candidate regression models (Baseline Mean, Ridge Regression, Decision Trees, and Random Forests) on authentic Kaggle student data.\n" +
                "• Offer an accessible desktop GUI built with modern Java Swing, real-time input validation, and interactive scientific charting via JFreeChart.\n" +
                "• Deliver personalized educational insights comparing user inputs against verified cohort distributions without unfounded medical or deterministic claims.");

        addSection(contentCard, "3. Verified Technology Stack (Java Only)",
                "• Programming Language: Java 17 LTS (Microsoft OpenJDK 17)\n" +
                "• Desktop Interface: Java Swing with modern FlatLaf light theme\n" +
                "• Machine Learning: Native Java Random Forest, Ridge Regression, Decision Tree, & Baseline Regressors\n" +
                "• Data Preprocessing: Java CSV Parser with missing-value imputation and outlier sanitization\n" +
                "• Scientific Visualizations: JFreeChart 1.5.4 (Distributions, Actual vs. Predicted, Correlations)\n" +
                "• Persistence: MySQL 8 with JDBC prepared statements + graceful JSON local fallback\n" +
                "• Build & Testing: Apache Maven 3.9.6, JUnit 5");

        addSection(contentCard, "4. Dataset Origin & Verification",
                "Source: Kaggle Competition — Student Performance Prediction Machine Learning Challenge\n" +
                "Verified Dimensions: 16,000 genuine training records across 19 features with verified numerical target 'productivity_score'.\n" +
                "Feature set encompasses verified lifestyle indicators: study hours, sleep duration, phone usage, coffee intake, breaks, exercise, attendance, and focus score.");

        addSection(contentCard, "5. Academic Limitations & Scope",
                "• Statistical Approximations: Model predictions are statistical estimates derived from historical cohort patterns; they do not dictate individual human potential.\n" +
                "• Non-Causal Nature: High correlation between study habits and productivity does not imply single-variable causality.\n" +
                "• Future Enhancements: Integration of longitudinal time-series tracking and adaptive hyperparameter grid search.");

        JScrollPane scroll = new JScrollPane(contentCard);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private void addSection(JPanel parent, String title, String text) {
        JLabel t = new JLabel(title);
        t.setFont(UiUtils.FONT_HEADER_3);
        t.setForeground(UiUtils.COLOR_PRIMARY);

        JTextArea a = new JTextArea(text);
        a.setFont(UiUtils.FONT_BODY);
        a.setForeground(UiUtils.COLOR_TEXT_MAIN);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setEditable(false);
        a.setBackground(Color.WHITE);
        a.setBorder(new EmptyBorder(4, 4, 12, 4));

        parent.add(t);
        parent.add(a);
    }
}
