package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;
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

    private Context context;
    private Ball ball;

    private int max_diamonds_count;
    private int last_diamonds_count;

    private Board board;
    private Vector accData=new Vector(0,0,0);

    DrawManager drawManager;
    List<Elevator> elevators;
    Finish finish;
    List<CheckPoint> checkPoints;
    List<HourGlass> hourGlasses;


    public Updater(Context context, Ball ball, Board board,SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        last_diamonds_count = max_diamonds_count = board.diamonds.size();
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        drawManager = new DrawManager(context);

        Elevator elevator1 = new Elevator(new Point(10f,0f,-10f), new Vector(0f,1f,0f), new BoxSize(5f,1f,5f), new Point(10f, -5f, -10f), new Point(10f, 5f, -10f), 0f, context);
        elevators = Arrays.asList(elevator1);
        finish = new Finish(new Point(0f,0f,0f), new ConicalFrustum(15f, 2f, 3f), false, context);
        CheckPoint checkPoint1 = new CheckPoint(new Point(-30f,0f,0f), new ConicalFrustum(15f, 2f, 3f), false, context);
        CheckPoint checkPoint2 = new CheckPoint(new Point(30f,0f,0f), new ConicalFrustum(15f, 2f, 3f), false, context);
        checkPoints = Arrays.asList(checkPoint1, checkPoint2);
        HourGlass hourGlass1 = new HourGlass(new Point(-15f, 1f, 0f), 5, context);
        HourGlass hourGlass2 = new HourGlass(new Point(15f, 1f, 0f), 5, context);
        hourGlasses = Arrays.asList(hourGlass1, hourGlass2);
    }

    public UpdateResult update() {
        float mu = getActualCoefficientFriction();
        ball.Update(0.035f, accData, mu);

        for(Beam beam : board.beams){
            beam.Update(0.035f);
        }
        for(Elevator elevator : elevators){
            elevator.Update(0.035f);
        }
        if (isUnderFloors())
            return UpdateResult.DEFEAT;

        for(Wall wall:board.walls)
            if(ball.CheckCollision(wall))
                ball.ReactOnCollision(wall);
        return UpdateResult.NONE;
    }

    private boolean isUnderFloors()
    {
        for (Floor floor : board.floors)
            if (floor.location.y - floor.measures.y / 2 <= ball.location.y + ball.getRadius())
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
        for(Elevator elevator : elevators){
            drawManager.drawElevator(elevator);
        }
        drawManager.drawBall(ball, 0f, ball.velocity.normalize());
        for(CheckPoint checkPoint : checkPoints){
            drawManager.drawCheckPoint(checkPoint);
        }
        drawManager.drawFinish(finish);
        for(HourGlass hourGlass : hourGlasses) {
            drawManager.drawHourglass(hourGlass);
        }
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
