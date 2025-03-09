package com.example.inki809;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private SearchView searchQuery;
    private LinearLayout translationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchQuery = findViewById(R.id.searchQuery);
        translationContainer = findViewById(R.id.translationContainer);

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
    }

    private void searchAndDisplayTranslation(String query) {
        translationContainer.removeAllViews();
        try {
            InputStream inputStream = getAssets().open("recnik.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
                    } else if (translation.equalsIgnoreCase(query)) {
                        TextView textView = new TextView(this);
                        textView.setText(word);
                        textView.setTextColor(getResources().getColor(R.color.white, getTheme()));
                        translationContainer.addView(textView);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}