package mini.paranormalgolf.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import mini.paranormalgolf.Helpers.BoardInfo;
import mini.paranormalgolf.R;

/**
 * Created by Kuba on 2014-12-14.
 */
public class LevelsListAdapter extends BaseAdapter
{
    BoardInfo[] boardInfos;
    boolean[] locked;
    Context context;
    int selectedIndex = 0;
    boolean first = true;
    ListView listView;
    int lastCount = -1;

    public LevelsListAdapter(Context context, BoardInfo[] boardInfos, ListView listView, boolean[] locked)
    {
        super();
        this.boardInfos = boardInfos;
        this.locked = locked;
        this.listView = listView;
        this.context = context;
    }

    public void setSelectedIndex(int selectedIndex)
    {
        if (locked[selectedIndex])
        {
            final Dialog dialog = new Dialog(context, R.style.LockDialogTheme);
            dialog.setContentView(R.layout.lock_dialog);
            ((TextView)dialog.findViewById(R.id.lock_dialog_ok)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.dismiss();
                }
            });
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "batmanFont.ttf");
            TextView tv = (TextView) dialog.findViewById(R.id.lock_dialog_ok);
            tv.setTypeface(tf);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            dialog.show();


            return;

        }
        this.selectedIndex = selectedIndex;
        ((LevelsActivity)context).selectedBoardChanged(boardInfos[selectedIndex], selectedIndex + 1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return boardInfos.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_view_item, viewGroup, false);
        if (locked[i])
        {
            rowView.findViewById(R.id.list_view_item_text).setVisibility(View.INVISIBLE);
            rowView.findViewById(R.id.list_view_item_image).setVisibility(View.VISIBLE);
        }
        else
        {
            rowView.findViewById(R.id.list_view_item_image).setVisibility(View.INVISIBLE);
            rowView.findViewById(R.id.list_view_item_text).setVisibility(View.VISIBLE);

            TextView textView = (TextView) rowView.findViewById(R.id.list_view_item_text);
            textView.setText(context.getString(R.string.level) + " #" + (i + 1));
            if (first)
                textView.setTag(i + "");
            else
                textView.setTag(-1);
            if (first && (i < lastCount))
                first = false;
            else
                lastCount = listView.getLastVisiblePosition();
            if (i == selectedIndex)
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_item));
            else
            {
                textView.setBackgroundDrawable(null);
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "batmanFont.ttf");
            textView.setTypeface(tf);
        }

        return rowView;
    }
}
