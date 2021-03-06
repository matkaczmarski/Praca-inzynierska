
package mini.paranormalgolf.Physics;

import android.test.InstrumentationTestCase;

import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Circle;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Sprawdza metody sprawdzające kolizje i realizujące rozwiązywanie kolizji.
 */
public class CollisionsTest extends InstrumentationTestCase {

    /**
     * Sprawdza kolizje sfery i prostopadłościanu równoległego do osi układu współrzędnych.
     */
    public void testCheckSphereAABBCollisionDetected() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 2);
        Box box = new Box(new Point(0, 0, 0), new BoxSize(1, 1, 1));
        assertEquals(true, Collisions.CheckSphereAABBCollision(sphere, box));

        Sphere sphere2 = new Sphere(new Point(0, 0, 3), 2);
        Box box2 = new Box(new Point(3, 3, 2), new BoxSize(4, 4, 4));
        assertEquals(true, Collisions.CheckSphereAABBCollision(sphere2, box2));

        Sphere sphere3 = new Sphere(new Point(0, 0, 0), 2);
        Box box3 = new Box(new Point(5, 10, 5), new BoxSize(3, 3, 1));
        assertEquals(false, Collisions.CheckSphereAABBCollision(sphere3, box3));

        Sphere sphere4 = new Sphere(new Point(-2, 5, 0), 3);
        Box box4 = new Box(new Point(0, 0, -2), new BoxSize(2, 6, 4));
        assertEquals(true, Collisions.CheckSphereAABBCollision(sphere4, box4));

        Sphere sphere5 = new Sphere(new Point(2, 2, 0), 2);
        Box box5 = new Box(new Point(-1, 0, 0), new BoxSize(2, 2, 1));
        assertEquals(false, Collisions.CheckSphereAABBCollision(sphere5, box5));
    }


    /**
     * Sprawdza kolizje sfery i walca o wysokości równoległej do osi OY układu współrzędnych.
     */
    public void testCheckSphereCylinderCollision() {
        Sphere sphere = new Sphere(new Point(2, 1, -1), 4);
        Cylinder cylinder = new Cylinder(new Point(3, 0, 1), 8, 6);
        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere, cylinder));

        Sphere sphere2 = new Sphere(new Point(-7, -4, 2), 4);
        Cylinder cylinder2 = new Cylinder(new Point(3, 0, 1), 2, 3);
        assertEquals(false, Collisions.CheckSphereCylinderCollision(sphere2, cylinder2));

        Sphere sphere3 = new Sphere(new Point(2, 1, -1), 4);
        Cylinder cylinder3 = new Cylinder(new Point(-5, -2, 3), 3, 2);
        assertEquals(false, Collisions.CheckSphereCylinderCollision(sphere3, cylinder3));

        Sphere sphere4 = new Sphere(new Point(-1, 0, 6), 8);
        Cylinder cylinder4 = new Cylinder(new Point(-5, -2, 3), 4, 7);
        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere4, cylinder4));

        Sphere sphere5 = new Sphere(new Point(0, 0, 0), 2);
        Cylinder cylinder5 = new Cylinder(new Point(6, 0, 0), 3, 4);
        assertEquals(false, Collisions.CheckSphereCylinderCollision(sphere5, cylinder5));

        Sphere sphere6 = new Sphere(new Point(0, 0, 0), 2);
        Cylinder cylinder6 = new Cylinder(new Point(4, 0, 0), 3, 4);
        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere6, cylinder6));

        Sphere sphere7 = new Sphere(new Point(0, 0, 0), 2);
        Cylinder cylinder7 = new Cylinder(new Point(4, 3, 0), 3, 4);
        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere7, cylinder7));

    }


    /**
     * Sprawdza, czy sfera znajduje się na okręgu równoległym do płaszczyzny OXZ.
     */
    public void testCheckSphereCircleCollision() {
        Circle circle = new Circle(new Point(4, 6, 4), 3);

        Sphere sphere = new Sphere(new Point(4, 10, 7), 2);
        assertEquals(false, Collisions.CheckSphereCircleCollision(sphere, circle));

        Sphere sphere2 = new Sphere(new Point(0, 8, 4), 2);
        assertEquals(false, Collisions.CheckSphereCircleCollision(sphere2, circle));

        Sphere sphere3 = new Sphere(new Point(2, 8, 6), 2);
        assertEquals(true, Collisions.CheckSphereCircleCollision(sphere3, circle));

        Sphere sphere4 = new Sphere(new Point(6.5f, 8, 5), 2);
        assertEquals(true, Collisions.CheckSphereCircleCollision(sphere4, circle));
    }

    /**
     * Sprawdza rozwiązanie kolizji kuli z prostopadłościanem statycznym równoległym do osi układu współrzędnych.
     */
    public void testResponseBallAABBCollisions() {

        //2 collisions with walls
        Updater.INTERVAL_TIME = 1;
        Box box = new Box(new Point(4, 0, 0), new BoxSize(4, 8, 6));

        Ball ball = new Ball(new Point(7.5f, 4, 2.5f), 2, new Vector(-1.5f, 3, 1.5f), Ball.BallTexture.amethystAlcove);
        ball.setLastMove(new Vector(-1.5f, 3, 1.5f));
        Collisions.ResponseBallAABBCollisions(ball, box);

        Vector velocity = ball.getVelocity();
        assertEquals(0.8f*1.5f, velocity.x);
        assertEquals(0.8f*3f, velocity.y);
        assertEquals(0.8f*1.5f, velocity.z);

        Ball ball2 = new Ball(new Point(4, 4.9f, 0), 1, new Vector(-1, -0.2f, 0), Ball.BallTexture.amethystAlcove);
        ball2.setLastMove(new Vector(-1, -0.2f, 0));
        Collisions.ResponseBallAABBCollisions(ball2, box);

        velocity = ball2.getVelocity();
        assertEquals(0.8f*-1f, velocity.x);
        assertEquals(0f, velocity.y);
        assertEquals(0f, velocity.z);


        //2 collisions with edges
        Ball ball3 = new Ball(new Point(8f, 7.6666666f, -2.3333333f), 5, new Vector(-2f, -0.6666666f, -0.6666666f), Ball.BallTexture.amethystAlcove);
        ball3.setLastMove(new Vector(-2f, -0.6666666f, -0.6666666f));
        Collisions.ResponseBallAABBCollisions(ball3, box);
        velocity = ball3.getVelocity();
        assertEquals(true, Math.abs(0.8f*-0.6666666f - velocity.z) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-0.6666666f - velocity.y) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-2 - velocity.x) < 0.00001f);


        Ball ball4 = new Ball(new Point(9, -3, 5.5f), 5, new Vector(-3, -3, -1.5f), Ball.BallTexture.amethystAlcove);
        ball4.setLastMove(new Vector(-3, -3, -1.5f));
        Collisions.ResponseBallAABBCollisions(ball4, box);
        velocity = ball4.getVelocity();
        assertEquals(true, Math.abs(0.8f*-3f - velocity.y) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-3 - velocity.x) < 0.00001f);
        assertEquals(false, Math.abs(0.8f*-1.5f - velocity.z) < 0.00001f);
    }

    /**
     * Sprawdza rozwiązanie kolizji kuli z poruszającym się równolegle do osi układu współrzędnych
     * prostopadłościanem równoległym do osi układu współrzędnych.
     */
    public void testResponseMovingBallAABBCollisions() {
        Updater.INTERVAL_TIME = 1;
        //2 collisions with walls

        //collision with beam
        MovableElement element = new Beam(new Point(2, 0, 0), new Vector(2, 0, 0), new BoxSize(10, 2, 2), new Point(-10, 0, 0), new Point(10, 0, 0));
        element.setLastMove(new Vector(2, 0, 0));
        Ball ball = new Ball(new Point(-3, 0, 0), 1, new Vector(4, 0, 0), Ball.BallTexture.amethystAlcove);
        ball.setLastMove(new Vector(4, 0, 0));
        Collisions.ResponseBallMovingAABBCollisions(ball, element);
        assertEquals(2f, element.getLocation().x);
        assertEquals(0f, element.getLocation().y);
        assertEquals(0f, element.getLocation().z);

        assertEquals(0.8f*-4f, ball.getVelocity().x);
        assertEquals(0f, ball.getVelocity().y);
        assertEquals(0f, ball.getVelocity().z);

        assertEquals(true, ball.getLocation().x < -5);
        assertEquals(0f, ball.getLocation().y);
        assertEquals(0f, ball.getLocation().z);


        //collision with moving elevator
        Updater.INTERVAL_TIME = 1;
        MovableElement element2 = new Elevator(new Point(0, 2, 0), new Vector(0, 2, 0), new BoxSize(10, 2, 2), new Point(0, -10, 0), new Point(0, 10, 0), 0.05f);
        element2.setLastMove(new Vector(0, 2, 0));
        Ball ball2 = new Ball(new Point(0, 2.5f, 0), 1, new Vector(0, -1, 0), Ball.BallTexture.amethystAlcove);
        ball2.setLastMove(new Vector(0, -1, 0));
        Collisions.ResponseBallMovingAABBCollisions(ball2, element2);

        assertEquals(0f, ball2.getLocation().x);
        assertEquals(true, Math.abs(ball2.getLocation().y - 4) < 0.00001);
        assertEquals(0f, ball2.getLocation().z);

        assertEquals(0f, ball2.getVelocity().y);

        //2 collisions with edges

        //collision with beam
        MovableElement element3 = new Beam(new Point(2, 0, 0), new Vector(2, 0, 0), new BoxSize(10, 2, 2), new Point(-10, 0, 0), new Point(10, 0, 0));
        element3.setLastMove(new Vector(2, 0, 0));
        Ball ball3 = new Ball(new Point(-1, 0, -1), (float) (2 * Math.sqrt(2)), new Vector(10, 0, 4), Ball.BallTexture.amethystAlcove);
        ball3.setLastMove(new Vector(10, 0, 4));
        Collisions.ResponseBallMovingAABBCollisions(ball3, element3);

        assertEquals(true, Math.abs(ball3.getVelocity().x + 0.8f*4) < 0.001f);
        assertEquals(true, Math.abs(ball3.getVelocity().y) < 0.001f);
        assertEquals(true, Math.abs(ball3.getVelocity().z + 0.8f*10) < 0.001f);


        //collision with moving elevator
        MovableElement element4 = new Elevator(new Point(0, 2, 0), new Vector(0, 2, 0), new BoxSize(10, 4, 10), new Point(0, -10, 0), new Point(0, 10, 0), 0.05f);
        element4.setLastMove(new Vector(0, 2, 0));
        Ball ball4 = new Ball(new Point(6, 0, -4), (float) (2 * Math.sqrt(2)), new Vector(-2, -2, 6), Ball.BallTexture.amethystAlcove);
        ball4.setLastMove(new Vector(-2, -2, 6));
        Collisions.ResponseBallMovingAABBCollisions(ball4, element4);

        assertEquals(true, Math.abs(ball4.getVelocity().x - 0.8f*6) < 0.001f);
        assertEquals(true, Math.abs(ball4.getVelocity().y + 0.8f*2) < 0.001f);
        assertEquals(true, Math.abs(ball4.getVelocity().z + 0.8f*2) < 0.001f);
    }
}
