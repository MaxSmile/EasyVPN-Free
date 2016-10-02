package com.vasilkoff.easyvpnfree.model;

/**
 * Created by Kusenko on 01.10.2016.
 */

public class Country {

    private String CountryName;
    private String CapitalName;
    private double CapitalLatitude;
    private double CapitalLongitude;
    private String CountryCode;

    public Country(String countryName, String capitalName, double capitalLatitude, double capitalLongitude, String countryCode) {
        CountryName = countryName;
        CapitalName = capitalName;
        CapitalLatitude = capitalLatitude;
        CapitalLongitude = capitalLongitude;
        CountryCode = countryCode;
    }

    public String getCountryName() {
        return CountryName;
    }

    public String getCapitalName() {
        return CapitalName;
    }

    public double getCapitalLatitude() {
        return CapitalLatitude;
    }

    public double getCapitalLongitude() {
        return CapitalLongitude;
    }

    public String getCountryCode() {
        return CountryCode;
    }
}
