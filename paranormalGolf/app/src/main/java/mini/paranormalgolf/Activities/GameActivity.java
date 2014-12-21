package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import mini.paranormalgolf.GameRenderer;
import mini.paranormalgolf.R;


public class GameActivity extends Activity implements Runnable {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    protected PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LoadFonts();
        glSurfaceView = (GLSurfaceView)findViewById(R.id.game_glsurface);

        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        Intent intent = getIntent();
        String board_id = intent.getStringExtra("BOARD_ID");

        final GameRenderer gameRenderer = new GameRenderer(this,(android.hardware.SensorManager)getSystemService(Context.SENSOR_SERVICE), board_id);

        if (supportsEs2) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(gameRenderer);
            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since
             * we're not doing anything, the app will crash if the device
             * doesn't support OpenGL ES 2.0. If we publish on the market, we
             * should also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //setContentView(glSurfaceView);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
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
*/
        @Override
        protected void onPause() {
            super.onPause();

            if (rendererSet) {
                glSurfaceView.onPause();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

            if (rendererSet) {
                glSurfaceView.onResume();
            }
        }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    public void updatePanel(int time, int diamonds)
    {
        ((TextView)findViewById(R.id.game_activity_time)).setText(time + "");
        ((TextView)findViewById(R.id.game_activity_diamonds)).setText(diamonds + "");
    }

    public void updatePanelDiamonds(int diamonds)
    {
        ((TextView)findViewById(R.id.game_activity_diamonds)).setText(diamonds + "");
    }

    public void updatePanelTime(long time)
    {
        ((TextView)findViewById(R.id.game_activity_time)).setText(time + "");
    }

    @Override
    public void run()
    {

    }

    public void LoadFonts()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");
        TextView tv = (TextView) findViewById(R.id.game_activity_time_header);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.game_activity_time);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.game_activity_diamonds_header);
        tv.setTypeface(tf);

        tv = (TextView)findViewById(R.id.game_activity_diamonds);
        tv.setTypeface(tf);
    }
}
