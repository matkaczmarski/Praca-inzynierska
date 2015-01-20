package mini.paranormalgolf.Physics;

import junit.framework.TestCase;

import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

public class FinishTest extends TestCase {


    public void testActivate() throws Exception {
        Finish finish=new Finish(new Point(0,0,0),new ConicalFrustum(4,2,3));
        assertEquals(false,finish.isActive());
        finish.activate();
        assertEquals(true,finish.isActive());
        finish.activate();
        assertEquals(true,finish.isActive());
    }
}