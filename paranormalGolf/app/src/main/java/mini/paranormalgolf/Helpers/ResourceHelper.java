package mini.paranormalgolf.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;
import mini.paranormalgolf.LoggerConfig;
import mini.paranormalgolf.R;

/**
 * Wczytuje dane z zasobów aplikacji.
 */
public class ResourceHelper {

    /**
     * Id dźwięku zebrania diamentu.
     */
    public static final int SOUND_DIAMOND = 1;

    /**
     * Id dźwięku zebrania klepsydry.
     */
    public static final int SOUND_HOURGLASS = 2;

    /**
     * Id dźwięku wygranej.
     */
    public static final int SOUND_WIN = 3;

    /**
     * Id dźwięku przegranej.
     */
    public static final int SOUND_LOSE = 4;

    /**
     * Id dźwięku wciśnięcia przycisku.
     */
    public static final int SOUND_BUTTON = 5;

    /**
     * Id dźwięku odbicia od ściany.
     */
    public static final int SOUND_WALL = 6;

    /**
     * Id dźwięku odbicia od belki.
     */
    public static final int SOUND_BEAM = 7;

    /**
     * Obiekt, do którego ładowane są dźwięki.
     */
    private static SoundPool soundPool;

    /**
     * Obiekt, który paruje id dźwięku z id, które jest otrzymywane po załadowaniu danego dźwięku.
     */
    private static HashMap<Integer, Integer> soundPoolMap;

    /**
     * Wartość TAG wpisywana w razie błędów w logach.
     */
    private static final String TAG = "TextureHelper";

    /**
     * Context aplikacji.
     */
    private static Context context;


    /**
     * Inicjalizuje dźwięki.
     * @param context Context aplikacji.
     */
    public static void initSounds(Context context)
    {
        if (soundPool != null)
            soundPool.release();
        ResourceHelper.context = context;
        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(SOUND_DIAMOND, soundPool.load(context, R.raw.sound_diamond_new, 1));
        soundPoolMap.put(SOUND_HOURGLASS, soundPool.load(context, R.raw.sound_hourglass_new, 1));
        soundPoolMap.put(SOUND_WIN, soundPool.load(context, R.raw.sound_win_new, 1));
        soundPoolMap.put(SOUND_LOSE, soundPool.load(context, R.raw.sound_lost_new, 1));
        soundPoolMap.put(SOUND_BUTTON, soundPool.load(context, R.raw.sound_button, 1));
        soundPoolMap.put(SOUND_WALL, soundPool.load(context, R.raw.sound_wall, 1));
        soundPoolMap.put(SOUND_BEAM, soundPool.load(context, R.raw.sound_wall_2, 1));
    }

    /**
     * Odtwarza wybrany dźwięk.
     * @param sound Id dźwięku.
     */
    public static void playSound(int sound)
    {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float volume;
        switch (am.getRingerMode())
        {
            case AudioManager.RINGER_MODE_SILENT:
                volume = 0.0f;
                break;
            default:
                volume = 1.0f;
                break;
        }
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
    }


    /**
     * Wczytuje i zwraca dane tekstowe z pliku.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @param resourceId Identyfikator pliku.
     * @return Tekst pliku.
     */
    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString();
    }

    /**
     * Wczytuje z zasobów teksturę.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @param resourceId Identyfikator tekstury.
     * @return Identyfikator OpenGL tekstury.
     */
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }

            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);

            return 0;
        }

        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    /**
     * Wczytuje tekstury i tworzy sześcienną mapę tekstur.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @param cubeResources Tablica identyfikatorów tekstur.
     * @return Identyfikator OpenGL utworzonej szesciennej mapy tekstur.
     */
    public static int loadCubeMap(Context context, int[] cubeResources) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }

            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];

        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] =
                    BitmapFactory.decodeResource(context.getResources(),
                            cubeResources[i], options);

            if (cubeBitmaps[i] == null) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Resource ID " + cubeResources[i]
                            + " could not be decoded.");
                }

                glDeleteTextures(1, textureObjectIds, 0);

                return 0;
            }
        }

        // Linear filtering for minification and magnification
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        glTexParameteri(GL_TEXTURE_CUBE_MAP,
                GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);

        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }

        return textureObjectIds[0];
    }

    /**
     * Zwalnia dźwięki.
     */
    public static void releaseSounds()
    {
        if (soundPool != null)
            soundPool.release();
    }
}
