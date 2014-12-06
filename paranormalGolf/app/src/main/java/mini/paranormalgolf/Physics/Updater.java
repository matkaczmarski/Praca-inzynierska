package mini.paranormalgolf.Physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import mini.paranormalgolf.Graphics.MatrixHelper;
import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Updater implements SensorEventListener {

    private Context context;
    private Ball ball;

    private Board board;
    private Vector accData=new Vector(0,0,0);

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private ColorShaderProgram colorShaderProgram;

    public Updater(Context context, Ball ball, Board board,SensorManager sensorManager) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        colorShaderProgram = new ColorShaderProgram(context);
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public boolean update() {
        ball.Update(0.001f, accData);
        return false;
    }

    //TODO zmienić tu żeby sie ekran nie obracal
    public void surfaceChange(int width, int height){
        glViewport(0, 0, width, height);

        //ustawianie pozycji kamery, sceny itd
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    public void draw(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


        //rysowanie podlogi
        for(Floor floor : board.floors) {
            positionObjectInScene(floor.location); //ustawianie pozycji
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, floor.rgba[0], floor.rgba[1], floor.rgba[2], floor.rgba[3]);
            floor.bindData(colorShaderProgram);
            floor.draw();
        }

        //Rysowanie kulki
        colorShaderProgram.useProgram();
        positionObjectInScene(ball.location); //ustawianie pozycji
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, ball.rgba[0], ball.rgba[1], ball.rgba[2], ball.rgba[3]);
        ball.bindData(colorShaderProgram);
        ball.draw();
    }

    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accData = new Vector(-event.values[1], -event.values[2], -event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
