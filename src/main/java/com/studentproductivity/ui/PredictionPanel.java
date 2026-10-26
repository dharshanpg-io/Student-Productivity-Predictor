package com.studentproductivity.ui;

import com.studentproductivity.prediction.PredictionRequest;
import com.studentproductivity.prediction.PredictionResult;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Prediction Panel (Page 2) - Interactive form for entering student lifestyle and
 * academic factors, triggering ML inference, and displaying estimated scores and insights.
 */
public class PredictionPanel extends JPanel {

    private final ProductivityService service;
    private final MainFrame mainFrame;

    // Form Inputs
    private JSpinner ageSpinner;
    private JComboBox<String> genderCombo;
    private JSpinner studyHoursSpinner;
    private JSpinner sleepHoursSpinner;
    private JSpinner phoneUsageSpinner;
    private JSpinner socialMediaSpinner;
    private JSpinner youtubeSpinner;
    private JSpinner gamingSpinner;
    private JSpinner breaksSpinner;
    private JSpinner coffeeSpinner;
    private JSpinner exerciseSpinner;
    private JSpinner assignmentsSpinner;
    private JSpinner attendanceSpinner;
    private JSlider stressSlider;
    private JSpinner focusScoreSpinner;
    private JSpinner finalGradeSpinner;

    // Results Display
    private JPanel resultCard;
    private JLabel scoreValueLabel;
    private JLabel categoryBadge;
    private JLabel confidenceIntervalLabel;
    private JTextArea insightsArea;
    private JButton predictButton;
    private JProgressBar progressBar;

    public PredictionPanel(ProductivityService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_BG);
        JLabel titleLbl = new JLabel("Productivity Score Prediction");
        titleLbl.setFont(UiUtils.FONT_HEADER_1);
        titleLbl.setForeground(UiUtils.COLOR_PRIMARY);

        JLabel subLbl = new JLabel("Enter your lifestyle and study habits to estimate a data-driven productivity index.");
        subLbl.setFont(UiUtils.FONT_BODY);
        subLbl.setForeground(UiUtils.COLOR_TEXT_MUTED);

        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center split: Left Form (in JScrollPane), Right Result View
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(UiUtils.COLOR_BG);

