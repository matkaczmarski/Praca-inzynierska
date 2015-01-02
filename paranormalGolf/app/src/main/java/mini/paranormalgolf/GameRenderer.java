package mini.paranormalgolf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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

    private final Activity context;
    private Updater updater;
    private SensorManager sensorManager;
    private String board_id;
    private BoardInfo boardInfo;
    private long lastTime;
    private long timeLeft;
    private boolean lastTimeUpdated = false;
    private boolean paused = false;

    private boolean vibrations;
    private boolean music;
    private boolean sound;

    private FPSCounter fpsCounter;

    public Updater getUpdater(){return updater;}

    public GameRenderer(Activity context, SensorManager sensorManager, String board_id, boolean vibrations, boolean music, boolean sound) {
        this.context = context;
        this.sensorManager = sensorManager;
        this.board_id = board_id;
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //TODO zmienić miejsce tworzenie updatera?
        Ball ball = new Ball(new Point(0f, 1f, 3f), 1f, new Vector(0f, 0f, 0f), Ball.BallTexture.beach, context);

        XMLParser xmlParser = new XMLParser(context);
        Board board = xmlParser.getBoard(board_id);
        boardInfo = xmlParser.getBoardInfo(board_id);

        timeLeft = boardInfo.getTime() * 1000;

        ((GameActivity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((GameActivity)context).updatePanel(boardInfo.getTime(), 0);
            }
        });

        updater = new Updater(context, ball, board, sensorManager, vibrations, music, sound, this);

        //String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        //boolean bul = extensions.contains("OES_depth_texture");

        fpsCounter = new FPSCounter();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        updater.surfaceChange(width, height);
    }


    @Override
    public void onDrawFrame(GL10 glUnused)
    {
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
            timeLeft -= actual_time - lastTime;
            lastTime = actual_time;
        }
        //long seconds_past = (actual_time - startTime) / 1000;
        final long seconds_left = timeLeft / 1000;
        if (paused)
        {
            lastTimeUpdated = false;
            boardInfo.setTime((int)seconds_left);
            return;
        }
        ((GameActivity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView)context.findViewById(R.id.game_activity_time)).setText(seconds_left + "");
            }
        });

        UpdateResult updateResult = updater.update();
        if (timeLeft <= 0)
            updateResult = UpdateResult.DEFEAT;
        if (updateResult != UpdateResult.NONE) {
            //dotarcie do mety?
            if (updateResult == UpdateResult.DEFEAT)
            {
                context.finish();
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
            updater.resume();
            paused = false;
        }
        else
        {
            paused = true;
            updater.pause();
        }
    }

    public void addTime(int value)
    {
        timeLeft += value * 1000;
    }

}
