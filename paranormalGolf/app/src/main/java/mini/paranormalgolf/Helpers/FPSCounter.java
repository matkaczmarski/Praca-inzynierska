package mini.paranormalgolf.Helpers;

import android.util.Log;

/**
 * Oblicza wartość FPS - frames per second.
 */
public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    /**
     * Wyświetla w logach aktualną wartość FPS.
     */
    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            Log.i("FPSCounter", "fps: " + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}
