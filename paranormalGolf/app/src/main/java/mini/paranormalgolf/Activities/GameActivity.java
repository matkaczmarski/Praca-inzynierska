package mini.paranormalgolf.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mini.paranormalgolf.Controls.MyBroadcastReceiver;
import mini.paranormalgolf.GameRenderer;
import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.R;


public class GameActivity extends Activity  {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private GameRenderer gameRenderer = null;
    private Dialog pause_dialog = null;
    private Dialog end_game_dialog = null;

    private boolean vibrations;
    private boolean music;
    private boolean sound;
    private boolean shadows;
    private int texture;

    public static boolean screen_lock = false;

    private boolean radius_set = false;
    private float radius = 1.0f;

    private boolean win = false;

    protected PowerManager.WakeLock mWakeLock;

    public static boolean game = false;

    private String board_id;

    private MediaPlayer backgroundMusic;

    private BroadcastReceiver broadcastReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //LoadFonts();

        Bundle extras = getIntent().getExtras();
        radius_set = extras.getBoolean(getString(R.string.radius_set));
        radius = extras.getFloat(getString(R.string.radius));

        checkSharedPreferences(true);
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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction() == Intent.ACTION_SCREEN_OFF)
                {
                    if (pause_dialog == null)
                        onPauseClick(null);
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        gameRenderer = new GameRenderer(this, getApplicationContext(), board_id, vibrations, music, sound, shadows, texture, radius_set, radius);

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

        glSurfaceView.setOnTouchListener(new View.OnTouchListener()
        {
            float previousX
                    ,
                    previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event != null)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();

                        glSurfaceView.queueEvent(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                gameRenderer.getUpdater().getDrawManager().handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }

                    return true;
                } else
                {
                    return false;
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
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
            // automatically handle clicks on the Home/Up sound_button, so long
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

            if(backgroundMusic != null)
            {
                backgroundMusic.release();
            }
            if (rendererSet) {
                glSurfaceView.onPause();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();

            checkSharedPreferences(false);
            gameRenderer.updatePreferences(vibrations, music, sound);
            if (rendererSet) {
                glSurfaceView.onResume();
            }
        }

    @Override
    protected void onDestroy() {
        gameRenderer.getUpdater().getDrawManager().releaseResources();
        if (backgroundMusic != null) {
            backgroundMusic.release();
        }
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        this.mWakeLock.release();
        super.onDestroy();
    }

    public void checkSharedPreferences(boolean onCreate)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);

        music = sharedPreferences.getBoolean(getString(R.string.options_music), false);
        sound = sharedPreferences.getBoolean(getString(R.string.options_sound_effects), false);
        vibrations = sharedPreferences.getBoolean(getString(R.string.options_vibrations), false);
        shadows = sharedPreferences.getBoolean(getString(R.string.options_shadows), false);
        texture = sharedPreferences.getInt(getString(R.string.options_texture), 0);

        if (music && !onCreate)
        {
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            backgroundMusic = MediaPlayer.create(this, R.raw.sound_motyw);
            backgroundMusic.setLooping(true);

            switch (am.getRingerMode())
            {
                case AudioManager.RINGER_MODE_SILENT:
                    backgroundMusic.setVolume(0.0f, 0.0f);
                    break;
                default:
                    backgroundMusic.setVolume(1.0f, 1.0f);
                    break;
            }
            backgroundMusic.start();
        }
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

    public void onPauseClick(View view)
    {
        if (!game)
            return;
        onButtonClick();

        if (pause_dialog == null)
        {
            if (gameRenderer != null)
                gameRenderer.pause();

            pause_dialog = new Dialog(this, R.style.PauseDialogTheme);
            pause_dialog.setContentView(R.layout.pause_dialog);
            pause_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            pause_dialog.setCanceledOnTouchOutside(false);
            //setFontsForPauseDialog(pause_dialog);
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
                    intent.putExtra("ON_PAUSE", true);
                    startActivity(intent);
                }
            });
            pause_dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    onPauseClick(null);
                }
            });
            pause_dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            pause_dialog.show();
        }
        else
        {
            pause_dialog.dismiss();
            pause_dialog = null;


            if (gameRenderer != null)
                gameRenderer.pause();
        }
    }

    /*public void setFontsForPauseDialog(Dialog dialog)
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
    }*/

    public void onButtonClick()
    {
        playSound(ResourceHelper.SOUND_BUTTON);
        vibrate();
    }

    public void playSound(final int sound)
    {
        if (this.sound)
        {
            new Runnable(){
                @Override
                public void run()
                {
                    ResourceHelper.playSound(sound);
                }
            }.run();
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
        //LoadFonts();
        ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        glSurfaceView = (GLSurfaceView)findViewById(R.id.game_glsurface);

        Intent intent = getIntent();
        String board_id = intent.getStringExtra("BOARD_ID");

        gameRenderer = new GameRenderer(this, getApplicationContext(), board_id, vibrations, music, sound, shadows, texture, radius_set, radius);
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
        dialog.dismiss();
    }

    @Override
    public void onBackPressed()
    {
        if (game)
            onPauseClick(glSurfaceView);
    }



    public void onWinDialog(int diamonds, int time, boolean win)
    {
        if (end_game_dialog != null)
            return;
        if (backgroundMusic != null)
            backgroundMusic.stop();
        playEndGameSound(win);
        glSurfaceView.onPause();
        this.win = win;
        GameActivity.game = false;
        end_game_dialog = new Dialog(this, R.style.EndGameDialogTheme);
        end_game_dialog.setContentView(R.layout.win_dialog);
        end_game_dialog.setCanceledOnTouchOutside(false);
        //loadFontsForDialog(end_game_dialog);
        setDialogTitleAndResult(end_game_dialog, diamonds, time, win);

        end_game_dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        end_game_dialog.show();
    }

    public void playEndGameSound(boolean win)
    {
        playSound(win ? ResourceHelper.SOUND_WIN : ResourceHelper.SOUND_LOSE);
    }

    public void setDialogTitleAndResult(Dialog dialog, int diamonds, int time, boolean win)
    {
        ((TextView)dialog.findViewById(R.id.end_game_title)).setText(win ? getString(R.string.win) : getString(R.string.defeat));
        int result = win ? time * getResources().getInteger(R.integer.points_for_second) + diamonds * getResources().getInteger(R.integer.points_for_diamond) : 0;
        ((TextView)dialog.findViewById(R.id.end_game_result)).setText(getString(R.string.result) + " " + result + " " + getString(R.string.points));

        BoardInfo boardInfo = (new XMLParser(this).getBoardInfo(board_id));
        ImageView imageView = (ImageView)dialog.findViewById(R.id.end_game_first_star);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(getResources().getDrawable(win ? R.drawable.star_full : R.drawable.star_empty_white));
        imageView = (ImageView)dialog.findViewById(R.id.end_game_second_star);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(getResources().getDrawable((result >= boardInfo.getTwo_stars()) ? R.drawable.star_full : R.drawable.star_empty_white));
        imageView = (ImageView)dialog.findViewById(R.id.end_game_third_star);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(getResources().getDrawable((result >= boardInfo.getThree_stars()) ? R.drawable.star_full : R.drawable.star_empty_white));

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

    /*public void loadFontsForDialog(Dialog dialog)
    {
        Typeface tf = Typeface.createFromAsset(getAssets(), "batmanFont.ttf");

        TextView textView = (TextView)dialog.findViewById(R.id.end_game_title);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.end_game_result);
        textView.setTypeface(tf);

        textView = (TextView)dialog.findViewById(R.id.end_game_ok_button);
        textView.setTypeface(tf);
    }*/



    public void onOkClick(View view)
    {
        onButtonClick();
        if (end_game_dialog != null)
        {
            end_game_dialog.dismiss();
            end_game_dialog = null;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("WIN", this.win);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
