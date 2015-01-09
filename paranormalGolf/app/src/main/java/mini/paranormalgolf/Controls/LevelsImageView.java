package mini.paranormalgolf.Controls;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by Kuba on 2015-01-09.
 */
public class LevelsImageView extends ImageView
{
    private final int duration = 500;
    private final int offset = 100;

    public LevelsImageView(Context context)
    {
        super(context);
    }

    public LevelsImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LevelsImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
        {
            myAnimate();
        }
    }

    public void myAnimate()
    {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, //fromXType
                1.5f,                       //fromXValue
                Animation.RELATIVE_TO_PARENT, //toXType
                0.0f,                      //toXValue
                Animation.RELATIVE_TO_SELF, //fromYType
                0.0f,                       //fromYValue
                Animation.RELATIVE_TO_SELF, //toYType
                0.0f);                      //toYValue
        animation.setDuration(duration);
        int nr = 0;
        try
        {
            nr = Integer.parseInt(getTag().toString());
        }
        catch(Exception ex)
        {
            nr = 0;
        }
        animation.setStartOffset(nr * offset);
        startAnimation(animation);
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
        myAnimate();
    }
}
