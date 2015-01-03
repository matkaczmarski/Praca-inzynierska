package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-29.
 */
public final class Collisions {

    public final static float USER_EXPERIENCE = 4e-4f;

    public static boolean CheckSphereAABBCollsion(Sphere sphere, Box box) {

        Point boxCenter = box.center;
        BoxSize boxSize = box.size;
        Point sphereCenter = sphere.center;
        float sphereRadius = sphere.radius;
        Point min = new Point(boxCenter.x - boxSize.x / 2, boxCenter.y - boxSize.y / 2, boxCenter.z - boxSize.z / 2);
        Point max = new Point(boxCenter.x + boxSize.x / 2, boxCenter.y + boxSize.y / 2, boxCenter.z + boxSize.z / 2);
        float d = 0;
        if (sphereCenter.x < min.x) {
            d += (sphereCenter.x - min.x) * (sphereCenter.x - min.x);
        } else if (sphereCenter.x > max.x) {
            d += (sphereCenter.x - max.x) * (sphereCenter.x - max.x);
        }

        if (sphereCenter.y < min.y) {
            d += (sphereCenter.y - min.y) * (sphereCenter.y - min.y);
        } else if (sphereCenter.y > max.y) {
            d += (sphereCenter.y - max.y) * (sphereCenter.y - max.y);
        }

        if (sphereCenter.z < min.z) {
            d += (sphereCenter.z - min.z) * (sphereCenter.z - min.z);
        } else if (sphereCenter.z > max.z) {
            d += (sphereCenter.z - max.z) * (sphereCenter.z - max.z);
        }
        return d < sphereRadius * sphereRadius + USER_EXPERIENCE;
    }

    public static void ResponseBallAABBCollisions(Ball ball, Box collidedBox) {

        //finding collision place and time
        Point startLocation = new Point(ball.getLocation().x - ball.getLastMove().x, ball.getLocation().y - ball.getLastMove().y, ball.getLocation().z - ball.getLastMove().z);
        Point endLocation = ball.getLocation();
        Point halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        float startTime = 0, endTime = 1, halfTime = 0.5f;
        for (int i = 0; i < 10; i++) {
            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfLocation, ball.getRadius()), collidedBox)) {
                endTime = halfTime;
                endLocation = halfLocation;
            } else {
                startTime = halfTime;
                startLocation = halfLocation;
            }
            halfTime = (startTime + endTime) / 2;
            halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        }

        //finding normal vector in place of collision
        Vector normal = new Vector(0, 0, 0);
        Point min = new Point(collidedBox.center.x - collidedBox.size.x / 2, collidedBox.center.y - collidedBox.size.y / 2, collidedBox.center.z - collidedBox.size.z / 2);
        Point max = new Point(collidedBox.center.x + collidedBox.size.x / 2, collidedBox.center.y + collidedBox.size.y / 2, collidedBox.center.z + collidedBox.size.z / 2);

        if (min.x - halfLocation.x > 0 && min.x - halfLocation.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = -1;
        else if (halfLocation.x - max.x > 0 && halfLocation.x - max.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = 1;

        if (min.y - halfLocation.y > 0 && min.y - halfLocation.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = -1;
        else if (halfLocation.y - max.y > 0 && halfLocation.y - max.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = 1;

        if (min.z - halfLocation.z > 0 && min.z - halfLocation.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = -1;
        else if (halfLocation.z - max.z > 0 && halfLocation.z - max.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = 1;

        //counting and setting new location and velocity after collision
        Vector velocity = ball.getVelocity();
        Vector normalVelocity;
        Vector newNormalVelocity = new Vector(0, 0, 0);

        if (normal.length() == 1) {
            if (Math.abs(normal.x) == 1) velocity.x = -velocity.x;
            else if (Math.abs(normal.z) == 1) velocity.z = -velocity.z;
            else // normal.y!=0
                velocity.y = -velocity.y;
        } else {
            if (normal.absSum() == 3) normal.y = 0;
            normal = normal.normalize();

            if (normal.y == 0) {
                normalVelocity = new Vector(-velocity.x, 0, -velocity.z).normalize();
                if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
                    float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                    velocity = new Vector(length * newNormalVelocity.x, velocity.y, length * newNormalVelocity.z);
                }
            } else if (normal.x == 0) {
                normalVelocity = new Vector(0, -velocity.y, -velocity.z).normalize();
                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.y * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.y)) /
                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
                    newNormalVelocity.y =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
                    // newNormalVelocity.x = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.y * velocity.y + velocity.z * velocity.z);
                    velocity = new Vector(velocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            } else { //normal.z==0
                normalVelocity = new Vector(-velocity.x, -velocity.y, 0).normalize();
                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.y =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
                    // newNormalVelocity.z = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, velocity.z);
                }
            }
        }

        ball.setLocation(new Point(halfLocation.x + velocity.x * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfLocation.y + velocity.y * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfLocation.z + velocity.z * (1 - halfTime) * Updater.INTERVAL_TIME));
        ball.setVelocity(velocity);
    }

    public static boolean CheckSphereCylinderCollsion(Sphere sphere, Cylinder cylinder) {
        Point cylinderCenter = cylinder.getCenter();
        float cylinderRadius = cylinder.getRadius();
        float cylinderMinY = cylinderCenter.y - cylinder.getHeight() / 2;
        float cylinderMaxY = cylinderCenter.y + cylinder.getHeight() / 2;
        Point sphereCenter = sphere.center;
        float sphereRadius = sphere.radius;
        float d = 0;
        if (sphereCenter.y < cylinderMinY) {
            d += (sphereCenter.y - cylinderMinY) * (sphereCenter.y - cylinderMinY);
        } else if (sphereCenter.y > cylinderMaxY) {
            d += (sphereCenter.y - cylinderMaxY) * (sphereCenter.y - cylinderMaxY);
        }
        d += (sphereCenter.x - cylinderCenter.x) * (sphereCenter.x - cylinderCenter.x) + (sphereCenter.z - cylinderCenter.z) * (sphereCenter.z - cylinderCenter.z);
        return d < (sphereRadius + cylinderRadius) * (sphereRadius + cylinderRadius) + USER_EXPERIENCE;
    }
}
