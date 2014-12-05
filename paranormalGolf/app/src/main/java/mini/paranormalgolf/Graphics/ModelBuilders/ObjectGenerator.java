package mini.paranormalgolf.Graphics.ModelBuilders;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Sphere;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class ObjectGenerator {

    public static GraphicsData createBall(Point center, float radius, int numPoints) {
        int size = 2 * (numPoints + 1) * (numPoints + 1);

        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendSphere(new Sphere(center, radius), numPoints);
        return builder.build();
    }

    public static GraphicsData createFloor(Point center, float sizeX, float sizeY) {

        ObjectBuilder builder = new ObjectBuilder(6);
        builder.appendRectangle(new Rectangle(center, sizeX, sizeY));
        return builder.build();
    }

}
