package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.zip.Inflater;

import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.R;

public class HelpActivity extends Activity
{
    private boolean music;
    private boolean sound;
    private boolean vibrations;

    private PowerManager.WakeLock mWakeLock;

    private int page = -1;
    private int page_max = 1;

    private int[] pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        pages = new int[4];
        pages[0] = R.layout.help_page_0;
        pages[1] = R.layout.help_page_1;
        pages[2] = R.layout.help_page_2;
        pages[3] = R.layout.help_page_3;

        page_max = pages.length;

        LoadFonts();
        checkSharedPreferences();
        changePage();
    }

    private void checkSharedPreferences()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0)
        {
            finish();
        }
        else
        {
            music = sharedPreferences.getBoolean(getString(R.string.options_music), false);
            sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
            vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
        }
    }

    private void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView)findViewById(R.id.help_title);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.help_menu);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.help_next);
        tv.setTypeface(tf);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up sound_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onButtonClick()
    {
        playSound(ResourceHelper.SOUND_BUTTON);
        vibrate();
    }

    public void playSound(int sound)
    {
        if (this.sound)
        {
            ResourceHelper.playSound(sound);
        }
    }

    private void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }

    public void onMenuClick(View view)
    {
        onButtonClick();
        finish();
    }

    public void onNextClick(View view)
    {
        onButtonClick();
        changePage();
    }

    private void changePage()
    {
        if (++page == page_max)
            page = 0;

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.help_view_to_inflate);
        relativeLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(pages[page], null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(view);
        relativeLayout.invalidate();
    }
}
