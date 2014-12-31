package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.translateM;

import mini.paranormalgolf.Activities.GameActivity;
import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Updater implements SensorEventListener {

    public final static float INTERVAL_TIME=0.035f;

    private Context context;
    private Ball ball;

    private int max_diamonds_count;
    private int last_diamonds_count;

    private Board board;
    private Vector accData=new Vector(0,0,0);

    private boolean paused = false;

    DrawManager drawManager;

    public DrawManager getDrawManager(){return drawManager;}

    public Updater(Context context, Ball ball, Board board, SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        last_diamonds_count = max_diamonds_count = board.diamonds.size();
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        drawManager = new DrawManager(context);
    }

    public UpdateResult update() {
        if (paused)
            return UpdateResult.PAUSE;
        float mu = getActualCoefficientFriction();
        ball.Update(INTERVAL_TIME, accData, mu);

        for (Beam beam : board.beams) {
            beam.Update(INTERVAL_TIME);
        }
        for (Elevator elevator : board.elevators) {
            elevator.Update(INTERVAL_TIME);
        }
        if (isUnderFloors())
            return UpdateResult.DEFEAT;

        for (Wall wall : board.walls)
            if (ball.CheckCollision(wall))
                ball.ReactOnCollision(wall);
        if (mu < 0)
            for (Floor floor : board.floors)
                if (ball.CheckCollision(floor))
                    ball.ReactOnCollision(floor);
        ;
        for (int i = 0; i < board.diamonds.size(); i++)
            if (ball.CheckCollision(board.diamonds.get(i))) {
                board.diamonds.set(i, null);
                // i dodaj jakieś punkty
            }

        for (int i = 0; i < board.hourGlasses.size(); i++)
            if (ball.CheckCollision(board.hourGlasses.get(i))) {
                board.hourGlasses.set(i, null);
                // i dodaj jakiś czas
            }
        return UpdateResult.NONE;
    }


    private boolean isUnderFloors()
    {
        float value = ball.location.y + ball.getRadius();
        for (Floor floor : board.floors)
            if (floor.location.y - floor.measures.y / 2 <= value)
                return false;
        for (Elevator elevator : board.elevators)
            if (elevator.location.y - elevator.measurements.y / 2 <= value)
                return false;
        return true;
    }

    private float getActualCoefficientFriction() {
        float mu = -1;
        for (Floor floor : board.floors) {
            if (floor.location.x - floor.measures.x / 2 <= ball.location.x && floor.location.x + floor.measures.x / 2 >= ball.location.x
                    && floor.location.z - floor.measures.z / 2 <= ball.location.z && floor.location.z + floor.measures.z / 2 >= ball.location.z
                    && Math.abs(ball.location.y - ball.getRadius() - (floor.location.y + floor.measures.y / 2)) < 0.0001) {
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
        for(Floor floor : board.floors) {
            drawManager.drawFloor(floor);
        }
        for(Wall wall : board.walls){
            drawManager.drawWall(wall);
        }
        for(Beam beam: board.beams){
            drawManager.drawBeam(beam);
        }
        for(Elevator elevator : board.elevators){
            drawManager.drawElevator(elevator);
        }
        drawManager.drawBall(ball, 0f, ball.velocity.normalize());
        for(CheckPoint checkPoint : board.checkpoints){
            drawManager.drawCheckPoint(checkPoint);
        }
        drawManager.drawFinish(board.finish);
        for(HourGlass hourGlass : board.hourGlasses) {
            if (hourGlass != null)
                drawManager.drawHourglass(hourGlass);
        }
        for(Diamond diamond : board.diamonds) {
            if (diamond != null)
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

    public void pause()
    {
        paused = true;
    }

    public void resume()
    {
        paused = false;
    }
}
