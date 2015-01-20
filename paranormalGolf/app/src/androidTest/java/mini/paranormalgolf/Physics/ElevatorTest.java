package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Sprawdza publiczne metody klasy Elevator.
 */
public class ElevatorTest extends TestCase {

    private Elevator elevator;

    public void setUp() throws Exception {
        super.setUp();
        elevator = new Elevator(new Point(0, 0, 0), new Vector(0, 1, 0), new BoxSize(10, 2, 3), new Point(0, -5, 0), new Point(0, 5, 0), 0.1f);
    }

    public void tearDown() throws Exception {

    }

    /**
     * Sprawdza poruszanie się windy pomiędzy punktami krańcowymi.
     * @throws Exception Wyjątek.
     */
    public void testUpdate() throws Exception {

        //poruszanie się windy pomiędzy 2 punktami
        elevator.Update(1);
        Point position = elevator.getLocation();
        assertEquals(1f, position.y);
        assertEquals(0f, position.x);

        elevator.setLocation(new Point(0, 1, 0));
        elevator.setVelocity(new Vector(0, -1, 0));
        elevator.Update(0.5f);
        position = elevator.getLocation();
        assertEquals(0.5f, position.y);
        assertEquals(0f, position.z);
    }

    /**
     * Sprawdza zachowanie się windy przy krańcowych punktach (oczekiwanie, zatrzymanie, ponowne wyruszenie)
     * Czas oczekiwania na końcach wynosi 2s.
     * @throws Exception Wyjątek.
     */
    public void testOnEndsUpdate() throws Exception {
        //sprawdzamy czy winda stoi na końcach, czas oczekiwania na końcach 2s
        elevator.setLocation(new Point(0, -4, 0));
        elevator.setVelocity(new Vector(0, -1, 0));
        elevator.Update(2);
        Point position = elevator.getLocation();
        assertEquals(-5.0f, position.y);

        elevator.Update(0.5f);
        position = elevator.getLocation();
        assertEquals(-5.0f, position.y);

        //wyruszenie windy
        elevator.Update(2f);
        position = elevator.getLocation();
        assertEquals(-3.5f, position.y);
    }
}