package org.leopub.repeater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TextActivity extends Activity { 
    private AudioManager mAudioManager;
    private Timer mTimer;
    private RepeatItem mItem; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        
        mAudioManager = AudioManager.getInstance();
        mTimer = null;
        
        Intent intent = getIntent();
        int itemId = intent.getIntExtra(MainActivity.TEXT_CONTENT, -1);
        mItem = RepeatItemManager.getInstance().getItems().get(itemId);

        // Set title
        setTitle(mItem.getTitle());
        
        // Set text
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mItem.getTextPath()));
            String line = reader.readLine();
            while (reader.ready()) {
                line = reader.readLine();
                sb.append(line + "\n");
            }
            reader.close();
        } catch(Exception e) {
            // throws new RuntimeExeption(e);
        }
        TextView textView = (TextView) findViewById(R.id.text_text);
        textView.setText(sb.toString());
    }

    @Override public void onPause() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onPause();
    }

    @Override public void onResume() {
        super.onResume();
        switch(mAudioManager.getStatus()) {
        case Unready:
            // doing nothing
            break;
        case Playing:
            setDisplayAudioPosTimer();
            // fall through
        case Paused:
            displayAudioPos();
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_repeat) {
            RepeatItemManager.getInstance().repeat(mItem);
            return true;
        } else if (id == android.R.id.home) {
        	onBackPressed();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayAudioPos() {
        Button button = (Button) findViewById(R.id.text_play);
        button.setText(mAudioManager.getPosString());
    }

    public void setDisplayAudioPosTimer() {
        DisplayAudioPosTask displayTask = new DisplayAudioPosTask(this);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(displayTask, 500, 500);
    }

    public void onAudioPlay(View view) { 
        switch(mAudioManager.getStatus()) {
        case Unready:
            mAudioManager.start(mItem.getAudioPath(), this);
            setDisplayAudioPosTimer();
            break;
        case Playing:
            mAudioManager.pause();
            mTimer.cancel();
            mTimer = null;
            break;
        case Paused:
            mAudioManager.resume(this);
            setDisplayAudioPosTimer();
            break;
        }
    }

    public void onAudioSeek(View view) {
        Button button = (Button)view;
        String instruction = button.getText().toString();
        boolean isForward = (instruction.charAt(0) == 'F');
        int offset = Integer.parseInt(instruction.substring(1)) * 1000;
        if (!isForward) {
            offset *= -1;
        }
        mAudioManager.seekBy(offset);
        displayAudioPos();
    }

    private class DisplayAudioPosTask extends TimerTask {
        private TextActivity mTextActivity;
        public DisplayAudioPosTask(TextActivity textActivity) {
            mTextActivity = textActivity;
        }
        public void run() {
            mTextActivity.runOnUiThread(new Runnable() {
                @SuppressLint("DefaultLocale")
                @Override public void run() {
                    mTextActivity.displayAudioPos();
                }
            });
        }
    }
}
