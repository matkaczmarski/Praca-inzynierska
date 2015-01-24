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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mini.paranormalgolf.Controls.ConsoleView;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.R;

/**
 * Aktywność odpowiadająca menu głównemu gry.
 */
public class MainMenuActivity extends Activity
{
    /**
     * Informacja o tym czy dźwięki w grze są dopuszczone przez użytkownika.
     */
    private boolean sound = false;

    /**
     * Informacja o tym czy wibracje są dopuszczone przez użytkownika
     */
    private boolean vibrations = false;

    /**
     * Lista kliknięć - do kontrolowania czy należy pokazać konsolę.
     */
    private List<Long> clicks = new ArrayList<Long>();

    /**
     * Stała informująca o tym ile kliknięć jest wymaganych do pokazania konsoli.
     */
    private final int clicks_nr = 3;

    /**
     * Stała informująca o tym ile czas w ms musi upłynąć od pierwszego do trzeciego kliknięcia, aby pokazaż konsolę.
     */
    private final long clicks_max_interval = 1000;

    /**
     * Informacja o tym, czy użytkownik zmienił domyślny promień kulki (poprzez konsolę).
     */
    public boolean radius_set = false;

    /**
     * Promień kulki zdefiniowany przez użytkownika.
     */
    public float radius = Ball.DEFAULT_RADIUS;

    /**
     * Metoda wywoływana, gdy aktywność jest startowana.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ResourceHelper.initSounds(getApplicationContext());
        checkSharedPreferences();

        setContentView(R.layout.activity_main_menu);
    }

    /**
     * Pobiera opcje zapisane przez użytkownika lub tworzy domyślny zestaw w przypadku ich braku
     */
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
            editor.putInt(getString(R.string.options_texture), 0);
            String language = Locale.getDefault().getLanguage();
            if (!language.equalsIgnoreCase("pl"))
                language = "en";
            editor.putString(getString(R.string.options_language), language);

            String[] boards_id = getResources().getStringArray(R.array.boards_id);
            for (String board_id : boards_id)
                editor.putInt(board_id, 0);

            editor.commit();

            sound = vibrations = true;
        }
        else
        {
            sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
            vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);

            String language = sharedPreferences.getString(getString(R.string.options_language), "en");
            changeLanguage(language);
        }
    }

    /**
     * Zmienia język.
     * @param language Oznaczenie języka
     */
    public void changeLanguage(String language)
    {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.equalsIgnoreCase("pl") ? "pl_PL" : "en_US");
        res.updateConfiguration(conf, dm);
    }

    /**
     * Zbiera informacje o kliknięciach nazwy gry i jeśli spełnione są warunki to wyświetla konsolę.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onTitleClick(View view)
    {
        long time = System.currentTimeMillis();
        if (clicks.size() < clicks_nr)
            clicks.add(time);
        else
        {
            clicks.remove(0);
            clicks.add(time);
        }
        if (clicks.size() == clicks_nr)
        {
            if (clicks.get(2) - clicks.get(0) <= clicks_max_interval)
                ((ConsoleView) findViewById(R.id.console_view)).show();
        }

    }

    /**
     * Wywoływana w przypadku wciśnięcia przycisku wstecz na urządzeniu.
     */
    @Override
    public void onBackPressed()
    {
        ConsoleView consoleView = (ConsoleView)findViewById(R.id.console_view);
        if (consoleView.getVisibility() == View.VISIBLE)
            consoleView.hide();
    }

    /**
     * Wywoływana w przypadku wciśnięcia przycisku Start.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onStartClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
        intent.putExtra(getString(R.string.radius_set), radius_set);
        intent.putExtra(getString(R.string.radius), radius);
        startActivity(intent);
    }

    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Opcje.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onOptionsClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        intent.putExtra("ON_PAUSE", false);
        startActivity(intent);
    }

    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Pomoc.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onHelpClick(View view)
    {
        onButtonClick();
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
    }


    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Koniec.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onExitClick(View view)
    {
        onButtonClick();
        finish();
    }

    /**
     * Wywoływana przy wciśnięciu przycisku - odtwarza dźwięk i powoduje wibracje.
     */
    public void onButtonClick()
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
     * Wywoływana przy wznawianiu aktywności.
     * Pobiera aktualne opcje wybrane przez użytkownika.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        checkSharedPreferences();
        setContentView(R.layout.activity_main_menu);
    }
}
