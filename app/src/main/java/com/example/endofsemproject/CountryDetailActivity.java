package com.example.endofsemproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class CountryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_country_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.country_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CountryEmission country = (CountryEmission) getIntent().getSerializableExtra("COUNTRY_DATA");

        if (country != null) {
            displayCountryData(country);
        }

        Button back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(v -> {
            Log.d("NAV", "Back button clicked");

            finish();
        });
    }

    private void displayCountryData(CountryEmission country) {
        TextView name = findViewById(R.id.tvCountryName);
        TextView emissions_total = findViewById(R.id.tvTotalEmissions);
        TextView emissions_capita = findViewById(R.id.tvPerCapita);
        TextView peak_year = findViewById(R.id.tvPeakYear);
        TextView population = findViewById(R.id.tvPopulation);
        TextView percentage = findViewById(R.id.tvPercentage);
        TextView density = findViewById(R.id.tvDensity);

        name.setText(country.getCountryName());

        emissions_total.setText(formatLargeNumber(country.getTotalEmissions()) + " tons");

        emissions_capita.setText(String.format(Locale.US, "%.2f tons per person", country.getAverageEmissionsPerCapita()));

        peak_year.setText(String.valueOf(country.getYearWithHighestEmissions()));

        population.setText(formatLargeNumber(country.getPopulation()));

        percentage.setText(country.getPercentageOfWorld());

        density.setText(country.getDensity() + " people per km²");
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
