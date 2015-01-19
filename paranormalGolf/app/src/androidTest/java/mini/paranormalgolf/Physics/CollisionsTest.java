package mini.paranormalgolf.Physics;

import android.test.InstrumentationTestCase;

import mini.paranormalgolf.Physics.Collisions;
import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;

/**
 * Created by Sławomir on 2015-01-18.
 */
public class CollisionsTest extends InstrumentationTestCase {
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

    public void testCheckSphereCylinderCollision(){
//        Sphere sphere = new Sphere(new Point(0, 0, 0), 2);
//        Box box = new Box(new Point(0, 0, 0), new BoxSize(1, 1, 1));
//        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere, box));
//
//        Sphere sphere2 = new Sphere(new Point(0, 0, 3), 2);
//        Box box2 = new Box(new Point(3, 3, 2), new BoxSize(4, 4, 4));
//        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere2, box2));
//
//        Sphere sphere3 = new Sphere(new Point(0, 0, 0), 2);
//        Box box3 = new Box(new Point(5, 10, 5), new BoxSize(3, 3, 1));
//        assertEquals(false, Collisions.CheckSphereCylinderCollision(sphere3, box3));
//
//        Sphere sphere4 = new Sphere(new Point(-2, 5, 0), 3);
//        Box box4 = new Box(new Point(0, 0, -2), new BoxSize(2, 6, 4));
//        assertEquals(true, Collisions.CheckSphereCylinderCollision(sphere4, box4));
//
//        Sphere sphere5 = new Sphere(new Point(2, 2, 0), 2);
//        Box box5 = new Box(new Point(-1, 0, 0), new BoxSize(2, 2, 1));
//        assertEquals(false, Collisions.CheckSphereCylinderCollision(sphere5, box5));
    }
//
//    public void test() throws Exception {
//        final int expected = 5;
//        final int reality = 5;
//        assertEquals(expected, reality);
//    }
}
