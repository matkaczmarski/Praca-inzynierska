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

    private static final int VERTEX_PER_RECTANGLE = 6;

    public static GraphicsData createBall(Point center, float radius, int numPoints) {
        int size = 4 * (numPoints + 1) * (numPoints + 1);

        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendSphere(new Sphere(new Point(0,0,0), radius), numPoints);
        return builder.build();
    }

    public static GraphicsData createFloor(Point center, float sizeX, float sizeY, float sizeZ) {

        ObjectBuilder builder = new ObjectBuilder( 1 *6 * VERTEX_PER_RECTANGLE);
        builder.appendRectangle(new Rectangle(new Point(0, sizeY / 2, 0), sizeX, sizeZ), Axis.yAxis, center);
        builder.appendRectangle(new Rectangle(new Point(0, - sizeY / 2, 0), sizeX, sizeZ), Axis.yAxis, center);

        builder.appendRectangle(new Rectangle(new Point( sizeX / 2, 0, 0), sizeY, sizeZ), Axis.xAxis, center);
        builder.appendRectangle(new Rectangle(new Point(- sizeX / 2, 0, 0), sizeY, sizeZ), Axis.xAxis, center);

        builder.appendRectangle(new Rectangle(new Point(0, 0, - sizeZ / 2), sizeX, sizeY), Axis.zAxis, center);
        builder.appendRectangle(new Rectangle(new Point(0, 0, sizeZ / 2), sizeX, sizeY), Axis.zAxis, center);

        return builder.build();
    }

}
