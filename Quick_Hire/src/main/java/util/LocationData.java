/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author RINKAL
 */

/*
 * Utility class for managing State and City dropdown data.
 *
 * PURPOSE:
 * This class stores predefined states and their cities
 * used in Recruiter Job Posting forms.
 *
 * WHY THIS FILE EXISTS:
 * - Avoid hardcoding states/cities in JSF pages
 * - Keep location data centralized
 * - Make dropdown population easy
 * - Simplify future maintenance
 *
 * HOW IT WORKS:
 * 1. getStates()
 *    -> returns all available states
 *
 * 2. getCitiesByState(state)
 *    -> returns cities for selected state
 *
 * NOTE FOR FUTURE DEVELOPERS:
 * If you want to add new states or cities,
 * update only STATE_CITY_MAP below.
 */



public class LocationData {

    // Stores State -> Cities mapping
    private static final Map<String, List<String>> STATE_CITY_MAP
            = new LinkedHashMap<>();

    static {

        // ================= GUJARAT =================
        STATE_CITY_MAP.put("Gujarat", Arrays.asList(
                "Ahmedabad",
                "Surat",
                "Vadodara",
                "Rajkot",
                "Bhavnagar",
                "Jamnagar"
        ));

        // ================= MAHARASHTRA =================
        STATE_CITY_MAP.put("Maharashtra", Arrays.asList(
                "Mumbai",
                "Pune",
                "Nagpur",
                "Nashik",
                "Thane",
                "Aurangabad"
        ));

        // ================= KARNATAKA =================
        STATE_CITY_MAP.put("Karnataka", Arrays.asList(
                "Bengaluru",
                "Mysuru",
                "Hubli",
                "Mangalore",
                "Belagavi"
        ));

        // ================= DELHI =================
        STATE_CITY_MAP.put("Delhi", Arrays.asList(
                "New Delhi",
                "Dwarka",
                "Rohini",
                "Saket",
                "Karol Bagh"
        ));

        // ================= TAMIL NADU =================
        STATE_CITY_MAP.put("Tamil Nadu", Arrays.asList(
                "Chennai",
                "Coimbatore",
                "Madurai",
                "Salem",
                "Tiruchirappalli"
        ));

        // ================= RAJASTHAN =================
        STATE_CITY_MAP.put("Rajasthan", Arrays.asList(
                "Jaipur",
                "Udaipur",
                "Jodhpur",
                "Kota",
                "Ajmer"
        ));

        // ================= UTTAR PRADESH =================
        STATE_CITY_MAP.put("Uttar Pradesh", Arrays.asList(
                "Lucknow",
                "Kanpur",
                "Noida",
                "Agra",
                "Varanasi"
        ));

        // ================= MADHYA PRADESH =================
        STATE_CITY_MAP.put("Madhya Pradesh", Arrays.asList(
                "Indore",
                "Bhopal",
                "Gwalior",
                "Jabalpur",
                "Ujjain"
        ));

        // ================= WEST BENGAL =================
        STATE_CITY_MAP.put("West Bengal", Arrays.asList(
                "Kolkata",
                "Howrah",
                "Durgapur",
                "Siliguri",
                "Asansol"
        ));

        // ================= TELANGANA =================
        STATE_CITY_MAP.put("Telangana", Arrays.asList(
                "Hyderabad",
                "Warangal",
                "Nizamabad",
                "Karimnagar"
        ));
    }

    // ================= GET ALL STATES =================
    public static List<String> getStates() {

        return new ArrayList<>(STATE_CITY_MAP.keySet());
    }

    // ================= GET CITIES BY STATE =================
    public static List<String> getCitiesByState(String state) {

        return STATE_CITY_MAP.getOrDefault(
                state,
                new ArrayList<>()
        );
    }
}