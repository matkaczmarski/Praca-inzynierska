package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

public class UpdaterTest extends TestCase {

    private Ball ball;
    private Board board;
    private List<Floor> floors = new ArrayList<Floor>();
    private List<Wall> walls = new ArrayList<Wall>();
    private List<Diamond> diamonds = new ArrayList<Diamond>();
    private List<Beam> beams = new ArrayList<Beam>();
    private List<Elevator> elevators = new ArrayList<Elevator>();
    private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
    private List<HourGlass> hourGlasses = new ArrayList<HourGlass>();
    private Finish finish = null;
    private Point ballLocation = new Point(0,0,0);

    private Updater updater;

    public void setUp() throws Exception {
        super.setUp();
        floors.add(new Floor(new BoxSize(10, 2, 8), 0.1f, new Point(0, 0, 0)));
        elevators.add(new Elevator(new Point(9, 3, 0), new Vector(0, 1, 0), new BoxSize(8, 2, 8), new Point(9, 0, 0), new Point(9, 6, 0), 0.01f));
        beams.add(new Beam(new Point(-2, 2, 0), new Vector(0, 0, -1), new BoxSize(4, 2, 8), new Point(-2, 2, -4), new Point(-2, 2, 4)));
        finish = new Finish(new Point(-10, 10, 10), new ConicalFrustum(10, 2, 3));
        ball = new Ball(new Point(1, 1, 1), 1, new Vector(0, 0, 0), Ball.BallTexture.beach);
        board = new Board(1, floors, walls, diamonds, beams, elevators, checkPoints, hourGlasses, finish, ballLocation);
        Updater.INTERVAL_FACTOR=1f;
        updater = new Updater(null, ball, board, false, false, false, false, null);
    }

    public void tearDown() throws Exception {

    }

    /**
     * Sprawdza odświeżenie planszy, kiedy kulka spada swobodnie.
     * @throws Exception
     */
    public void testFlyingBallUpdate() throws Exception {
        ball.setLocation(new Point(6,5,6));
        ball.setVelocity(new Vector(1, -1, 1));
        updater.accData=new Vector(0,-1,0);
        updater.update(1);

        assertEquals(-2f,beams.get(0).getLocation().x);
        assertEquals(2f,beams.get(0).getLocation().y);
        assertEquals(-1f,beams.get(0).getLocation().z);

        assertEquals(true,ball.getLocation().x>6);
        assertEquals(true,ball.getLocation().y<4);
        assertEquals(true,ball.getLocation().z>6);

        assertEquals(true,ball.getVelocity().x<1);
        assertEquals(true,ball.getVelocity().y<=-1);
        assertEquals(true,ball.getVelocity().z<1);


    }

    /**
     * Sprawdza odświeżenie planszy, kiedy kulka toczy się po podłodze.
     * @throws Exception
     */
    public void testRollingBallUpdate() throws Exception {

        ball.setLocation(new Point(3,2,3));
        ball.setVelocity(new Vector(1, 0, 1));
        updater.accData=new Vector(0,0,0);
        updater.update(1);

        assertEquals(true,ball.getLocation().x>3 &&ball.getLocation().x<=4);
        assertEquals(true,ball.getLocation().y==2f);
        assertEquals(true,ball.getLocation().z>3 &&ball.getLocation().z<=4);

        assertEquals(true,ball.getVelocity().x>=0&&ball.getVelocity().x<1);
        assertEquals(true,ball.getVelocity().y==0f);
        assertEquals(true,ball.getVelocity().z>=0&&ball.getVelocity().z<1);
    }

    /**
     * Sprawdza odświeżenie planszy, kiedy kulka toczy się po poruszającej się windzie.
     * @throws Exception
     */
    public void testRollingBallOnElevatorUpdate() throws Exception{

        ball.setLocation(new Point(7,5,2));
        ball.setVelocity(new Vector(1, 0, -1));
        updater.accData=new Vector(0,0,0);
        updater.update(1);

        assertEquals(true,ball.getLocation().x>7 &&ball.getLocation().x<=8);
        assertEquals(true,ball.getLocation().y==6f);
        assertEquals(true,ball.getLocation().z<2 &&ball.getLocation().z>=1);

        assertEquals(true,ball.getVelocity().x>=0&&ball.getVelocity().x<1);
        assertEquals(true,ball.getVelocity().y==0f);
        assertEquals(true,ball.getVelocity().z<=0&&ball.getVelocity().z>-1);

    }
}