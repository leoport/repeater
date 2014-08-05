package org.leopub.repeater;

public class RepeatItem {
    private String mName;
    private String mTitle;
    private String mTextPath;
    private String mAudioPath; 
    private String mDate;
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public void setTextPath(String textPath) {
        mTextPath = textPath;
    }
    public void setAudioPath(String audioPath) {
        mAudioPath = audioPath;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getTextPath() {
        return mTextPath;
    }
    public String getAudioPath() {
        return mAudioPath;
    }
    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        mDate = date;
    }
}
