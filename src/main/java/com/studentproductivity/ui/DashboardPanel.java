package com.studentproductivity.ui;

import com.studentproductivity.data.Dataset;
import com.studentproductivity.prediction.PredictionResult;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Dashboard Panel (Page 1) - Overview of dataset status, model training, key metrics,
 * and quick navigation actions.
 */
public class DashboardPanel extends JPanel {

    private final ProductivityService service;
    private final MainFrame mainFrame;

    private JLabel statusDatasetLabel;
    private JLabel statusModelLabel;
    private JLabel countRecordsLabel;
    private JLabel countFeaturesLabel;
    private JLabel latestPredLabel;
    private JPanel chartContainer;

    public DashboardPanel(ProductivityService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // 1. Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UiUtils.COLOR_BG);

        JLabel titleLbl = new JLabel("Academic Dashboard");
        titleLbl.setFont(UiUtils.FONT_HEADER_1);
        titleLbl.setForeground(UiUtils.COLOR_PRIMARY);

        JLabel subtitleLbl = new JLabel("Machine Learning-Driven Student Lifestyle & Productivity Analysis");
        subtitleLbl.setFont(UiUtils.FONT_BODY);
        subtitleLbl.setForeground(UiUtils.COLOR_TEXT_MUTED);

        headerPanel.add(titleLbl, BorderLayout.NORTH);
        headerPanel.add(subtitleLbl, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content: Metrics Cards + Chart Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UiUtils.COLOR_BG);

        // Metric Grid (4 Cards)
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        metricsGrid.setBackground(UiUtils.COLOR_BG);
        metricsGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        statusDatasetLabel = new JLabel("Not Loaded");
        statusDatasetLabel.setFont(UiUtils.FONT_HEADER_2);
        JPanel card1 = UiUtils.createCardPanel();
        card1.setLayout(new BorderLayout(5, 5));
        card1.add(new JLabel("DATASET STATUS"), BorderLayout.NORTH);
        card1.add(statusDatasetLabel, BorderLayout.CENTER);
        card1.add(new JLabel("Kaggle Student Dataset"), BorderLayout.SOUTH);

        statusModelLabel = new JLabel("Untrained");
        statusModelLabel.setFont(UiUtils.FONT_HEADER_2);
        JPanel card2 = UiUtils.createCardPanel();
        card2.setLayout(new BorderLayout(5, 5));
        card2.add(new JLabel("MODEL STATUS"), BorderLayout.NORTH);
        card2.add(statusModelLabel, BorderLayout.CENTER);
        card2.add(new JLabel("Random Forest Regressor"), BorderLayout.SOUTH);

        countRecordsLabel = new JLabel("0");
        countRecordsLabel.setFont(UiUtils.FONT_METRIC);
        JPanel card3 = UiUtils.createCardPanel();
        card3.setLayout(new BorderLayout(5, 5));
        card3.add(new JLabel("VERIFIED RECORDS"), BorderLayout.NORTH);
        card3.add(countRecordsLabel, BorderLayout.CENTER);
        card3.add(new JLabel("Verified Training Rows"), BorderLayout.SOUTH);

        latestPredLabel = new JLabel("None Yet");
        latestPredLabel.setFont(UiUtils.FONT_HEADER_2);
        JPanel card4 = UiUtils.createCardPanel();
        card4.setLayout(new BorderLayout(5, 5));
        card4.add(new JLabel("LATEST PREDICTION"), BorderLayout.NORTH);
        card4.add(latestPredLabel, BorderLayout.CENTER);
        card4.add(new JLabel("Score / 100"), BorderLayout.SOUTH);

        metricsGrid.add(card1);
        metricsGrid.add(card2);
        metricsGrid.add(card3);
        metricsGrid.add(card4);

        centerPanel.add(metricsGrid);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Chart Container Card
        chartContainer = UiUtils.createCardPanel();
        chartContainer.setLayout(new BorderLayout());
        chartContainer.setPreferredSize(new Dimension(800, 320));
        centerPanel.add(chartContainer);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionsPanel.setBackground(UiUtils.COLOR_BG);

        JButton loadBtn = UiUtils.createOutlineButton("Load / Change Dataset CSV");
        loadBtn.addActionListener(e -> chooseAndLoadDataset());

        JButton trainBtn = UiUtils.createOutlineButton("Train / Retrain Models");
        trainBtn.addActionListener(e -> executeTraining());

        JButton predictBtn = UiUtils.createPrimaryButton("Predict Productivity ->");
        predictBtn.addActionListener(e -> mainFrame.navigateTo(MainFrame.PAGE_PREDICT));

        actionsPanel.add(loadBtn);
        actionsPanel.add(trainBtn);
        actionsPanel.add(predictBtn);

        add(actionsPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        if (service.isDatasetLoaded()) {
            Dataset ds = service.getDataset();
            statusDatasetLabel.setText("Active (" + ds.size() + ")");
            statusDatasetLabel.setForeground(UiUtils.COLOR_SUCCESS);
            countRecordsLabel.setText(String.format("%,d", ds.size()));
        } else {
            statusDatasetLabel.setText("Not Loaded");
            statusDatasetLabel.setForeground(UiUtils.COLOR_DANGER);
            countRecordsLabel.setText("0");
        }

        if (service.isModelTrained()) {
            statusModelLabel.setText(service.getActiveModel().getName());
            statusModelLabel.setForeground(UiUtils.COLOR_SUCCESS);
        } else {
            statusModelLabel.setText("Untrained");
            statusModelLabel.setForeground(UiUtils.COLOR_WARNING);
        }

        PredictionResult latest = service.getLatestPrediction();
        if (latest != null) {
            latestPredLabel.setText(String.format("%.1f", latest.getPredictedScore()));
            latestPredLabel.setForeground(Color.decode(latest.getCategory().getHexColor()));
        } else {
            latestPredLabel.setText("None Yet");
            latestPredLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);
        }

        renderOverviewChart();
    }

