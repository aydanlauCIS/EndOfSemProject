package com.example.endofsemproject;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVReader {
    private static final String TAG = "CSVReader"; // good convention apparently
    public static ArrayList<CountryEmission> readCSV(Context context, String filename) {
        ArrayList<CountryEmission> result = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String headerLine = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) { // cool syntax
                String[] values = parseLine(line);
                int col_no = values.length;

                if (col_no < 5) { // subject to change
                    Log.w(TAG, "Skipping line cuz too short: " + line);
                    continue;
                }

                String country = values[0].trim();

                HashMap<Integer, Long> year_emissions = new HashMap<>();
                int year = safeParseInt(values[3]);
                long emissions = safeParseLong(values[4]);
                year_emissions.put(year, emissions);

//                Log.d(TAG, "Year: " + year + ", emissions: " + emissions);

                int population = 0;
                if (col_no > 5) {
                    population = safeParseInt(values[5]); // good practice
                }

                String percentage = "N/A";
                if (col_no > 7) {
                    if (percentage.length() < 2) {
                        percentage = values[7].trim();
                    } else {
                        percentage = "N/A";
                    }
                }

                String density = "N/A";
                if (col_no > 8) {
                    density = values[8].trim();
                    if (density.length() > 4) {
                        density = density.substring(0, density.length() - 4);
                    } else {
                        density = "N/A";
                    }
                }

                boolean found = false;
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getCountryName().equals(country)) {
                        result.get(i).updateCo2Emissions(year, emissions);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    result.add(new CountryEmission(country, year_emissions, population, percentage, density));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + e.getMessage());
        }

        return result;
    }

    private static String[] parseLine(String line) {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder currVal = new StringBuilder();
        boolean double_quotes = false; // to deal with the stupid double quotes

        for (char c: line.toCharArray()) {
            if (c == '"') {
                double_quotes = !double_quotes;
            } else if (c == ',') {
                if (double_quotes) {
                    currVal.append(c);
                } else {
                    values.add(currVal.toString());
                    currVal = new StringBuilder();
                }
            } else {
                currVal.append(c);
            }
        }
        values.add(currVal.toString());

        return values.toArray(new String[0]); // cool syntax
    }

    private static int safeParseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) { // just in case there's something stupid
            return 0;
        }
    }

    private static long safeParseLong(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) { // just in case there's something stupid
            return 0;
        }
    }
}
