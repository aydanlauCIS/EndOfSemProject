package com.example.endofsemproject;

import android.util.Log;
import java.io.Serializable;
import java.util.HashMap;

public class CountryEmission implements Serializable {
    private String countryName;
    private HashMap<Integer, Long> co2Emissions;
    private int population;
    private String percentageOfWorld;
    private String density;
    private static final String TAG = "CountryEmission";

    public CountryEmission(String countryName, HashMap<Integer, Long> co2Emissions,
                           int population, String percentageOfWorld, String density) {
        this.countryName = countryName;
        this.co2Emissions = co2Emissions;
        this.population = population;
        this.percentageOfWorld = percentageOfWorld;
        this.density = density;
    }

    public String getCountryName() {
        return countryName;
    }
    public HashMap<Integer, Long> getCo2Emissions() {
        return co2Emissions;
    }
    public int getPopulation() {
        return population;
    }
    public String getPercentageOfWorld() {
        return percentageOfWorld;
    }
    public String getDensity() {
        return density;
    }

    public void updateCo2Emissions(int year, long emissions) {
        co2Emissions.put(year, emissions);
    }

    public long getTotalEmissions() {
        long total = 0;
        for (int yr: co2Emissions.keySet()) {
            if (yr >= 1750 && yr <= 2022) {
                Log.d(TAG, String.valueOf(co2Emissions.getOrDefault(yr, Long.valueOf(0))));
                total += co2Emissions.getOrDefault(yr, Long.valueOf(0)); // potential null error fix later
            }
        }
        return total;
    }

    public double getAverageEmissionsPerCapita() {
        if (population == 0) {
            return 0;
        }

        return (double) getTotalEmissions() / population;
    }

    public int getYearWithHighestEmissions() {
        int max_yr = -1;
        long max_val = -1;

        for (int yr: co2Emissions.keySet()) {
            if (co2Emissions.getOrDefault(yr, Long.valueOf(0)) > max_val) { // again null pointer maybe
                max_val = co2Emissions.getOrDefault(yr, Long.valueOf(0));
                max_yr = yr;
            }
        }

        return max_yr;
    }
}
