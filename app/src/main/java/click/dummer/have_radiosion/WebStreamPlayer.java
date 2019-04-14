package click.dummer.have_radiosion;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;

public class WebStreamPlayer implements OnCompletionListener, OnErrorListener, OnInfoListener, OnPreparedListener
{
  public enum State { Preparing,Playing,Pause,Stopped };
  private static WebStreamPlayer instance;
  private MediaPlayer mediaPlayer;
  private State curState = State.Stopped;
  private  BaseVisualizer visualizer;
  private int sessionId = -1;

  private WebStreamPlayer() { }

  public static WebStreamPlayer getInstance() {
    if (instance==null) {
      instance = new WebStreamPlayer();
    }
    return instance;
  }
  
  public synchronized void play(String url) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    if (curState==State.Playing) {
      throw new IllegalStateException("MediaPlayer is playing, cant start now.");
    } else if (curState==State.Preparing) {
      throw new IllegalStateException("MediaPlayer busy, cant start now.");
    } else if (curState==State.Pause) {
      throw new IllegalStateException("MediaPlayer paused, cant stop now.");
    } else if (curState==State.Stopped) {
      getMediaPlayer().setDataSource(url);
      setState(State.Preparing);
      getMediaPlayer().prepareAsync();
    } else {
      throw new IllegalStateException("Unknown State: "+curState);
    }
  }

  public void setVisualizer(BaseVisualizer vis) {
    this.visualizer = vis;
  }

  public MediaPlayer getMediaPlayer() {
    if (mediaPlayer==null) {
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setOnCompletionListener(this);
      mediaPlayer.setOnErrorListener(this);
      mediaPlayer.setOnInfoListener(this);
      mediaPlayer.setOnPreparedListener(this);
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    return mediaPlayer;
  }

  private void setState(State state) {
    curState = state;
  }
  
  public synchronized boolean stop() {
    if (curState==State.Stopped) { return true; }
    setState(State.Stopped);
    if (mediaPlayer==null) { return true; }
    if (curState==State.Preparing) { return false; }
    mediaPlayer.pause();
    mediaPlayer.stop();
    mediaPlayer.reset();
    return true;
  }
  
  public State getState() { return curState; }

  @Override
  public void onPrepared(MediaPlayer mp) {
    setState(State.Playing);
    mediaPlayer.start();
    if (sessionId == -1) {
      sessionId = getMediaPlayer().getAudioSessionId();
      visualizer.setPlayer(sessionId);
    }
  }

  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra)
  {
    return false;
  }

  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    if (curState==State.Playing) {
      stop();
    } else if (curState==State.Preparing) {
      stop();
    } else if (curState==State.Pause) {
      stop();
    } else if (curState==State.Stopped) {
      // Nothing to do!
    } else {
      throw new IllegalStateException("Unknown State: "+curState);
    }
    return false;
  }


  @Override
  public void onCompletion(MediaPlayer mp)
  {
    stop();
  }
}
