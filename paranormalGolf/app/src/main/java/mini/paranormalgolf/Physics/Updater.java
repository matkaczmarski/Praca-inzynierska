package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.translateM;

import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Updater implements SensorEventListener {

    private Context context;
    private Ball ball;


    private Board board;
    private Vector accData=new Vector(0,0,0);

    DrawManager drawManager;

    private List<Diamond> diamonds;
    private List<Wall> walls;
    private List<Beam> beams;

    public Updater(Context context, Ball ball, Board board,SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        drawManager = new DrawManager(context);

        Diamond diamond1 = new Diamond(new Point(5f, 2f, 5f), 100, new Pyramid(1f, 2f, new Vector(0f, 1f, 0f), 6), context);
        Diamond diamond2 = new Diamond(new Point(-5f, 2f, 5f), 100, new Pyramid(1f, 2f, new Vector(0f, 1f, 0f), 6), context);
        Diamond diamond3 = new Diamond(new Point(5f, 2f, 0f), 100, new Pyramid(1f, 2f, new Vector(0f, 1f, 0f), 6), context);
        Diamond diamond4 = new Diamond(new Point(-5f, 2f, 0f), 100, new Pyramid(1f, 2f, new Vector(0f, 1f, 0f), 6), context);

        diamonds = Arrays.asList(diamond1, diamond2, diamond3, diamond4);

        Wall wall1 = new Wall(new Point(0f,2.5f,-5f), new BoxSize(10f, 5f, 2f), context);
        walls = Arrays.asList(wall1);

        Beam beam1 = new Beam(new Point(0f, 1.5f, -20f), new Vector(1f, 0f, 0f), new BoxSize(15f, 2f, 2f), new Point(-15f, 2.5f, -20f), new Point(15f, 2.5f, -20f), context);
        beams = Arrays.asList(beam1);
    }

    public UpdateResult update() {
        float mu = getActualCoefficientFriction();
        ball.Update(0.035f, accData, mu);
        for(Beam beam : beams){
            beam.Update(0.035f);
        }
        return UpdateResult.NONE;
    }

    private float getActualCoefficientFriction() {
        float mu = -1;
        for (Floor floor : board.floors) {
            if (floor.location.X - floor.measurements.x / 2 <= ball.location.X && floor.location.X + floor.measurements.x / 2 >= ball.location.X
                    && floor.location.Z - floor.measurements.z / 2 <= ball.location.Z && floor.location.Z + floor.measurements.z / 2 >= ball.location.Z
                    && Math.abs(ball.location.Y - ball.getRadius() - (floor.location.Y + floor.measurements.y / 2)) < 0.0001) {
                mu = floor.mu;
                break;
            }
        }
        return mu;
    }

    public void surfaceChange(int width, int height){
        drawManager.surfaceChange(width, height);
    }

    public void draw(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawManager.preDraw(ball.getLocation());
        drawManager.drawSkybox();
        //drawManager.preDraw(ball.getLocation());
        for(Floor floor : board.floors) {
            drawManager.drawFloor(floor);
        }

        for(Wall wall : walls){
            drawManager.drawWall(wall);
        }
        for(Beam beam: beams){
            drawManager.drawBeam(beam);
        }

        drawManager.drawBall(ball, 0f, ball.velocity.normalize());

        for(Diamond diamond : diamonds) {
            drawManager.drawDiamond(diamond);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accData = new Vector(event.values[1], -event.values[2], event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
