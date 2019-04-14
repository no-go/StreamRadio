package click.dummer.have_radiosion;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class RockApplication extends Application {

    private static Context contextOfApplication;

    public static String INITIAL_CHANNELS = "" +
            "RockAntenne§" +
            "http://mp3channels.webradio.antenne.de:80/rockantenne\n" +
            "RockAntenne Metall§" +
            "http://mp3channels.webradio.antenne.de:80/heavy-metal\n" +
            "RockAntenne Classics§" +
            "http://mp3channels.webradio.antenne.de:80/classic-perlen\n" +
            "Wackenradio§" +
            "http://wackenradio-high.rautemusik.fm\n" +
            "RadioBOB§" +
            "http://streams.radiobob.de/bob-live/aac-64/mediaplayer\n" +
            "RadioBOB harte Saite§" +
            "http://streams.radiobob.de/bob-hartesaite/aac-64/mediaplayerbob\n" +
            "RadioBOB Classics§" +
            "http://streams.radiobob.de/bob-classicrock/aac-64/mediaplayer\n" +
            "RadioBOB Metal§" +
            "http://streams.radiobob.de/bob-metal/aac-64/mediaplayer\n" +
            "RadioBOB Metalcore§" +
            "http://streams.radiobob.de/metalcore/aac-64/mediaplayer\n" +
            "RadioBOB Grunge§" +
            "http://streams.radiobob.de/bob-grunge/aac-64/mediaplayer\n" +
            "Sunshine-live§" +
            "http://stream.sunshine-live.de/hq/mp3-128/surfmusik/\n" +
            "Japanrock§" +
            "http://23.226.236.193:8100/;.mp3\n" +
            "Fallout 76 General§" +
            "http://fallout.fm:8000/falloutfm10.ogg\n" +
            "JAZZ lovers§" +
            "http://streaming.radionomy.com/jazzlovers?lang=en-US";

    public static SharedPreferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        contextOfApplication = getApplicationContext();
    }

    public static String implode(String separator, ArrayList<String> data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size() - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data.get(i).matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data.get(i));
                sb.append(separator);
            }
        }
        sb.append(data.get(data.size() - 1).trim());
        return sb.toString();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
