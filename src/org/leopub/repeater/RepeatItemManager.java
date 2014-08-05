package org.leopub.repeater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;

import android.os.Environment;
import android.util.Log;

public class RepeatItemManager {
    private static RepeatItemManager sManager = null;
    private List<RepeatItem> mItems = null;
    private DatabaseManager mDatabaseManager = null;
    private DateFormat mDateFormat;

    private RepeatItemManager() {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        initFiles();
        mDatabaseManager = DatabaseManager.getInstance();
        mItems = generateItems();
    }
    
    static public RepeatItemManager getInstance() {
        if (sManager == null) {
            sManager = new RepeatItemManager();
        }
        return sManager;
    }
    
    public List<RepeatItem> getItems() {
        return mItems;
    }
    
    public List<RepeatItem> generateItems() {
        List<RepeatItem> result = new ArrayList<RepeatItem>();
        Queue<String> txtFilenames = new PriorityQueue<String>(getFilenameList(Configure.TEXT_PATH, ".txt"));
        Queue<String> audioFilenames = new PriorityQueue<String>(getFilenameList(Configure.AUDIO_PATH, ".mp3"));
        String txtFilename = null;
        String audioFilename = null;
        while (!txtFilenames.isEmpty() || !audioFilenames.isEmpty()) {
            if (!txtFilenames.isEmpty() && txtFilename == null) txtFilename = txtFilenames.poll();
            if (!audioFilenames.isEmpty() && audioFilename == null) audioFilename = audioFilenames.poll();
            
            String txtMainName = null;
            if (txtFilename != null) txtMainName = txtFilename.substring(0, txtFilename.length() - 4);
            String audioMainName = null;
            if (audioFilename != null) audioMainName = audioFilename.substring(0, audioFilename.length() - 4);

            RepeatItem item = new RepeatItem();
            if (txtMainName == null || txtMainName.compareTo(audioMainName) >= 0) { 
                item.setAudioPath(Configure.AUDIO_PATH + audioFilename);
                item.setName(audioMainName);
                audioFilename = null;
            }
            if (audioMainName == null || txtMainName.compareTo(audioMainName) <= 0) {
                item.setTextPath(Configure.TEXT_PATH + txtFilename);
                item.setName(txtMainName);
                txtFilename = null;
            }
            fillTitle(item);
            fillDate(item);
            result.add(item);
        }
        return result;
    }
    
    private void fillTitle(RepeatItem item) {
        if (item.getTextPath() == null) {
            item.setTitle(item.getName());
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(item.getTextPath()));
                item.setTitle(reader.readLine());
                reader.close();
            } catch (Exception e) {
                item.setTitle(item.getName());
            }
        }
    }
    
    private void fillDate(RepeatItem item) {
        List<Date> records = mDatabaseManager.getRecords(item.getName());
        if (records.size() > 0) {
            item.setDate(mDateFormat.format(records.get(0)));
        } else {
            item.setDate("");
        }
    }

    private void initFiles() {
        String dirNames[] = {Configure.HOME_PATH, Configure.TEXT_PATH, Configure.AUDIO_PATH};
        for (String dirName : dirNames) {
            File dir = new File(Environment.getExternalStorageDirectory() + dirName);
            if (! dir.exists()) {
                boolean result = dir.mkdir();
                String message = "Creating dir '" + dir + "' ";
                if (result) {
                    message += "success.";
                } else {
                    message += "failed.";
                }
                Log.w("org.leopub.repeater", message);
            }
        }
    }
    
    private List<String> getFilenameList(String path, final String suffix) {
        List<String> result = new ArrayList<String>();
        File dir = new File(path);
        String[] names = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return ! file.getName().endsWith(suffix);
            }
        });
        result = Arrays.asList(names);
        Collections.sort(result);
        return result;
        
    }
    
    public void repeat(RepeatItem item) {
        mDatabaseManager.addRecord(item.getName());
        fillDate(item);
    }
}
