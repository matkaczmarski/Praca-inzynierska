package mini.paranormalgolf;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mini.paranormalgolf.Activities.GameActivity;
import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.FPSCounter;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Board;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.Updater;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_FRONT_AND_BACK;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompressedTexImage2D;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFinish;


/**
 * Created by Mateusz on 2014-12-05.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private static Activity activity;

    private Updater updater;
    private String board_id;
    private BoardInfo boardInfo;
    private long lastTime;
    private long timeLeft;
    private boolean lastTimeUpdated = false;
    private boolean paused = false;

    private boolean vibrations;
    private boolean music;
    private boolean sound;
    private boolean shadows;
    private int texture;

    private FPSCounter fpsCounter;

    public Updater getUpdater(){return updater;}

    public GameRenderer(Activity activity, Context context, String board_id, boolean vibrations, boolean music, boolean sound, boolean shadows, int texture, boolean radius_set, float radius)
    {
        this.context = context;
        this.activity = activity;
        this.board_id = board_id;
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;
        this.shadows = shadows;
        this.texture = texture;

        Board board = loadBoard(board_id);
        float ball_radius = radius_set ? radius : Ball.DEFAULT_RADIUS;
        Ball ball = new Ball(new Point(board.ballLocation.x, board.ballLocation.y + ball_radius, board.ballLocation.z), ball_radius, new Vector(0f, 0f, 0f), Ball.BallTexture.values()[texture], context);

        boardInfo = loadBoardInfo(board_id);
        timeLeft = boardInfo.getTime() * 1000;

        final Context contextForUiThread = activity;

        ((GameActivity)activity).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((GameActivity)contextForUiThread).updatePanel(boardInfo.getTime(), 0);
            }
        });
        updater = new Updater(context, ball, board, vibrations, music, sound, shadows, this);
        fpsCounter = new FPSCounter();
    }

    public void changeBoard(String board_id)
    {
        Ball ball = new Ball(new Point(0f, 1f, 3f), 1f, new Vector(0f, 0f, 0f), Ball.BallTexture.values()[texture], context);
        updater.changeBoardAndBall(loadBoard(board_id), ball);
    }

    public Board loadBoard(String board_id)
    {
        XMLParser xmlParser = new XMLParser(context);
        return xmlParser.getBoard(board_id);
    }

    public BoardInfo loadBoardInfo (String board_id)
    {
        XMLParser xmlParser = new XMLParser(context);
        return xmlParser.getBoardInfo(board_id);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GLES20.GL_CULL_FACE);

        updater.setContext(activity);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        updater.surfaceChange(width, height);
    }


    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        float interval = 0;
        if(LoggerConfig.ON) {
            fpsCounter.logFrame();
        }
        if (!lastTimeUpdated)
        {
            lastTime = System.currentTimeMillis();
            lastTimeUpdated = true;
        }
        if (!paused)
        {
            long actual_time = System.currentTimeMillis();
            interval = actual_time - lastTime;
            timeLeft -= actual_time - lastTime;
            lastTime = actual_time;
        }
        //long seconds_past = (actual_time - startTime) / 1000;
        final long seconds_left = timeLeft / 1000;
        if (paused)
        {
            lastTimeUpdated = false;
            boardInfo.setTime((int)seconds_left);
            //interval = 0;
            return;
        }
        ((GameActivity)activity).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView)(activity.findViewById(R.id.game_activity_time))).setText(seconds_left + "");
            }
        });

        UpdateResult updateResult = updater.update((float)(interval / 1000f));
        if (timeLeft <= 0)
        {
            updateResult = UpdateResult.DEFEAT;
        }
        if (updateResult != UpdateResult.NONE) {
            //dotarcie do mety?
            if (updateResult == UpdateResult.DEFEAT || updateResult == UpdateResult.WIN)
            {
                final boolean win = updateResult == UpdateResult.WIN;
                ((GameActivity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity) activity).onWinDialog(updater.getCollectedDiamondsCount(), (int) Math.ceil(timeLeft / 1000), win);
                    }
                });
            }
            if (updateResult == UpdateResult.PAUSE)
                return;
        }
        updater.draw();
    }

    public void pause()
    {
        if (paused)
        {
            lastTimeUpdated = false;
            paused = false;
        }
        else
        {
            paused = true;
        }
    }

    public void addTime(int value)
    {
        timeLeft += value * 1000;
    }

    public void updatePreferences (boolean vibrations, boolean music, boolean sound)
    {
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;

        if(updater != null)
            updater.updatePreferences(vibrations, music, sound);
    }

    public void changeActivity(Activity activity)
    {
        this.activity = activity;
    }

}
