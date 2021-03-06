package mini.paranormalgolf.Helpers;

import android.content.Context;
import android.content.res.AssetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Physics.Beam;
import mini.paranormalgolf.Physics.Board;
import mini.paranormalgolf.Physics.CheckPoint;
import mini.paranormalgolf.Physics.Diamond;
import mini.paranormalgolf.Physics.Elevator;
import mini.paranormalgolf.Physics.Finish;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.HourGlass;
import mini.paranormalgolf.Physics.Wall;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Klasa odpowiedzialna za czytanie danych z plików XML.
 */
public class XMLParser
{
    /**
     * Context aplikacji.
     */
    private Context context;

    /**
     * Konstruktor.
     * @param context Context aplikacji.
     */
    public XMLParser(Context context)
    {
        this.context = context;
    }

    /**
     * Pobiera obiekt planszy na podstawie id poziomu.
     * @param board_id Id poziomu.
     * @return Obiekt planszy utworzony z danych z pliku XML.
     */
    public Board getBoard(String board_id)
    {
        List<Floor> floors = new ArrayList<Floor>();
        List<Wall> walls = new ArrayList<Wall>();
        List<Diamond> diamonds = new ArrayList<Diamond>();
        List<Beam> beams = new ArrayList<Beam>();
        List<Elevator> elevators = new ArrayList<Elevator>();
        List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
        List<HourGlass> hourGlasses = new ArrayList<HourGlass>();
        Finish finish = null;
        Point ballLocation = null;

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
                    else if (name.equals("Wall"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float boxsize_x = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_x"));
                        float boxsize_y = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_y"));
                        float boxsize_z = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_z"));

                        walls.add(new Wall(new Point(location_x, location_y, location_z), new BoxSize(boxsize_x, boxsize_y, boxsize_z)));
                    }
                    else if (name.equals("Diamond"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        int value = Integer.parseInt(xpp.getAttributeValue(null, "value"));
                        float yShift = Float.parseFloat(xpp.getAttributeValue(null, "y_shift"));

                        diamonds.add(new Diamond(new Point(location_x, location_y, location_z), value, yShift));
                    }
                    else if (name.equals("Beam"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float vector_x = Float.parseFloat(xpp.getAttributeValue(null, "vector_x"));
                        float vector_y = Float.parseFloat(xpp.getAttributeValue(null, "vector_y"));
                        float vector_z = Float.parseFloat(xpp.getAttributeValue(null, "vector_z"));

                        float boxsize_x = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_x"));
                        float boxsize_y = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_y"));
                        float boxsize_z = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_z"));

                        float from_x = Float.parseFloat(xpp.getAttributeValue(null, "from_x"));
                        float from_y = Float.parseFloat(xpp.getAttributeValue(null, "from_y"));
                        float from_z = Float.parseFloat(xpp.getAttributeValue(null, "from_z"));

                        float to_x = Float.parseFloat(xpp.getAttributeValue(null, "to_x"));
                        float to_y = Float.parseFloat(xpp.getAttributeValue(null, "to_y"));
                        float to_z = Float.parseFloat(xpp.getAttributeValue(null, "to_z"));

                        beams.add(new Beam(new Point(location_x, location_y, location_z), new Vector(vector_x, vector_y, vector_z), new BoxSize(boxsize_x, boxsize_y, boxsize_z), new Point(from_x, from_y, from_z), new Point(to_x, to_y, to_z)));
                    }
                    else if (name.equals("Elevator"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float vector_x = Float.parseFloat(xpp.getAttributeValue(null, "vector_x"));
                        float vector_y = Float.parseFloat(xpp.getAttributeValue(null, "vector_y"));
                        float vector_z = Float.parseFloat(xpp.getAttributeValue(null, "vector_z"));

                        float boxsize_x = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_x"));
                        float boxsize_y = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_y"));
                        float boxsize_z = Float.parseFloat(xpp.getAttributeValue(null, "boxsize_z"));

                        float from_x = Float.parseFloat(xpp.getAttributeValue(null, "from_x"));
                        float from_y = Float.parseFloat(xpp.getAttributeValue(null, "from_y"));
                        float from_z = Float.parseFloat(xpp.getAttributeValue(null, "from_z"));

                        float to_x = Float.parseFloat(xpp.getAttributeValue(null, "to_x"));
                        float to_y = Float.parseFloat(xpp.getAttributeValue(null, "to_y"));
                        float to_z = Float.parseFloat(xpp.getAttributeValue(null, "to_z"));

                        float friction = Float.parseFloat(xpp.getAttributeValue(null, "friction"));

                        elevators.add(new Elevator(new Point(location_x, location_y, location_z), new Vector(vector_x, vector_y, vector_z), new BoxSize(boxsize_x, boxsize_y, boxsize_z), new Point(from_x, from_y, from_z), new Point(to_x, to_y, to_z), friction));
                    }
                    else if (name.equals("Finish"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float frustum_x = Float.parseFloat(xpp.getAttributeValue(null, "frustum_x"));
                        float frustum_y = Float.parseFloat(xpp.getAttributeValue(null, "frustum_y"));
                        float frustum_z = Float.parseFloat(xpp.getAttributeValue(null, "frustum_z"));

                        finish = new Finish(new Point(location_x, location_y, location_z), new ConicalFrustum(frustum_x, frustum_y, frustum_z));
                    }
                    else if (name.equals("Checkpoint"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        float frustum_x = Float.parseFloat(xpp.getAttributeValue(null, "frustum_x"));
                        float frustum_y = Float.parseFloat(xpp.getAttributeValue(null, "frustum_y"));
                        float frustum_z = Float.parseFloat(xpp.getAttributeValue(null, "frustum_z"));

                        checkPoints.add(new CheckPoint(new Point(location_x, location_y, location_z), new ConicalFrustum(frustum_x, frustum_y, frustum_z)));
                    }
                    else if (name.equals("HourGlass"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        int value = Integer.parseInt(xpp.getAttributeValue(null, "value"));
                        float yShift = Float.parseFloat(xpp.getAttributeValue(null, "y_shift"));

                        hourGlasses.add(new HourGlass(new Point(location_x, location_y, location_z), value, yShift));
                    }
                    else if (name.equals("Ball"))
                    {
                        float location_x = Float.parseFloat(xpp.getAttributeValue(null, "location_x"));
                        float location_y = Float.parseFloat(xpp.getAttributeValue(null, "location_y"));
                        float location_z = Float.parseFloat(xpp.getAttributeValue(null, "location_z"));

                        ballLocation = new Point(location_x, location_y, location_z);
                    }
                }
                eventType = xpp.next();
            }
        }
        catch (Exception ex)
        {
            return null;
        }

        Board board = new Board(Integer.valueOf(board_id.split("_")[1]), floors, walls, diamonds, beams, elevators, checkPoints, hourGlasses, finish, ballLocation);
        return board;
    }

    /**
     * Pobiera obiekt BoardInfo poziomu na podstawie id poziomu.
     * @param board_id Id poziomu.
     * @return Obiekt BoardInfo utworzony z danych z pliku XML.
     */
    public BoardInfo getBoardInfo(String board_id)
    {
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            AssetManager manager = context.getResources().getAssets();
            InputStream input = manager.open(context.getString(R.string.boardInfoFile));
            xpp.setInput(input, null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = xpp.getName();
                if (eventType == XmlPullParser.START_TAG)
                {
                    if(name.equals("Board"))
                    {
                        if (board_id.equalsIgnoreCase(String.valueOf(xpp.getAttributeValue(null, "id"))))
                        {
                            int two_stars = Integer.parseInt(xpp.getAttributeValue(null, "two_stars"));
                            int three_stars = Integer.parseInt(xpp.getAttributeValue(null, "three_stars"));
                            int time = Integer.parseInt(xpp.getAttributeValue(null, "time"));

                            return new BoardInfo(Integer.valueOf(board_id.split("_")[1]), two_stars, three_stars, time);
                        }
                    }
                }
                eventType = xpp.next();
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return null;
    }
}
