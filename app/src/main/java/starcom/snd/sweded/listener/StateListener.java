package starcom.snd.sweded.listener;

import starcom.snd.sweded.WebStreamPlayer.State;

public interface StateListener
{
  public void stateChanged(State state);
  public void stateLoading(int percent);
}
