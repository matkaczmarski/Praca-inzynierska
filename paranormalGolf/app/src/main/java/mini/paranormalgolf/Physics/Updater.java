package mini.paranormalgolf.Physics;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.Surface;
import android.view.WindowManager;

import static android.opengl.Matrix.translateM;

import mini.paranormalgolf.Activities.GameActivity;
import mini.paranormalgolf.GameRenderer;
import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Odświeża pozycję wszystkich elementów.
 */
public class Updater implements SensorEventListener {

    /**
     * Czas (w sekundach), który upływa pomiędzy 2 wyświetlanymi klatkami.
     */
    public static float INTERVAL_TIME = 0.035f;

    /**
     * Współczynnik skalujący <b><em>INTERVAL_TIME</em></b>.
     */
    protected static float INTERVAL_FACTOR = 1.5f;

    /**
     * Kulka znajdująca się na planszy.
     */
    private Ball ball;

    /**
     * Aktualna zawartość planszy.
     */
    private Board board;

    /**
     * Wektor wartości przyspieszenia dla aktualnego położenia urządzenia mobilnego.
     */
    protected Vector accData=new Vector(0,0,0);


    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private Context context;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private DrawManager drawManager;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean landscape;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private int max_diamonds_count;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private int last_diamonds_count;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean paused = false;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean vibrations;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean sound;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean music;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private boolean shadows;

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private GameRenderer gameRenderer;

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @return
     */
    public DrawManager getDrawManager(){return drawManager;}

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @param context
     */
    public void setContext(Context context)
    {
        this.context = context;
        if (drawManager != null)
        {
            drawManager.initialize(context);
        }
        else
            drawManager = new DrawManager(context, shadows);
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @param context
     * @param ball Kulka znajdująca się na planszy.
     * @param board Aktualna zawartość planszy.
     * @param vibrations
     * @param music
     * @param sound
     * @param shadows
     * @param gameRenderer
     */
    public Updater(Context context, Ball ball, Board board, boolean vibrations, boolean music, boolean sound, boolean shadows, GameRenderer gameRenderer) {
        this.ball = ball;
        this.context = context;
        this.board = board;
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;
        this.shadows = shadows;
        this.gameRenderer = gameRenderer;
        last_diamonds_count = max_diamonds_count = board.diamonds.size();
        RegisterAccelerometer();
        landscape = getDeviceDefaultOrientation();
        if (context != null)
            drawManager = new DrawManager(context, shadows);
    }

    /**
     * Odświeża pozycję wszystkich elementów.
     * @param interval Czas (w sekundach), który upłynął pomiędzy 2 ostatnimi klatkami.
     * @return Aktualny stan rozgrywki.
     */
    public UpdateResult update(float interval) {
        INTERVAL_TIME = interval * INTERVAL_FACTOR;
        if (paused)
            return UpdateResult.PAUSE;

        float mu = getActualCoefficientFriction();
        int index = getIndexOfElevatorBallOn();

        for (Beam beam : board.beams) {
            beam.Update(INTERVAL_TIME);
        }

        for (Elevator elevator : board.elevators) {
            elevator.Update(INTERVAL_TIME);
        }
        if (index >= 0) setBallOnElevator(index);
        ball.Update(INTERVAL_TIME, accData, mu);


        if (isUnderFloors())
            return UpdateResult.DEFEAT;

        for (Elevator elevator : board.elevators)
            if (ball.CheckCollision(elevator))
                ball.ReactOnCollision(elevator);

        for (Beam beam : board.beams)
            if (ball.CheckCollision(beam)) {
                ball.ReactOnCollision(beam);
                onBeamCollision();
            }

        for (Wall wall : board.walls)
            if (ball.CheckCollision(wall)) {
                ball.ReactOnCollision(wall);
                onWallCollision();
            }

        for (Floor floor : board.floors)
            if (ball.CheckCollision(floor))
                ball.ReactOnCollision(floor);

        for (int i = 0; i < board.diamonds.size(); i++)
            if (ball.CheckCollision(board.diamonds.get(i))) {
                //board.diamonds.set(i, null);
                board.diamonds.remove(i--);
                onDiamondCollision();
                // i dodaj jakieś punkty
            }

        for (int i = 0; i < board.hourGlasses.size(); i++)
            if (ball.CheckCollision(board.hourGlasses.get(i))) {
                //board.hourGlasses.set(i, null);
                gameRenderer.addTime(board.hourGlasses.get(i).getValue());
                board.hourGlasses.remove(i--);
                onHourGlassCollision();
                // i dodaj jakiś czas
            }

        boolean areAllCheckpointVisited = true;
        for (int i = 0; i < board.checkpoints.size(); i++) {
            if (ball.CheckCollision(board.checkpoints.get(i))) {
                board.checkpoints.get(i).visit();
                continue;
            }
            if (!board.checkpoints.get(i).isVisited())
                areAllCheckpointVisited = false;
        }
        if (areAllCheckpointVisited) {
            board.finish.activate();
        }
        if (board.finish.isActive())
            if (ball.CheckCollision(board.finish))
                return UpdateResult.WIN;

        if (Collisions.getWasNotResolvedCollision()) return UpdateResult.DEFEAT;
        return UpdateResult.NONE;
    }

    /**
     * Sprawdza, czy kulka znajduje się poniżej podłóg i wind.
     * @return Informacja, czy kulka znajduje się poniżej podłóg i wind.
     */
    private boolean isUnderFloors() {
        float value = ball.location.y + ball.getRadius();
        for (Floor floor : board.floors)
            if (floor.location.y - floor.getMeasurements().y / 2 <= value)
                return false;
        for (Elevator elevator : board.elevators)
            if (elevator.location.y - elevator.getMeasurements().y / 2 <= value)
                return false;
        return true;
    }

    /**
     * Znajduje współczynnik tarcia dla powierzchni na której znajduje się kulka.
     * @return Wartość współczynnika tarcia dla powierzchni, na której kulka się znajduje
     * lub -1, jeśli kulka nie znajduje się na powierzchni.
     */
    private float getActualCoefficientFriction() {
        float mu = -1;
        for (Floor floor : board.floors) {
            BoxSize floorMeasurements =floor.getMeasurements();
            if (floor.location.x - floorMeasurements.x / 2 <= ball.location.x && floor.location.x + floorMeasurements.x / 2 >= ball.location.x
                    && floor.location.z - floorMeasurements.z / 2 <= ball.location.z && floor.location.z + floorMeasurements.z / 2 >= ball.location.z
                    && Math.abs(ball.location.y - ball.getRadius() - (floor.location.y + floorMeasurements.y / 2)) < Collisions.USER_EXPERIENCE) {
                mu = floor.getMu();
                break;
            }
        }
        if (mu < 0)
            for (Elevator elevator : board.elevators) {
                BoxSize elevatorMeasurements = elevator.getMeasurements();
                if (elevator.location.x - elevatorMeasurements.x / 2 <= ball.location.x && elevator.location.x + elevatorMeasurements.x / 2 >= ball.location.x
                        && elevator.location.z - elevatorMeasurements.z / 2 <= ball.location.z && elevator.location.z + elevatorMeasurements.z / 2 >= ball.location.z
                        && Math.abs(ball.location.y - ball.getRadius() - (elevator.location.y + elevatorMeasurements.y / 2)) < Collisions.USER_EXPERIENCE) {
                    mu = elevator.getMu();
                    break;
                }
            }
        return mu;
    }

    /**
     * Numer windy z listy wind, na której kulka się znajduje.
     * @return Indeks windy, na ktorej kulka się znajduje lub -1, jeśli kulka nie znajduje się na windzie.
     */
    private int getIndexOfElevatorBallOn() {
        int index = -1;
        for (int i = 0; i < board.elevators.size(); i++) {
            Elevator elevator = board.elevators.get(i);
            if (elevator.location.x - elevator.getMeasurements().x / 2 <= ball.location.x && elevator.location.x + elevator.getMeasurements().x / 2 >= ball.location.x
                    && elevator.location.z - elevator.getMeasurements().z / 2 <= ball.location.z && elevator.location.z + elevator.getMeasurements().z / 2 >= ball.location.z
                    && Math.abs(ball.location.y - ball.getRadius() - (elevator.location.y + elevator.getMeasurements().y / 2)) < Collisions.USER_EXPERIENCE) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Poprawia pozycję kulki, ustawiając ją na windzie
     * @param index Indeks windy, na której kulka się znajduje.
     */
    private void setBallOnElevator(int index) {
        Point lastLocation = ball.getLocation();
        ball.setLocation(new Point(lastLocation.x + board.elevators.get(index).getLastMove().x,
                lastLocation.y + board.elevators.get(index).getLastMove().y,
                lastLocation.z + board.elevators.get(index).getLastMove().z));
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    public void draw() {
        //if (paused)
        //    return;
        drawManager.drawBoard(board, ball);

        if (last_diamonds_count != board.diamonds.size()) {
            last_diamonds_count = board.diamonds.size();
            ((GameActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((GameActivity) context).updatePanelDiamonds(max_diamonds_count - last_diamonds_count);
                }
            });
            //((GameActivity)context).updatePanelDiamonds(max_diamonds_count - last_diamonds_count);
        }
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @param width
     * @param height
     */
    public void surfaceChange(int width, int height){
        drawManager.surfaceChange(width, height);
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @param board
     * @param ball
     */
    public void changeBoardAndBall(Board board, Ball ball)
    {
        this.ball = ball;
        this.board = board;
    }

    /**
     * UZUPEŁNIJ KUBA TUTAJ
     */
    private void onWallCollision()
    {
        playSound(ResourceHelper.SOUND_WALL);
        vibrate();
    }

    /**
     * UZUPEŁNIJ KUBA TUTAJ
     */
    private void onBeamCollision()
    {
        playSound(ResourceHelper.SOUND_BEAM);
        vibrate();
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    public void onHourGlassCollision()
    {
        playSound(ResourceHelper.SOUND_HOURGLASS);
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    public void onDiamondCollision()
    {
        playSound(ResourceHelper.SOUND_DIAMOND);
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @return
     */
    public int getCollectedDiamondsCount()
    {
        return max_diamonds_count - last_diamonds_count;
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     * @param vibrations
     * @param music
     * @param sound
     */
    public void updatePreferences (boolean vibrations, boolean music, boolean sound)
    {
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;
    }

    /**
     * Sprawdza, czy urządzenie mobilne ma domyślnie oientację typu landscape
     * @return Informacja, czy urządzenie mobilne ma domyślnie oientację typu landscape
     */
    private boolean getDeviceDefaultOrientation() {
        if (context != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            Configuration config = context.getResources().getConfiguration();

            int rotation = windowManager.getDefaultDisplay().getRotation();

            return ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                    config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                    config.orientation == Configuration.ORIENTATION_PORTRAIT);
        }
        return false;
    }

    /**
     * Umożliwia pobieranie danych z akcelerometru.
     */
    private void RegisterAccelerometer() {
        if(context!=null) {
            SensorManager sensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Wymagana do implementacji przez interfejs <em>SensorEventListener</em>. Zachowuje dane
     * przekazywane przez akcelerometr.
     * @param event Dane związane z sensorem.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (drawManager != null) {
            float angle = landscape ? ((float)(2* Math.PI*(drawManager.getxRotation() - 270)))/360 : ((float)(2* Math.PI*(drawManager.getxRotation() - 180)))/360;

            accData = new Vector((float) (event.values[1] * Math.cos(angle) + event.values[0] * Math.sin(angle)), -event.values[2], (float) (event.values[0] * Math.cos(angle) - event.values[1] * Math.sin(angle)));
        }
    }

    /**
     * Wymagana do implementacji przez interfejs <em>SensorEventListener</em>.
     * @param sensor Sensor.
     * @param accuracy Dokładność.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private void playSound(int sound)
    {
        if (this.sound)
        {
            ResourceHelper.playSound(sound);
        }
    }

    /**
     * UZUPEŁNIJCIE TUTAJ
     */
    private void vibrate()
    {
        if (vibrations)
        {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(context.getResources().getInteger(R.integer.vibrations_click_time));
        }
    }
}
