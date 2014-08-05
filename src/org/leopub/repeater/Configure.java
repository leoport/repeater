package org.leopub.repeater;

import android.os.Environment;

public class Configure {
    public static final String HOME_PATH = Environment.getExternalStorageDirectory() + "/Repeater/";
    public static final String SQLITE_FILE_PATH = HOME_PATH + "records.db";
    public static final String TEXT_PATH         = HOME_PATH + "txt/";
    public static final String AUDIO_PATH        = HOME_PATH + "audio/";
}
