package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Klasa dziedzicząca po CheckBox'ie. Posiada animację.
 */
public class OptionCheckBox extends CheckBox
{
    /**
     * Czas trwania animacji w ms.
     */
    private final int duration = 500;

    /**
     * Opóźnienie animacji w ms.
     */
    private final int offset = 100;

    /**
     * Konstruktor.
     * @param context
     */
    public OptionCheckBox(Context context)
    {
        super(context);
        init();
    }

    /**
     * Konstruktor.
     * @param context
     * @param attrs
     */
    public OptionCheckBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Konstruktor.
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public OptionCheckBox(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Wywoływana gdy dany widok powinien rozmiar i pozycję widoków potomnych.
     * @param changed Czy nastąpiła zmiana rozmiaru.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && (getVisibility() == VISIBLE))
        {
            myAnimate();
        }
    }

    /**
     * Rozpoczęcie animacji.
     */
    private void myAnimate()
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

    /**
     * Ustawia czcionkę.
     */
    private void init()
    {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "SonsieOne-Regular.otf");//"batmanFont.ttf");
        setTypeface(tf);
    }
}
