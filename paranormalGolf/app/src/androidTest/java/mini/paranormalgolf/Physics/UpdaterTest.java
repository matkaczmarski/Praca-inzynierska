package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

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
    private Point ballLocation = null;

    public void setUp() throws Exception {
        super.setUp();
        ball = new Ball(new Point(1, 1, 1), 1, new Vector(0, 0, 0), Ball.BallTexture.beach);
        board = new Board(1, floors, walls, diamonds, beams, elevators, checkPoints, hourGlasses, finish, ballLocation);
    }

    public void tearDown() throws Exception {

    }

    public void testUpdate() throws Exception {

        Updater updater = new Updater(null, ball, board, false, false, false, false, null);
        // updater.update(0.1f);
        //coś tam sprawdź

    }

    public void testDraw() throws Exception {

    }

    public void testGetCollectedDiamondsCount() throws Exception {

    }

    public void testOnSensorChanged() throws Exception {

    }
}