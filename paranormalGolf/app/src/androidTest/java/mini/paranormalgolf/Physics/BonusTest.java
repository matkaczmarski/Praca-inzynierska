package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.Point;

public class BonusTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    /**
     * Sprawdza działanie podnoszenia i opadanie bonus wzdłuż osi OY. Prędkość podnoszenia: 0.002 / wywołanie
     * @throws Exception
     */
    public void testGetLocation() throws Exception {
        HourGlass bonus = new HourGlass(new Point(0, 1, 1), 5, 0.5f);
        float yLocation = bonus.getLocation().y;
        float yLocation2 = bonus.getLocation().y;
        assertEquals(true, yLocation < yLocation2);
        bonus.setLocation(new Point(0, 1.5f, 0));
        yLocation = bonus.getLocation().y;
        yLocation2 = bonus.getLocation().y;
        assertEquals(true, yLocation < 1.5f);
        assertEquals(true, yLocation2 + Math.abs(bonus.upDownSpeed) == yLocation);
    }

    /**
     * Sprawdza działanie obrotu elementu. Prędkość obrotu: 1 stopień/wywołanie
     * @throws Exception
     */
    public void testRotate() throws Exception {

        Bonus bonus=new Diamond(new Point(0,1,1),5,0.5f);
        float angle1=bonus.rotate();
        float angle2=bonus.rotate();
        assertEquals(true,angle1<angle2);
        float angle3=bonus.rotate();
        assertEquals(true,angle2<angle3);
        assertEquals(true,angle2-angle1==angle3-angle2);
    }
}