package mini.paranormalgolf.Graphics.ModelBuilders;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Sphere;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.Axis;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class ObjectGenerator {

    public static final int VERTEX_PER_RECTANGLE = 4;

    public static GraphicsData createBall(Point center, float radius, int numPoints) {
        int size = 2 * (numPoints + 1) * (numPoints + 1);

        ObjectBuilder builder = new ObjectBuilder(size, true);
        builder.appendSphere(new Sphere(new Point(0,0,0), radius), numPoints);
        return builder.build();
    }

//    public static GraphicsData createFloorPart(Rectangle rectangle, Axis axis) {
//
//        ObjectBuilder builder = new ObjectBuilder(VERTEX_PER_RECTANGLE,true);
//        // TODO poprawic punkt center!!!
//        builder.appendRectangle(new Rectangle(new Point(0,0,0), rectangle.a, rectangle.b), axis, new Point (0,0,0));
//        return builder.build();
//    }

    public static GraphicsData createFloorPart(Rectangle rectangle, Axis axis, float normalVectorDirection) {

        ObjectBuilder builder = new ObjectBuilder(VERTEX_PER_RECTANGLE,true);
        // TODO poprawic punkt center!!!
        builder.appendRectangle(new Rectangle(new Point(0,0,0), rectangle.a, rectangle.b), axis, normalVectorDirection);
        return builder.build();
    }

}
