package com.os.timeguardian.backend.service;

import android.content.Context;
import java.io.*;
import java.util.*;

public class FileHelper {

    private Context context;
    private String fileName = "data.txt";

    public FileHelper(Context context) {
        this.context = context;
    }

    // Methode zum Speichern eines Datenpakets
    public void saveData(String key, String value) {
        List<Map.Entry<String, String>> entries = readAllData();
        boolean found = false;
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals(key)) {
                entry.setValue(value);
                found = true;
                break;
            }
        }
        if (!found) {
            entries.add(new AbstractMap.SimpleEntry<>(key, value));
        }
        writeAllData(entries);
    }

    // Methode zum Lesen der gespeicherten Daten
    public String readData() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis = context.openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // Methode zum Entfernen eines Datenpakets
    public void removeData(String key) {
        List<Map.Entry<String, String>> entries = readAllData();
        entries.removeIf(entry -> entry.getKey().equals(key));
        writeAllData(entries);
    }

    // Methode zum Aktualisieren eines Datenpakets
    public void updateData(String key, String newValue) {
        List<Map.Entry<String, String>> entries = readAllData();
        boolean found = false;
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals(key)) {
                entry.setValue(newValue);
                found = true;
                break;
            }
        }
        if (!found) {
            entries.add(new AbstractMap.SimpleEntry<>(key, newValue));
        }
        writeAllData(entries);
    }

    // Hilfsmethode zum Lesen aller Daten
    public List<Map.Entry<String, String>> readAllData() {
        List<Map.Entry<String, String>> entries = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    entries.add(new AbstractMap.SimpleEntry<>(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    // Hilfsmethode zum Schreiben aller Daten
    private void writeAllData(List<Map.Entry<String, String>> entries) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            for (Map.Entry<String, String> entry : entries) {
                String data = entry.getKey() + ":" + entry.getValue() + "\n";
                fos.write(data.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
