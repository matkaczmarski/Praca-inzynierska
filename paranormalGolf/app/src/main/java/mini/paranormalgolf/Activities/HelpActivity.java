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

/**
 * Aktywność odpowiadająca oknie pomocy.
 */
public class HelpActivity extends Activity
{
    /**
     * Informacja o tym czy dźwięki w grze są dopuszczone przez użytkownika.
     */
    private boolean sound;

    /**
     * Informacja o tym czy wibracje są dopuszczone przez użytkownika
     */
    private boolean vibrations;

    /**
     * Informacja o tym, która strona pomocy jest aktualnie wyświetlana.
     */
    private int page = -1;

    /**
     * Liczba stron pomocy.
     */
    private int page_max = 1;

    /**
     * Identyfikatory stron pomocy.
     */
    private int[] pages;

    /**
     * Metoda wywoływana, gdy aktywność jest startowana.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        pages = new int[4];
        pages[0] = R.layout.help_page_0;
        pages[1] = R.layout.help_page_1;
        pages[2] = R.layout.help_page_2;
        pages[3] = R.layout.help_page_3;

        page_max = pages.length;

        checkSharedPreferences();
        changePage();
    }

    /**
     * Pobiera opcje zapisane przez użytkownika.
     */
    private void checkSharedPreferences()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0)
        {
            finish();
        }
        else
        {
            sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
            vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
        }
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
    private void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(getResources().getInteger(R.integer.vibrations_click_time));
        }
    }

    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Menu.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onMenuClick(View view)
    {
        onButtonClick();
        finish();
    }

    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Dalej.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onNextClick(View view)
    {
        onButtonClick();
        changePage();
    }

    /**
     * Zmienia wyświetlaną stronę pomocy na kolejną.
     */
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
