package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import mini.paranormalgolf.R;

public class OptionsActivity extends Activity
{

    private boolean music;
    private boolean sound;
    private boolean vibrations;
    private boolean shadows;

    private String language;

    private MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        LoadFonts();
        checkSharedPreferences();
    }

    public void checkSharedPreferences()
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
            shadows = sharedPreferences.getBoolean(getString(R.string.options_shadows), false);

            language = sharedPreferences.getString(getString(R.string.options_language), "en");
            changeLanguage(language);

            updateControls();
        }
    }

    public void updateControls()
    {
        ((CheckBox)findViewById(R.id.options_music)).setChecked(music);
        ((CheckBox)findViewById(R.id.options_sounds)).setChecked(sound);
        ((CheckBox)findViewById(R.id.options_vibrations)).setChecked(vibrations);
        ((CheckBox)findViewById(R.id.options_shadows)).setChecked(shadows);

        if (language.equalsIgnoreCase("pl"))
        {
            ((CheckBox) findViewById(R.id.options_pl)).setChecked(true);
            ((CheckBox) findViewById(R.id.options_en)).setChecked(false);
        }
        else
        {
            ((CheckBox) findViewById(R.id.options_pl)).setChecked(false);
            ((CheckBox) findViewById(R.id.options_en)).setChecked(true);
        }
    }

    public void changeLanguage(String language)
    {
        this.language = language;
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.equalsIgnoreCase("pl") ? "pl_PL" : "en_US");
        res.updateConfiguration(conf, dm);
        findViewById(android.R.id.content).invalidate();
    }

    public void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView) findViewById(R.id.options_title);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.options_music);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.options_sounds);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.options_vibrations);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.options_cancel_button);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.options_save_button);
        tv.setTypeface(tf);

        CheckBox checkBox = (CheckBox)findViewById(R.id.options_pl);
        checkBox.setTypeface(tf);

        checkBox = (CheckBox)findViewById(R.id.options_en);
        checkBox.setTypeface(tf);

        checkBox = (CheckBox)findViewById(R.id.options_shadows);
        checkBox.setTypeface(tf);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCancelClick(View view)
    {
        onButtonClick();
        finish();
    }

    public void onSaveClick(View view)
    {
        onButtonClick();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.options_music), ((CheckBox)findViewById(R.id.options_music)).isChecked());
        editor.putBoolean(getString(R.string.options_sound_effects), ((CheckBox)findViewById(R.id.options_sounds)).isChecked());
        editor.putBoolean(getString(R.string.options_vibrations), ((CheckBox)findViewById(R.id.options_vibrations)).isChecked());
        editor.putBoolean(getString(R.string.options_shadows), ((CheckBox)findViewById(R.id.options_shadows)).isChecked());
        boolean polish = ((CheckBox)findViewById(R.id.options_pl)).isChecked();
        editor.putString(getString(R.string.options_language), polish ? "pl" : "en");
        editor.commit();
        finish();
    }

    public void onPolishPick(View view)
    {
        ((CheckBox)view).setChecked(true);
        ((CheckBox)findViewById(R.id.options_en)).setChecked(false);
        changeLanguage("pl");
        setContentView(R.layout.activity_options);
        LoadFonts();
        updateControls();
        onButtonClick();
    }

    public void onEnglishPick(View view)
    {
        ((CheckBox)view).setChecked(true);
        ((CheckBox)findViewById(R.id.options_pl)).setChecked(false);
        changeLanguage("en");
        setContentView(R.layout.activity_options);
        LoadFonts();
        updateControls();
        onButtonClick();
    }

    public void onMusicClick (View view)
    {
        music = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    public void onSoundClick (View view)
    {
        sound = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    public void onVibrationsClick (View view)
    {
        vibrations = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    public void onShadowsClick (View view)
    {
        shadows = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    public void onButtonClick()
    {
        playSound("button.wav");
        vibrate();
    }

    public void playSound(String sound)
    {
        if (this.sound)
        {
            if (mp.isPlaying())
                mp.stop();
            try
            {
                mp.reset();
                AssetFileDescriptor afd = getAssets().openFd(sound);
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
                mp.start();
            } catch (IllegalStateException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }
}
