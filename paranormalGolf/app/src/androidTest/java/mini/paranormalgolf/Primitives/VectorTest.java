package mini.paranormalgolf.Primitives;

import junit.framework.TestCase;

public class VectorTest extends TestCase {


    public void testLength() throws Exception {
        Vector v = new Vector(3, 4, 0);
        assertEquals(5f, v.length());
        v = new Vector(-2, 2, 0);
        assertEquals(((float) Math.sqrt(8)), v.length());
    }

    public void testDotProduct() throws Exception {
        Vector v1=new Vector(2,3,4);
        Vector v2=new Vector(3,-1,0);
        Vector v3=new Vector(-1,0,0);
        assertEquals(3f,v1.dotProduct(v2));
        assertEquals(3f,v2.dotProduct(v1));
        assertEquals(-2f,v1.dotProduct(v3));
        assertEquals(-3f,v3.dotProduct(v2));
    }

    public void testScale() throws Exception {
        Vector v1 = new Vector(3, -5, 0);
        Vector v2 = v1.scale(0.4f);
        assertEquals(1.2f, v2.x);
        assertEquals(-2f, v2.y);
        assertEquals(0f, v2.z);
        Vector v3 = v1.scale(0);
        assertEquals(0f, v3.x);
    }

    public void testNormalize() throws Exception {

        Vector v1 = new Vector(3, -2, 1);
        Vector v2 = v1.normalize();
        assertEquals(true, Math.abs(v2.length() - 1) < 0.0001);
        assertEquals(true, Math.abs(Math.abs(v2.x) / Math.abs(v2.z) - 3) < 0.0001);
        assertEquals(true, Math.abs(Math.abs(v2.x) / Math.abs(v2.y) - 1.5) < 0.0001);
    }

    public void testIsParallelToAxis() throws Exception {
        Vector v1 = new Vector(3, -1, 2);
        assertEquals(false, v1.IsParallelToAxis());

        Vector v2 = new Vector(2, 7, 0);
        assertEquals(false, v2.IsParallelToAxis());

        Vector v3 = new Vector(0, 0, 2);
        assertEquals(true, v3.IsParallelToAxis());

        Vector v4 = new Vector(-3, 0, 0);
        assertEquals(true, v4.IsParallelToAxis());
    }
}