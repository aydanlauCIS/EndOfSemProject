package com.example.endofsemproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.filter), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NumberPicker yearPicker = findViewById(R.id.yearPicker);
        yearPicker.setMinValue(1750);
        yearPicker.setMaxValue(2022);
        yearPicker.setValue(2020);

//        HashMap<Integer, Long> t = new HashMap<>();
//        t.put(1111, 5050L);
//        CountryEmission temp = new CountryEmission("Test", t, 5050, "test", "test");

        yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateDataForYear(newVal);
        });
//
        updateDataForYear(yearPicker.getValue());

        Button back_btn = findViewById(R.id.back_button_filter);
        back_btn.setOnClickListener(v -> {
            Log.d("NAV", "Back button clicked");

            finish();
        });
    }

    private void updateDataForYear(int year) {
        CountryEmission country = MainActivity.getTopCountryForYear(year);

        TextView name = findViewById(R.id.country_name);
        TextView emission = findViewById(R.id.emission);

        name.setText(country.getCountryName());
        emission.setText(formatLargeNumber(country.getCo2Emissions().getOrDefault(year, 0L)));
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
