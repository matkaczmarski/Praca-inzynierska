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
import mini.paranormalgolf.Primitives.CuboidMeasurement;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
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

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        //TODO zmienić miejsce tworzenie updatera?
        Ball ball = new Ball(new Point(0f, 0.1f, 0f), 0.1f, new Vector(0f, 0f, 0f));

        Floor floor = new Floor(new CuboidMeasurement(0.7f, 0.2f, 1.7f),0.05f, new Point(1f, -0.1f, 0f));
        Floor floor2 = new Floor(new CuboidMeasurement(5.0f, 0.2f, 0.7f),0.05f, new Point(0f, -0.1f, 0f));
        Floor floor3 = new Floor(new CuboidMeasurement(0.7f, 0.2f, 1.7f),0.05f, new Point(-1.0f, -0.1f, 0f));
        List<Floor> floors = new ArrayList<Floor>();
        floors.add(floor);
        floors.add(floor2);
        floors.add(floor3);
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
