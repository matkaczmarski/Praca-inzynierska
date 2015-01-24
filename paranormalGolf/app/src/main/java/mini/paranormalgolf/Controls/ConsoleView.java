package mini.paranormalgolf.Controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import mini.paranormalgolf.Activities.MainMenuActivity;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.R;

/**
 * Klasa odpowiadająca konsoli.
 */
public class ConsoleView extends LinearLayout
{
    /**
     * Czas trwania animacji.
     */
    private final int duration = 500;

    /**
     * Konstruktor
     * @param context
     */
    public ConsoleView(Context context)
    {
        super(context);
        inflate();
    }

    /**
     * Konstruktor
     * @param context
     * @param attrs
     */
    public ConsoleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        inflate();
    }

    /**
     * Wypełnia widok konsoli.
     */
    public void inflate()
    {
        inflate(getContext(), R.layout.console, this);
        ((Button)findViewById(R.id.console_button)).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onOkClick(view);
            }
        });
        final EditText editText = (EditText)findViewById(R.id.console_command);
        editText.setText(getContext().getString(R.string.command_prompt));
        editText.setSelection(getContext().getString(R.string.command_prompt).length());
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                String command_prompt = getContext().getString(R.string.command_prompt);
                if (editable.length() < command_prompt.length())
                    editText.setText(command_prompt);
                editText.setSelection(editText.getText().toString().length());
            }
        });
    }

    /**
     * Wywyoływana w przypadku wciśnięcia przycisku Ok.
     * @param view Kontrolka, która została kliknięta.
     */
    public void onOkClick(View view)
    {
        EditText editText = (EditText)findViewById(R.id.console_command);
        String command = editText.getText().toString();
        TextView textView = (TextView)findViewById(R.id.console_history);
        textView.setText(textView.getText().toString() + "\n"  + command + "\n" + processCommand(command));
        editText.setText(getContext().getString(R.string.command_prompt));
        editText.setSelection(getContext().getString(R.string.command_prompt).length());
    }

    /**
     * Przetwarza wpisaną przez użytkownika komendę.
     * @param command Komenda wpisana przez użytkownika.
     * @return Odpowiedź konsoli.
     */
    private String processCommand(String command)
    {
        String[] args = (command.substring(getContext().getString(R.string.command_prompt).length())).split(" ");
        if (args[0].equalsIgnoreCase("unlock"))
        {
            if (args.length == 1)
                return getContext().getString(R.string.command_error);
            return unlockLevelCommand(args[1]) ? getContext().getString(R.string.command_ok) : getContext().getString(R.string.command_error);
        }
        else if (args[0].equalsIgnoreCase("ball_radius"))
        {
            if (args.length == 1)
                return getContext().getString(R.string.command_error);
            return changeBallRadiusCommand(args[1]) ? getContext().getString(R.string.command_ok) : getContext().getString(R.string.command_error);
        }
        else
            return getContext().getString(R.string.command_error);
    }

    /**
     * Przetwarza komendę odblokowania poziomów.
     * @param level Parametr komendy.
     * @return Informacja o tym czy udało się przetworzyć komendę dla danego parametru.
     */
    private boolean unlockLevelCommand(String level)
    {
        int nr;
        String[] board_ids = getContext().getResources().getStringArray(R.array.boards_id);
        if (level.equalsIgnoreCase("all"))
            nr = board_ids.length - 1;
        else
        {
            try
            {
                nr = Integer.parseInt(level) - 1;
            }
            catch (Exception ex)
            {
                return false;
            }
        }
        if (nr > board_ids.length - 1)
            nr = board_ids.length - 1;

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < nr; i++)
        {
            String board_id = board_ids[i];
            if (sharedPreferences.getInt(board_id, 0) == 0)
                editor.putInt(board_id, 1);
        }
        editor.commit();
        return true;
    }

    /**
     * Przetwarza komendę zmiany promienia kulki.
     * @param radius_string Parametr komendy.
     * @return Informacja o tym czy udało się przetworzyć komendę dla danego parametru.
     */
    private boolean changeBallRadiusCommand (String radius_string)
    {
        float radius = 0;
        if (radius_string.equalsIgnoreCase("default"))
            radius = Ball.DEFAULT_RADIUS;
        else
        {
            try
            {
                radius = Float.parseFloat(radius_string);
                if (radius <= 0)
                    radius = Ball.DEFAULT_RADIUS;
            } catch (Exception ex)
            {
                return false;
            }
        }
        ((MainMenuActivity)getContext()).radius_set = true;
        ((MainMenuActivity)getContext()).radius = radius;
        return true;
    }

    /**
     * Wysuwa konsolę.
     */
    public void show()
    {
        myAnimate(true);
    }

    /**
     * Ukrywa konsolę.
     */
    public void hide()
    {
        myAnimate(false);
    }

    /**
     * Animuje konsolę.
     * @param slideDown Czy konsola ma się pokazać.
     */
    private void myAnimate(final boolean slideDown)
    {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, //fromXType
                0.0f,                       //fromXValue
                Animation.RELATIVE_TO_SELF, //toXType
                0.0f,                      //toXValue
                Animation.RELATIVE_TO_SELF, //fromYType
                slideDown ? -1.5f : 0.0f,                       //fromYValue
                Animation.RELATIVE_TO_SELF, //toYType
                slideDown ? 0.0f : -1.5f);                      //toYValue
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                if (slideDown)
                    setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (!slideDown)
                    setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        startAnimation(animation);
    }
}
