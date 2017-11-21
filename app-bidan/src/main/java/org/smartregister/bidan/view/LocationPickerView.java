package org.smartregister.bidan.view;

import java.util.ArrayList;

/**
 * Created by sid-tech on 11/17/17.
 */

public class LocationPickerView {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String PREF_VILLAGE_LOCATIONS = "PREF_VILLAGE_LOCATIONS";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add("Village");
    }
}
