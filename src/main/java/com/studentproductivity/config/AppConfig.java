package com.studentproductivity.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application Configuration Manager.
 * Loads and manages configuration properties for database, files, and project metadata.
 */
public class AppConfig {

    private static final String APP_PROPS_FILE = "app.properties";
    private static final String DB_PROPS_FILE = "database.properties";

    private final Properties appProperties = new Properties();
    private final Properties dbProperties = new Properties();

    private static AppConfig instance;

    private AppConfig() {
        loadProperties(appProperties, APP_PROPS_FILE);
        loadProperties(dbProperties, DB_PROPS_FILE);
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties(Properties props, String resourceName) {
        // First try to load from classpath
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load " + resourceName + " from classpath: " + e.getMessage());
        }

        // Second, try to load from working directory override if present
        File override = new File(resourceName);
        if (override.exists() && override.canRead()) {
            try (FileInputStream fis = new FileInputStream(override)) {
                props.load(fis);
            } catch (Exception e) {
                System.err.println("Notice: Could not load " + resourceName + " from local file: " + e.getMessage());
            }
        }
    }

    // Application Metadata
    public String getAppName() {
        return appProperties.getProperty("app.name", "Student Productivity Predictor");
    }

    public String getAppVersion() {
        return appProperties.getProperty("app.version", "1.0.0");
    }

    public String getStudent1Name() {
        return appProperties.getProperty("app.team.student1.name", "P.G. DHARSHAN");
    }

    public String getStudent1Reg() {
        return appProperties.getProperty("app.team.student1.reg", "RA2511026050012");
    }

    public String getStudent2Name() {
        return appProperties.getProperty("app.team.student2.name", "NITHISH RAJ A");
    }

    public String getStudent2Reg() {
        return appProperties.getProperty("app.team.student2.reg", "RA2511026050034");
    }

    public String getDefaultDatasetPath() {
        return appProperties.getProperty("app.dataset.defaultPath", "data/train.csv");
    }

    public String getDefaultModelPath() {
        return appProperties.getProperty("app.model.defaultPath", "models/trained_productivity_model.json");
    }

    // Database Properties
    public String getDbUrl() {
        return dbProperties.getProperty("db.url", "jdbc:mysql://localhost:3306/student_productivity_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    }

    public String getDbUsername() {
        return dbProperties.getProperty("db.username", "root");
    }

    public String getDbPassword() {
        return dbProperties.getProperty("db.password", "");
    }

    public String getDbDriver() {
        return dbProperties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public int getDbConnectTimeout() {
        try {
            return Integer.parseInt(dbProperties.getProperty("db.connectTimeout", "2000"));
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public boolean isDbAutoCreate() {
        return Boolean.parseBoolean(dbProperties.getProperty("db.autoCreateDatabase", "true"));
    }
}
