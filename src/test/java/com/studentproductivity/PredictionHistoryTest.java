package com.studentproductivity;

import com.studentproductivity.database.DatabaseManager;
import com.studentproductivity.database.HistoryRecord;
import com.studentproductivity.database.LocalHistoryStore;
import com.studentproductivity.database.PredictionHistoryDao;
import com.studentproductivity.prediction.PredictionRequest;
import com.studentproductivity.prediction.PredictionResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PredictionHistoryTest {

    @Test
    void testLocalHistoryStorePersistenceAndFilter() {
        LocalHistoryStore localStore = new LocalHistoryStore();
        localStore.clear();

        DatabaseManager dbManager = new DatabaseManager(); // In offline test environment
        PredictionHistoryDao dao = new PredictionHistoryDao(dbManager, localStore);

        PredictionRequest req = new PredictionRequest(21, "Female", 6.0, 8.0, 2.0, 1.0, 0.5, 0.0,
                3, 150.0, 30.0, 12, 90.0, 3, 80.0, 85.0);
        PredictionResult res = new PredictionResult(75.5, 72.0, 79.0, 1.8, "RandomForest-Test", req, Collections.emptyList());

        boolean saved = dao.savePrediction(res);
        assertTrue(saved);

        List<HistoryRecord> list = dao.getAllHistory();
        assertFalse(list.isEmpty());
        assertEquals("Female", list.get(0).getGender());

        // Test search filter
        List<HistoryRecord> searchFemale = dao.searchHistory("female");
        assertFalse(searchFemale.isEmpty());

        List<HistoryRecord> searchUnknown = dao.searchHistory("nonexistent_token_xyz");
        assertTrue(searchUnknown.isEmpty());

        // Test delete
        long id = list.get(0).getId();
        dao.deleteById(id);
        List<HistoryRecord> afterDelete = dao.getAllHistory();
        assertTrue(afterDelete.stream().noneMatch(r -> r.getId() == id));
    }
}
