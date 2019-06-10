package click.dummer.ooohAhhh;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class RockApplication extends Application {

    private static Context contextOfApplication;

    public static String INITIAL_CHANNELS = "" +
            "JAZZ lovers§" +
            "http://streaming.radionomy.com/jazzlovers?lang=en-US\n" +

            "ACID Jazz§" +
            "http://acidjazz.stream.laut.fm/acidjazz?t302=2019-05-18_21-11-59&uuid=b8563a96-26f5-4d6c-8496-650129c3c798\n" +

            "Capital Public Radio Jazz§" +
            "http://14543.live.streamtheworld.com:3690/JAZZSTREAM_SC\n" +

            "DR P8 Radio Jazz§" +
            "http://live-icy.gslb01.dr.dk/A/A22H.mp3\n" +

            "Jazz Medley Webradio§" +
            "http://server01.ouvir.radio.br:8006/stream\n" +

            "1.FM Blue Radio§" +
            "http://strm112.1.fm/blues_mobile_mp3\n" +

            "Perfecto Blue§" +
            "http://radioperfecto.net-radio.fr/perfectoblues.mp3\n" +

            "CJRT JazzFM91 Grooveyard§" +
            "http://streamergamma.jazz.fm:8002";


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
