package mini.paranormalgolf.Physics;



import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

public class BallTest extends TestCase {

    /**
     * Sprawdza zachowanie się kulki podczas spadku swobodnego.
     * @throws Exception Wyjątek.
     */
    public void testFlyingBallUpdate() throws Exception {


        Ball ball = new Ball(new Point(3, 4, 2), 1, new Vector(0, -1, 0), Ball.BallTexture.amethystAlcove);
        float[] lastRotation = ball.getRotation().clone();
        ball.Update(1, new Vector(0, -10, 0), -1);

        float[] nextRotation = ball.getRotation();
        assertEquals(true, ball.getLocation().x == 3.0f);
        assertEquals(true, ball.getLocation().y < 4.0f);
        assertEquals(true, ball.getLocation().z == 2.0f);

        assertEquals(true, ball.getVelocity().x == 0f);
        assertEquals(true, ball.getVelocity().y < -1.0f);
        assertEquals(true, ball.getVelocity().z == 0f);

        for (int i = 0; i < 16; i++)
            assertEquals(true, lastRotation[i] == nextRotation[i]);

    }

    /**
     * Sprawdza zachowanie się kulki podczas toczenia po powierzchni.
     * @throws Exception Wyjątek.
     */
    public void testRollingBallUpdate() throws Exception {

        Ball ball = new Ball(new Point(3, 4, 2), 1, new Vector(2, 0, -2), Ball.BallTexture.amethystAlcove);
        float[] lastRotation = ball.getRotation().clone();
        ball.Update(1, new Vector(0, 0, 0), 0.1f);

        float[] nextRotation = ball.getRotation();
        assertEquals(true, ball.getLocation().x > 3.0f && ball.getLocation().x <= 5.0f);
        assertEquals(true, ball.getLocation().y == 4.0f);
        assertEquals(true, ball.getLocation().z < 2.0f && ball.getLocation().z >= 0f);

        assertEquals(true, ball.getVelocity().x >= 0f && ball.getVelocity().x < 2f);
        assertEquals(true, ball.getVelocity().y == 0f);
        assertEquals(true, ball.getVelocity().z <= 0f && ball.getVelocity().z > -2f);

        boolean isEqual = true;
        for (int i = 0; i < 16; i++)
            if (lastRotation[i] != nextRotation[i]) {
                isEqual = false;
                break;
            }
        assertEquals(false, isEqual);
    }

