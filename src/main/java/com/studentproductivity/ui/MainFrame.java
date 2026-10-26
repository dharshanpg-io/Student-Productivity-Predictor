package com.studentproductivity.ui;

import com.studentproductivity.config.AppConfig;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main Application Window.
 * Houses sidebar navigation, top application header, and the central CardLayout container.
 */
public class MainFrame extends JFrame {

    public static final String PAGE_DASHBOARD = "DASHBOARD";
    public static final String PAGE_PREDICT = "PREDICT";
    public static final String PAGE_ANALYSIS = "ANALYSIS";
    public static final String PAGE_PERFORMANCE = "PERFORMANCE";
    public static final String PAGE_HISTORY = "HISTORY";
    public static final String PAGE_ABOUT = "ABOUT";

    private final ProductivityService service;
    private final CardLayout cardLayout;
    private final JPanel contentCards;

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String currentActivePage = PAGE_DASHBOARD;

    // Sub-panels
    private DashboardPanel dashboardPanel;
    private PredictionPanel predictionPanel;
    private DataAnalysisPanel analysisPanel;
    private ModelPerformancePanel performancePanel;
    private PredictionHistoryPanel historyPanel;
    private AboutProjectPanel aboutPanel;

    private JLabel dbStatusLabel;

    public MainFrame(ProductivityService service) {
        this.service = service;
        this.cardLayout = new CardLayout();
        this.contentCards = new JPanel(cardLayout);

        setTitle("Student Productivity Predictor — Academic AIML Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 800);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        initUi();
    }

    private void initUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiUtils.COLOR_BG);

        // 1. Top Header Bar
        JPanel topHeader = createTopHeader();
        root.add(topHeader, BorderLayout.NORTH);

        // 2. Left Sidebar Navigation
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 3. Central Content Cards
        dashboardPanel = new DashboardPanel(service, this);
        predictionPanel = new PredictionPanel(service, this);
        analysisPanel = new DataAnalysisPanel(service);
        performancePanel = new ModelPerformancePanel(service, this);
        historyPanel = new PredictionHistoryPanel(service);
        aboutPanel = new AboutProjectPanel();

        contentCards.add(dashboardPanel, PAGE_DASHBOARD);
        contentCards.add(predictionPanel, PAGE_PREDICT);
        contentCards.add(analysisPanel, PAGE_ANALYSIS);
        contentCards.add(performancePanel, PAGE_PERFORMANCE);
        contentCards.add(historyPanel, PAGE_HISTORY);
        contentCards.add(aboutPanel, PAGE_ABOUT);

        root.add(contentCards, BorderLayout.CENTER);
        setContentPane(root);

        updateNavSelection(PAGE_DASHBOARD);
    }

    private JPanel createTopHeader() {
        AppConfig cfg = AppConfig.getInstance();
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left Branding
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("📊");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(cfg.getAppName());
        titleLabel.setFont(UiUtils.FONT_HEADER_2);
        titleLabel.setForeground(Color.WHITE);

        JLabel verBadge = UiUtils.createBadge("v" + cfg.getAppVersion(), new Color(255, 255, 255, 40), Color.WHITE);

        brandPanel.add(logoLabel);
        brandPanel.add(titleLabel);
        brandPanel.add(verBadge);

        // Right Team Credits & DB Indicator
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel teamLabel = new JLabel(String.format("Team: %s (%s) & %s (%s)",
                cfg.getStudent1Name(), cfg.getStudent1Reg(), cfg.getStudent2Name(), cfg.getStudent2Reg()));
        teamLabel.setFont(UiUtils.FONT_SMALL);
        teamLabel.setForeground(new Color(226, 232, 240));

        dbStatusLabel = UiUtils.createBadge("Checking DB...", new Color(255, 255, 255, 40), Color.WHITE);
        updateDbStatus();

        rightPanel.add(teamLabel);
        rightPanel.add(dbStatusLabel);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiUtils.COLOR_BORDER));

        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        addNavButton(sidebar, "Dashboard", PAGE_DASHBOARD);
        addNavButton(sidebar, "Predict Productivity", PAGE_PREDICT);
        addNavButton(sidebar, "Data Analysis", PAGE_ANALYSIS);
        addNavButton(sidebar, "Model Performance", PAGE_PERFORMANCE);
        addNavButton(sidebar, "Prediction History", PAGE_HISTORY);
        addNavButton(sidebar, "About Project", PAGE_ABOUT);

        sidebar.add(Box.createVerticalGlue());

        // Footer in sidebar
        JLabel footerLbl = new JLabel("<html><center><small>CSE (AIML) Academic Project<br/>Java 17 & Machine Learning</small></center></html>", SwingConstants.CENTER);
        footerLbl.setFont(UiUtils.FONT_SMALL);
        footerLbl.setForeground(UiUtils.COLOR_TEXT_MUTED);
        footerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLbl.setBorder(new EmptyBorder(10, 10, 15, 10));
        sidebar.add(footerLbl);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String label, String pageId) {
        JButton btn = new JButton(label);
        btn.setFont(UiUtils.FONT_BODY_BOLD);
        btn.setForeground(UiUtils.COLOR_TEXT_MAIN);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> navigateTo(pageId));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!currentActivePage.equals(pageId)) {
                    btn.setBackground(new Color(241, 245, 249));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!currentActivePage.equals(pageId)) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });

        navButtons.put(pageId, btn);
        sidebar.add(btn);
    }

    public void navigateTo(String pageId) {
        currentActivePage = pageId;
        cardLayout.show(contentCards, pageId);
        updateNavSelection(pageId);

        // Auto-refresh target tab
        switch (pageId) {
            case PAGE_DASHBOARD:
                dashboardPanel.refreshData();
                break;
            case PAGE_ANALYSIS:
                analysisPanel.refreshAnalysis();
                break;
            case PAGE_PERFORMANCE:
                performancePanel.refreshPerformance();
                break;
            case PAGE_HISTORY:
                historyPanel.refreshHistory();
                break;
        }
    }

    private void updateNavSelection(String activeId) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(activeId)) {
                btn.setBackground(new Color(238, 242, 255));
                btn.setForeground(UiUtils.COLOR_PRIMARY);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, UiUtils.COLOR_PRIMARY),
                        BorderFactory.createEmptyBorder(12, 16, 12, 20)
                ));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(UiUtils.COLOR_TEXT_MAIN);
                btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            }
        }
    }

    public void onDatasetUpdated() {
        dashboardPanel.refreshData();
        analysisPanel.refreshAnalysis();
    }

    public void onModelUpdated() {
        dashboardPanel.refreshData();
        performancePanel.refreshPerformance();
    }

    public void onPredictionGenerated() {
        dashboardPanel.refreshData();
        historyPanel.refreshHistory();
    }

    private void updateDbStatus() {
        boolean dbOnline = service.getDbManager().isDatabaseReachable();
        if (dbOnline) {
            dbStatusLabel.setText("MySQL: Online");
            dbStatusLabel.setBackground(new Color(16, 185, 129));
        } else {
            dbStatusLabel.setText("MySQL: Offline (Local Store)");
            dbStatusLabel.setBackground(new Color(245, 158, 11));
        }
    }
}
