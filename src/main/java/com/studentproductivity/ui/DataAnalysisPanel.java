package com.studentproductivity.ui;

import com.studentproductivity.data.Dataset;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Data Analysis Panel (Page 3) - Statistical profile of features, missing values,
 * descriptive summary statistics, and correlation visualizations.
 */
public class DataAnalysisPanel extends JPanel {

    private final ProductivityService service;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JPanel chartPanelContainer;
    private JLabel summaryLabel;

    public DataAnalysisPanel(ProductivityService service) {
        this.service = service;
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        initComponents();
        refreshAnalysis();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_BG);
        JLabel title = new JLabel("Exploratory Data Analysis");
        title.setFont(UiUtils.FONT_HEADER_1);
        title.setForeground(UiUtils.COLOR_PRIMARY);

        summaryLabel = new JLabel("Examining feature distributions and statistical correlations.");
        summaryLabel.setFont(UiUtils.FONT_BODY);
        summaryLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(summaryLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center split: Top Descriptive Table, Bottom Correlation Chart
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        // Top Table Card
        JPanel tableCard = UiUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout(5, 5));
        JLabel tableTitle = new JLabel("Descriptive Feature Statistics & Missing Values");
        tableTitle.setFont(UiUtils.FONT_HEADER_3);
        tableTitle.setForeground(UiUtils.COLOR_PRIMARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Feature Name", "Valid Rows", "Missing", "Min", "Max", "Mean", "Std Dev", "Median"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        statsTable = new JTable(tableModel);
        statsTable.setFont(UiUtils.FONT_BODY);
        statsTable.setRowHeight(24);
        statsTable.getTableHeader().setFont(UiUtils.FONT_BODY_BOLD);
        statsTable.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane tableScroll = new JScrollPane(statsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiUtils.COLOR_BORDER));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        splitPane.setTopComponent(tableCard);

        // Bottom Chart Card
        JPanel chartCard = UiUtils.createCardPanel();
        chartCard.setLayout(new BorderLayout(5, 5));
        JLabel chartTitle = new JLabel("Pearson Correlation of Features with Target (Productivity Score)");
        chartTitle.setFont(UiUtils.FONT_HEADER_3);
        chartTitle.setForeground(UiUtils.COLOR_PRIMARY);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.setBackground(Color.WHITE);
        chartCard.add(chartPanelContainer, BorderLayout.CENTER);

        splitPane.setBottomComponent(chartCard);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshAnalysis() {
        tableModel.setRowCount(0);
        chartPanelContainer.removeAll();

        if (!service.isDatasetLoaded()) {
            summaryLabel.setText("No dataset loaded. Please load data from the Dashboard.");
            chartPanelContainer.add(new JLabel("No data available for analysis.", SwingConstants.CENTER));
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();
            return;
        }

        Dataset ds = service.getDataset();
        summaryLabel.setText(String.format("Loaded Dataset: %,d rows across %d features | Target: %s",
                ds.size(), ds.getColumnNames().size(), ds.hasTarget() ? "productivity_score" : "None"));

        // Populate table
        for (Map.Entry<String, Dataset.FeatureStats> entry : ds.getAllStatistics().entrySet()) {
            Dataset.FeatureStats s = entry.getValue();
            tableModel.addRow(new Object[]{
                    s.getFeatureName(),
                    String.format("%,d", s.getCount()),
                    s.getMissingCount(),
                    String.format("%.2f", s.getMin()),
                    String.format("%.2f", s.getMax()),
                    String.format("%.2f", s.getMean()),
                    String.format("%.2f", s.getStandardDeviation()),
                    String.format("%.2f", s.getMedian())
            });
        }

        // Render Correlation Bar Chart
        Map<String, Double> correlations = ds.getCorrelationsWithTarget();
        if (!correlations.isEmpty()) {
            DefaultCategoryDataset catData = new DefaultCategoryDataset();
            for (Map.Entry<String, Double> entry : correlations.entrySet()) {
                catData.addValue(entry.getValue(), "Correlation (r)", entry.getKey());
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    null,
                    "Feature Name",
                    "Correlation with Productivity Score (r)",
                    catData,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            barChart.setBackgroundPaint(Color.WHITE);
            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(new Color(248, 250, 252));
            plot.setDomainGridlinePaint(UiUtils.COLOR_BORDER);
            plot.setRangeGridlinePaint(UiUtils.COLOR_BORDER);

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, UiUtils.COLOR_PRIMARY_LIGHT);

            ChartPanel cp = new ChartPanel(barChart);
            cp.setPreferredSize(new Dimension(800, 260));
            chartPanelContainer.add(cp, BorderLayout.CENTER);
        } else {
            chartPanelContainer.add(new JLabel("Target variable not present to compute correlation chart.", SwingConstants.CENTER));
        }

        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }
}
