package mini.paranormalgolf.Primitives;

import junit.framework.TestCase;

public class PointTest extends TestCase {

    public void testSubtract() throws Exception {

        Point p1=new Point(0,2,3);
        Point p2=p1.Subtract(new Point(3, 5, -2));
        assertEquals(-3f,p2.x);
        assertEquals(5f,p2.z);
        assertEquals(0f,p1.x);
        Point p3=p1.Subtract(new Point(3, 3, 3));
        assertEquals(0f,p3.z);
        assertEquals(2f,p1.y);
    }
}