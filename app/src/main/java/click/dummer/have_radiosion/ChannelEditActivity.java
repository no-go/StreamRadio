package click.dummer.have_radiosion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class ChannelEditActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ArrayList<EditText> nameEdit;
    ArrayList<EditText> urlEdit;
    ArrayList<String> channels;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                storeChannels();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        storeChannels();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (RockApplication.mPreferences.getBoolean("is_dark", false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        }
        setContentView(R.layout.edit_main);
        readChannels();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle(" " + getString(R.string.edit_list));
            ab.setElevation(0);
        }
    }

    public void readChannels() {
        linearLayout = (LinearLayout) findViewById(R.id.feedsourceList);
        linearLayout.removeAllViews();
        String dummy = RockApplication.mPreferences.getString("channels", RockApplication.INITIAL_CHANNELS);
        String[] chan = dummy.split("\n");
        channels = new ArrayList<>(Arrays.asList(chan));
        nameEdit = new ArrayList<>();
        urlEdit = new ArrayList<>();

        for (int id=0; id < channels.size() + 5; id++) {
            nameEdit.add(id, new EditText(this));
            urlEdit.add(id, new EditText(this));
            if (id < channels.size()) {
                String[] c = channels.get(id).split("§");
                nameEdit.get(id).setText(c[0]);
                urlEdit.get(id).setText(c[1]);
            }

            LinearLayout ll = new LinearLayout(RockApplication.getContextOfApplication());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.addView(nameEdit.get(id), 0);
            ll.addView(urlEdit.get(id), 1);
            if(id%2 == 1) ll.setBackgroundColor(getResources().getColor(R.color.colorOdd));
            nameEdit.get(id).setMinWidth(300);
            nameEdit.get(id).setMaxWidth(300);
            urlEdit.get(id).setMinWidth(500);
            linearLayout.addView(ll, id);
        }
    }

    public void storeChannels() {
        StringBuilder sb = new StringBuilder();
        String s1,s2;
        int id =0;
        for(id =0; id<nameEdit.size(); id++) {
            s1 = nameEdit.get(id).getText().toString().trim();
            s2 = urlEdit.get(id).getText().toString().trim();
            if (s1.length() > 0 && s2.length() > 0) {
                sb.append(s1 + "§" + s2 + "\n");
            }
        }
        // remove last \n
        RockApplication.mPreferences.edit().putString("channels", sb.toString().substring(0, sb.length()-1)).commit();
    }

    public void addLine(View v) {
        int id = urlEdit.size();
        nameEdit.add(id, new EditText(this));
        urlEdit.add(id, new EditText(this));

        LinearLayout ll = new LinearLayout(RockApplication.getContextOfApplication());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(nameEdit.get(id), 0);
        ll.addView(urlEdit.get(id), 1);
        if(id%2 == 1) ll.setBackgroundColor(getResources().getColor(R.color.colorOdd));
        nameEdit.get(id).setMinWidth(300);
        nameEdit.get(id).setMaxWidth(300);
        urlEdit.get(id).setMinWidth(500);
        linearLayout.addView(ll, id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        readChannels();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        storeChannels();
        super.onSaveInstanceState(outState);
    }
}
