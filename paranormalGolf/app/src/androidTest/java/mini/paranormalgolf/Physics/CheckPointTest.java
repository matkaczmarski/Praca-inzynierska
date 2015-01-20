package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

public class CheckPointTest extends TestCase {

    public void testVisit() throws Exception {
        CheckPoint checkPoint=new CheckPoint(new Point(0,0,0),new ConicalFrustum(4,2,3));
        assertEquals(false,checkPoint.isVisited());
        checkPoint.visit();
        assertEquals(true,checkPoint.isVisited());
        checkPoint.visit();
        assertEquals(true,checkPoint.isVisited());
    }
}