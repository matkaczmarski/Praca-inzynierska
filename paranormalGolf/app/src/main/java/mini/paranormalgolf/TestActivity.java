package mini.paranormalgolf;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;

import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;


public class TestActivity extends Activity implements SensorEventListener {

    private Ball ball;

    private Vector accelerometrData=new Vector(0,0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SensorManager mSensorManager = (android.hardware.SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        setContentView(R.layout.activity_test);
        ball=new Ball(new Vector(0,0,0),new Point(0,0,2));
        CountDownTimer timer=new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                ball.Update(1, accelerometrData);
                Point p = ball.getPosition();
                setContentView(R.layout.activity_test);
                TextView tv1 = (TextView) findViewById(R.id.x);
                tv1.setText("X: " + p.X);
                tv1 = (TextView) findViewById(R.id.y);
                tv1.setText("Y: " + p.Y);
                tv1 = (TextView) findViewById(R.id.z);
                tv1.setText("Z: " + p.Z);
            }

            public void onFinish() {
            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accelerometrData = new Vector(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
