package mini.paranormalgolf;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.widget.TextView;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mini.paranormalgolf.Activities.GameActivity;
import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.Helpers.FPSCounter;
import mini.paranormalgolf.Helpers.UpdateResult;
import mini.paranormalgolf.Helpers.XMLParser;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Board;
import mini.paranormalgolf.Physics.Updater;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_CW;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DITHER;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glHint;


/**
 * Renderer.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    /**
     * Context aplikacji.
     */
    private final Context context;

    /**
     * Aktywność gry.
     */
    private static Activity activity;

    /**
     * Updater, współpracujący z danym rendererem.
     */
    private Updater updater;

    /**
     * Obiekt BoardInfo rozgrywanego poziomu.
     */
    private BoardInfo boardInfo;

    /**
     * Ostatnio odczytany czas.
     */
    private long lastTime;

    /**
     * Pozostały czas na rozegranie poziomu.
     */
    private long timeLeft;

    /**
     * Czy wartość lastTime została ustawiona.
     */
    private boolean lastTimeUpdated = false;

    /**
     * Czy gra jest zapauzowana.
     */
    private boolean paused = false;

    /**
     * Informacja o tym czy wibracje są dopuszczone przez użytkownika.
     */
    private boolean vibrations;

    /**
     * Informacja o tym czy muzyka w grze jest dopuszczona przez użytkownika.
     */
    private boolean music;

    /**
     * Informacja o tym czy dźwięki w grze są dopuszczone przez użytkownika.
     */
    private boolean sound;

    /**
     * Informacja o tym czy cienie w grze są dopuszczone przez użytkownika.
     */
    private boolean shadows;

    /**
     * Informacja o tym, czy użytkownik zmienił domyślny promień kulki (poprzez konsolę).
     */
    private boolean radius_set;

    /**
     * Promień kulki zdefiniowany przez użytkownika.
     */
    private float radius;

    /**
     * Nr tekstury wybranej przez użytkownika.
     */
    private int texture;

    /**
     * Czy wywołano onCreate.
     */
    private boolean onCreate = false;

    /**
     * Obiekt liczący klatki animacji.
     */
    private FPSCounter fpsCounter;

    /**
     * Pobiera Updater powiązany z rendererem.
     * @return Updater.
     */
    public Updater getUpdater(){return updater;}

    /**
     * Konstruktor
     * @param activity Aktywność gry.
     * @param context Context aplikacji.
     * @param board_id Id poziomu.
     * @param vibrations Informacja o tym czy wibracje są dopuszczone przez użytkownika.
     * @param music Informacja o tym czy muzyka jest dopuszczona przez użytkownika.
     * @param sound Informacja o tym czy dźwięki są dopuszczone przez użytkownika.
     * @param shadows Informacja o tym czy cienie są dopuszczone przez użytkownika.
     * @param texture Nr wybranej przez użytkownika tekstury.
     * @param radius_set Informacja o tym, czy użytkownik zmienił domyślny promień kulki (poprzez konsolę).
     * @param radius Promień kulki zdefiniowany przez użytkownika.
     */
    public GameRenderer(Activity activity, Context context, String board_id, boolean vibrations, boolean music, boolean sound, boolean shadows, int texture, boolean radius_set, float radius){
        this.context = context;
        this.activity = activity;
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;
        this.shadows = shadows;
        this.texture = texture;
        this.radius_set = radius_set;
        this.radius = radius;

        onCreate = true;

        boardInfo = loadBoardInfo(board_id);
        timeLeft = boardInfo.getTime() * 1000;

        final Context contextForUiThread = activity;

        ((GameActivity)activity).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((GameActivity)contextForUiThread).updatePanel(boardInfo.getTime(), 0);
            }
        });
        fpsCounter = new FPSCounter();
    }

    /**
     * Wczytuje poziom.
     * @param board_id Id poziomu.
     * @return Obiekt Board poziomu.
     */
    public Board loadBoard(String board_id)
    {
        XMLParser xmlParser = new XMLParser(context);
        return xmlParser.getBoard(board_id);
    }

    /**
     * Wczytuje obiekt BoardInfo danego poziomu.
     * @param board_id Id poziomu.
     * @return Obiekt BoardInfo poziomu.
     */
    public BoardInfo loadBoardInfo (String board_id)
    {
        XMLParser xmlParser = new XMLParser(context);
        return xmlParser.getBoardInfo(board_id);
    }

    /**
     *
     * @param glUnused
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glDepthFunc(GL_LEQUAL);
        glFrontFace(GL_CCW);
        glDisable(GL_DITHER);

        if (onCreate)
        {
            onCreate = false;
            Board board = loadBoard(boardInfo.getBoard_id());
            float ball_radius = radius_set ? radius : Ball.DEFAULT_RADIUS;
            Ball ball = new Ball(new Point(board.ballLocation.x, board.ballLocation.y + ball_radius, board.ballLocation.z), ball_radius, new Vector(0f, 0f, 0f), Ball.BallTexture.values()[texture]);

            updater = new Updater(context, ball, board, vibrations, sound, shadows, this);
        }
        updater.setContext(activity);
    }

    /**
     *
     * @param glUnused
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        if (updater != null)
            updater.surfaceChange(width, height);
    }

    /**
     * Wywoływana gdy należy narysować kolejną klatkę animacji.
     * @param glUnused
     */
    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        float interval = 0;
        if(LoggerConfig.ON) {
            fpsCounter.logFrame();
        }
        if (!lastTimeUpdated)
        {
            lastTime = System.currentTimeMillis();
            lastTimeUpdated = true;
        }
        if (!paused)
        {
            long actual_time = System.currentTimeMillis();
            interval = actual_time - lastTime;
            timeLeft -= actual_time - lastTime;
            lastTime = actual_time;
        }
        //long seconds_past = (actual_time - startTime) / 1000;
        final long seconds_left = (long)Math.ceil(timeLeft / 1000);
        if (paused)
        {
            lastTimeUpdated = false;
            boardInfo.setTime((int)seconds_left);
            //interval = 0;
            return;
        }
        ((GameActivity)activity).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView)(activity.findViewById(R.id.game_activity_time))).setText(seconds_left + "");
            }
        });

        UpdateResult updateResult = updater.update((float)(interval / 1000f));
        if (timeLeft <= 0)
        {
            updateResult = UpdateResult.DEFEAT;
        }
        if (updateResult != UpdateResult.NONE) {
            //dotarcie do mety?
            if (updateResult == UpdateResult.DEFEAT || updateResult == UpdateResult.WIN)
            {
                final boolean win = updateResult == UpdateResult.WIN;
                ((GameActivity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GameActivity) activity).onWinDialog(updater.getCollectedDiamondsCount(), (int) Math.ceil(timeLeft / 1000), win);
                    }
                });
            }
            if (updateResult == UpdateResult.PAUSE)
                return;
        }
        updater.draw();
    }

    /**
     * Zatrzymuje działanie renderera.
     */
    public void pause()
    {
        if (paused)
        {
            lastTimeUpdated = false;
            paused = false;
        }
        else
        {
            paused = true;
        }
    }

    /**
     * Zwiększa czas pozostały na ukończenie poziomu.
     * @param value Wartość w sekundach o jaką należy zwiększyć czas.
     */
    public void addTime(int value)
    {
        timeLeft += value * 1000;
    }

    /**
     * Aktualizuje opcje wybrane przez użytkownika.
     * @param vibrations Informacja o tym czy wibracje są dopuszczone przez użytkownika.
     * @param music Informacja o tym czy muzyka jest dopuszczona przez użytkownika.
     * @param sound Informacja o tym czy dźwięki są dopuszczone przez użytkownika.
     */
    public void updatePreferences (boolean vibrations, boolean music, boolean sound)
    {
        this.vibrations = vibrations;
        this.music = music;
        this.sound = sound;

        if(updater != null)
            updater.updatePreferences(vibrations, music, sound);
    }

}
