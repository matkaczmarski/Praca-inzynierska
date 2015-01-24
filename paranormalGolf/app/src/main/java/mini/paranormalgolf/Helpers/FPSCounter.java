package mini.paranormalgolf.Helpers;

import android.util.Log;

/**
 * Created by Mateusz on 2014-12-29.
 */
public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            Log.i("FPSCounter", "fps: " + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}
