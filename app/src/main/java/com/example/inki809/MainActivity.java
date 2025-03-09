package com.example.inki809;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private SearchView searchQuery;
    private LinearLayout translationContainer;
    private EditText tagQuery;
    private Button btnSave;
    private Button clearButton;

    private void copyAssetFileToInternalStorage(String filename) {
        File file = new File(getFilesDir(), filename);
        if (!file.exists()) {
            try {
                InputStream inputStream = getAssets().open(filename);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchQuery = findViewById(R.id.searchQuery);
        translationContainer = findViewById(R.id.translationContainer);
        tagQuery = findViewById(R.id.tagQuery);
        btnSave = findViewById(R.id.btnSave);
        clearButton = findViewById(R.id.clearButton);

        copyAssetFileToInternalStorage("recnik.txt"); // Copy on first run

        searchQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    searchAndDisplayTranslation(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newWords = tagQuery.getText().toString().trim();
                if (!newWords.isEmpty() && newWords.contains(",")) {
                    String[] parts = newWords.split(",");
                    if (parts.length == 2) {
                        String word1 = parts[0].trim();
                        String word2 = parts[1].trim();
                        addNewWord(word1, word2);
                        tagQuery.setText("");

                        Toast.makeText(MainActivity.this, "Word pair added!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translationContainer.removeAllViews();
                Toast.makeText(MainActivity.this, "Translations cleared!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchAndDisplayTranslation(String query) {
        translationContainer.removeAllViews();
        File file = new File(getFilesDir(), "recnik.txt");
        String filePath = file.getAbsolutePath();
        boolean translationFound = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    String translation = parts[1].trim();

                    if (word.equalsIgnoreCase(query)) {
                        TextView textView = new TextView(this);
                        textView.setText(translation);
                        textView.setTextColor(getResources().getColor(R.color.white, getTheme()));
                        translationContainer.addView(textView);
                        translationFound = true;
                    } else if (translation.equalsIgnoreCase(query)) {
                        TextView textView = new TextView(this);
                        textView.setText(word);
                        textView.setTextColor(getResources().getColor(R.color.white, getTheme()));
                        translationContainer.addView(textView);
                        translationFound = true;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!translationFound) {
            TextView noTranslation = new TextView(this);
            noTranslation.setText("No translation found");
            noTranslation.setTextColor(getResources().getColor(R.color.white, getTheme()));
            translationContainer.addView(noTranslation);
        }
    }

    private void addNewWord(String word1, String word2) {
        File file = new File(getFilesDir(), "recnik.txt");
        String filePath = file.getAbsolutePath();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(word1 + "," + word2);
            writer.newLine();
            Log.d("Dictionary", "Word pair added: " + word1 + "," + word2);
        } catch (IOException e) {
            Log.e("Dictionary", "Error adding word pair: " + e.getMessage());
        }
    }
}