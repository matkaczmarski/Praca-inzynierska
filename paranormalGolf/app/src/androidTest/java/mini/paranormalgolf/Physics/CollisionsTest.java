package mini.paranormalgolf.Physics;

import android.test.InstrumentationTestCase;

import mini.paranormalgolf.Physics.Collisions;
import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Cylinder;
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
        Sphere sphere=new Sphere(new Point(2,1,-1),4);
        Cylinder cylinder=new Cylinder(new Point(3,0,1),8,6);
        assertEquals(true,Collisions.CheckSphereCylinderCollision(sphere,cylinder));

        Sphere sphere2=new Sphere(new Point(-7,-4,2),4);
        Cylinder cylinder2=new Cylinder(new Point(3,0,1),2,3);
        assertEquals(false,Collisions.CheckSphereCylinderCollision(sphere2,cylinder2));

        Sphere sphere3=new Sphere(new Point(2,1,-1),4);
        Cylinder cylinder3=new Cylinder(new Point(-5,-2,3),3,2);
        assertEquals(false,Collisions.CheckSphereCylinderCollision(sphere3,cylinder3));

        Sphere sphere4=new Sphere(new Point(-1,0,6),8);
        Cylinder cylinder4=new Cylinder(new Point(-5,-2,3),4,7);
        assertEquals(true,Collisions.CheckSphereCylinderCollision(sphere4,cylinder4));

        Sphere sphere5=new Sphere(new Point(0,0,0),2);
        Cylinder cylinder5=new Cylinder(new Point(6,0,0),3,4);
        assertEquals(false,Collisions.CheckSphereCylinderCollision(sphere5,cylinder5));

        Sphere sphere6=new Sphere(new Point(0,0,0),2);
        Cylinder cylinder6=new Cylinder(new Point(4,0,0),3,4);
        assertEquals(true,Collisions.CheckSphereCylinderCollision(sphere6,cylinder6));

//        Sphere sphere7=new Sphere(new Point(0,0,0),2);
//        Cylinder cylinder7=new Cylinder(new Point(4,4,0),3,4);
//        assertEquals(false,Collisions.CheckSphereCylinderCollision(sphere7,cylinder7));

        Sphere sphere8=new Sphere(new Point(0,0,0),2);
        Cylinder cylinder8=new Cylinder(new Point(4,3,0),3,4);
        assertEquals(true,Collisions.CheckSphereCylinderCollision(sphere8,cylinder8));
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
