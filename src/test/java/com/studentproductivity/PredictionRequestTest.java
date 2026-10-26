package com.studentproductivity;

import com.studentproductivity.prediction.PredictionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PredictionRequestTest {

    @Test
    void testValidPredictionRequest() {
        PredictionRequest req = new PredictionRequest(
                20, "Female", 5.0, 7.5, 2.0, 1.0, 0.5, 0.0,
                4, 150.0, 30.0, 15, 90.0, 4, 75.0, 82.0
        );
        List<String> errors = req.validate();
        assertTrue(errors.isEmpty(), "Valid request should produce zero validation errors.");
    }

    @Test
    void testExceeding24HoursDayThrowsError() {
        PredictionRequest req = new PredictionRequest(
                20, "Male", 18.0, 10.0, 2.0, 1.0, 0.5, 0.0,
                4, 150.0, 30.0, 15, 90.0, 4, 75.0, 82.0
        ); // Study 18h + Sleep 10h = 28h > 24h
        List<String> errors = req.validate();
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("cannot exceed 24 hours"));
    }

    @Test
    void testInvalidStressLevelThrowsError() {
        PredictionRequest req = new PredictionRequest(
                20, "Other", 5.0, 7.0, 2.0, 1.0, 0.5, 0.0,
                4, 150.0, 30.0, 15, 90.0, 15, 75.0, 82.0 // Stress 15 > 10
        );
        List<String> errors = req.validate();
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Stress level")));
    }
}
