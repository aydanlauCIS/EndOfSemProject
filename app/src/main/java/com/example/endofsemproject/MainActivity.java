package com.example.endofsemproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<CountryEmission> data = new ArrayList<>();
    private final TableRow[] rows = new TableRow[10];
    private CountryEmission selected;
    private static final String TAG = "Main"; // good convention apparently
    private static HashMap<Integer, CountryEmission> topCountryByYear;

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

        setupSearchBar();
        initializeCountryRows();

        data = CSVReader.readCSV(MainActivity.this, "co2_emission_by_countries.csv");

        displayTop10Polluters();
        selected = null;
        selectRow(0);

        Button selectedCountryButton = findViewById(R.id.btnSelectedCountry);
        selectedCountryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CountryDetailActivity.class);
            intent.putExtra("COUNTRY_DATA", selected);
            startActivity(intent);
        });

        findTopCountryEachYear();
        Button filterYearButton = findViewById(R.id.btnFilterByYear);
        filterYearButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivity(intent);
        });
    }

    public static CountryEmission getTopCountryForYear(int year) {
        return topCountryByYear.get(year);
    }

    private void findTopCountryEachYear() {
        topCountryByYear = new HashMap<>();
        for (int yr = 1750; yr <= 2022; yr++) {
            long topEmissions = -1;
            for (CountryEmission country: data) {
                if (country.getCo2Emissions().getOrDefault(yr, 0L) > topEmissions) {
                    topCountryByYear.put(yr, country);
                    topEmissions = country.getCo2Emissions().getOrDefault(yr, 0L);

                }
            }
        }
    }

    private void setupSearchBar() {
        SearchView search_bar = findViewById(R.id.searchView);

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_bar.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_bar.getWindowToken(), 0);

                performSearch(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void performSearch(String name) {
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Data unavailable :(", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean found = false;
        for (CountryEmission country: data) {
            if (country.getCountryName().equalsIgnoreCase(name)) {
                Intent intent = new Intent(this, CountryDetailActivity.class);
                intent.putExtra("COUNTRY_DATA", country);
                startActivity(intent);

                found = true;
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Country not found :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeCountryRows() {
        rows[0] = findViewById(R.id.row1);
        rows[1] = findViewById(R.id.row2);
        rows[2] = findViewById(R.id.row3);
        rows[3] = findViewById(R.id.row4);
        rows[4] = findViewById(R.id.row5);
        rows[5] = findViewById(R.id.row6);
        rows[6] = findViewById(R.id.row7);
        rows[7] = findViewById(R.id.row8);
        rows[8] = findViewById(R.id.row9);
        rows[9] = findViewById(R.id.row10);

        for (int i = 0; i < rows.length; i++) {
            final int finalI = i;
            rows[i].setOnClickListener(v -> selectRow(finalI));
        }
    }

    private void selectRow(int i) {
        for (TableRow r: rows) {
            r.setBackgroundColor(Color.TRANSPARENT);
        }

        selected = data.get(i);
        rows[i].setBackgroundColor(Color.parseColor("#E3F2FD"));
    }

    private void displayTop10Polluters() {
        data.sort((c1, c2) -> {
            Long e1 = c1.getCo2Emissions().getOrDefault(2020, 0L);
            Long e2 = c2.getCo2Emissions().getOrDefault(2020, 0L);
            return e2.compareTo(e1); // Reverse for descending
        });

//        Log.d(TAG, String.valueOf(data.size()));

        for (int i = 0; i < 10; i++) {
            CountryEmission country = data.get(i);

            // This approach is bad but it works I guess
            TextView rank = rows[i].findViewById(getResources().getIdentifier(
                    "rank" + (i+1), "id", getPackageName()));

            TextView countryName = rows[i].findViewById(getResources().getIdentifier(
                    "country" + (i+1), "id", getPackageName()));

            TextView emissions = rows[i].findViewById(getResources().getIdentifier(
                    "emissions" + (i+1), "id", getPackageName()));

            rank.setText(String.valueOf(i + 1));
            countryName.setText(country.getCountryName());

            Long e = country.getCo2Emissions().get(2020);
            emissions.setText(formatLargeNumber(e));
        }
    }

    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format(Locale.US, "%.2fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format(Locale.US, "%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format(Locale.US, "%.1fK", number / 1_000.0);
        }
        return String.format(Locale.US, "%,d", number);
    }
}