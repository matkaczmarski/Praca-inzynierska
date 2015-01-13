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

public class LevelsActivity extends Activity
{
    String board_id = null;
    private boolean music;
    private boolean sound;
    private boolean vibrations;

    private boolean last_game_win = false;

    private boolean firstResume = true;

    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        LoadFonts();
        InitializeBoardList();
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
        }
    }

    public void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView) findViewById(R.id.select_level_title);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.select_level_back);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.select_level_start);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.levels_select_best_result);
        tv.setTypeface(tf);
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.levels, menu);
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

    public void onBackClick (View view)
    {
        onButtonClick();
        finish();
    }

    public void onStartClick (View view)
    {
        onButtonClick();
        if (board_id != null)
        {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("BOARD_ID", board_id);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0)
        {
            last_game_win = data.getBooleanExtra("WIN", false);
        }
    }

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

    @Override
    protected void onDestroy()
    {
        this.mWakeLock.release();
        super.onDestroy();
    }

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