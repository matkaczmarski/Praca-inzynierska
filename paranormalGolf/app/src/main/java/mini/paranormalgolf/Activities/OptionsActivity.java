package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.R;

/**
 * Aktywność odpowiadająca menu opcji.
 */
public class OptionsActivity extends Activity
{
    /**
     * Informacja o tym czy muzyka w grze jest dopuszczona przez użytkownika.
     */
    private boolean music;

    /**
     * Informacja o tym czy dźwięki w grze są dopuszczone przez użytkownika.
     */
    private boolean sound;

    /**
     * Informacja o tym czy wibracje są dopuszczone przez użytkownika.
     */
    private boolean vibrations;

    /**
     * Informacja o tym czy cienie w grze są dopuszczone przez użytkownika.
     */
    private boolean shadows;

    /**
     * Id wybranej przez użytkownika tekstury.
     */
    private int texture;

    /**
     * Informacja o tym czy aktywność została wywołana podczas pauzy w grze.
     */
    private boolean onPause;

    /**
     * Wybrany przez użytkownika język.
     */
    private String language;

    /**
     * Metoda wywoływana, gdy aktywność jest startowana.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Bundle extras = getIntent().getExtras();
        onPause = extras.getBoolean("ON_PAUSE");
        if (onPause)
        {
            findViewById(R.id.options_pl).setVisibility(View.GONE);
            findViewById(R.id.options_en).setVisibility(View.GONE);
            findViewById(R.id.options_shadows).setVisibility(View.GONE);
            findViewById(R.id.options_texture_textview).setVisibility(View.GONE);
            findViewById(R.id.options_texture_scroll_view).setVisibility(View.GONE);
            findViewById(R.id.options_chosen_texture).setVisibility(View.GONE);
            findViewById(R.id.options_texture_chosen_textview).setVisibility(View.GONE);
        }

        checkSharedPreferences();
        if (!onPause)
            loadTextures();
    }

    /**
     * Ładuje dostępne tekstury do ScrollView oraz aktualnie wybraną teksturę.
     */
    public void loadTextures()
    {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.options_texture_scroll_view);
        linearLayout.removeAllViews();
        int i = 0;
        for (Ball.BallTexture ballTexture : Ball.BallTexture.values())
        {
            if (i == texture)
                findViewById(R.id.options_chosen_texture).setBackgroundResource(getTextureResource(ballTexture));
            View view = getLayoutInflater().inflate(R.layout.texture_item, null);
            view.findViewById(R.id.texture_item_image).setBackgroundResource(getTextureResource(ballTexture));
            view.findViewById(R.id.texture_item_image).setTag(i++);
            linearLayout.addView(view);
        }
    }

    /**
     * Zwraca id tekstury.
     * @param ballTextureType Tekstura, której id chcemy otrzymać.
     * @return Id tekstury.
     */
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

    /**
     * Pobiera opcje zapisane przez użytkownika lub tworzy domyślny zestaw w przypadku ich braku
     */
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

            texture = sharedPreferences.getInt(getString(R.string.options_texture), 0);

            updateControls();
        }
    }

    /**
     * Ustawia wartości kontrolek na te wybrane przez użytkownika.
     */
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

    /**
     * Zmienia język.
     * @param language Oznaczenie języka.
     */
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

    /**
     * Wywoływana w przypadku wciśnięcia przycisku Anuluj.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onCancelClick(View view)
    {
        onButtonClick();
        finish();
    }

    /**
     * Wywoływana w przypadku wciśnięcia przycisku Zapisz.
     * @param view Kontrolka, która została kliknięta.
     */
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
        editor.putInt(getString(R.string.options_texture), texture);
        editor.commit();
        finish();
    }

    /**
     * Wywoływana w przypadku wyboru polskiego języka.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onPolishPick(View view)
    {
        ((CheckBox)view).setChecked(true);
        ((CheckBox)findViewById(R.id.options_en)).setChecked(false);
        changeLanguage("pl");
        setContentView(R.layout.activity_options);
        updateControls();
        if (!onPause)
            loadTextures();
        onButtonClick();
    }

    /**
     * Wywoływana w przypadku wyboru angielskiego języka.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onEnglishPick(View view)
    {
        ((CheckBox)view).setChecked(true);
        ((CheckBox)findViewById(R.id.options_pl)).setChecked(false);
        changeLanguage("en");
        setContentView(R.layout.activity_options);
        //LoadFonts();
        updateControls();
        if (!onPause)
            loadTextures();
        onButtonClick();
    }

    /**
     * Wywoływana w przypadku wciśnięcia checkbox'u odpowiedzialnego za muzykę.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onMusicClick (View view)
    {
        music = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    /**
     * Wywoływana w przypadku wciśnięcia checkbox'u odpowiedzialnego za dźwięki.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onSoundClick (View view)
    {
        sound = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    /**
     * Wywoływana w przypadku wciśnięcia checkbox'u odpowiedzialnego za wibracje.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onVibrationsClick (View view)
    {
        vibrations = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    /**
     * Wywoływana w przypadku wciśnięcia checkbox'u odpowiedzialnego za cienie.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onShadowsClick (View view)
    {
        shadows = ((CheckBox)view).isChecked();
        onButtonClick();
    }

    /**
     * Wywoływana przy wciśnięciu przycisku - odtwarza dźwięk i powoduje wibracje.
     */
    private void onButtonClick()
    {
        playSound(ResourceHelper.SOUND_BUTTON);
        vibrate();
    }

    /**
     * Odtwarza dźwięk (jeśli użytkownik go dopuszcza).
     * @param sound Id dźwięku
     */
    public void playSound(int sound)
    {
        if (this.sound)
        {
            ResourceHelper.playSound(sound);
        }
    }

    /**
     * Uruchamia wibracje (jeśli użytkownik je dopuszcza).
     */
    public void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }

    /**
     * Wywoływana w przypadku zmiany wyboru textury.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onTextureClick(View view)
    {
        onButtonClick();

        int id = Integer.parseInt(view.getTag().toString());
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.options_texture_scroll_view);
        for (int i = 0; i < linearLayout.getChildCount(); i++)
        {
            View child = linearLayout.getChildAt(i);
            ImageView child_imageView = (ImageView)child.findViewById(R.id.texture_item_image);
            if (id == Integer.parseInt(child_imageView.getTag().toString()))
            {
                findViewById(R.id.options_chosen_texture).setBackgroundResource(getTextureResource(Ball.BallTexture.values()[id]));
                texture = id;
            }
        }
    }
}
