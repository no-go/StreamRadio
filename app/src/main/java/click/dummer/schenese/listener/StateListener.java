package click.dummer.schenese.listener;

import click.dummer.schenese.WebStreamPlayer.State;

public interface StateListener
{
  public void stateChanged(State state);
  public void stateLoading(int percent);
}