    /**
     * Testuje sprawdzanie kolizji kuli ze ścianą.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithWall() throws Exception {
        Ball ball = new Ball(new Point(0, 0, 3), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Wall wall = new Wall(new Point(3, 3, 2), new BoxSize(4, 4, 4));
        assertEquals(true, ball.CheckCollision(wall));

        Ball ball2 = new Ball(new Point(0, 0, 0), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Wall wall2 = new Wall(new Point(5, 10, 5), new BoxSize(3, 3, 1));
        assertEquals(false, ball2.CheckCollision(wall2));

    }

    /**
     * Testuje sprawdzanie kolizji kuli z podłogą.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithFloor() throws Exception {

        Ball ball = new Ball(new Point(-2, 5, 0), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Floor floor = new Floor(new BoxSize(2, 6, 4), 0.05f, new Point(0, 0, -2));
        assertEquals(true, ball.CheckCollision(floor));

        Ball ball2 = new Ball(new Point(2, 2, 0), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Floor floor2 = new Floor(new BoxSize(2, 2, 1), 0.05f, new Point(-1, 0, 0));
        assertEquals(false, ball2.CheckCollision(floor2));

    }

    /**
     * Testuje sprawdzanie kolizji kuli z diamentem.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithDiamond() throws Exception {

        Ball ball = new Ball(new Point(-3, 0.7f, 2.5f), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Diamond diamond=new Diamond(new Point(0,0,0),4,0);
        assertEquals(false,ball.CheckCollision(diamond));

        Ball ball2 = new Ball(new Point(2, 1.5f, -2), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        assertEquals(true,ball2.CheckCollision(diamond));
    }

    /**
     * Testuje sprawdzanie kolizji kuli z klepsydrą.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithHourGlass() throws Exception {

        Ball ball = new Ball(new Point(3, 0.2f, 1f), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        HourGlass hourGlass=new HourGlass(new Point(0,0,0),4,0);
        assertEquals(true,ball.CheckCollision(hourGlass));

        Ball ball2 = new Ball(new Point(3, 1.7f, -2), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        assertEquals(false,ball2.CheckCollision(hourGlass));

    }

    /**
     * Testuje sprawdzanie kolizji kuli z metą.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithFinish() throws Exception {

        Ball ball = new Ball(new Point(4, 10, 7), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Finish finish = new Finish(new Point(4, 6, 4), new ConicalFrustum(10, 3, 4));
        assertEquals(false, ball.CheckCollision(finish));

        Ball ball2 = new Ball(new Point(6.5f, 8, 5), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Finish finish2 = new Finish(new Point(4, 6, 4), new ConicalFrustum(10, 3, 4));
        assertEquals(true, ball2.CheckCollision(finish2));
    }

    /**
     * Testuje sprawdzanie kolizji kuli z punktem kontrolnym.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithCheckPoint() throws Exception {

        Ball ball = new Ball(new Point(0, 8, 4), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        CheckPoint checkPoint = new CheckPoint(new Point(4, 6, 4), new ConicalFrustum(10, 3, 4));
        assertEquals(false, ball.CheckCollision(checkPoint));

        Ball ball2 = new Ball(new Point(2, 8, 6), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        CheckPoint checkPoint2 = new CheckPoint(new Point(4, 6, 4), new ConicalFrustum(10, 3, 4));
        assertEquals(true, ball2.CheckCollision(checkPoint2));
    }

    /**
     * Testuje sprawdzanie kolizji kuli z belką.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithBeam() throws Exception {
        Ball ball = new Ball(new Point(0, 0, 3), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Beam beam = new Beam(new Point(3, 3, 2), new Vector(-1, 0, 0), new BoxSize(4, 4, 4), new Point(0, 3, 2), new Point(6, 3, 2));
        assertEquals(true, ball.CheckCollision(beam));

        Ball ball2 = new Ball(new Point(0, 0, 0), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Beam beam2 = new Beam(new Point(5, 10, 5), new Vector(0, 0, 1), new BoxSize(3, 3, 1), new Point(5, 10, 0), new Point(5, 10, 10));
        assertEquals(false, ball2.CheckCollision(beam2));
    }

    /**
     * Testuje sprawdzanie kolizji kuli z windą.
     * @throws Exception Wyjątek.
     */
    public void testCheckCollisionWithElevator() throws Exception {
        Ball ball = new Ball(new Point(-2, 5, 0), 3, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Elevator elevator = new Elevator(new Point(0, 0, -2), new Vector(0, 1, 0), new BoxSize(2, 6, 4), new Point(0, -10, -2), new Point(0, 10, -2), 0.05f);
        assertEquals(true, ball.CheckCollision(elevator));

        Ball ball2 = new Ball(new Point(2, 2, 0), 2, new Vector(0, 0, 0), Ball.BallTexture.amethystAlcove);
        Elevator elevator2 = new Elevator(new Point(-1, 0, 0), new Vector(0, 1, 0), new BoxSize(2, 2, 1), new Point(-1, -10, 0), new Point(-1, 10, 0), 0.05f);
        assertEquals(false, ball2.CheckCollision(elevator2));
    }

    /**
     * Testuje rozwiązywanie kolizji kuli z podłogą.
     * @throws Exception Wyjątek.
     */
    public void testReactOnCollisionWithFloor() throws Exception {
        Updater.INTERVAL_TIME = 1;
        Floor floor = new Floor( new BoxSize(4, 8, 6),0.05f,new Point(4, 0, 0));

        //collision with wall
        Ball ball2 = new Ball(new Point(4, 4.9f, 0), 1, new Vector(-1, -0.2f, 0), Ball.BallTexture.amethystAlcove);
        ball2.setLastMove(new Vector(-1, -0.2f, 0));
        ball2.ReactOnCollision(floor);

        Vector velocity = ball2.getVelocity();
        assertEquals(0.8f*-1f, velocity.x);
        assertEquals(0f, velocity.y);
        assertEquals(0f, velocity.z);


        //collsion with edge
        Ball ball3 = new Ball(new Point(8f, 7.6666666f, -2.3333333f), 5, new Vector(-2f, -0.6666666f, -0.6666666f), Ball.BallTexture.amethystAlcove);
        ball3.setLastMove(new Vector(-2f, -0.6666666f, -0.6666666f));
        ball3.ReactOnCollision(floor);

        velocity = ball3.getVelocity();
        assertEquals(true, Math.abs(0.8f*-0.6666666f - velocity.z) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-0.6666666f - velocity.y) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-2 - velocity.x) < 0.00001f);
    }

    /**
     * Testuje rozwiązywanie kolizji kuli ze ścianą.
     * @throws Exception Wyjątek.
     */
    public void testReactOnCollisionWithWall() throws Exception {

        Updater.INTERVAL_TIME = 1;
        Wall wall = new Wall(new Point(4, 0, 0), new BoxSize(4, 8, 6));

        //collision with wall
        Ball ball = new Ball(new Point(7.5f, 4, 2.5f), 2, new Vector(-1.5f, 3, 1.5f), Ball.BallTexture.amethystAlcove);
        ball.setLastMove(new Vector(-1.5f, 3, 1.5f));
        ball.ReactOnCollision(wall);

        Vector velocity = ball.getVelocity();
        assertEquals(0.8f*1.5f, velocity.x);
        assertEquals(0.8f*3f, velocity.y);
        assertEquals(0.8f*1.5f, velocity.z);


        //collision with edge
        Ball ball4 = new Ball(new Point(9, -3, 5.5f), 5, new Vector(-3, -3, -1.5f), Ball.BallTexture.amethystAlcove);
        ball4.setLastMove(new Vector(-3, -3, -1.5f));
        ball4.ReactOnCollision(wall);

        velocity = ball4.getVelocity();
        assertEquals(true, Math.abs(0.8f*(-3f) - velocity.y) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*(-3) - velocity.x) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*(-1.5f) - velocity.z) < 0.00001f);
    }

    /**
     * Testuje rozwiązywanie kolizji kuli z belką.
     * @throws Exception Wyjątek.
     */
    public void testReactOnCollisionWithBeam() throws Exception {
        Updater.INTERVAL_TIME = 1;

        //collision with wall
        Beam element = new Beam(new Point(2, 0, 0), new Vector(2, 0, 0), new BoxSize(10, 2, 2), new Point(-10, 0, 0), new Point(10, 0, 0));
        element.setLastMove(new Vector(2, 0, 0));
        Ball ball = new Ball(new Point(-3, 0, 0), 1, new Vector(4, 0, 0), Ball.BallTexture.amethystAlcove);
        ball.setLastMove(new Vector(4, 0, 0));
        ball.ReactOnCollision(element);
        assertEquals(2f, element.getLocation().x);
        assertEquals(0f, element.getLocation().y);
        assertEquals(0f, element.getLocation().z);

        assertEquals(-3.2f, ball.getVelocity().x);
        assertEquals(0f, ball.getVelocity().y);
        assertEquals(0f, ball.getVelocity().z);

        assertEquals(true, ball.getLocation().x < -5);
        assertEquals(0f, ball.getLocation().y);
        assertEquals(0f, ball.getLocation().z);


        //collision with edge
        Beam element3 = new Beam(new Point(2, 0, 0), new Vector(2, 0, 0), new BoxSize(10, 2, 2), new Point(-10, 0, 0), new Point(10, 0, 0));
        element3.setLastMove(new Vector(2, 0, 0));
        Ball ball3 = new Ball(new Point(-1, 0, -1), (float) (2 * Math.sqrt(2)), new Vector(10, 0, 4), Ball.BallTexture.amethystAlcove);
        ball3.setLastMove(new Vector(10, 0, 4));
        ball3.ReactOnCollision(element3);

        assertEquals(true, Math.abs(ball3.getVelocity().x + 0.8f*4) < 0.001f);
        assertEquals(true, Math.abs(ball3.getVelocity().y) < 0.001f);
        assertEquals(true, Math.abs(ball3.getVelocity().z + 0.8f*10) < 0.001f);
    }

    /**
     * Testuje rozwiązywanie kolizji kuli z windą.
     * @throws Exception Wyjątek.
     */
    public void testReactOnCollisionWithElevator() throws Exception {
        Updater.INTERVAL_TIME = 1;

        //collision with wall
        Elevator element2 = new Elevator(new Point(0, 2, 0), new Vector(0, 2, 0), new BoxSize(10, 2, 2), new Point(0, -10, 0), new Point(0, 10, 0), 0.05f);
        element2.setLastMove(new Vector(0, 2, 0));
        Ball ball2 = new Ball(new Point(0, 2.5f, 0), 1, new Vector(0, -1, 0), Ball.BallTexture.amethystAlcove);
        ball2.setLastMove(new Vector(0, -1, 0));
        ball2.ReactOnCollision(element2);

        assertEquals(0f, ball2.getLocation().x);
        assertEquals(true, Math.abs(ball2.getLocation().y - 4) < 0.00001);
        assertEquals(0f, ball2.getLocation().z);

        assertEquals(0f, ball2.getVelocity().y);


        //collision with edge
        Elevator element4 = new Elevator(new Point(0, 2, 0), new Vector(0, 2, 0), new BoxSize(10, 4, 10), new Point(0, -10, 0), new Point(0, 10, 0), 0.05f);
        element4.setLastMove(new Vector(0, 2, 0));
        Ball ball4 = new Ball(new Point(6, 0, -4), (float) (2 * Math.sqrt(2)), new Vector(-2, -2, 6), Ball.BallTexture.amethystAlcove);
        ball4.setLastMove(new Vector(-2, -2, 6));
        ball4.ReactOnCollision(element4);

        assertEquals(true, Math.abs(ball4.getVelocity().x - 0.8f*6) < 0.001f);
        assertEquals(true, Math.abs(ball4.getVelocity().y + 0.8f*2) < 0.001f);
        assertEquals(true, Math.abs(ball4.getVelocity().z + 0.8f*2) < 0.001f);

    }
}