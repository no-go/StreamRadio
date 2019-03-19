package starcom.snd.geschwedet;

import starcom.debug.LoggingSystem;
import starcom.snd.geschwedet.WebStreamPlayer.State;

import starcom.snd.geschwedet.array.ChannelList;
import starcom.snd.geschwedet.array.SimpleArrayAdapter;
import starcom.snd.geschwedet.dialog.ChannelsDialog;
import starcom.snd.geschwedet.dialog.SettingsDialog;
import starcom.snd.geschwedet.dialog.TextDialog;
import starcom.snd.geschwedet.listener.CallStateListener;
import starcom.snd.geschwedet.listener.CallbackListener;
import starcom.snd.geschwedet.listener.StateListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class WebRadio extends AppCompatActivity implements OnClickListener, StateListener, CallbackListener
{
  private int NOTIFICATION = R.string.app_name;
  final static String TXT_LABEL = "WebStreamPlayer";
  final static String TXT_NOTIFICATION = "StreamPlayer";
  static WebRadioChannel lastPlayChannel;
  static WebRadioChannel lastSelectedChannel;
  static NotificationManager mNM;
  TextView label;
  Button playButton;
  boolean bPlayButton = false;
  Spinner choice;
  WebStreamPlayer streamPlayer;
  int progress = 100;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null)
    {
      if (inTimeSpan(6, 18))
      {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      } else {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      }
      recreate();
    }
    setContentView(R.layout.activity_main);
    ChannelList.init(this);

    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setIcon(R.mipmap.logo);

    playButton = (Button) findViewById(R.id.mainPlay);
    playButton.setOnClickListener(this);
    label = (TextView) findViewById(R.id.mainText);
    label.setText(TXT_LABEL);
    choice = (Spinner) findViewById(R.id.mainSpinner);
    
    SimpleArrayAdapter arrayAdapter = new SimpleArrayAdapter(this.getApplicationContext());
    choice.setAdapter(arrayAdapter);
    choice.setOnItemSelectedListener(createSpinnerListener());
    
    streamPlayer = WebStreamPlayer.getInstance();
    streamPlayer.setStateListener(this);
    stateChanged(streamPlayer.getState());
    
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
        label.setText(lastPlayChannel.getName());
      }
      catch (Exception e)
      {
        Toast.makeText(getApplicationContext(), "Player is busy", Toast.LENGTH_SHORT).show();
        LoggingSystem.warn(WebRadio.class, "Cant play because player is busy: "+e);
      }
    }
    else
    {
      boolean result = streamPlayer.stop();
      if (result==false)
      {
        Toast.makeText(getApplicationContext(), "Player is busy", Toast.LENGTH_SHORT).show();
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
      showNotification();
    }
    else if (state == State.Stopped)
    {
      playButton.setText(R.string.play);
      bPlayButton = true;
      label.setText(TXT_LABEL);
      hideNotification();
    }
    else if (state == State.Preparing) {}
    else if (state == State.Pause) {}
    else
    {
      LoggingSystem.severe(WebRadio.class, "Error, unknown State: "+state);
    }
  }

  private void showNotification()
  {
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WebRadio.class), PendingIntent.FLAG_UPDATE_CURRENT);
    if (mNM==null)
    {
      mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.mipmap.logo)  // the status icon
        .setTicker(TXT_NOTIFICATION)  // the status text
        .setWhen(System.currentTimeMillis())  // the time stamp
        .setContentTitle(TXT_NOTIFICATION)  // the label of the entry
        .setContentText(TXT_NOTIFICATION)  // the contents of the entry
        .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
        .setOngoing(true)
        .build();
    mNM.notify(NOTIFICATION, notification);
  }

  void hideNotification()
  {
    if (mNM==null) { return; }
    mNM.cancel(NOTIFICATION);
    mNM = null;
  }

  @Override
  public void stateLoading(int percent)
  {
    if (percent!=0 && percent<=progress) { return; }
    progress = percent;
    Toast.makeText(getApplicationContext(), "Loading: " + percent + "%", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case R.id.action_setting:

        SettingsDialog.showSettings(null, getFragmentManager(), "fragment_channels", ChannelsDialog.class, this, null);
        return true;

      case R.id.action_about:

        SettingsDialog.showSettings(null, getFragmentManager(), "fragment_text", TextDialog.class, null, null);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

      public static boolean inTimeSpan(int startH, int stopH)
  {
    int nowH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    if (startH == stopH && startH == nowH) return true;
    if (startH > stopH && (nowH <= stopH || nowH >= startH)) return true;
    if (startH < stopH && nowH >= startH && nowH <= stopH) return true;
    return false;
  }
}
