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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.R;

public class OptionsActivity extends Activity
{

    private boolean music;
    private boolean sound;
    private boolean vibrations;
    private boolean shadows;

    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //ResourceHelper.initSounds(this);
        Bundle extras = getIntent().getExtras();
        boolean onPause = extras.getBoolean("ON_PAUSE");
        if (onPause)
        {
            findViewById(R.id.options_pl).setVisibility(View.INVISIBLE);
            findViewById(R.id.options_en).setVisibility(View.INVISIBLE);
            findViewById(R.id.options_shadows).setVisibility(View.INVISIBLE);
            findViewById(R.id.options_texture_scroll_view).setVisibility(View.INVISIBLE);
        }
        else
            loadTextures();

        LoadFonts();
        checkSharedPreferences();
    }

    public void loadTextures()
    {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.options_texture_scroll_view);
        linearLayout.removeAllViews();
        for (Ball.BallTexture ballTexture : Ball.BallTexture.values())
        {
            View view = getLayoutInflater().inflate(R.layout.texture_item, null);
            view.findViewById(R.id.texture_item_image).setBackgroundResource(getTextureResource(ballTexture));
            linearLayout.addView(view);
        }
    }

    private int getTextureResource(Ball.BallTexture ballTextureType)
    {
        switch (ballTextureType){
            case redAndWhite:
                return R.drawable.ball_texture_red_white_dots;
            case noise:
                return R.drawable.ball_texture_noise;
            case beach:
                return R.drawable.ball_texture_beachball;
            case lava:
                return R.drawable.ball_texture_lava;
            case sun:
                return R.drawable.ball_texture_sun;
            case jelly:
                return R.drawable.ball_texture_jelly;
            case marble:
                return R.drawable.ball_texture_marble;
            case frozen:
                return R.drawable.ball_texture_frozen;
            case tiger:
                return R.drawable.ball_texture_tiger;
            case orangeSkin:
                return R.drawable.ball_texture_orange_skin;
            case amethystAlcove:
                return R.drawable.ball_texture_amethyst_alcove;
            case drizzledPaint:
                return R.drawable.ball_texture_drizzled_paint;
            case eyeOfTheSunGod:
                return R.drawable.ball_texture_eye_of_the_sun_god;
            case girlsBestFriend:
                return R.drawable.ball_texture_girls_best_friend;
            case homeWorld:
                return R.drawable.ball_texture_home_world;
            case jupiter:
                return R.drawable.ball_texture_jupiter;
            case liquidCrystal:
                return R.drawable.ball_texture_liquid_crystal;
            case methaneLakes:
                return R.drawable.ball_texture_methane_lakes;
            case spottedBianco:
                return R.drawable.ball_texture_spotted_bianco;
            case toxicByproduct:
                return R.drawable.ball_texture_toxic_byproduct;
            case verdeJaspe:
                return R.drawable.ball_texture_verde_jaspe;
            case dyedStonework:
                return R.drawable.ball_texture_dyed_stonework;

        }
        return -1;
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

    public void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }
}