    private void renderOverviewChart() {
        chartContainer.removeAll();

        if (!service.isDatasetLoaded()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            JLabel emptyLabel = new JLabel("<html><center><h3>No Dataset Loaded</h3>Load the Kaggle dataset to view productivity score distributions.</center></html>");
            emptyLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            chartContainer.add(emptyPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        Dataset ds = service.getDataset();
        if (!ds.hasTarget()) {
            JLabel emptyLabel = new JLabel("Target variable 'productivity_score' not found in loaded data.", SwingConstants.CENTER);
            chartContainer.add(emptyLabel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        // Build Histogram of Productivity Score
        double[] targetValues = new double[ds.size()];
        for (int i = 0; i < ds.size(); i++) {
            targetValues[i] = ds.getRecords().get(i).getProductivityScore();
        }

        HistogramDataset histData = new HistogramDataset();
        histData.addSeries("Student Productivity Scores", targetValues, 20, 0.0, 100.0);

        JFreeChart chart = ChartFactory.createHistogram(
                "Productivity Score Distribution (Kaggle Dataset, N = " + ds.size() + ")",
                "Productivity Score (0 - 100)",
                "Student Frequency",
                histData,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(new Color(248, 250, 252));

        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new Dimension(800, 300));
        chartContainer.add(cp, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void chooseAndLoadDataset() {
        JFileChooser chooser = new JFileChooser(new File("data"));
        chooser.setDialogTitle("Select Student Productivity CSV File");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                service.loadDataset(selected);
                refreshData();
                mainFrame.onDatasetUpdated();
                JOptionPane.showMessageDialog(this,
                        "Successfully loaded " + service.getDataset().size() + " records from " + selected.getName(),
                        "Dataset Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load CSV: " + ex.getMessage(), "Loading Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void executeTraining() {
        if (!service.isDatasetLoaded()) {
            JOptionPane.showMessageDialog(this, "Please load a dataset before training.", "No Dataset", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                service.trainAndEvaluateModels(42L);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get();
                    refreshData();
                    mainFrame.onModelUpdated();
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Models successfully trained and evaluated on 20% test split!",
                            "Training Completed", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Error during training: " + e.getMessage(), "Training Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
