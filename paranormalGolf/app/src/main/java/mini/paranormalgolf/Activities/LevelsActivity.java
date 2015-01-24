package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;

import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.R;

/**
 * Aktywność odpowiadająca okno wyboru poziomu.
 */
public class LevelsActivity extends Activity
{
    /**
     * Id wybranego poziomu.
     */
    String board_id = null;

    /**
     * Informacja o tym czy dźwięki w grze są dopuszczone przez użytkownika.
     */
    private boolean sound;

    /**
     * Informacja o tym czy wibracje są dopuszczone przez użytkownika
     */
    private boolean vibrations;


    private boolean last_game_win = false;

    /**
     * Czy wywołano pierwsze onResume.
     */
    private boolean firstResume = true;

    /**
     * Czy użytkownik zdefiniował promień kulki.
     */
    private boolean radius_set = false;

    /**
     * Promień kulki zdefiniowany przez użytkownika
     */
    private float radius = 1.0f;

    /**
     * Metoda wywoływana, gdy aktywność jest startowana.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        Bundle extras = getIntent().getExtras();
        radius_set = extras.getBoolean(getString(R.string.radius_set));
        radius = extras.getFloat(getString(R.string.radius));

        InitializeBoardList();
        checkSharedPreferences();
    }

    /**
     * Pobiera opcje zapisane przez użytkownika.
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
            sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
            vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
        }
    }

    /**
     * Wypełnia listę poziomów.
     */
    public void InitializeBoardList()
    {
        String[] board_id = getResources().getStringArray(R.array.boards_id);
        BoardInfo[] boardInfos = new BoardInfo[board_id.length];
        boolean[] board_locked = new boolean[board_id.length];
        XMLParser xmlParser = new XMLParser(this);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        boolean locked = false;
        for (int i = 0; i < board_id.length; i++)
        {
            BoardInfo boardInfo = xmlParser.getBoardInfo(board_id[i]);
            boardInfos[i] = boardInfo;
            board_locked[i] = locked;
            if (!locked)
                if (sharedPreferences.getInt(board_id[i], 0) == 0)
                    locked = true;
        }
        final ListView listView = (ListView)findViewById(R.id.levels_list);
        LevelsListAdapter levelsListAdapter = new LevelsListAdapter(this, boardInfos, listView, board_locked);
        listView.setAdapter(levelsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ((LevelsListAdapter) listView.getAdapter()).setSelectedIndex(position);
            }
        });
    }

    /**
     * Zmienia wybrany poziom (automatycznie, nie na skutek działania użytkownika).
     * @param levelsListAdapter Adapter listy poziomów.
     * @param nr Nr wybranego poziomu.
     */
    public void changeSelectedBoard(LevelsListAdapter levelsListAdapter, int nr)
    {
        boolean temp_vibrations = vibrations;
        boolean temp_sound = sound;

        vibrations = sound = false;
        ((ListView)findViewById(R.id.levels_list)).setSelection(nr);
        levelsListAdapter.setSelectedIndex(nr);

        vibrations = temp_vibrations;
        sound = temp_sound;
    }

    /**
     * Wywoływana w przypadku wciśnięcia przycisku Wstecz.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onBackClick (View view)
    {
        onButtonClick();
        finish();
    }

    /**
     * Wywoływana w przypadku wciśnięcia przycisku Start.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onStartClick (View view)
    {
        onButtonClick();
        if (board_id != null)
        {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("BOARD_ID", board_id);
            intent.putExtra(getString(R.string.radius_set), radius_set);
            intent.putExtra(getString(R.string.radius), radius);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Wywołana gdy aktywność zwraca rezultat.
     * @param requestCode Kod żądania.
     * @param resultCode Kod rezultatu.
     * @param data Przesłane informacje.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0)
        {
            last_game_win = data.getBooleanExtra("WIN", false);
        }
    }

    /**
     * Wywoływana gdy następuje zmiania wybranego poziomu.
     * @param boardInfo Obiekt BoardInfo danego poziomu.
     * @param nr Numer wybranego poziomu.
     */
    public void selectedBoardChanged(BoardInfo boardInfo, int nr)
    {
        onButtonClick();
        board_id = boardInfo.getBoard_id();
        int picId = getResources().getIdentifier("board_" + nr, "drawable", getApplicationContext().getPackageName());
        ((ImageView)findViewById(R.id.board_image)).setImageDrawable(getResources().getDrawable(picId));

        if (boardInfo == null)
        {
            Toast.makeText(this, "No board info data!", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        int bestResult = sharedPreferences.getInt(board_id, 0);

        ((TextView)findViewById(R.id.levels_select_best_result)).setText(getString(R.string.best_result) + " " + bestResult + " " + getString(R.string.points));

        if (bestResult > 0)
            ((ImageView) findViewById(R.id.level_select_first_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView) findViewById(R.id.level_select_first_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
        if (boardInfo.getTwo_stars() <= bestResult)
            ((ImageView)findViewById(R.id.level_select_second_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView)findViewById(R.id.level_select_second_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
        if (boardInfo.getThree_stars() <= bestResult)
            ((ImageView)findViewById(R.id.level_select_third_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView)findViewById(R.id.level_select_third_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
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
     * Wywoływana przy wznawianiu aktywności.
     * Ustawia wybrany poziom.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (firstResume)
        {
            chooseLastAvailableLevel();
            firstResume = false;
            return;
        }

        InitializeBoardList();
        if (board_id == null)
            chooseLastAvailableLevel();
        else
        {
            int nr = Integer.parseInt(board_id.split("_")[1]) - 1;
            LevelsListAdapter levelsListAdapter = (LevelsListAdapter)(((ListView)findViewById(R.id.levels_list)).getAdapter());
            if (last_game_win)
            {
                String[] boards_id = getResources().getStringArray(R.array.boards_id);
                if (nr + 1 != boards_id.length)
                    changeSelectedBoard(levelsListAdapter, nr + 1);
                else
                    changeSelectedBoard(levelsListAdapter, nr);
            }
            else changeSelectedBoard(levelsListAdapter, nr);
        }
    }

    /**
     * Wybiera ostatni z dostępnych poziomów.
     */
    private void chooseLastAvailableLevel()
    {
        LevelsListAdapter levelsListAdapter = (LevelsListAdapter)(((ListView)findViewById(R.id.levels_list)).getAdapter());
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        String[] boards_id = getResources().getStringArray(R.array.boards_id);
        for (int i = 0; i < boards_id.length; i++)
        {
            String board_id = boards_id[i];
            int result = sharedPreferences.getInt(board_id, 0);
            if (result == 0)
            {
                changeSelectedBoard(levelsListAdapter, i);
                return;
            }
        }
        changeSelectedBoard(levelsListAdapter, boards_id.length - 1);
    }
}