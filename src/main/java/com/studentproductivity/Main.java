package com.studentproductivity;

import com.formdev.flatlaf.FlatLightLaf;
import com.studentproductivity.service.ProductivityService;
import com.studentproductivity.ui.MainFrame;

import javax.swing.*;

/**
 * Main application entry point for Student Productivity Predictor.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize Modern FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            System.err.println("Notice: FlatLaf initialization notice: " + e.getMessage());
        }

        // 2. Initialize Service & Pre-load Kaggle Dataset if available
        ProductivityService service = new ProductivityService();

        System.out.println("Starting Student Productivity Predictor application...");
        boolean loaded = service.loadDefaultDataset();
        if (loaded) {
            System.out.println("Default dataset auto-loaded (" + service.getDataset().size() + " records).");
            try {
                System.out.println("Training baseline and ensemble models on 80/20 train-test split...");
                service.trainAndEvaluateModels(42L);
                System.out.println("Models trained successfully. Champion model: " + service.getActiveModel().getName());
            } catch (Exception e) {
                System.err.println("Notice during startup training: " + e.getMessage());
            }
        } else {
            System.out.println("Default dataset not found at default path. User can load it manually from UI.");
        }

        // 3. Launch UI on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
