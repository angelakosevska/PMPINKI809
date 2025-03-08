package com.example.inki809;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private EditText searchQuery, tagQuery;
    private TextView taggedSearchesText;
    private Button btnSave, btnClear;
    private File dictionaryFile;
    private HashMap<String, String> wordMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchQuery = findViewById(R.id.searchQuery);
        tagQuery = findViewById(R.id.tagQuery);
        taggedSearchesText = findViewById(R.id.taggedSearchesText);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);

        dictionaryFile = new File(getFilesDir(), "recnik.txt");
        copyDictionaryIfNeeded(); // Ensure dictionary exists in internal storage
        loadDictionary();

        searchQuery.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchWord(searchQuery.getText().toString().trim());
                return true;
            }
            return false;
        });

        btnSave.setOnClickListener(v -> addWord(tagQuery.getText().toString().trim()));
        btnClear.setOnClickListener(v -> taggedSearchesText.setText("Matched words"));
    }

    // Copy recnik.txt from res/raw/ to internal storage if it doesn't exist
    private void copyDictionaryIfNeeded() {
        if (!dictionaryFile.exists()) {
            try (InputStream inputStream = getResources().openRawResource(R.raw.recnik);
                 FileOutputStream outputStream = new FileOutputStream(dictionaryFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Load dictionary from internal storage
    private void loadDictionary() {
        wordMap.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    wordMap.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Search for a word in the dictionary
    private void searchWord(String query) {
        if (query.isEmpty()) {
            taggedSearchesText.setText("Matched words");
            return;
        }

        taggedSearchesText.setText("Matched words");

        LinearLayout resultsContainer = findViewById(R.id.resultsContainer); // Ensure this ID exists in XML
        resultsContainer.removeAllViews(); // Clear previous results

        String translation = wordMap.get(query.toLowerCase());
        if (translation != null) {
            Button resultButton = new Button(this);
            resultButton.setText(query + " → " + translation);
            resultButton.setBackgroundColor(getResources().getColor(R.color.main));
            resultButton.setTextColor(getResources().getColor(R.color.white));
            resultButton.setTextSize(16);
            resultButton.setPadding(10, 10, 10, 10);

            resultsContainer.addView(resultButton);
        } else {
            taggedSearchesText.setText("No match found");
        }
    }

    // Add a new word pair to the dictionary
    private void addWord(String newEntry) {
        if (!newEntry.contains(",")) {
            taggedSearchesText.setText("Invalid format! Use: word, translation");
            return;
        }

        try (FileWriter writer = new FileWriter(dictionaryFile, true)) {
            writer.append(newEntry).append("\n");
            taggedSearchesText.setText("Word added: " + newEntry);
            loadDictionary(); // Reload dictionary
        } catch (IOException e) {
            e.printStackTrace();
            taggedSearchesText.setText("Error saving word");
        }
    }
}
