package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.translateM;

import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Helpers.UpdateResult;
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

    public Updater(Context context, Ball ball, Board board,SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        drawManager = new DrawManager(context);
    }

    public UpdateResult update() {
        float mu = getActualCoefficientFriction();
        ball.Update(0.035f, accData, mu);
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
        //drugi argument - o ile stopni obrót, 3 argument - oś obrotu
        drawManager.drawBall(ball, 0f, ball.velocity.normalize());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accData = new Vector(event.values[1], -event.values[2], event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
