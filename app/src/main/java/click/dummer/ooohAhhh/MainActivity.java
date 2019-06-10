package click.dummer.ooohAhhh;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button playButton;
    boolean asPlayButton = true;
    Spinner choice;
    WebStreamPlayer streamPlayer;
    boolean fulls = false;

    Menu optionsmenu;
    MaulmiauVisualizer gVisualizer;
    ArrayList<String> channels;
    String selectedChannel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (RockApplication.mPreferences.getBoolean("is_dark", false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        }
        setContentView(R.layout.activity_main);
        readChannels();
        storeChannels();

        playButton = (Button) findViewById(R.id.mainPlay);
        choice = (Spinner) findViewById(R.id.mainSpinner);
        gVisualizer = findViewById(R.id.visualizer);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getChannelNames());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choice.setAdapter(dataAdapter);
        choice.setOnItemSelectedListener(this);
        selectedChannel = getChannelNames().get(0);

        streamPlayer = WebStreamPlayer.getInstance();
        streamPlayer.setVisualizer(gVisualizer);

        toFullscreen();
    }

    public void onClick(View v) {
        if (asPlayButton) {
            try {
                if (streamPlayer.getState() != WebStreamPlayer.State.Stopped) {
                    throw new IllegalStateException("Player is busy on state: " + streamPlayer.getState());
                }
                selectedChannel = choice.getSelectedItem().toString();
                streamPlayer.play(getUrl(selectedChannel));
                playing();

            } catch (Exception e) {
                streamPlayer.stop();
                stopped();
            }
        } else {
            streamPlayer.stop();
            stopped();
        }
    }

    private void stopped() {
        asPlayButton = true;
        playButton.setText(R.string.play);
    }

    private void playing() {
        asPlayButton = false;
        playButton.setText(R.string.stop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        optionsmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:

                Intent intentFileChooser = new Intent()
                        .setType(Intent.normalizeMimeType("audio/*"))
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentFileChooser, getString(R.string.open_file)), 42);

                return true;

            case R.id.action_dark:

                if (item.isChecked()) {
                    item.setChecked(false);
                    RockApplication.mPreferences.edit().putBoolean("is_dark", false).commit();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Intent mStartActivity = new Intent(this, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
                    System.exit(0);
                } else {
                    item.setChecked(true);
                    RockApplication.mPreferences.edit().putBoolean("is_dark", true).commit();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Intent mStartActivity = new Intent(this, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
                    System.exit(0);
                }
                return true;

            case R.id.action_power:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        optionsmenu.findItem(R.id.action_dark).setChecked(RockApplication.mPreferences.getBoolean("is_dark", false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            optionsmenu.findItem(R.id.action_power).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public ArrayList<String> getChannelNames() {
        ArrayList<String> back = new ArrayList<>();
        readChannels();
        for (String ch : channels) {
            back.add(ch.split("§")[0]);
        }
        return back;
    }

    public String getUrl(String name) {
        readChannels();
        for (String ch : channels) {
            String[] dummy = ch.split("§");
            if (dummy[0].equals(name)) return dummy[1];
        }
        return "";
    }

    public ArrayList<String> readChannels() {
        String dummy = RockApplication.mPreferences.getString("channels", RockApplication.INITIAL_CHANNELS);
        String[] chan = dummy.split("\n");
        channels = new ArrayList<>(Arrays.asList(chan));
        return channels;
    }

    public void storeChannels() {
        RockApplication.mPreferences.edit().putString("channels", RockApplication.implode("\n", channels)).commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedChannel = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            if(resultCode == Activity.RESULT_OK) {
                if (streamPlayer.mediaPlayer != null && streamPlayer.mediaPlayer.isPlaying()) streamPlayer.stop();
                try {
                    if (streamPlayer.getMediaPlayer() == null) {
                        streamPlayer.mediaPlayer = MediaPlayer.create(RockApplication.getContextOfApplication(), data.getData());
                    } else {
                        streamPlayer.mediaPlayer.setDataSource(RockApplication.getContextOfApplication(), data.getData());
                    }
                    streamPlayer.mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                streamPlayer.mediaPlayer.start();
                streamPlayer.mediaPlayer.setLooping(true);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        toFullscreen();
    }

    @Override
    protected void onPause() {
        fulls = false;
        super.onPause();
    }

    public void toFullscreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!fulls) {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            int newUiOptions = uiOptions;

            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            fulls = true;
        }
    }
}
