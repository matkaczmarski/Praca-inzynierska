package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.translateM;

import mini.paranormalgolf.Activities.GameActivity;
import mini.paranormalgolf.GameRenderer;
import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Updater implements SensorEventListener {

    private Context context;
    private Ball ball;

    private int max_diamonds_count;
    private int last_diamonds_count;

    private Board board;
    private Vector accData=new Vector(0,0,0);

    DrawManager drawManager;

    public Updater(Context context, Ball ball, Board board,SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        last_diamonds_count = max_diamonds_count = board.diamonds.size();
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        drawManager = new DrawManager(context);
    }

    public UpdateResult update() {
        float mu = getActualCoefficientFriction();
        ball.Update(0.035f, accData, mu);

        for(Beam beam : board.beams){
            beam.Update(0.035f);
        }
        if (isUnderFloors())
            return UpdateResult.DEFEAT;

        return UpdateResult.NONE;
    }

    private boolean isUnderFloors()
    {
        for (Floor floor : board.floors)
            if (floor.location.Y - floor.measurements.y / 2 <= ball.location.Y + ball.getRadius())
                return false;
        return true;
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

        for(Wall wall : board.walls){
            drawManager.drawWall(wall);
        }
        for(Beam beam: board.beams){
            drawManager.drawBeam(beam);
        }

        drawManager.drawBall(ball, 0f, ball.velocity.normalize());

        for(Diamond diamond : board.diamonds) {
            drawManager.drawDiamond(diamond);
        }
        if (last_diamonds_count != board.diamonds.size())
        {
            last_diamonds_count = board.diamonds.size();
            ((GameActivity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ((GameActivity)context).updatePanelDiamonds(max_diamonds_count - last_diamonds_count);
                }
            });
            //((GameActivity)context).updatePanelDiamonds(max_diamonds_count - last_diamonds_count);
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
