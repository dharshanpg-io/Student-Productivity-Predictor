package com.studentproductivity.ui;

import com.studentproductivity.data.DataPreprocessor;
import com.studentproductivity.model.EvaluationMetrics;
import com.studentproductivity.model.MLModel;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Model Performance Panel (Page 4) - Algorithm benchmark comparison, regression metrics,
 * actual vs predicted validation plots, and metric interpretation.
 */
public class ModelPerformancePanel extends JPanel {

    private final ProductivityService service;
    private final MainFrame mainFrame;

    private JTable comparisonTable;
    private DefaultTableModel tableModel;
    private JPanel chartContainer;
    private JLabel testSizeLabel;
    private JComboBox<String> activeModelCombo;

    public ModelPerformancePanel(ProductivityService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        initComponents();
        refreshPerformance();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_BG);
        JLabel title = new JLabel("Machine Learning Performance & Validation");
        title.setFont(UiUtils.FONT_HEADER_1);
        title.setForeground(UiUtils.COLOR_PRIMARY);

        testSizeLabel = new JLabel("Evaluation metrics derived from 20% held-out test data.");
        testSizeLabel.setFont(UiUtils.FONT_BODY);
        testSizeLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(testSizeLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Split: Top Benchmark Table, Bottom Chart + Explanation
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(null);

        // Top Benchmark Card
        JPanel topCard = UiUtils.createCardPanel();
        topCard.setLayout(new BorderLayout(8, 8));

        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Color.WHITE);
        JLabel tableTitle = new JLabel("Candidate Models Benchmark (Evaluated on Identical Test Split)");
        tableTitle.setFont(UiUtils.FONT_HEADER_3);
        tableTitle.setForeground(UiUtils.COLOR_PRIMARY);

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        comboPanel.setBackground(Color.WHITE);
        comboPanel.add(new JLabel("Active Inference Model:"));
        activeModelCombo = new JComboBox<>();
        activeModelCombo.addActionListener(e -> {
            String selected = (String) activeModelCombo.getSelectedItem();
            if (selected != null) {
                service.setActiveModel(selected);
                renderActualVsPredictedChart();
            }
        });
        comboPanel.add(activeModelCombo);

        topHeader.add(tableTitle, BorderLayout.WEST);
        topHeader.add(comboPanel, BorderLayout.EAST);
        topCard.add(topHeader, BorderLayout.NORTH);

        String[] cols = {"Algorithm / Model", "Family", "MAE (Score)", "RMSE (Score)", "R² (Variance)", "Max Residual", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        comparisonTable = new JTable(tableModel);
        comparisonTable.setFont(UiUtils.FONT_BODY);
        comparisonTable.setRowHeight(25);
        comparisonTable.getTableHeader().setFont(UiUtils.FONT_BODY_BOLD);
        comparisonTable.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane tableScroll = new JScrollPane(comparisonTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiUtils.COLOR_BORDER));
        topCard.add(tableScroll, BorderLayout.CENTER);

        splitPane.setTopComponent(topCard);

        // Bottom Content: Chart (Left) + Metric Explanation Cards (Right)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(UiUtils.COLOR_BG);

        // Chart Card
        JPanel chartCard = UiUtils.createCardPanel();
        chartCard.setLayout(new BorderLayout(5, 5));
        JLabel chartTitle = new JLabel("Actual vs. Predicted Productivity (Held-out Test Samples)");
        chartTitle.setFont(UiUtils.FONT_HEADER_3);
        chartTitle.setForeground(UiUtils.COLOR_PRIMARY);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartCard.add(chartContainer, BorderLayout.CENTER);
        bottomPanel.add(chartCard);

        // Explanations Card
        JPanel expCard = UiUtils.createCardPanel();
        expCard.setLayout(new BoxLayout(expCard, BoxLayout.Y_AXIS));

        JLabel expTitle = new JLabel("Regression Metric Explanations");
        expTitle.setFont(UiUtils.FONT_HEADER_3);
        expTitle.setForeground(UiUtils.COLOR_PRIMARY);
        expCard.add(expTitle);
        expCard.add(Box.createRigidArea(new Dimension(0, 10)));

