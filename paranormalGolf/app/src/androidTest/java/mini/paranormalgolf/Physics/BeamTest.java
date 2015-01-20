package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

public class BeamTest extends TestCase {

    private Beam beam;

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    /**
     * Sprawdza poruszanie się belki pomiędzy punktami krańcowymi.
     * @throws Exception Wyjątek.
     */
    public void testUpdate() throws Exception {

        //poruszanie się belki pomiędzy 2 punktami równolegle do osi OX
        beam = new Beam(new Point(0, 0, 5), new Vector(1, 0, 0), new BoxSize(3, 4, 2), new Point(-3, 0, 5), new Point(4, 0, 5));
        beam.Update(1);
        Point position = beam.getLocation();
        assertEquals(0f, position.y);
        assertEquals(1f, position.x);

        beam.setLocation(new Point(1, 0, 5));
        beam.setVelocity(new Vector(-1, 0, 0));
        beam.Update(0.5f);
        position = beam.getLocation();
        assertEquals(0f, position.y);
        assertEquals(0.5f, position.x);

        //poruszanie się belki pomiędzy 2 punktami równolegle do osi OZ
        beam = new Beam(new Point(0, 0, 5), new Vector(0, 0, -1), new BoxSize(3, 4, 2), new Point(0, 0, 3), new Point(0, 0, 7));
        beam.Update(1);
        position = beam.getLocation();
        assertEquals(0f, position.y);
        assertEquals(4f, position.z);

        beam.setLocation(new Point(0, 0, 6));
        beam.setVelocity(new Vector(0, 0, -2));
        beam.Update(0.5f);
        position = beam.getLocation();
        assertEquals(0f, position.x);
        assertEquals(5f, position.z);
    }

    /**
     * Sprawdza zachowanie się belki przy krańcowych punktach (zmiana kierunku poruszania się)
     * @throws Exception Wyjątek.
     */
    public void testOnEndsUpdate() throws Exception {

        beam = new Beam(new Point(0, 0, 5), new Vector(0, 0, -2), new BoxSize(3, 4, 2), new Point(0, 0, 3), new Point(0, 0, 7));
        beam.Update(2);
        Point position = beam.getLocation();
        assertEquals(true, position.z <= 3);

        //zmiana kierunku dążenia
        float lastZPosition = position.z;
        beam.Update(0.5f);
        Point nextPosition = beam.getLocation();
        assertEquals(true, lastZPosition < nextPosition.z);
        assertEquals(2.0f, nextPosition.z);

        //podążanie w nowym kierunku
        lastZPosition = nextPosition.z;
        beam.Update(2f);
        nextPosition = beam.getLocation();
        assertEquals(true, lastZPosition < nextPosition.z);
        assertEquals(6.0f, nextPosition.z);
    }
}