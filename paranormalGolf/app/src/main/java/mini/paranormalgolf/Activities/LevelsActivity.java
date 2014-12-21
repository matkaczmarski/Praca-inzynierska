package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.R;

public class LevelsActivity extends Activity
{
    String board_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        LoadFonts();
        InitializeBoardList();
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
    }

    public void InitializeBoardList()
    {
        String[] board_id = getResources().getStringArray(R.array.boards_id);
        int[] results = new int[board_id.length];
        for (int i = 0; i < board_id.length; i++)
        {
            String id = board_id[i];
            //TODO wczytanie najlepszych wyników
            results[i] = 0;
        }
        LevelsListAdapter levelsListAdapter = new LevelsListAdapter(this, board_id, results);
        final ListView listView = (ListView)findViewById(R.id.levels_list);
        listView.setAdapter(levelsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ((LevelsListAdapter) listView.getAdapter()).setSelectedIndex(position);
            }
        });
        levelsListAdapter.setSelectedIndex(0);
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
        finish();
    }

    public void onStartClick (View view)
    {
        if (board_id != null)
        {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("BOARD_ID", board_id);
            startActivity(intent);
        }
    }

    public void selectedBoardChanged(String id, int result, int nr)
    {
        board_id = id;
        int picId = getResources().getIdentifier("board_" + nr, "drawable", getApplicationContext().getPackageName());
        ((ImageView)findViewById(R.id.board_image)).setImageDrawable(getResources().getDrawable(picId));

        XMLParser xmlParser = new XMLParser(this);
        BoardInfo boardInfo = xmlParser.getBoardInfo(id);

        if (boardInfo == null)
        {
            Toast.makeText(this, "No board info data!", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        if (boardInfo.isAccomplished())
            ((ImageView) findViewById(R.id.level_select_first_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView) findViewById(R.id.level_select_first_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
        if (boardInfo.getTwo_stars() <= boardInfo.getBest_result())
            ((ImageView)findViewById(R.id.level_select_second_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView)findViewById(R.id.level_select_second_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
        if (boardInfo.getThree_stars() <= boardInfo.getBest_result())
            ((ImageView)findViewById(R.id.level_select_third_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        else
            ((ImageView)findViewById(R.id.level_select_third_star)).setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
    }

    public static int getResId(String variableName, Class<?> c) {

        try
        {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
}