        addMetricExplanation(expCard, "MAE (Mean Absolute Error)",
                "Average absolute discrepancy between actual and predicted productivity scores in original units (points). Lower is superior.");
        addMetricExplanation(expCard, "RMSE (Root Mean Squared Error)",
                "Square root of mean squared prediction errors. Heavily penalizes large outliers. Lower indicates closer fit.");
        addMetricExplanation(expCard, "R² (Coefficient of Determination)",
                "Proportion of variance in productivity score explainable by lifestyle features. Value closer to 1.0 demonstrates strong predictive utility.");
        addMetricExplanation(expCard, "Reproducibility Notice",
                "Evaluation is strictly computed on uncorrupted held-out test records using fixed seed 42 to prevent data leakage.");

        bottomPanel.add(expCard);

        splitPane.setBottomComponent(bottomPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private void addMetricExplanation(JPanel parent, String title, String text) {
        JLabel t = new JLabel(title);
        t.setFont(UiUtils.FONT_BODY_BOLD);
        t.setForeground(UiUtils.COLOR_TEXT_MAIN);

        JLabel d = new JLabel("<html><p style='width: 320px;'>" + text + "</p></html>");
        d.setFont(UiUtils.FONT_SMALL);
        d.setForeground(UiUtils.COLOR_TEXT_MUTED);

        parent.add(t);
        parent.add(Box.createRigidArea(new Dimension(0, 2)));
        parent.add(d);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public void refreshPerformance() {
        tableModel.setRowCount(0);
        activeModelCombo.removeAllItems();

        if (!service.isModelTrained()) {
            testSizeLabel.setText("Models have not been trained yet. Please train models from the Dashboard.");
            chartContainer.removeAll();
            chartContainer.add(new JLabel("Train models to view validation curves.", SwingConstants.CENTER));
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        DataPreprocessor.SplitResult split = service.getSplitResult();
        if (split != null) {
            testSizeLabel.setText(String.format("Training Set: %,d samples (80%%)  |  Held-out Test Set: %,d samples (20%%)",
                    split.getTrainY().length, split.getTestY().length));
        }

        Map<String, EvaluationMetrics> evalMap = service.getEvaluationRegistry();
        for (Map.Entry<String, EvaluationMetrics> entry : evalMap.entrySet()) {
            String modelName = entry.getKey();
            EvaluationMetrics m = entry.getValue();
            MLModel mod = service.getModelRegistry().get(modelName);
            String type = (mod != null) ? mod.getAlgorithmType() : "Regression";

            boolean isActive = service.getActiveModel() != null && service.getActiveModel().getName().equals(modelName);

            tableModel.addRow(new Object[]{
                    modelName,
                    type,
                    String.format("%.4f", m.getMae()),
                    String.format("%.4f", m.getRmse()),
                    String.format("%.4f", m.getRSquared()),
                    String.format("%.2f", m.getMaxResidual()),
                    isActive ? "ACTIVE CHAMPION" : "Evaluated"
            });

            activeModelCombo.addItem(modelName);
        }

        if (service.getActiveModel() != null) {
            activeModelCombo.setSelectedItem(service.getActiveModel().getName());
        }

        renderActualVsPredictedChart();
    }

    private void renderActualVsPredictedChart() {
        chartContainer.removeAll();
        DataPreprocessor.SplitResult split = service.getSplitResult();
        MLModel model = service.getActiveModel();

        if (split == null || model == null) {
            chartContainer.add(new JLabel("Test evaluation data unavailable.", SwingConstants.CENTER));
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        XYSeries actualVsPred = new XYSeries("Predictions vs Actual");
        XYSeries idealLine = new XYSeries("Ideal 45° Fit");

        idealLine.add(0.0, 0.0);
        idealLine.add(100.0, 100.0);

        // Sample up to 200 points for a crisp, high-performance scatter plot
        double[][] testX = split.getTestX();
        double[] testY = split.getTestY();
        int step = Math.max(1, testY.length / 200);

        for (int i = 0; i < testY.length; i += step) {
            double actual = testY[i];
            double pred = model.predict(testX[i]);
            actualVsPred.add(actual, pred);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(idealLine);
        dataset.addSeries(actualVsPred);

        JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                "Actual Kaggle Productivity Score",
                "Model Predicted Score",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(248, 250, 252));
        plot.setDomainGridlinePaint(UiUtils.COLOR_BORDER);
        plot.setRangeGridlinePaint(UiUtils.COLOR_BORDER);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // Series 0: Ideal line (Line visible, no shapes)
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesPaint(0, UiUtils.COLOR_DANGER);

        // Series 1: Scatter points (Shapes visible, no lines)
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesPaint(1, UiUtils.COLOR_PRIMARY_LIGHT);

        plot.setRenderer(renderer);

        ChartPanel cp = new ChartPanel(chart);
        chartContainer.add(cp, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
}
