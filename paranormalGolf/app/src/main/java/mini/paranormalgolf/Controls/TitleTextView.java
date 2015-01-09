package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Kuba on 2015-01-09.
 */
public class TitleTextView extends TextView
{

    public TitleTextView(Context context)
    {
        super(context);
    }

    public TitleTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
        {
            getPaint().setShader(new LinearGradient(
                    0, 0, 0, getHeight(),
                    Color.WHITE, Color.BLACK,
                    Shader.TileMode.CLAMP
            ));
        }
    }
}
