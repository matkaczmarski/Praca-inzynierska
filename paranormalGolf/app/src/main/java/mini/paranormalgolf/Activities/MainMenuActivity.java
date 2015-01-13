package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.R;

public class MainMenuActivity extends Activity
{
    private boolean music = false;
    private boolean sound = false;
    private boolean vibrations = false;
    private boolean shadows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ResourceHelper.initSounds(getApplicationContext());
        checkSharedPreferences();

        setContentView(R.layout.activity_main_menu);
        LoadFonts();
        ManageFiles();
    }

    public void checkSharedPreferences()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.options_music), true);
            editor.putBoolean(getString(R.string.options_sound_effects), true);
            editor.putBoolean(getString(R.string.options_vibrations), true);
            editor.putBoolean(getString(R.string.options_shadows), true);
            String language = Locale.getDefault().getLanguage();
            if (!language.equalsIgnoreCase("pl"))
                language = "en";
            editor.putString(getString(R.string.options_language), language);

            String[] boards_id = getResources().getStringArray(R.array.boards_id);
            for (String board_id : boards_id)
                editor.putInt(board_id, 0);

            editor.commit();

            music = sound = vibrations = true;
        }
        else
        {
            music = sharedPreferences.getBoolean(getString(R.string.options_music), false);
            sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
            vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
            shadows = sharedPreferences.getBoolean(getString(R.string.options_shadows), false);

            String language = sharedPreferences.getString(getString(R.string.options_language), "en");
            changeLanguage(language);
        }
    }

    public void changeLanguage(String language)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.equalsIgnoreCase("pl") ? "pl_PL" : "en_US");
        res.updateConfiguration(conf, dm);
    }

    public void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView) findViewById(R.id.main_menu_title);
        tv.setTypeface(tf);

        Shader textShader=new LinearGradient(0, 0, 0, 20,
                new int[]{Color.GREEN,Color.BLUE},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        tv.getPaint().setShader(textShader);

        tv = (TextView)findViewById(R.id.main_menu_start);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.main_menu_options);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.main_menu_help);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.main_menu_exit);
        tv.setTypeface(tf);
    }

    public void ManageFiles()
    {
        //TODO sprawdzenie czy pliki istnieją i ich ewentualne utworzenie
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

    public void onStartClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
        startActivity(intent);
    }

    public void onOptionsClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        intent.putExtra("ON_PAUSE", false);
        startActivity(intent);
    }

    public void onHelpClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
    }

    public void onExitClick(View view)
    {
        onButtonClick();
        finish();
    }

    public void onButtonClick()
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

    public void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkSharedPreferences();
        setContentView(R.layout.activity_main_menu);
        LoadFonts();
    }

    @Override
    protected void onStop()
    {
        //ResourceHelper.releaseSounds();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        //ResourceHelper.releaseSounds();
        super.onDestroy();
    }
}
