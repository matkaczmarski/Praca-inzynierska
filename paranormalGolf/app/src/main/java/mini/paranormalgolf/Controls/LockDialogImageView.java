package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by Kuba on 2015-01-11.
 */
public class LockDialogImageView extends ImageView
{
    private final int duration = 500;
    private final int offset = 100;

    public LockDialogImageView(Context context)
    {
        super(context);
    }

    public LockDialogImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LockDialogImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (getVisibility() == INVISIBLE)
            return;
        if (changed)
        {
            myAnimate();
        }
    }

    public void myAnimate()
    {
        int nr = 0;
        try
        {
            nr = Integer.parseInt(getTag().toString());
        }
        catch(Exception ex)
        {
            nr = 0;
        }
        if (nr == -1)
            return;

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, //fromXType
                -1.5f,                       //fromXValue
                Animation.RELATIVE_TO_PARENT, //toXType
                0.0f,                      //toXValue
                Animation.RELATIVE_TO_SELF, //fromYType
                0.0f,                       //fromYValue
                Animation.RELATIVE_TO_SELF, //toYType
                0.0f);                      //toYValue
        animation.setDuration(duration);
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