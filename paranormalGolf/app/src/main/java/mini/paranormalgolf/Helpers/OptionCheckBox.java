package mini.paranormalgolf.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Kuba on 2015-01-09.
 */
public class OptionCheckBox extends CheckBox
{
    private final int duration = 500;
    private final int offset = 100;

    public OptionCheckBox(Context context)
    {
        super(context);
    }

    public OptionCheckBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OptionCheckBox(Context context, AttributeSet attrs, int defStyleAttr)
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
                    Color.WHITE, Color.GRAY,
                    Shader.TileMode.REPEAT
            ));
            Animation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, //fromXType
                    -1.0f,                       //fromXValue
                    Animation.RELATIVE_TO_PARENT, //toXType
                    0.0f,                      //toXValue
                    Animation.RELATIVE_TO_PARENT, //fromYType
                    0.0f,                       //fromYValue
                    Animation.RELATIVE_TO_PARENT, //toYType
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
    }
}
