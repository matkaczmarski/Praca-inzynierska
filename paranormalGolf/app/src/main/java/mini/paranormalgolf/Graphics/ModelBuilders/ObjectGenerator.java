package mini.paranormalgolf.Graphics.ModelBuilders;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
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


    public static GraphicsData createFloorPart(Rectangle rectangle, Axis axis, float normalVectorDirection, float textureUnit) {
        ObjectBuilder builder = new ObjectBuilder(VERTEX_PER_RECTANGLE,true);
        builder.appendRectangle(new Rectangle(CENTER_POINT, rectangle.a, rectangle.b), axis, normalVectorDirection, textureUnit);
        return builder.build();
    }

    public static GraphicsData createBox(BoxSize boxSize, float textureUnit) {
        ObjectBuilder builder = new ObjectBuilder(6 * VERTEX_PER_RECTANGLE, true);
        builder.appendRectangle(new Rectangle(new Point(0f, boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, -boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(-boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, -boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, -1, textureUnit);
        return builder.build();
    }

    public static GraphicsData createElevator(BoxSize boxSize, Point from, Point to, float textureUnit) {
        ObjectBuilder builder = new ObjectBuilder(2 * 6 * VERTEX_PER_RECTANGLE, true);

        builder.appendRectangle(new Rectangle(new Point(0f, to.Y, 0f), boxSize.x/5, boxSize.z/5), Axis.yAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, from.Y, 0f), boxSize.x/5, boxSize.z/5), Axis.yAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(boxSize.x / 10, 0f, 0f), boxSize.y/5, boxSize.z/5), Axis.xAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(-boxSize.x / 10, 0f, 0f), boxSize.y/5, boxSize.z/5), Axis.xAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, boxSize.z / 10), boxSize.x/5, boxSize.y/5), Axis.zAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, -boxSize.z / 10), boxSize.x/5, boxSize.y/5), Axis.zAxis, -1, textureUnit);


        builder.appendRectangle(new Rectangle(new Point(0f, boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, -boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(-boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, -boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, -1, textureUnit);
        return builder.build();
    }


    public static GraphicsData createDiamond(Pyramid pyramid) {

        ObjectBuilder builder = new ObjectBuilder(2 * (pyramid.baseVerticesCount + 1),true);
        builder.appendPyramidWithoutBase(CENTER_POINT, pyramid, -1);
        builder.appendPyramidWithoutBase(CENTER_POINT, pyramid, 1);
        return builder.build();
    }

    public static GraphicsData createControlPointPlatform(float radius, int numPoints, float yShift) {
        ObjectBuilder builder = new ObjectBuilder(1 + (numPoints + 1),true);
        builder.appendCircle(new Point(0f, yShift, 0f), radius, 1, numPoints);
        return builder.build();
    }

    public static GraphicsData createControlPointGlow(ConicalFrustum conicalFrustum, int numPoints) {
        ObjectBuilder builder = new ObjectBuilder(2 * (numPoints + 1),false);
        builder.appendCylindersCurvedSurface(new Point(0, 0, 0f), conicalFrustum.getBottomRadius(),new Point(0, conicalFrustum.getHeight(), 0f), conicalFrustum.getTopRadius(), numPoints );
        return builder.build();
    }

}
