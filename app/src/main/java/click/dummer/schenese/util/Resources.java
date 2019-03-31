package click.dummer.schenese.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class Resources
{

  /** Read final readonly raw file. **/
  public static String readTextRaw(Context context, int rawID)
  {
    StringBuilder sb = new StringBuilder();
    InputStream is = null;
    try
    {
      is = context.getResources().openRawResource(rawID);
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      while (true)
      {
        String line = br.readLine();
        if (line==null) { break; }
        sb.append(line);
        sb.append("\n");
      }
    }
    catch (IOException e)
    {
      return "";
    }
    finally
    {
      if (is != null)
      {
        try { is.close(); } catch (Exception e) {}
      }
    }
    return sb.toString();
  }
}
