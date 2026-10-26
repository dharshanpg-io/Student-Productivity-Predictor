package com.studentproductivity;

import com.studentproductivity.data.Dataset;
import com.studentproductivity.data.StudentRecord;
import com.studentproductivity.prediction.InsightGenerator;
import com.studentproductivity.prediction.PredictionRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InsightGeneratorTest {

    @Test
    void testInsightGeneratorContainsNoMedicalClaims() {
        List<StudentRecord> records = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            records.add(new StudentRecord(i, i, 21, "Male", 5.0, 7.5, 3.0, 2.0, 1.0, 0.5, 4, 150, 30, 10, 85.0, 5, 70.0, 75.0, 60.0));
        }
        Dataset dataset = new Dataset(records, Arrays.asList("study_hours_per_day", "sleep_hours", "phone_usage_hours"));

        PredictionRequest req = new PredictionRequest(21, "Male", 1.0, 4.0, 8.0, 4.0, 2.0, 1.0, 2, 400.0, 10.0, 5, 60.0, 9, 40.0, 65.0);
        List<String> insights = InsightGenerator.generateInsights(req, dataset);

        assertNotNull(insights);
        assertFalse(insights.isEmpty());

        // Check academic disclaimer presence
        boolean hasDisclaimer = insights.stream().anyMatch(s -> s.contains("Academic Notice") || s.contains("Disclaimer") || s.contains("statistical associations"));
        assertTrue(hasDisclaimer, "Insights must contain academic disclaimer.");

        // Check sleep & study observations triggered
        boolean hasStudyObs = insights.stream().anyMatch(s -> s.contains("Study Duration"));
        boolean hasSleepObs = insights.stream().anyMatch(s -> s.contains("Sleep Habit"));
        assertTrue(hasStudyObs);
        assertTrue(hasSleepObs);
    }
}
