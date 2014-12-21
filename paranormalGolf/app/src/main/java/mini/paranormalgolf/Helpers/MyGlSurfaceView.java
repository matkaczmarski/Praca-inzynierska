package mini.paranormalgolf.Helpers;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import mini.paranormalgolf.GameRenderer;

/**
 * Created by Kuba on 2014-12-20.
 */
public class MyGlSurfaceView extends GLSurfaceView
{
    public static boolean isES2Supported;
    public static GameRenderer gameRenderer;

    public MyGlSurfaceView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(gameRenderer);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(gameRenderer);
    }
}
