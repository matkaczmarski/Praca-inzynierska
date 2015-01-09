package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingDeque;

import mini.paranormalgolf.GameRenderer;
import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.R;


public class GameActivity extends Activity implements Runnable {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private GameRenderer gameRenderer = null;
    private Dialog pause_dialog = null;
    private Dialog end_game_dialog = null;

    private boolean vibrations;
    private boolean music;
    private boolean sound;
    private boolean shadows;

    protected PowerManager.WakeLock mWakeLock;

    private MediaPlayer mp = new MediaPlayer();

    public static boolean game = false;

    private String board_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LoadFonts();
        checkSharedPreferences();
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
        board_id = intent.getStringExtra("BOARD_ID");

        gameRenderer = new GameRenderer(this,(android.hardware.SensorManager)getSystemService(Context.SENSOR_SERVICE), board_id, vibrations, music, sound, shadows);

        if (supportsEs2) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(gameRenderer);
            rendererSet = true;
            game = true;
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
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                gameRenderer.getUpdater().getDrawManager().handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });
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

            checkSharedPreferences();
            gameRenderer.updatePreferences(vibrations, music, sound);
            if (rendererSet) {
                glSurfaceView.onResume();
            }
        }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    public void checkSharedPreferences()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);

        music = sharedPreferences.getBoolean(getString(R.string.options_music), false);
        sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
        vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
        shadows = sharedPreferences.getBoolean(getString(R.string.options_shadows), false);
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

    public void onPauseClick(View view)
    {
        onButtonClick();
        if (gameRenderer != null)
            gameRenderer.pause();
        //glSurfaceView.onPause();
        if (pause_dialog == null)
        {
            pause_dialog = new Dialog(this);
            pause_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pause_dialog.setContentView(R.layout.pause_dialog);
            setFontsForPauseDialog(pause_dialog);
            ((TextView)pause_dialog.findViewById(R.id.pause_resume)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onPauseClick(view);
                }
            });
            ((TextView)pause_dialog.findViewById(R.id.pause_restart)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onButtonClick();
                    restart();
                }
            });
            ((TextView)pause_dialog.findViewById(R.id.pause_menu)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onButtonClick();
                    pause_dialog.dismiss();
                    pause_dialog = null;
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, 0);
                }
            });
            ((TextView)pause_dialog.findViewById(R.id.pause_options)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onButtonClick();
                    Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                    startActivity(intent);
                }
            });
            pause_dialog.show();
            pause_dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        }
        else
        {
            pause_dialog.dismiss();
            pause_dialog = null;
        }
    }

    public void setFontsForPauseDialog(Dialog dialog)
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");

        TextView textView = (TextView)dialog.findViewById(R.id.pause_resume);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.pause_restart);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.pause_options);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.pause_menu);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.pause_dialog_title);
        textView.setTypeface(tf);
    }

    public void onButtonClick()
    {
        playSound("button.wav");
        vibrate();
    }

    public void playSound(String sound)
    {
        if (this.sound)
        {
            if (mp.isPlaying())
                mp.stop();
            try
            {
                mp.reset();
                AssetFileDescriptor afd = getAssets().openFd(sound);
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
                mp.start();
            } catch (IllegalStateException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
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

    public void restart()
    {
        setContentView(R.layout.activity_game);
        LoadFonts();
        glSurfaceView = (GLSurfaceView)findViewById(R.id.game_glsurface);

        Intent intent = getIntent();
        String board_id = intent.getStringExtra("BOARD_ID");

        gameRenderer = new GameRenderer(this,(android.hardware.SensorManager)getSystemService(Context.SENSOR_SERVICE), board_id, vibrations, music, sound, shadows);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(gameRenderer);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                gameRenderer.getUpdater().getDrawManager().handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });
        rendererSet = true;
        if (pause_dialog != null)
        {
            pause_dialog.dismiss();
            pause_dialog = null;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (game)
            onPauseClick(glSurfaceView);
    }



    public void onWinDialog(int diamonds, int time, boolean win)
    {
        glSurfaceView.onPause();
        GameActivity.game = false;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.win_dialog);
        loadFontsForDialog(dialog);
        setDialogTitleAndResult(dialog, diamonds, time, win);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }

    public void setDialogTitleAndResult(Dialog dialog, int diamonds, int time, boolean win)
    {
        ((TextView)dialog.findViewById(R.id.end_game_title)).setText(win ? getString(R.string.win) : getString(R.string.defeat));
        int result = win ? time * getResources().getInteger(R.integer.points_for_second) + diamonds * getResources().getInteger(R.integer.points_for_diamond) : 0;
        ((TextView)dialog.findViewById(R.id.end_game_result)).setText(getString(R.string.result) + " " + result + " " + getString(R.string.points));

        BoardInfo boardInfo = (new XMLParser(this).getBoardInfo(board_id));
        ((ImageView)dialog.findViewById(R.id.end_game_first_star)).setImageDrawable(getResources().getDrawable(win ? R.drawable.star_full : R.drawable.star_empty));
        ((ImageView)dialog.findViewById(R.id.end_game_second_star)).setImageDrawable(getResources().getDrawable((result >= boardInfo.getTwo_stars()) ? R.drawable.star_full : R.drawable.star_empty));
        ((ImageView)dialog.findViewById(R.id.end_game_third_star)).setImageDrawable(getResources().getDrawable((result >= boardInfo.getThree_stars()) ? R.drawable.star_full : R.drawable.star_empty));

        updateBestResult(board_id, result);

        ((TextView) dialog.findViewById(R.id.end_game_ok_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onOkClick(view);
            }
        });
    }

    public void updateBestResult(String board_id, int result)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        if (result > sharedPreferences.getInt(board_id, 0))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(board_id);
            editor.putInt(board_id, result);
            editor.commit();
        }
    }

    public void loadFontsForDialog(Dialog dialog)
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");

        TextView textView = (TextView)dialog.findViewById(R.id.end_game_title);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.end_game_result);
        textView.setTypeface(tf);
    }



    public void onOkClick(View view)
    {
        onButtonClick();
        if (end_game_dialog != null)
        {
            end_game_dialog.dismiss();
            end_game_dialog = null;
        }
        finish();
    }
}
