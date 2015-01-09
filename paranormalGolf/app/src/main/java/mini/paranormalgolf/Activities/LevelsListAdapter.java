package mini.paranormalgolf.Activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import mini.paranormalgolf.R;

/**
 * Created by Kuba on 2014-12-14.
 */
public class LevelsListAdapter extends BaseAdapter
{
    String[] board_id;
    int[] best_results;
    Context context;
    int selectedIndex = 0;
    boolean first = true;

    public LevelsListAdapter(Context context, String[] board_id, int[] best_results)
    {
        super();
        this.board_id = board_id;
        this.best_results = best_results;
        this.context = context;
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
        ((LevelsActivity)context).selectedBoardChanged(board_id[selectedIndex], best_results[selectedIndex], selectedIndex + 1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return board_id.length;
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
        TagInfo tagInfo = new TagInfo();
        tagInfo.setId(board_id[i]);
        tagInfo.setResult(best_results[i]);
        rowView.setTag(tagInfo);
        TextView textView = (TextView)rowView.findViewById(R.id.list_view_item_text);
        textView.setText(context.getString(R.string.level) + " #" + (i + 1));
        if (first)
            textView.setTag(i + "");
        else
            textView.setTag(-1);
        if (first && (i == getCount() - 1))
            first = false;
        if (i == selectedIndex)
            textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_item));
        else
        {
            textView.setBackgroundDrawable(null);
            textView.setBackgroundColor(Color.TRANSPARENT);
        }
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "batmanFont.ttf");
        textView.setTypeface(tf);

        return rowView;
    }

    public void addItem(String id, int best_result)
    {
        String[] new_board_id = new String[board_id.length + 1];
        int[] new_best_results = new int[best_results.length + 1];

        for (int i = 0; i < board_id.length; i++)
        {
            new_board_id[i] = board_id[i];
            new_best_results[i] = best_results[i];
        }

        new_board_id[board_id.length] = id;
        new_best_results[board_id.length] = best_result;

        board_id = new_board_id;
        best_results = new_best_results;

        notifyDataSetChanged();
    }
}
