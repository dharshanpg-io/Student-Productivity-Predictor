package com.studentproductivity;

import com.studentproductivity.data.DataPreprocessor;
import com.studentproductivity.data.Dataset;
import com.studentproductivity.data.StudentRecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataPreprocessorTest {

    @Test
    void testExtractRawFeaturesAndOneHotEncoding() {
        DataPreprocessor preprocessor = new DataPreprocessor();

        StudentRecord male = new StudentRecord(1, 101, 21, "Male", 5.0, 8.0, 2.0, 1.0, 1.0, 0.5, 3, 100, 30, 15, 90.0, 4, 80.0, 85.0, 70.0);
        StudentRecord female = new StudentRecord(2, 102, 22, "Female", 6.0, 7.0, 3.0, 2.0, 0.5, 0.0, 4, 150, 45, 18, 95.0, 2, 85.0, 90.0, 75.0);
        StudentRecord other = new StudentRecord(3, 103, 23, "Other", 4.0, 6.0, 4.0, 3.0, 1.5, 1.0, 2, 200, 20, 10, 80.0, 6, 65.0, 70.0, 55.0);

        double[] featMale = preprocessor.extractRawFeatures(male);
        double[] featFemale = preprocessor.extractRawFeatures(female);
        double[] featOther = preprocessor.extractRawFeatures(other);

        assertEquals(DataPreprocessor.FEATURE_NAMES.length, featMale.length);

        // Check gender one-hot columns (index 1: Male, index 2: Female, index 3: Other)
        assertEquals(1.0, featMale[1]);
        assertEquals(0.0, featMale[2]);
        assertEquals(0.0, featMale[3]);

        assertEquals(0.0, featFemale[1]);
        assertEquals(1.0, featFemale[2]);
        assertEquals(0.0, featFemale[3]);

        assertEquals(0.0, featOther[1]);
        assertEquals(0.0, featOther[2]);
        assertEquals(1.0, featOther[3]);
    }

    @Test
    void testTrainTestSplitReproducibility() {
        DataPreprocessor preprocessor = new DataPreprocessor();
        List<StudentRecord> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new StudentRecord(i, i, 20, "Male", 4.0 + (i % 5), 7.0, 2.0, 1.0, 1.0, 0.5, 3, 100, 30, 10, 85.0, 5, 70.0, 75.0, 50.0 + (i % 40)));
        }
        Dataset dataset = new Dataset(list, Arrays.asList("id", "study_hours_per_day", "productivity_score"));

        DataPreprocessor.SplitResult split1 = preprocessor.splitTrainTest(dataset, 0.8, 42L);
        DataPreprocessor.SplitResult split2 = preprocessor.splitTrainTest(dataset, 0.8, 42L);

        assertEquals(80, split1.getTrainX().length);
        assertEquals(20, split1.getTestX().length);

        // Ensure reproducible identical splits with seed 42
        for (int i = 0; i < 20; i++) {
            assertEquals(split1.getTestY()[i], split2.getTestY()[i], 1e-9);
        }
    }
}
