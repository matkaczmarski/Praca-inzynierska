package mini.paranormalgolf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
import static android.opengl.GLES20.glActiveTexture;
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

    public GameRenderer(Activity context, SensorManager sensorManager, String board_id) {
        this.context = context;
        this.sensorManager = sensorManager;
        this.board_id = board_id;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
       // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        //TODO zmienić miejsce tworzenie updatera?
        Ball ball = new Ball(new Point(0f, 1f, 0f), 1f, new Vector(0f, 0f, 0f), Ball.BallTexture.poziomeKreski, context);

        XMLParser xmlParser = new XMLParser(context);
        Board board = xmlParser.getBoard(board_id);

        updater = new Updater(context, ball, board, sensorManager);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        updater.surfaceChange(width, height);
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        UpdateResult updateResult = updater.update();
        if (updateResult != UpdateResult.NONE) {
            //dotarcie do mety?
            if (updateResult == UpdateResult.DEFEAT)
            {
                context.finish();
            }
        }
        updater.draw();
    }

}
