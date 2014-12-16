package mini.paranormalgolf.Helpers;

import android.content.Context;
import android.content.res.AssetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Physics.Board;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Kuba on 2014-12-15.
 */
public class XMLParser
{
    private Context context;
    public XMLParser(Context context)
    {
        this.context = context;
    }
    public Board getBoard(String board_id)
    {
        List<Floor> floors = new ArrayList<Floor>();
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            AssetManager manager = context.getResources().getAssets();
            InputStream input = manager.open(board_id + ".xml");
            xpp.setInput(input, null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = xpp.getName();
                if(eventType == XmlPullParser.START_TAG)
                {
                    if(name.equals("Floor"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float boxsize_x = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_x"));
                        float boxsize_y = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_y"));
                        float boxsize_z = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_z"));

                        float friction = Float.parseFloat(xpp.getAttributeValue(null, "friction"));

                        floors.add(new Floor(new BoxSize(boxsize_x, boxsize_y, boxsize_z), friction, new Point(location_x, location_y, location_z)));
                    }
                }
                eventType = xpp.next();
            }
        }
        catch (Exception ex)
        {
            return null;
        }

        return new Board(Integer.valueOf(board_id.split("_")[1]), floors);
    }
}
