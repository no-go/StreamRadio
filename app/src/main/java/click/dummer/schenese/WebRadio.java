package click.dummer.schenese;

import click.dummer.schenese.WebStreamPlayer.State;
import click.dummer.schenese.array.ChannelList;
import click.dummer.schenese.array.SimpleArrayAdapter;
import click.dummer.schenese.dialog.ChannelsDialog;
import click.dummer.schenese.dialog.SettingsDialog;
import click.dummer.schenese.listener.CallStateListener;
import click.dummer.schenese.listener.CallbackListener;
import click.dummer.schenese.listener.StateListener;
import click.dummer.schenese.visualizer.BarVisualizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class WebRadio extends AppCompatActivity implements OnClickListener, StateListener, CallbackListener
{
  static WebRadioChannel lastPlayChannel;
  static WebRadioChannel lastSelectedChannel;
  Button playButton;
  boolean bPlayButton = false;
  Spinner choice;
  WebStreamPlayer streamPlayer;
  int progress = 100;
  ActionBar ab;

  private SharedPreferences mPreferences;
  private Menu optionsmenu;
  ProgressBar progressBar;
  BarVisualizer barVisualizer;
  int audioSession = -1;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (savedInstanceState == null)
    {
      if (mPreferences.getBoolean("is_dark", false))
      {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      } else {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }
      recreate();
    }
    setContentView(R.layout.activity_main);
    ChannelList.init(this);
    
    playButton = (Button) findViewById(R.id.mainPlay);
    playButton.setOnClickListener(this);
    choice = (Spinner) findViewById(R.id.mainSpinner);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    barVisualizer = findViewById(R.id.visualizer);

    SimpleArrayAdapter arrayAdapter = new SimpleArrayAdapter(this.getApplicationContext());
    choice.setAdapter(arrayAdapter);
    choice.setOnItemSelectedListener(createSpinnerListener());
    
    streamPlayer = WebStreamPlayer.getInstance();
    streamPlayer.setStateListener(this);
    stateChanged(streamPlayer.getState());

    ab = getSupportActionBar();
    if(ab != null) {
      ab.setDisplayShowHomeEnabled(true);
      ab.setHomeButtonEnabled(true);
      ab.setDisplayUseLogoEnabled(true);
      ab.setLogo(R.mipmap.logo);
      ab.setTitle(" " + getString(R.string.app_name));
      ab.setElevation(0);
    }

    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    telephonyManager.listen(new CallStateListener(streamPlayer), PhoneStateListener.LISTEN_CALL_STATE);
  }
  
  private OnItemSelectedListener createSpinnerListener()
  {
    OnItemSelectedListener l = new OnItemSelectedListener()
    {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
      {
        lastSelectedChannel = (WebRadioChannel) choice.getSelectedItem();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent)
      {
      }
    };
    return l;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    updateSpinner();
  }
  
  public void onCallback()
  {
    updateSpinner();
  }
  
  private void updateSpinner()
  {
    WebRadioChannel selChannel = lastSelectedChannel;
    SimpleArrayAdapter adapter = (SimpleArrayAdapter) choice.getAdapter();
    adapter.clear();
    ArrayList<WebRadioChannel> selectedChannels = ChannelList.getInstance().createSelectedChannelList();
    adapter.addAll(selectedChannels);
    int idx = selectedChannels.indexOf(selChannel);
    if (idx >= 0) { choice.setSelection(idx); }
  }

  @Override
  public void onClick(View v)
  {
    if (bPlayButton)
    {
      try
      {
        if (streamPlayer.getState() != WebStreamPlayer.State.Stopped)
        {
          throw new IllegalStateException("Player is busy on state: " + streamPlayer.getState());
        }
        WebRadioChannel curChannel = (WebRadioChannel) choice.getSelectedItem();
        streamPlayer.play(curChannel.getUrl());
        lastPlayChannel = curChannel;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
      }
      catch (Exception e)
      {
        Toast.makeText(getApplicationContext(), R.string.busy, Toast.LENGTH_SHORT).show();
        streamPlayer.stop();
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
      }
    }
    else
    {
      boolean result = streamPlayer.stop();
      if (result==false)
      {
        Toast.makeText(getApplicationContext(), R.string.busy, Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void stateChanged(State state)
  {
    if (state == State.Playing)
    {
      playButton.setText(R.string.stop);
      bPlayButton = false;
      progressBar.setIndeterminate(false);
      progressBar.setVisibility(View.INVISIBLE);
      if(ab != null) ab.setTitle(" 100% " + lastPlayChannel.getName());
      if (audioSession == -1) {
        audioSession = streamPlayer.getMediaPlayer().getAudioSessionId();
        barVisualizer.setPlayer(audioSession);
      }
    }
    else if (state == State.Stopped)
    {
      playButton.setText(R.string.play);
      bPlayButton = true;
      progressBar.setIndeterminate(false);
      progressBar.setVisibility(View.INVISIBLE);
      if(ab != null) ab.setTitle(" " + getString(R.string.app_name));
    }
    else if (state == State.Preparing) {
      progressBar.setIndeterminate(true);
      progressBar.setVisibility(View.VISIBLE);
      if(ab != null) ab.setTitle(" " + getString(R.string.app_name));
    }
    else if (state == State.Pause) {}
  }

  @Override
  public void stateLoading(int percent)
  {
    if (percent!=0 && percent<=progress) { return; }
    progress = percent;
    if(ab != null) {
      if (percent > 98) {
        ab.setTitle(" 100% " + lastPlayChannel.getName());
      } else {
        ab.setTitle(" " + String.valueOf(percent) + "%");
      }
    } else {
      Toast.makeText(
              getApplicationContext(),
              String.format(getString(R.string.loading), String.valueOf(percent) + "%"),
              Toast.LENGTH_SHORT
      ).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.options, menu);
    optionsmenu = menu;
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case R.id.action_setting:

        SettingsDialog.showSettings(null, getFragmentManager(), "fragment_channels", ChannelsDialog.class, this, null);
        return true;

      case R.id.action_dark:

        if (item.isChecked())
        {
          item.setChecked(false);
          mPreferences.edit().putBoolean("is_dark", false).apply();
          getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
          barVisualizer.release();
          recreate();
        } else {
          item.setChecked(true);
          mPreferences.edit().putBoolean("is_dark", true).apply();
          getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
          barVisualizer.release();
          recreate();
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
    optionsmenu.findItem(R.id.action_dark).setChecked(mPreferences.getBoolean("is_dark", false));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
      optionsmenu.findItem(R.id.action_power).setVisible(true);
    }
    return super.onPrepareOptionsMenu(menu);
  }
}
