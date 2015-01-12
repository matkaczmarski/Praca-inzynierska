package mini.paranormalgolf.Controls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Kuba on 2015-01-06.
 */
public class FontTextView extends TextView
{
    public FontTextView(Context context)
    {
        super(context);
        init();
    }

    public FontTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "batmanFont.ttf");
        setTypeface(tf);
    }
}