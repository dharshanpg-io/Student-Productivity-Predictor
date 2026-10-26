package com.studentproductivity;

import com.studentproductivity.data.CsvLoader;
import com.studentproductivity.data.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CsvLoaderTest {

    private CsvLoader loader;

    @BeforeEach
    void setUp() {
        loader = new CsvLoader();
    }

    @Test
    void testLoadValidCsv(@TempDir Path tempDir) throws IOException {
        File csv = tempDir.resolve("test_valid.csv").toFile();
        try (FileWriter w = new FileWriter(csv)) {
            w.write("id,student_id,age,gender,study_hours_per_day,sleep_hours,phone_usage_hours,social_media_hours,youtube_hours,gaming_hours,breaks_per_day,coffee_intake_mg,exercise_minutes,assignments_completed,attendance_percentage,stress_level,focus_score,final_grade,productivity_score\n");
            w.write("1,101,20,Female,5.5,8.0,2.0,1.0,0.5,0.0,3,100,30,15,90.0,4,75,80.0,68.5\n");
            w.write("2,102,22,Male,7.0,7.0,3.0,2.0,1.0,1.0,4,200,45,18,95.0,2,85,88.0,78.2\n");
        }

        CsvLoader.LoadResult result = loader.loadCsv(csv);
        assertNotNull(result);
        assertEquals(2, result.getDataset().size());
        assertEquals(2, result.getReport().getValidRecordsLoaded());
        assertEquals(0, result.getReport().getSkippedRowsCount());
        assertTrue(result.getDataset().hasTarget());
    }

    @Test
    void testDuplicateIdDetection(@TempDir Path tempDir) throws IOException {
        File csv = tempDir.resolve("test_duplicates.csv").toFile();
        try (FileWriter w = new FileWriter(csv)) {
            w.write("id,student_id,age,gender,study_hours_per_day,sleep_hours,phone_usage_hours,social_media_hours,youtube_hours,gaming_hours,breaks_per_day,coffee_intake_mg,exercise_minutes,assignments_completed,attendance_percentage,stress_level,focus_score,final_grade,productivity_score\n");
            w.write("10,201,20,Female,5.0,8.0,2.0,1.0,0.5,0.0,3,100,30,15,90.0,4,75,80.0,65.0\n");
            w.write("10,201,20,Female,5.0,8.0,2.0,1.0,0.5,0.0,3,100,30,15,90.0,4,75,80.0,65.0\n"); // Duplicate ID
        }

        CsvLoader.LoadResult result = loader.loadCsv(csv);
        assertEquals(1, result.getDataset().size());
        assertEquals(1, result.getReport().getDuplicateRowsCount());
        assertEquals(1, result.getReport().getSkippedRowsCount());
    }

    @Test
    void testMissingEssentialColumnThrows(@TempDir Path tempDir) throws IOException {
        File csv = tempDir.resolve("test_invalid_schema.csv").toFile();
        try (FileWriter w = new FileWriter(csv)) {
            w.write("id,age,gender\n"); // Missing essential features
            w.write("1,20,Male\n");
        }

        assertThrows(IOException.class, () -> loader.loadCsv(csv));
    }
}
