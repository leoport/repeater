package org.leopub.repeater;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
    public final static String TEXT_CONTENT = "org.leopub.repeater.text";
    private final static String KEY_SELECTION_POS = "selection_pos";
    private final static String KEY_SELECTION_Y = "selection_y";

    private int mSelectionPos;
    private int mSelectionY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_fetch) {
            fetchAudioFromDownload();
            RepeatItemManager.getInstance().update();
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mSelectionPos = getListView().getFirstVisiblePosition();
        View view = getListView().getChildAt(0);
        mSelectionY = (view == null) ? 0 : view.getTop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        RepeatItemManager itemManager = RepeatItemManager.getInstance();
        List<RepeatItem> items = itemManager.getItems();

        RepeatItemAdapter adapter = new RepeatItemAdapter(this, R.layout.list_item, R.id.item_title, items);
        getListView().setAdapter(adapter);
        getListView().setSelectionFromTop(mSelectionPos, mSelectionY);
        
        AudioManager.getInstance().reset();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, TextActivity.class);
        intent.putExtra(TEXT_CONTENT, position);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        mSelectionPos = getListView().getFirstVisiblePosition();
        View view = getListView().getChildAt(0);
        mSelectionY = (view == null) ? 0 : view.getTop();

        savedInstanceState.putInt(KEY_SELECTION_POS, mSelectionPos);
        savedInstanceState.putInt(KEY_SELECTION_Y, mSelectionY);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectionPos = savedInstanceState.getInt(KEY_SELECTION_POS);
        mSelectionY = savedInstanceState.getInt(KEY_SELECTION_Y);
        getListView().setSelectionFromTop(mSelectionPos, mSelectionY);
    }


    private class RepeatItemAdapter extends ArrayAdapter<RepeatItem> {
        public RepeatItemAdapter(Context context, int resource, int textViewId, List<RepeatItem> items) {
            super(context, resource, textViewId, items);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
            }
            RepeatItem item = getItem(position);
            TextView itemTitleView = (TextView) convertView.findViewById(R.id.item_title) ;
            itemTitleView.setText(item.getTitle());

            TextView itemRepeatDateView = (TextView) convertView.findViewById(R.id.item_repeat_date); 
            itemRepeatDateView.setText(item.getDate());

            return convertView;
        }
    }
    
    private void fetchAudioFromDownload() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE);
        File[] files = downloadDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(".mp3");
            }
        });
        for (File file : files) {
            file.renameTo(new File(Configure.AUDIO_PATH + file.getName()));
        }
    }
}
