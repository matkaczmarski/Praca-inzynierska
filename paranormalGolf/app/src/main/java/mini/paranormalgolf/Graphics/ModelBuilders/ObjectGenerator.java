package mini.paranormalgolf.Graphics.ModelBuilders;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Sphere;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.Axis;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class ObjectGenerator {

    public static final int VERTEX_PER_RECTANGLE = 4;
    private static final Point CENTER_POINT = new Point(0f, 0f, 0f);

    public static GraphicsData createBall(Point center, float radius, int numPoints) {
        int size = 2 * (numPoints + 1) * (numPoints + 1);

        ObjectBuilder builder = new ObjectBuilder(size, true);
        builder.appendSphere(new Sphere(CENTER_POINT, radius), numPoints);
        return builder.build();
    }


    public static GraphicsData createFloorPart(Rectangle rectangle, Axis axis, float normalVectorDirection) {

        ObjectBuilder builder = new ObjectBuilder(VERTEX_PER_RECTANGLE,true);
        builder.appendRectangle(new Rectangle(CENTER_POINT, rectangle.a, rectangle.b), axis, normalVectorDirection);
        return builder.build();
    }

    public static GraphicsData createDiamond(Pyramid pyramid) {

        ObjectBuilder builder = new ObjectBuilder(2 * (pyramid.baseVerticesCount + 1),true);
        builder.appendPyramidWithoutBase(CENTER_POINT, pyramid, -1);
        builder.appendPyramidWithoutBase(CENTER_POINT, pyramid, 1);
        return builder.build();
    }

}
