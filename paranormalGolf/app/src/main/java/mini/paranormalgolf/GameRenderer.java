package mini.paranormalgolf;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mini.paranormalgolf.Helpers.UpdateResult;
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
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glEnable;


/**
 * Created by Mateusz on 2014-12-05.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private Updater updater;
    private SensorManager sensorManager;

    public GameRenderer(Context context, SensorManager sensorManager) {
        this.context = context;
        this.sensorManager = sensorManager;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
       // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        //TODO zmienić miejsce tworzenie updatera?
        Ball ball = new Ball(new Point(0f, 0.1f, 0f), 0.1f, new Vector(0f, 0f, 0f));

        Floor floor1 = new Floor(new BoxSize(1.5f, 0.2f, 1.5f),0.05f, new Point(0f, -0.1f, 0f));
        Floor floor2 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(0f, -0.1f, -2f));
        Floor floor3 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(0f, -0.1f, 2f));
        Floor floor4 = new Floor(new BoxSize(2.5f, 0.2f, 0.5f),0.05f, new Point(-2f, -0.1f, 0f));
        Floor floor5 = new Floor(new BoxSize(2.5f, 0.2f, 0.5f),0.05f, new Point(2f, -0.1f, 0f));
        Floor floor6 = new Floor(new BoxSize(1.5f, 0.2f, 0.5f),0.05f, new Point(-1f, -0.1f, 2f));
        Floor floor7 = new Floor(new BoxSize(1.5f, 0.2f, 0.5f),0.05f, new Point(1f, -0.1f, 2f));
        Floor floor8 = new Floor(new BoxSize(1.5f, 0.2f, 0.5f),0.05f, new Point(-1f, -0.1f, -2f));
        Floor floor9 = new Floor(new BoxSize(1.5f, 0.2f, 0.5f),0.05f, new Point(1f, -0.1f, -2f));
        Floor floor10 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(-2f, -0.1f, 1.5f));
        Floor floor11 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(2f, -0.1f, 1.5f));
        Floor floor12 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(-2f, -0.1f, -1.5f));
        Floor floor13 = new Floor(new BoxSize(0.5f, 0.2f, 2.5f),0.05f, new Point(2f, -0.1f, -1.5f));
        List<Floor> floors = new ArrayList<Floor>();
        floors.add(floor1);
        floors.add(floor2);
        floors.add(floor3);
        floors.add(floor4);
        floors.add(floor5);
        floors.add(floor6);
        floors.add(floor7);
        floors.add(floor8);
        floors.add(floor9);
        floors.add(floor10);
        floors.add(floor11);
        floors.add(floor12);
        floors.add(floor13);
        Board board = new Board(0, floors);

        updater = new Updater(context, ball, board,sensorManager);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        updater.surfaceChange(width, height);
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (updater.update() != UpdateResult.NONE) {
            //dotarcie do mety?
        }
        updater.draw();
    }

}
