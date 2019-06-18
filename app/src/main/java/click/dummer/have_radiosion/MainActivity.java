package click.dummer.have_radiosion;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String NOTIFICATION_CHANNEL_ID_LOCATION = "click_dummer_have_radiosion_channel_location";
    int NOTIFICATION = R.string.app_name;

    Button playButton;
    boolean asPlayButton = true;
    Spinner choice;
    WebStreamPlayer streamPlayer;
    ActionBar ab;

    Menu optionsmenu;
    ProgressBar progressBar;
    GrrrVisualizer gVisualizer;
    ArrayList<String> channels;
    ArrayAdapter<String> dataAdapter;
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gVisualizer = findViewById(R.id.visualizer);

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getChannelNames());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choice.setAdapter(dataAdapter);
        choice.setOnItemSelectedListener(this);
        selectedChannel = getChannelNames().get(0);

        streamPlayer = WebStreamPlayer.getInstance();
        streamPlayer.setVisualizer(gVisualizer);

        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.mipmap.logo_color);
            ab.setTitle(" " + getString(R.string.app_name));
            ab.setElevation(0);
        }
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

    private void showNotification() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager mngr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mngr.getNotificationChannel(NOTIFICATION_CHANNEL_ID_LOCATION) != null) {
                mngr.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID_LOCATION);
            }
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_LOCATION,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW // sound off in 8 and higher
            );
            channel.setDescription(getString(R.string.app_name));
            channel.enableLights(false);
            channel.enableVibration(false);
            mngr.createNotificationChannel(channel);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_color);
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(choice.getSelectedItem().toString());

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_LOCATION)
                .setSmallIcon(R.mipmap.logo_sw)  // the status icon
                .setLargeIcon(largeIcon)
                .setStyle(bigStyle)
                .setPriority(NotificationCompat.PRIORITY_LOW) // sound off in 7.1 and below
                .setTicker(choice.getSelectedItem().toString())  // the status text
                .setWhen(Calendar.getInstance().getTimeInMillis())  // the time stamp
                .setContentTitle(getString(R.string.app_name))  // the label of the entry
                .setContentText(getString(R.string.playing))  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setOngoing(true)                 // remove/only cancel by stop button
                .build();
        mNM.notify(NOTIFICATION, notification);
    }

    void hideNotification() {
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(NOTIFICATION);
    }

    private void stopped() {
        asPlayButton = true;
        playButton.setText(R.string.play);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        hideNotification();
    }

    private void playing() {
        asPlayButton = false;
        playButton.setText(R.string.stop);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        showNotification();
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
            case R.id.action_setting:

                Intent intentfs = new Intent(MainActivity.this, ChannelEditActivity.class);
                intentfs.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intentfs, 42);
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

    public void readChannels() {
        String dummy = RockApplication.mPreferences.getString("channels", RockApplication.INITIAL_CHANNELS);
        String[] chan = dummy.split("\n");
        channels = new ArrayList<>(Arrays.asList(chan));
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
                dataAdapter.clear();
                ArrayList<String> cn = getChannelNames();
                dataAdapter.addAll(cn);
                int idx = cn.indexOf(selectedChannel);
                if (idx >= 0) { choice.setSelection(idx); }
            }
        }
    }
}
