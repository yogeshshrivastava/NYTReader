package com.codepath.nytreader.models;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashSet;
import java.util.Set;

/**
 * Recent list for storing latest search keyword.
 *
 * @author yvastavaus.
 */
public class Recents {

    private static final String PREF_KEY = "Recents.PREF_KEY";

    private Set<String> recents;

    public Set<String> getRecents() {
        return recents;
    }

    public void setRecents(Set<String> recents) {
        this.recents = recents;
    }

    public static void writeToPref(Set<String> recents) {
        Prefs.putOrderedStringSet(PREF_KEY, recents);
    }

    public static void addToPref(String item) {
        Set<String> recents = Prefs.getOrderedStringSet(PREF_KEY, new HashSet<String>());
        if(!recents.contains(item)) {
            recents.add(item);
        }
        Prefs.putOrderedStringSet(PREF_KEY, recents);
    }

    public static Set<String> readPrefs() {
        Set<String> recents = Prefs.getOrderedStringSet(PREF_KEY, new HashSet<String>());
        return recents;
    }
}