        // Left Form
        JPanel formCard = UiUtils.createCardPanel();
        formCard.setLayout(new BorderLayout(10, 10));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Initialize fields
        ageSpinner = new JSpinner(new SpinnerNumberModel(20, 15, 60, 1));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        studyHoursSpinner = new JSpinner(new SpinnerNumberModel(4.5, 0.0, 20.0, 0.5));
        sleepHoursSpinner = new JSpinner(new SpinnerNumberModel(7.5, 2.0, 16.0, 0.5));
        phoneUsageSpinner = new JSpinner(new SpinnerNumberModel(3.0, 0.0, 18.0, 0.5));
        socialMediaSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.0, 12.0, 0.5));
        youtubeSpinner = new JSpinner(new SpinnerNumberModel(1.5, 0.0, 12.0, 0.5));
        gamingSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 12.0, 0.5));
        breaksSpinner = new JSpinner(new SpinnerNumberModel(4, 0, 30, 1));
        coffeeSpinner = new JSpinner(new SpinnerNumberModel(150.0, 0.0, 1500.0, 25.0));
        exerciseSpinner = new JSpinner(new SpinnerNumberModel(45.0, 0.0, 300.0, 15.0));
        assignmentsSpinner = new JSpinner(new SpinnerNumberModel(12, 0, 50, 1));
        attendanceSpinner = new JSpinner(new SpinnerNumberModel(85.0, 0.0, 100.0, 1.0));

        stressSlider = new JSlider(1, 10, 5);
        stressSlider.setMajorTickSpacing(1);
        stressSlider.setPaintTicks(true);
        stressSlider.setPaintLabels(true);
        stressSlider.setBackground(Color.WHITE);

        focusScoreSpinner = new JSpinner(new SpinnerNumberModel(70.0, 0.0, 100.0, 5.0));
        finalGradeSpinner = new JSpinner(new SpinnerNumberModel(75.0, 0.0, 100.0, 5.0));

        addFormField(formGrid, gbc, row++, "Age (Years):", ageSpinner);
        addFormField(formGrid, gbc, row++, "Gender:", genderCombo);
        addFormField(formGrid, gbc, row++, "Daily Study Hours:", studyHoursSpinner);
        addFormField(formGrid, gbc, row++, "Nightly Sleep Hours:", sleepHoursSpinner);
        addFormField(formGrid, gbc, row++, "Phone Usage (Hours/day):", phoneUsageSpinner);
        addFormField(formGrid, gbc, row++, "Social Media (Hours/day):", socialMediaSpinner);
        addFormField(formGrid, gbc, row++, "YouTube Usage (Hours/day):", youtubeSpinner);
        addFormField(formGrid, gbc, row++, "Gaming Duration (Hours/day):", gamingSpinner);
        addFormField(formGrid, gbc, row++, "Breaks Taken per Day:", breaksSpinner);
        addFormField(formGrid, gbc, row++, "Daily Coffee Intake (mg):", coffeeSpinner);
        addFormField(formGrid, gbc, row++, "Daily Exercise (Minutes):", exerciseSpinner);
        addFormField(formGrid, gbc, row++, "Assignments Completed:", assignmentsSpinner);
        addFormField(formGrid, gbc, row++, "Attendance Percentage (%):", attendanceSpinner);
        addFormField(formGrid, gbc, row++, "Self-Reported Stress (1-10):", stressSlider);
        addFormField(formGrid, gbc, row++, "Focus Score (0-100):", focusScoreSpinner);
        addFormField(formGrid, gbc, row++, "Previous Term Grade (%):", finalGradeSpinner);

        JScrollPane formScroll = new JScrollPane(formGrid);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(14);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Buttons at bottom of form
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setBackground(Color.WHITE);

        JButton sampleBtn = UiUtils.createOutlineButton("Load Peer Sample");
        sampleBtn.addActionListener(e -> loadSampleProfile());

        predictButton = UiUtils.createPrimaryButton("Predict Productivity Score");
        predictButton.addActionListener(e -> runPrediction());

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        btnPanel.add(progressBar);
        btnPanel.add(sampleBtn);
        btnPanel.add(predictButton);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        centerPanel.add(formCard);

        // Right Result Card
        resultCard = UiUtils.createCardPanel();
        resultCard.setLayout(new BorderLayout(15, 15));

        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setBackground(Color.WHITE);
        JLabel resTitle = new JLabel("Estimation Results");
        resTitle.setFont(UiUtils.FONT_HEADER_2);
        resTitle.setForeground(UiUtils.COLOR_PRIMARY);
        resultHeader.add(resTitle, BorderLayout.WEST);

        categoryBadge = UiUtils.createBadge("Awaiting Input", UiUtils.COLOR_BORDER, UiUtils.COLOR_TEXT_MUTED);
        resultHeader.add(categoryBadge, BorderLayout.EAST);
        resultCard.add(resultHeader, BorderLayout.NORTH);

        JPanel scoreBox = new JPanel();
        scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));
        scoreBox.setBackground(new Color(248, 250, 252));
        scoreBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiUtils.COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        scoreValueLabel = new JLabel("--.-", SwingConstants.CENTER);
        scoreValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        scoreValueLabel.setForeground(UiUtils.COLOR_PRIMARY);

        JLabel maxLbl = new JLabel("Productivity Index (Scale: 0 to 100)", SwingConstants.CENTER);
        maxLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        maxLbl.setFont(UiUtils.FONT_SMALL);
        maxLbl.setForeground(UiUtils.COLOR_TEXT_MUTED);

        confidenceIntervalLabel = new JLabel("Confidence Interval: -- to --", SwingConstants.CENTER);
        confidenceIntervalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confidenceIntervalLabel.setFont(UiUtils.FONT_BODY);
        confidenceIntervalLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);

        scoreBox.add(scoreValueLabel);
        scoreBox.add(maxLbl);
        scoreBox.add(Box.createRigidArea(new Dimension(0, 8)));
        scoreBox.add(confidenceIntervalLabel);

        // Insights Section
        JPanel insightsPanel = new JPanel(new BorderLayout(5, 5));
        insightsPanel.setBackground(Color.WHITE);
        JLabel insTitle = new JLabel("Data-Driven Insights & Lifestyle Observations:");
        insTitle.setFont(UiUtils.FONT_HEADER_3);
        insTitle.setForeground(UiUtils.COLOR_TEXT_MAIN);

        insightsArea = new JTextArea();
        insightsArea.setFont(UiUtils.FONT_BODY);
        insightsArea.setLineWrap(true);
        insightsArea.setWrapStyleWord(true);
        insightsArea.setEditable(false);
        insightsArea.setBackground(new Color(250, 250, 250));
        insightsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        insightsArea.setText("Enter your daily habits and click 'Predict Productivity Score' to view individualized insights.");

        JScrollPane insScroll = new JScrollPane(insightsArea);
        insScroll.setBorder(new LineBorder(UiUtils.COLOR_BORDER, 1, true));

        insightsPanel.add(insTitle, BorderLayout.NORTH);
        insightsPanel.add(insScroll, BorderLayout.CENTER);

        // Disclaimer Note
        JLabel disclaimerLbl = new JLabel("<html><small><i>* Disclaimer: This academic model outputs statistical approximations based on training data. It does not dictate individual potential or provide medical guidance.</i></small></html>");
        disclaimerLbl.setForeground(UiUtils.COLOR_TEXT_MUTED);

        JPanel centerResult = new JPanel(new BorderLayout(0, 15));
        centerResult.setBackground(Color.WHITE);
        centerResult.add(scoreBox, BorderLayout.NORTH);
        centerResult.add(insightsPanel, BorderLayout.CENTER);
        centerResult.add(disclaimerLbl, BorderLayout.SOUTH);

        resultCard.add(centerResult, BorderLayout.CENTER);

        centerPanel.add(resultCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.45;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UiUtils.FONT_BODY_BOLD);
        lbl.setForeground(UiUtils.COLOR_TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        field.setFont(UiUtils.FONT_BODY);
        panel.add(field, gbc);
    }

    private void loadSampleProfile() {
        ageSpinner.setValue(21);
        genderCombo.setSelectedItem("Female");
        studyHoursSpinner.setValue(6.5);
        sleepHoursSpinner.setValue(8.0);
        phoneUsageSpinner.setValue(2.5);
        socialMediaSpinner.setValue(1.5);
        youtubeSpinner.setValue(1.0);
        gamingSpinner.setValue(0.5);
        breaksSpinner.setValue(5);
        coffeeSpinner.setValue(200.0);
        exerciseSpinner.setValue(45.0);
        assignmentsSpinner.setValue(16);
        attendanceSpinner.setValue(92.0);
        stressSlider.setValue(3);
        focusScoreSpinner.setValue(85.0);
        finalGradeSpinner.setValue(84.0);
    }

    private void runPrediction() {
        if (!service.isModelTrained()) {
            JOptionPane.showMessageDialog(this,
                    "The Machine Learning model is not yet trained.\nPlease go to Dashboard and click 'Train / Retrain Models'.",
                    "Model Not Trained", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PredictionRequest req = new PredictionRequest(
                (int) ageSpinner.getValue(),
                (String) genderCombo.getSelectedItem(),
                ((Number) studyHoursSpinner.getValue()).doubleValue(),
                ((Number) sleepHoursSpinner.getValue()).doubleValue(),
                ((Number) phoneUsageSpinner.getValue()).doubleValue(),
                ((Number) socialMediaSpinner.getValue()).doubleValue(),
                ((Number) youtubeSpinner.getValue()).doubleValue(),
                ((Number) gamingSpinner.getValue()).doubleValue(),
                (int) breaksSpinner.getValue(),
                ((Number) coffeeSpinner.getValue()).doubleValue(),
                ((Number) exerciseSpinner.getValue()).doubleValue(),
                (int) assignmentsSpinner.getValue(),
                ((Number) attendanceSpinner.getValue()).doubleValue(),
                stressSlider.getValue(),
                ((Number) focusScoreSpinner.getValue()).doubleValue(),
                ((Number) finalGradeSpinner.getValue()).doubleValue()
        );

        List<String> errors = req.validate();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n• ", errors),
                    "Input Validation Issues", JOptionPane.WARNING_MESSAGE);
            return;
        }

        predictButton.setEnabled(false);
        progressBar.setVisible(true);

        SwingWorker<PredictionResult, Void> worker = new SwingWorker<>() {
            @Override
            protected PredictionResult doInBackground() {
                return service.predict(req);
            }

            @Override
            protected void done() {
                predictButton.setEnabled(true);
                progressBar.setVisible(false);
                try {
                    PredictionResult res = get();
                    displayResult(res);
                    mainFrame.onPredictionGenerated();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PredictionPanel.this,
                            "Prediction failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void displayResult(PredictionResult res) {
        scoreValueLabel.setText(String.format("%.1f", res.getPredictedScore()));
        Color catColor = Color.decode(res.getCategory().getHexColor());
        scoreValueLabel.setForeground(catColor);

        categoryBadge.setText(res.getCategory().getLabel());
        categoryBadge.setBackground(catColor);
        categoryBadge.setForeground(Color.WHITE);

        confidenceIntervalLabel.setText(String.format("Expected Range (95%% CI): %.1f to %.1f  |  Model: %s",
                res.getLowerBound(), res.getUpperBound(), res.getModelVersion()));

        StringBuilder sb = new StringBuilder();
        for (String ins : res.getPersonalizedInsights()) {
            sb.append("• ").append(ins).append("\n\n");
        }
        insightsArea.setText(sb.toString());
        insightsArea.setCaretPosition(0);
    }
}
