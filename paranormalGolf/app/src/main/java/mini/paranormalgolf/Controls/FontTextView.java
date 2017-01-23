package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Klasa odpowiedzialna za wyświetlanie tesktów w grze.
 */
public class FontTextView extends TextView
{
    /**
     * Konstruktor.
     * @param context
     */
    public FontTextView(Context context)
    {
        super(context);
        init();
    }

    /**
     * Konstruktor.
     * @param context
     * @param attrs
     */
    public FontTextView(Context context, AttributeSet attrs)
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
    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Ustawia czcionkę.
     */
    private void init()
    {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"batmanFont.ttf" );//"SonsieOne-Regular.otf");
        setTypeface(tf);
    }
}
