package click.dummer.schenese.listener;

import android.app.DialogFragment;
import android.content.DialogInterface;

public class DialogFragmentWithListener extends DialogFragment
{
  private CallbackListener l;
  
  /** Set the Listener for callback.
   *  @return The last listener. **/
  public CallbackListener setCallbackListener(CallbackListener l)
  {
    CallbackListener last = this.l;
    this.l = l;
    return last;
  }
  
  @Override
  public void onDismiss(DialogInterface di)
  {
    super.onDismiss(di);
    if (l != null) { l.onCallback(); }
  }
}
