package com.studentproductivity.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Local JSON-backed prediction history store used when MySQL is offline.
 * Ensures the application is fully functional and persists predictions offline.
 */
public class LocalHistoryStore {

    private static final String LOCAL_FILE = "data/prediction_history_local.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<HistoryRecord> cache = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);
    private final File storageFile;

    public LocalHistoryStore() {
        this.storageFile = new File(LOCAL_FILE);
        loadFromFile();
    }

    private synchronized void loadFromFile() {
        if (!storageFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(storageFile)) {
            Type listType = new TypeToken<ArrayList<HistoryRecord>>() {}.getType();
            List<HistoryRecord> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                cache.clear();
                cache.addAll(loaded);
                long maxId = 0;
                for (HistoryRecord r : cache) {
                    if (r.getId() > maxId) maxId = r.getId();
                }
                idSequence.set(maxId + 1);
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load local history file: " + e.getMessage());
        }
    }

    private synchronized void persistToFile() {
        if (storageFile.getParentFile() != null && !storageFile.getParentFile().exists()) {
            storageFile.getParentFile().mkdirs();
        }
        try (Writer writer = new FileWriter(storageFile)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            System.err.println("Notice: Could not persist local history: " + e.getMessage());
        }
    }

    public synchronized void addRecord(HistoryRecord record) {
        if (record.getId() <= 0) {
            record.setId(idSequence.getAndIncrement());
        }
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        cache.add(0, record); // prepend newest
        persistToFile();
    }

    public synchronized List<HistoryRecord> getAll() {
        return new ArrayList<>(cache);
    }

    public synchronized boolean deleteById(long id) {
        boolean removed = cache.removeIf(r -> r.getId() == id);
        if (removed) {
            persistToFile();
        }
        return removed;
    }

    public synchronized void clear() {
        cache.clear();
        persistToFile();
    }
}
