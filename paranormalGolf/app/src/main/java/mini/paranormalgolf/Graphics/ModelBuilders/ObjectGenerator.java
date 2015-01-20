package mini.paranormalgolf.Graphics.ModelBuilders;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Sphere;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.Axis;

/**
 * Generuje modele obiektów występujących na planszy. Siatka trójkątów generowana jest względem środka globalnego układu współrzędnych - punktu (0,0,0).
 */
public class ObjectGenerator {

    /**
     * Stała opisująca ilość wierzchołków w prostokącie
     */
    public static final int VERTEX_PER_RECTANGLE = 4;
    /**
     * Stała opisująca środek układu współrzędnych.
     */
    private static final Point CENTER_POINT = new Point(0f, 0f, 0f);

    /**
     * Generuje siatkę trójkątów dla obiektu kulki.
     * @param radius Wartość promienia kulki.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu kulki oraz reguły jego ryowania.
     */
    public static GraphicsData createBallModel(float radius, int numPoints) {
        int size = 2 * numPoints * (numPoints + 1);

        ObjectBuilder builder = new ObjectBuilder(size, ObjectBuilder.DrawType.texturing);
        builder.appendSphere(new Sphere(CENTER_POINT, radius), numPoints);
        return builder.build();
    }

    /**
     * Generuje model dla przestrzennego tła.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu tła oraz reguły jego ryowania.
     */
    public static GraphicsData createSkyBoxModel(){
        ObjectBuilder builder = new ObjectBuilder(0, ObjectBuilder.DrawType.skyBox);
        builder.appendSkyBox();
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów prostopadłościanu.
     * @param boxSize Wymiary prostopadłościanu.
     * @param textureUnit Wielkość kwadratowego kafelka tekstury.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu prostopadłościanu oraz reguły jego ryowania.
     */
    public static GraphicsData createBoxModel(BoxSize boxSize, float textureUnit) {
        ObjectBuilder builder = new ObjectBuilder(6 * VERTEX_PER_RECTANGLE, ObjectBuilder.DrawType.texturing);
        builder.appendRectangle(new Rectangle(new Point(0f, boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, -boxSize.y / 2, 0f), boxSize.x, boxSize.z), Axis.yAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(-boxSize.x / 2, 0f, 0f), boxSize.y, boxSize.z), Axis.xAxis, -1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, 1, textureUnit);
        builder.appendRectangle(new Rectangle(new Point(0f, 0f, -boxSize.z / 2), boxSize.x, boxSize.y), Axis.zAxis, -1, textureUnit);
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów dla obiektu diamentu.
     * @param pyramid Wymiary ostrosłupa definiującego jedną z części diamentu.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu diamentu oraz reguły jego ryowania.
     */
    public static GraphicsData createDiamondModel(Pyramid pyramid) {
        ObjectBuilder builder = new ObjectBuilder(2 * (pyramid.baseVerticesCount + 1),ObjectBuilder.DrawType.texturing);
        builder.appendPyramidWithoutBase(pyramid, -1);
        builder.appendPyramidWithoutBase(pyramid, 1);
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów dla platformy punktów kontrolnych oraz mety.
     * @param radius Wartość promienia platformy.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @param yShift Wartość podniesienia platformy stosowana w celu uniknięcia nałożenia modelu z modelem podłogi.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu platformy oraz reguły jego ryowania.
     */
    public static GraphicsData createControlPointPlatformModel(float radius, int numPoints, float yShift) {
        ObjectBuilder builder = new ObjectBuilder(1 + (numPoints + 1),ObjectBuilder.DrawType.texturing);
        builder.appendCircle(new Point(0f, yShift, 0f), radius, 1, numPoints);
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów dla poświaty.
     * @param conicalFrustum Wymiary ściętego stożka definiującego poświatę.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu poświaty oraz reguły jego ryowania.
     */
    public static GraphicsData createControlPointGlowModel(ConicalFrustum conicalFrustum, int numPoints) {
        ObjectBuilder builder = new ObjectBuilder(2 * (numPoints + 1),ObjectBuilder.DrawType.coloring);
        builder.appendCylindersCurvedSurface(new Point(0, 0, 0f), conicalFrustum.bottomRadius,new Point(0, conicalFrustum.height, 0f), conicalFrustum.topRadius, numPoints, 0f );
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów dla szklanej części klepsydry.
     * @param lowerCone Wymiary dolnej części klepsydry.
     * @param upperCone Wymiary górnej części klepsydry.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu szklanej części klepsydry oraz reguły jego ryowania.
     */
    public static GraphicsData createHourglassGlassPartModel(ConicalFrustum lowerCone, ConicalFrustum upperCone, int numPoints) {
        ObjectBuilder builder = new ObjectBuilder(2 * 2 * (numPoints + 1),ObjectBuilder.DrawType.coloring);
        builder.appendCylindersCurvedSurface(new Point(0f, 0f, 0f), upperCone.bottomRadius,new Point(0,upperCone.height, 0f), upperCone.topRadius, numPoints, 0f );
        builder.appendCylindersCurvedSurface(new Point(0f, -lowerCone.height, 0f), lowerCone.bottomRadius,new Point(0, 0f, 0f), lowerCone.topRadius, numPoints, 0f );
        return builder.build();
    }

    /**
     * Generuje siatkę trójkątów dla drewnianych części klepsydry.
     * @param upperCylinder Wymiary górnej drewnianej części klepsydry.
     * @param lowerCylinder Wymiary dolnej drewnianej części klepsydry.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @param textureUnit Wielkość kwadratowego kafelka tekstury.
     * @return Obiekt typu <em>GraphicsData</em> zawierający wierzchołki modelu drewnianych części klepsydry oraz reguły jego ryowania.
     */
    public static GraphicsData createHourglassWoodenPartsModel(Cylinder upperCylinder, Cylinder lowerCylinder, int numPoints, float textureUnit) {
        ObjectBuilder builder = new ObjectBuilder(2*(2 * (1 + (numPoints + 1)) + 2 * (numPoints + 1)),ObjectBuilder.DrawType.texturing);
        builder.appendCircle(new Point(0f, upperCylinder.center.y + upperCylinder.height/2, 0f), upperCylinder.radius, 1, numPoints);
        builder.appendCircle(new Point(0f, upperCylinder.center.y - upperCylinder.height/2, 0f), upperCylinder.radius, -1, numPoints);
        builder.appendCylindersCurvedSurface(new Point(0f, upperCylinder.center.y - upperCylinder.height/2, 0f), upperCylinder.radius, new Point(0f, upperCylinder.center.y + upperCylinder.height/2, 0f), upperCylinder.radius,numPoints, textureUnit );

        builder.appendCircle(new Point(0f, lowerCylinder.center.y + lowerCylinder.height/2, 0f), lowerCylinder.radius, 1, numPoints);
        builder.appendCircle(new Point(0f, lowerCylinder.center.y - lowerCylinder.height/2, 0f), lowerCylinder.radius, -1, numPoints);
        builder.appendCylindersCurvedSurface(new Point(0f, lowerCylinder.center.y - lowerCylinder.height/2, 0f), lowerCylinder.radius, new Point(0f, lowerCylinder.center.y + lowerCylinder.height/2, 0f), lowerCylinder.radius,numPoints, textureUnit );

        return builder.build();
    }

}
