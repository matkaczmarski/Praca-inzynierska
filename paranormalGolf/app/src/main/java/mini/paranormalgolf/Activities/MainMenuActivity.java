package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import mini.paranormalgolf.GameActivity;
import mini.paranormalgolf.R;

public class MainMenuActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LoadFonts();
        ManageFiles();
    }

    public void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView) findViewById(R.id.main_menu_title);
        tv.setTypeface(tf);

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
        Intent intent = new Intent(this, LevelsActivity.class);
        startActivity(intent);
    }

    public void onOptionsClick(View view)
    {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    public void onHelpClick(View view)
    {

    }

    public void onExitClick(View view)
    {
        finish();
    }
}
