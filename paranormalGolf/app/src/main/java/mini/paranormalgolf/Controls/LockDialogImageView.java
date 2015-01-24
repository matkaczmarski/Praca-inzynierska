package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Klasa odpowiedzialna za wyświetlanie obrazków. Posiada animację.
 */
public class LockDialogImageView extends ImageView
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
    public LockDialogImageView(Context context)
    {
        super(context);
    }

    /**
     * Konstruktor.
     * @param context
     * @param attrs
     */
    public LockDialogImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Konstruktor.
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public LockDialogImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
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
        if (getVisibility() == INVISIBLE)
            return;
        if (changed)
        {
            myAnimate();
        }
    }

    /**
     * Rozpoczyna animację.
     */
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

    /**
     * Wywoływana gdy nastąpi ustawienie obrazka.
     * @param drawable Ustawiony obrazek.
     */
    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
        myAnimate();
    }
}
