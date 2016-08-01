package com.codepath.nytreader.application;

import android.app.Application;
import android.content.ContextWrapper;

import com.codepath.nytreader.R;
import com.pixplicity.easyprefs.library.Prefs;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application class to configure application.
 *
 * @author Yogesh Shrivastava
 */
public class NytReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
