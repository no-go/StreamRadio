package starcom.snd.geschwedet.listener;

import starcom.snd.geschwedet.WebStreamPlayer.State;

public interface StateListener
{
  public void stateChanged(State state);
  public void stateLoading(int percent);
}
