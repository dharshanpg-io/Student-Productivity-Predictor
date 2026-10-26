package com.studentproductivity.ui;

import com.studentproductivity.database.HistoryRecord;
import com.studentproductivity.database.PredictionHistoryDao;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Prediction History Panel (Page 5) - Table of historical inference records with search,
 * deletion, full clear, and database connection state monitoring.
 */
public class PredictionHistoryPanel extends JPanel {

    private final ProductivityService service;
    private final PredictionHistoryDao dao;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusBadge;
    private JLabel recordCountLabel;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PredictionHistoryPanel(ProductivityService service) {
        this.service = service;
        this.dao = service.getHistoryDao();

        setLayout(new BorderLayout(15, 15));
        setBackground(UiUtils.COLOR_BG);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        initComponents();
        refreshHistory();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtils.COLOR_BG);

        JPanel titleArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleArea.setBackground(UiUtils.COLOR_BG);
        JLabel title = new JLabel("Prediction History & Audit Log");
        title.setFont(UiUtils.FONT_HEADER_1);
        title.setForeground(UiUtils.COLOR_PRIMARY);

        statusBadge = UiUtils.createBadge("Checking DB...", UiUtils.COLOR_BORDER, UiUtils.COLOR_TEXT_MAIN);

        titleArea.add(title);
        titleArea.add(statusBadge);

        recordCountLabel = new JLabel("Showing all saved student predictions.");
        recordCountLabel.setFont(UiUtils.FONT_BODY);
        recordCountLabel.setForeground(UiUtils.COLOR_TEXT_MUTED);

        header.add(titleArea, BorderLayout.NORTH);
        header.add(recordCountLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Content Card
        JPanel card = UiUtils.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search / Filter:"));
        searchField = new JTextField(20);
        searchField.setFont(UiUtils.FONT_BODY);
        searchField.addActionListener(e -> performSearch());
        JButton searchBtn = new JButton("Filter");
        searchBtn.addActionListener(e -> performSearch());
        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            refreshHistory();
        });

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);
        toolbar.add(searchPanel, BorderLayout.WEST);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(Color.WHITE);

        JButton exportBtn = UiUtils.createOutlineButton("Export CSV");
        exportBtn.addActionListener(e -> exportToCsv());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setForeground(UiUtils.COLOR_DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());

        JButton clearBtn = new JButton("Clear All History");
        clearBtn.setForeground(UiUtils.COLOR_DANGER);
        clearBtn.addActionListener(e -> clearAllHistory());

        actions.add(exportBtn);
        actions.add(deleteBtn);
        actions.add(clearBtn);
        toolbar.add(actions, BorderLayout.EAST);

        card.add(toolbar, BorderLayout.NORTH);

        // History Table
        String[] cols = {
                "ID", "Timestamp", "Age", "Gender", "Study (hrs)", "Sleep (hrs)",
                "Phone (hrs)", "Attendance (%)", "Stress", "Focus", "Predicted Score", "Model Version"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setFont(UiUtils.FONT_BODY);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(UiUtils.FONT_BODY_BOLD);
        historyTable.getTableHeader().setBackground(new Color(241, 245, 249));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtils.COLOR_BORDER));
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void refreshHistory() {
        // Update database connection status indicator
        boolean dbOnline = service.getDbManager().isDatabaseReachable();
        if (dbOnline) {
            statusBadge.setText("MySQL 8 Connected");
            statusBadge.setBackground(new Color(209, 250, 229));
            statusBadge.setForeground(new Color(6, 95, 70));
        } else {
            statusBadge.setText("Offline (Local JSON Storage Active)");
            statusBadge.setBackground(new Color(254, 243, 199));
            statusBadge.setForeground(new Color(146, 64, 14));
        }

        List<HistoryRecord> records = dao.getAllHistory();
        renderTableRows(records);
    }

    private void performSearch() {
        String term = searchField.getText();
        List<HistoryRecord> filtered = dao.searchHistory(term);
        renderTableRows(filtered);
    }

    private void renderTableRows(List<HistoryRecord> records) {
        tableModel.setRowCount(0);
        for (HistoryRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getCreatedAt() != null ? dateFormat.format(r.getCreatedAt()) : "--",
                    r.getAge(),
                    r.getGender(),
                    String.format("%.1f", r.getStudyHours()),
                    String.format("%.1f", r.getSleepHours()),
                    String.format("%.1f", r.getPhoneUsageHours()),
                    String.format("%.1f%%", r.getAttendancePercentage()),
                    r.getStressLevel(),
                    String.format("%.1f", r.getFocusScore()),
                    String.format("%.2f", r.getPredictedScore()),
                    r.getModelVersion()
            });
        }
        recordCountLabel.setText("Showing " + records.size() + " saved predictions.");
    }

    private void deleteSelected() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = (long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete prediction record #" + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteById(id);
            refreshHistory();
        }
    }

    private void clearAllHistory() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "History is already empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently clear all historical predictions?",
                "Confirm Clear History", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dao.clearHistory();
            refreshHistory();
        }
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No history records to export.", "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("prediction_history_export.csv"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                // Header
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    writer.write(tableModel.getColumnName(c) + (c == tableModel.getColumnCount() - 1 ? "\n" : ","));
                }
                // Rows
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        writer.write((val != null ? val.toString() : "") + (c == tableModel.getColumnCount() - 1 ? "\n" : ","));
                    }
                }
                JOptionPane.showMessageDialog(this, "Successfully exported history to " + file.getName(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
